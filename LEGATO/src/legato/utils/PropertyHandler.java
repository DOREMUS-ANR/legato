package legato.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import ca.pfv.spmf.algorithms.clustering.dbscan.AlgoDBSCAN;
import ca.pfv.spmf.patterns.cluster.Cluster;
import ca.pfv.spmf.patterns.cluster.DoubleArray;
import legato.LEGATO;
import legato.document.FileManager;
import legato.rdf.ModelManager;

/**
 * @author Manel Achichi
 **/

public class PropertyHandler {

	/*******
	 * This class deletes problematic properties
	 *******/
	
	public static void clean(String srcPath, String tgtPath) throws IOException {
	
		LEGATO legato = LEGATO.getInstance();
		
		Model srcModel = ModelManager.loadModel(srcPath);
		Model tgtModel = ModelManager.loadModel(tgtPath);
		
		Model s = ModelFactory.createDefaultModel();
		Model t = ModelFactory.createDefaultModel();
		
		s = ModelManager.rewrite(srcModel, false);
	    t = ModelManager.rewrite(tgtModel, false);
	    
	    Model mergedModel = ModelFactory.createDefaultModel();
	    mergedModel.add(s);
		mergedModel.add(t);
		
		List<Resource> properties = getDistinctProperties(mergedModel);
		
		System.out.println(legato.getPropList());
		
		HashMap<String,String> propScoreList = new HashMap<String,String>();
		
		properties.forEach((property) -> {
			propScoreList.put(property.toString(), String.valueOf(getScore(property, mergedModel)));
		});
		
		ValueComparator<String> comp = new ValueComparator<String>(propScoreList);
		TreeMap<String,String> mapTriee = new TreeMap<String,String>(comp);
	    mapTriee.putAll(propScoreList);
	    
	    System.out.println(mapTriee);
	    
	    StringBuilder sb = new StringBuilder();
	    for (int i=0; i<mapTriee.entrySet().size(); i++)
	    {
	    	sb.append(Double.valueOf((String) mapTriee.values().toArray()[i])+ "\n");
	    };
	    FileManager.create("nom", sb.toString().trim());
	    int minPts=1; 
	    double epsilon = 5d; 
	    AlgoDBSCAN algo = new AlgoDBSCAN();  
	    List<Cluster> clusters = algo.runAlgorithm(legato.getPath()+"store"+File.separator+"nom.txt", minPts, epsilon, "\n"); 
	    algo.printStatistics();
	    
	    double highMean =0;
	    double[] heterCluster = null;
	    for(Cluster cluster : clusters) { 
	    	double[] arr =new double[cluster.getVectors().size()];
	    	int i=0;
	    	for(DoubleArray dataPoint : cluster.getVectors()) { 
	    		arr[i++] = dataPoint.data[0];
	    	} 
	    	A a =new A(arr);
	    	if (highMean<a.getMean()) {
	    		highMean=a.getMean();
	    		heterCluster = arr;
	    	};
	    } 
	    List<String> propList = new ArrayList<String>();
	    Iterator it = mapTriee.entrySet().iterator();
	    while(it.hasNext()){
	    	Entry<String,String> entry = (Entry<String,String>) it.next();
	    	boolean f = false;
	    	for (int i = 0; i < heterCluster.length; i++) {
				if (String.valueOf(heterCluster[i]).equals(entry.getValue()))
					propList.add(entry.getKey());;
			}
	    }
	    
	    System.out.println(propList); 
		
		srcModel = ModelManager.rewrite(srcModel, true);
		System.out.println("source");
		tgtModel = ModelManager.rewrite(tgtModel, true);
		
		Model srcFinalModel = ModelFactory.createDefaultModel();
		srcModel.listStatements().toSet().forEach((stmt) -> {
			Property property = stmt.getPredicate();
			if (!(propList.contains(property.toString()))){
				srcFinalModel.add(stmt); 
			}
		});
		
		Model tgtFinalModel = ModelFactory.createDefaultModel();
		tgtModel.listStatements().toSet().forEach((stmt) -> {
			Property property = stmt.getPredicate();
			if (!propList.contains(property.toString())){
				tgtFinalModel.add(stmt);
			}
		}); 
		
	//	FileManager.createRDFile(new File(legato.getPath()+"store"), "source", srcFinalModel, "TTL");
	//	FileManager.createRDFile(new File(legato.getPath()+"store"), "target", tgtFinalModel, "TTL"); 
		
		legato.setSource(FileManager.getCreatedRDFile("source", srcFinalModel));
		legato.setTarget(FileManager.getCreatedRDFile("target", tgtFinalModel));
		
		System.out.println("finish");
	}
	
	public static double getScore (Resource property, Model model)
	{
		/**************
		 * Get distinct resources having the property "property"
		 **************/
		List<Resource> resources = new ArrayList<Resource>();
		String sparqlQueryString = 
				   				   "SELECT DISTINCT ?resource WHERE {"+
				   				   "?resource <"+ property +"> ?object"+
				   				   "}";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet queryResults = qexec.execSelect();
		while (queryResults.hasNext()) 
		{
			QuerySolution qs = queryResults.nextSolution();
			resources.add(qs.getResource("?resource"));
		}
		qexec.close();
			
		/**************
		 * Get all DISTINCT objects of the property "property"
		 **************/
		List<RDFNode> disObj = new ArrayList<RDFNode>();
		String sparqlQueryString2 = 
				   "SELECT DISTINCT ?object WHERE {"+
				   "?resource <"+ property +"> ?object"+
				   "}";

		Query query2 = QueryFactory.create(sparqlQueryString2);
		QueryExecution qexec2 = QueryExecutionFactory.create(query2, model);

		ResultSet queryResults2 = qexec2.execSelect();
		while (queryResults2.hasNext()) 
		{
			QuerySolution qs = queryResults2.nextSolution();
			disObj.add(qs.get("?object"));
		}
		qexec2.close();
		
		/**************
		 * Get all objects of the property "property"
		 **************/
		List<RDFNode> obj = new ArrayList<RDFNode>();	
		String sparqlQueryString3 = 
				   "SELECT ?object WHERE {"+
				   "?resource <"+ property +"> ?object"+
				   "}";

		Query query3 = QueryFactory.create(sparqlQueryString3);
		QueryExecution qexec3 = QueryExecutionFactory.create(query3, model);
		ResultSet queryResults3 = qexec3.execSelect();
		while (queryResults3.hasNext()) 
		{
			QuerySolution qs = queryResults3.nextSolution();
			obj.add(qs.get("?object"));
		}
		qexec3.close();
		double redondance = (obj.size()-disObj.size());
		if (redondance == 0) redondance=1;
		return (resources.size()/redondance);
	}
	
	/*****************************
	 * Get all distinct properties
	 *****************************/
	public static List<Resource> getDistinctProperties(Model model)
	{
		List<Resource> properties = new ArrayList<Resource>();
		String sparqlQueryString = 
				   "SELECT DISTINCT ?prop WHERE {"+
				   "?resource ?prop ?object"+
				   "}";

		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet queryResults = qexec.execSelect();
		while (queryResults.hasNext()) {
			QuerySolution qs = queryResults.nextSolution();
			Resource prop = qs.getResource("?prop");
			properties.add(prop);
		}
		qexec.close();
		return properties;
	}
}
