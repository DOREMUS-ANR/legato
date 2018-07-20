package legato.match;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.Cell;
import ca.pfv.spmf.algorithms.clustering.distanceFunctions.DistanceCorrelation;
import ca.pfv.spmf.algorithms.clustering.distanceFunctions.DistanceFunction;
import ca.pfv.spmf.patterns.cluster.DoubleArray;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import legato.LEGATO;
import legato.cluster.Cluster;
import legato.cluster.ClusterList;
import legato.cluster.Clustering;
import legato.document.CBDBuilder;
import legato.indexer.DocVector;
import legato.indexer.VectorGenerator;
import legato.keys.KeysClassifier;
import legato.keys.SILK;
import legato.keys.SilkConfig;
import legato.measures.CosineSimilarity;
import legato.rdf.ModelManager;

public class Matchifier {

	public MapList match() throws Exception
	{
		
		LEGATO legato = LEGATO.getInstance();
		MapList mapList = new MapList();   //Final links
	    MapList mapList1 = new MapList();  //Recall++ 
	    MapList mapList2 = new MapList();  //Precision++ 
	    
		/*********
		 * Getting vectors from "Source" and "Target" datasets
		 *********/
		VectorGenerator vectorGenerator = new VectorGenerator();
	    vectorGenerator.GetAllTerms();       
	    DocVector[] docVectors = vectorGenerator.GetDocumentVectors(); //List of "Source" and "Target" TF-IDF vectors
	    HashMap<String,double[]> srcMap = new HashMap<String, double[]>(); //List of "Source" TF-IDF vectors with their "docName"  
	    HashMap<String,double[]> tgtMap = new HashMap<String, double[]>(); //List of "Target" TF-IDF vectors with their "docName"
	    
	    //int ind=0;
	    for(DocVector doc: docVectors) //Identify "Source" and "Target" vectors
	    {
	    	//System.out.println(doc.getVector()+" - size : "+doc.getVector().length+" number "+ ++ind);
	    	
	    	try
	    	{
	    		double[] vector = doc.getVector();
	    		if (doc.parentFolder.equals("source"))
		    		srcMap.put(doc.docName, vector);
		    	else if (doc.parentFolder.equals("target"))
		    		tgtMap.put(doc.docName, vector);
	    	}
	    	catch(NullPointerException e)
	    	{
	    		System.err.println("null vectors");
	    	}
	    }
	    
	    /********
	     * "Hierarchical Clustering" on "Source" and "Target" datasets. 
	     * For each pair --> apply RANkey and link instances based on the
	     * best key. 
	     ********/
	    ClusterList srcClS = Clustering.getClusters(srcMap); //List of "Source" clusters
	    ClusterList tgtClS = Clustering.getClusters(tgtMap); //List of "target" clusters
	    
	    /********
	     * RANKey
	     ********/
	    int pairNumber = 0;
	    File dir = new File(legato.getPath()+File.separator+"clusters"); //All pairs of clusters will be contained in folder "clusters"
		dir.mkdirs();
	    DistanceFunction distanceFunction = new DistanceCorrelation(); 
	    
	    for (Cluster clust1 : srcClS) //For each cluster from "Source" dataset
	    {
	    	for (Cluster clust2 : tgtClS) //For each cluster from "Target" dataset
		    {
	    		DoubleArray cs = new DoubleArray(clust1.getCentroid().elements);
	    		 DoubleArray ct = new DoubleArray(clust2.getCentroid().elements);
	    	//	 if (distanceFunction.calculateDistance(cs, ct)<0.4)

	    		 if (CosineSimilarity.cosineSimilarity(clust1.getCentroid(), clust2.getCentroid())>0.4) 
	    		 {
	    			 if (clust1.size()>1 && clust2.size()>1) //If both clusters contain more than one instance
	    			 {
	    				 pairNumber = pairNumber+1;
	    				 File dirClusters = new File(dir.getAbsolutePath()+File.separator+pairNumber); //Both clusters will be contained in folder "pairNumber"
	    				 dirClusters.mkdirs();
	    			 
	    				 /*******
	    				  * Retrieve RDF Model from the 2 clusters
	    				  *******/
	    				 Model srcModel = ModelManager.loadModel(legato.src.toString());
	    				 Model model1 = ModelFactory.createDefaultModel();
	    				 String[] resources = clust1.getIDs().split("\n");
	    				 for (String rsrce : resources) 
	    				 {
	    					 String uri = legato.getSrcURIs().get(rsrce);
	    					 Resource resource = ResourceFactory.createResource(uri);
	    					 model1.add(CBDBuilder.getCBD(srcModel, resource));
	    				 }
	    				 Model tgtModel = ModelManager.loadModel(legato.tgt.toString());
	    				 Model model2 = ModelFactory.createDefaultModel();
	    				 resources = clust2.getIDs().split("\n");
	    				 for (String rsrce : resources) 
	    				 {
	    					 String uri = legato.getTgtURIs().get(rsrce);
	    					 Resource resource = ResourceFactory.createResource(uri);
	    					 model2.add(CBDBuilder.getCBD(tgtModel, resource));
	    				 }
	    				 
	    				 /************
	    				  * Execute RANKey 
	    				  ************/
	    				 HashSet<String> bestKey = KeysClassifier.getBestKey(model1, model2, dirClusters);
	    				 if (!(bestKey==null))
	    			{
	    					 /************
		    				  * Execute SILK 
		    				  ************/
	    					 SilkConfig.config(bestKey, dirClusters, dirClusters.toString()+File.separator+"source.nt", dirClusters.toString()+File.separator+"target.nt");
	    					 SILK.link(dirClusters.toString());
	    					 File file = new File(dirClusters.toString()+File.separator+"links.rdf");
	    					 AlignmentParser aparser = new AlignmentParser(0);
	    					 Alignment links = aparser.parse(file.toURI());
	    					 for (Cell cell :links)
						     {
						    	 mapList2.add(cell.getObject1AsURI().toString(), cell.getObject2AsURI().toString(), cell.getStrength());
						     }
					 	 }
	    			 }
	    			 else  if (clust1.size()==1 && clust2.size()==1) 
	    			 {
	    			//	 mapList2.add("http://data.doremus.org/expression/"+clust1.getExemplar().getID(), "http://data.doremus.org/expression/"+clust2.getExemplar().getID(), CosineSimilarity.cosineSimilarity(clust1.getCentroid(), clust2.getCentroid()));

	    			 }
	    		 }
		    }
	    }
	    
	    /*****
	     * Comparison 
	     *****/
	    System.out.println("comparison");
	    for(int i = 0; i < docVectors.length; i++)
	    {
	    	DocVector srcDoc = docVectors[i];
	    	String tgtDoc = null;
	        double simVal = 0;
	    	for (int j = 0; j < docVectors.length; j++) 
	    	{
	    		try
	    		{
		    		if ((srcDoc.parentFolder.equals("source"))&&(docVectors[j].parentFolder.equals("target"))) 
		    		{
		    			if ((tgtDoc==null)||(CosineSimilarity.cosineSimilarity(srcDoc, docVectors[j])>simVal))
		    			{
		    				tgtDoc = docVectors[j].docName;
		    				simVal = CosineSimilarity.cosineSimilarity(srcDoc, docVectors[j]);
		    			}
		    		}
	    		}
	    		catch(NullPointerException e)
	    		{
	    			System.err.println("Null vectors");
	    		}
	    	}
	    	if ((tgtDoc != null) && simVal >=legato.getThreshold())
	    	{
	    	/*	Model srcModel = ModelManager.loadModel(legato.src.toString());
	    		Model model1 = ModelFactory.createDefaultModel();
	    		Resource rsrce1 = model1.createResource(legato.getSrcURIs().get(srcDoc.docName));
	    		String str1 = legato.getType(rsrce1, srcModel).toString();
	    		
	    		Model tgtModel = ModelManager.loadModel(legato.tgt.toString());
	    		Model model2 = ModelFactory.createDefaultModel();
	    		Resource rsrce2 = model2.createResource(legato.getTgtURIs().get(tgtDoc));
	    		String str2 = legato.getType(rsrce2, tgtModel).toString();
	    				
	    		if (str1.equals(str2)) */
	    		mapList1.add(legato.getSrcURIs().get(srcDoc.docName), legato.getTgtURIs().get(tgtDoc), simVal);
	    	}
	     }
	    
	    /*************
	     * Link repairing 
	     ************/
	    for(Map map1 : mapList1)
	    {
	    	boolean exist = false;
	    	for(Map map2 : mapList2)
		    {
		    	if (map1.getSourceURI().equals(map2.getSourceURI()))
		    	{
		    		if (map1.getTargetURI().equals(map2.getTargetURI())) System.out.println("OUI");
		    		else System.out.println("NON");
		    		exist = true;
		    		mapList.add(map2);
		    	}
		    }
	    	if (exist==false) mapList.add(map1);
	    }
	   for(Map map2 : mapList2)
	    {
	    	boolean exist = false;
	    	for(Map map1 : mapList1)
		    {
		    	if (map2.getSourceURI().equals(map1.getSourceURI()))
		    	{
		    		exist = true;
		    	}
		    }
	    	if (exist==false) 
	    	{
	    		System.out.println("+1");
	    		mapList.add(map2);
	    	}
	    }
	   
	   	/*********
		 ** Delete doublons of alignment
		 *********/
	   mapList = deleteDoublons(mapList);
	    /*********
		 ** Create and save the alignment file
		 *********/
	   suppressAll();
	   
	   Align.saveMappings(mapList);
	   
	   return mapList;
	}
	
	public static MapList deleteDoublons(MapList mapList)
	{
		System.out.println("Reperage des doublons !");
		MapList mapFinal = new MapList();
		boolean find=false;
		for(int i=0;i<mapList.size();i++)
		{
			//System.out.println("Ressource "+mapList.get(i).getSourceURI());
			if(!mapFinal.contains(mapList.get(i).getSourceURI()))
			{
				//System.out.println("\tPasse sans doublons");
				find=false;
				for(int j=0;j<mapFinal.size();j++)
				{
					if(mapFinal.get(j).getTargetURI()==mapList.get(i).getTargetURI())
					{
						System.out.println("Ressource "+mapList.get(i).getSourceURI()+" est un doublon !");
						if(mapFinal.get(j).getSimValue().doubleValue() < mapList.get(i).getSimValue().doubleValue())
						{
							mapFinal.replaceBysURI(mapFinal.get(j).getSourceURI(), mapList.get(i).getTargetURI(), mapList.get(i).getSimValue());
							find=true;
						}
					}
				}
				if(!find)
					mapFinal.add(mapList.get(i));
			}
			else
			{
				//System.out.println("\tEst un doublon !");
				System.out.println("Ressource "+mapList.get(i).getSourceURI()+" est en doublon !");
				if(mapFinal.getSim(mapList.get(i).getSourceURI()).doubleValue() < mapList.get(i).getSimValue().doubleValue())
					mapFinal.replaceBysURI(mapList.get(i).getSourceURI(), mapList.get(i).getTargetURI(), mapList.get(i).getSimValue());
			}
		}
		return mapFinal;
	}
	
	public static void suppressAll() throws IOException
	{
		LEGATO legato = LEGATO.getInstance();
		
		File dirr = new File(legato.getPath()+File.separator+"docs");
		delete(dirr);
		File dirind = new File(legato.getPath()+File.separator+"index");
		delete(dirind);
		File srcFile = new File(legato.getPath()+File.separator+"source.rdf");
		srcFile.deleteOnExit();
		File tgtFile = new File(legato.getPath()+File.separator+"target.rdf");
		tgtFile.deleteOnExit();
		File txtFile = new File(legato.getPath()+File.separator+"nom.txt");
		txtFile.deleteOnExit();
	}
	
	private static void delete(File file) throws IOException {
		
		if(file.exists())
		{
			for(File childFile : file.listFiles())
			{
				try
				{
					if(childFile.isDirectory())
						delete(childFile);
					else
					{
						if(!childFile.delete())
							java.nio.file.Files.deleteIfExists(childFile.toPath());
					}
				}
				catch(IOException e)
				{System.err.println("File "+childFile+" cannot be deleted !");}
			}
			file.delete();
		}
		else
		{
			System.err.println("File "+file+" don't exist !");
		}
	}
}
