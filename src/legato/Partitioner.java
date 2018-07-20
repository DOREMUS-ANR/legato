package legato;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Seq;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import legato.document.CBDBuilder;
import legato.match.Align;
import legato.match.Map;
import legato.match.MapList;
import legato.match.Matchifier;
import legato.rdf.ModelManager;
import legato.rdf.PropList;
import legato.utils.PropertyHandler;
import legato.utils.QueryManager;

public class Partitioner {
	
	public static LEGATO legato;
	
	//source path
	public static String path1="..\\bnf.ttl";
	//name of partitionned source files
	public static String p1="bnf";
	//target path
	public static String path2="..\\pp.ttl";
	//name of partitionned target files
	public static String p2="pp";
	//Property to partitionate
	public static String type="ecrm:E21_Person";
	//Store path
	public static String dir="..\\partitionner";
	
	//Ontology prefixes
	public static String sparqlPrefix = "prefix schema: <http://schema.org/> " + 
			"prefix owl:   <http://www.w3.org/2002/07/owl#> " + 
			"prefix ecrm:  <http://erlangen-crm.org/current/> " + 
			"prefix xsd:   <http://www.w3.org/2001/XMLSchema#> " + 
			"prefix efrbroo: <http://erlangen-crm.org/efrbroo/> " + 
			"prefix dcterms: <http://purl.org/dc/terms/> " + 
			"prefix mus:   <http://data.doremus.org/ontology#> " + 
			"prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> " + 
			"prefix time:  <http://www.w3.org/2006/time#> " + 
			"prefix prov:  <http://www.w3.org/ns/prov#> " + 
			"prefix foaf:  <http://xmlns.com/foaf/0.1/> ";
	
	//Query to extract subModels (<X> define each resources with the type "type" extracted in source and target model)
	public static String sparqlQuery = "DESCRIBE ?res "
			+ "WHERE { ?s a efrbroo:F28_Expression_Creation . "
			+ "?s ecrm:P9_consists_of ?p . "
			+ "?p ecrm:P14_carried_out_by <X> . "
			+ "?s efrbroo:R17_created ?res }"; //Here, ?res is a F22_Self_Contained_Expression
	
	//List of classes to align
	public static String[] classes = {"http://erlangen-crm.org/efrbroo/F22_Self-Contained_Expression"};
	
	//List of properties to align
	public static String[] properties = {"http://data.doremus.org/ontology#U16_has_catalogue_statement"};
	//Query for specialized manual alignment
	public static String sparqlManualQuery = "SELECT DISTINCT ?item1 ?item2 "
			+ "WHERE { ?item1 mus:U16_has_catalogue_statement ?cat1 . "
			+ "?item2 mus:U16_has_catalogue_statement ?cat2 . "
			+ "?cat1 rdfs:label ?lbl . "
			+ "?cat2 rdfs:label ?lbl . "
			+ "FILTER(?item1 != ?item2) }";
	
	//Automatic (true) or manual (false) mode
	static boolean automatic = false;
	
	public static void main(String[] args) throws Exception
	{
		
		/**********************************************************************
		 * Définition des paramètres
		 **********************************************************************/
		//Initialize LEGATO
		legato = LEGATO.getInstance();
		
		//List of classes to align
		List<String> classname = new ArrayList<String>();
		for(String elem : classes)
		{
			classname.add(elem);
		}
		//List of properties to align
		List<String> propertyname = new ArrayList<String>();
		for(String elem : properties)
		{
			propertyname.add(elem);
		}
		
		long beginTime = System.currentTimeMillis()/1000;
		legato.setBeginTime(beginTime);
		legato.setThreshold(0.2);
		
		String chain="";
		
		for(int i=0;i<classname.size();i++)
		{
			if(i>0) chain+="\n";
			chain+=classname.get(i);
		}
		legato.addClasses(chain);
		
		chain="";
		for(int i=0;i<propertyname.size();i++)
		{
			if(i>0) chain+="\n";
			chain+=propertyname.get(i);
		}
		legato.addProperties(chain);
		
		
		/**********************************************************************
		 * Construction des modèles et extractions des ressources comparatives
		 **********************************************************************/
		System.out.println("Construct");
		
		//source and target loading
		Model model1 = ModelManager.loadModel(path1);
		Model model2 = ModelManager.loadModel(path2);
		
		Model mergedModel = model1;
		mergedModel.add(model2);
		
		MapList manual = QueryManager.selectAlignQuery(mergedModel, sparqlPrefix, sparqlManualQuery, "?item1", "?item2");	
		//Suppression de doublons
		MapList finalMap = new MapList();
		MapList doublonMap = new MapList();
		for(int i=0; i<manual.size();i++)
		{
			if(model1.getResource(manual.get(i).getSourceURI()).hasURI(manual.get(i).getSourceURI()))
			{
				//manual.removeMap(manual.get(i).getTargetURI(), manual.get(i).getSourceURI());
				if(finalMap.contains(manual.get(i).getSourceURI())||doublonMap.contains(manual.get(i).getSourceURI())) //On a déja rencontré cet URI
				{
					if(finalMap.contains(manual.get(i).getSourceURI()))
					{
						doublonMap.add(manual.get(i).getSourceURI(), finalMap.getTargetURI(manual.get(i).getSourceURI()), finalMap.getSim(manual.get(i).getSourceURI()));
						finalMap.removeMap(manual.get(i).getSourceURI(), finalMap.getTargetURI(manual.get(i).getSourceURI()));
					}
					doublonMap.add(manual.get(i));
				}
				else
				{
					boolean doublon = false;
					for(Map map : finalMap)
					{
						if(map.getTargetURI()==manual.get(i).getTargetURI())
						{
							doublon = true;
							doublonMap.add(map);
							finalMap.removeMap(map.getSourceURI(), map.getTargetURI());
							
						}
						if(!doublon)
						{
							for(Map map2 : doublonMap)
								if(map2.getTargetURI()==manual.get(i).getTargetURI()) doublon = true;
							
						}
					}
					if(!doublon) finalMap.add(manual.get(i));
					else doublonMap.add(manual.get(i));
				}
			}
			else System.out.println(manual.get(i).getSourceURI());
		}
		legato.setPath(dir);
		//Align.saveMappings(manual);
		Align.saveMappings(finalMap);
		//Align.saveMappings(doublonMap);
		//Réecriture
		//writeModel(describe(model1),dir+"\\test.ttl","TTL");
		//rewrite(model2);
		
		//Extraction of all resources with the property <type>
		ArrayList<String> resources1 = extractResourcesWithType(type, model1);
		ArrayList<String> resources2 = extractResourcesWithType(type, model2);
		
		/**********************************************************************
		 * Fusion des ressources
		 **********************************************************************/
		System.out.println("Initial Resources source : "+resources1.size());
		System.out.println("Initial Resources target : "+resources2.size());
		System.out.println("Fusion");
		
		//Saving intersection of commons extracted resources in source and target
		ArrayList<String> finalResources = new ArrayList<String>();
		for(String r: resources1)
		{
			if(resources2.contains(r))
				finalResources.add(r);
		}
		System.out.println("Resources fusionned : "+finalResources.size());
		
		/*********************************************************************
		 * Partitionnement des fichiers à partir des ressources comparatives.
		 *********************************************************************/
		System.out.println("Partitionning");
		File repo = new File(dir);
		if(!repo.exists())
			repo.mkdirs();
		
		//Run Legato for each finalResources
		generateAllParts(finalResources, model1, model2, classname, propertyname);
		
		System.out.println("End !");
		
	}
	
	private static Model describe(Model model)
	{
		model.listStatements().toSet().forEach((stmt) -> {
			Resource sub = stmt.getSubject();
			Property prop = stmt.getPredicate();
			RDFNode object = stmt.getObject();
			
			if(!object.isLiteral())
			{
				System.out.println(object.getClass());
				model.remove(sub, prop, object);
				Model describer = CBDBuilder.getCBD(model, object.asResource());
				if(!describer.isEmpty())
				{
					describer = describe(describer);
					for(Statement state : describer.listStatements().toList())
					{
						Property tmpP = state.getPredicate();
						RDFNode tmpO = state.getObject();
						if(tmpO.isLiteral())
							model.createProperty(sub.getURI()).addProperty(tmpP, tmpO);
						
					}
				}
			}
			else {
				System.out.println(object.getClass());
			}
		});
		return model;
	}

	private static Model rewrite(Model model) {
		// TODO Auto-generated method stub
		Model finalModel = ModelFactory.createDefaultModel();
		
		finalModel.setNsPrefixes(model.getNsPrefixMap());
		//ModelManager.parseCBD(model);
		model.listStatements().toSet().forEach((stmt) -> {
			Resource sub = stmt.getSubject();
			Property prop = stmt.getPredicate();
			RDFNode object = stmt.getObject();
			
			//System.out.println("New statement");
			//System.out.println("\t subject : "+sub+"\n\t property : "+prop+"\n\t object : "+object);
			if(object.isLiteral())
			{
				if(finalModel.containsResource(sub)) finalModel.getResource(sub.getURI()).addProperty(prop, object);
				else finalModel.createResource(sub.getURI()).addProperty(prop, object);
				//finalModel.createStatement(sub, prop, object);
			}
			else if (prop.equals(RDF.type) && (legato.hasType(sub)))
			{
				if(finalModel.containsResource(sub)) finalModel.getResource(sub.getURI()).addProperty(RDF.type, object);
				else finalModel.createResource(sub.getURI()).addProperty(RDF.type, object);
				//finalModel.createStatement(sub, prop, object);
			}
			else
			{
				//Model describer = CBDBuilder.getCBD(model, object.asResource());
				if(!finalModel.containsResource(sub)) finalModel.createResource(sub.getURI());
				rewriteRec(model, finalModel, CBDBuilder.getCBD(model, object.asResource()), sub);
			}
		});
		return finalModel;
	}

	private static void rewriteRec(Model model, Model finalModel, Model describe, Resource sub) {
		// TODO Auto-generated method stub
		//ModelManager.parseCBD(model);
		describe.listStatements().toSet().forEach((stmt) -> {
			Resource subject = stmt.getSubject();
			Property prop = stmt.getPredicate();
			RDFNode object = stmt.getObject();
			
			//System.out.println("New statement");
			//System.out.println("\t subject : "+subject+"\n\t property : "+prop+"\n\t object : "+object);
			if(object.isLiteral())
			{
				if(finalModel.containsResource(sub)) finalModel.getResource(sub.getURI()).addProperty(prop, object);
				else finalModel.createResource(sub).addProperty(prop, object);
				//finalModel.createStatement(sub, prop, object);
			}
			else if (prop.equals(RDF.type) && (legato.hasType(sub)))
			{
				if(finalModel.containsResource(sub)) finalModel.getResource(sub.getURI()).addProperty(RDF.type, object);
				else finalModel.createResource(sub).addProperty(RDF.type, object);
				//finalModel.createStatement(sub, prop, object);
			}
			else
			{
				//Model describer = CBDBuilder.getCBD(model, object.asResource());
				if(!finalModel.containsResource(sub)) finalModel.createResource(sub.getURI());
				rewriteRec(model, finalModel, CBDBuilder.getCBD(model, object.asResource()), sub);
			}
		});
	}

	private static void generateAllParts(ArrayList<String> finalResources, Model model1, Model model2, List<String> classname, List<String> propertyname) throws Exception {
		
		Model cbdSuccessors1;
		Model cbdSuccessors2;
		
		legato.defaultMode = automatic;

		Model finalResults = ModelFactory.createDefaultModel();
		MapList res = new MapList();
		
		//Save of stopWords file
		File stopwords = new File(legato.getPath()+"\\stopWords.txt");
		
		//Partitionning on each finalResources
		for(int i=0;i<finalResources.size();i++)
		{
			File repo = new File(dir+"\\"+i);
			if(!repo.exists())
				repo.mkdirs();
			
			//Extract submodels by current resource
			cbdSuccessors1 = extractNewModel(finalResources.get(i),model1);
			cbdSuccessors2 = extractNewModel(finalResources.get(i),model2);
			
			if(!automatic)
			{
				File tmp = new File(repo.getAbsolutePath()+"\\manual");
				if(!tmp.exists()) tmp.mkdirs();
				
				//Copy of stopWords file for current repository
				legato.setPath(tmp.getAbsolutePath());
				File rewrite = new File(legato.getPath()+"\\stopWords.txt");
				copy(stopwords,rewrite);
				
				//save submodels
				writeModel(cbdSuccessors1,tmp.getAbsolutePath()+"\\"+p1+i+".ttl","TTL");
				writeModel(cbdSuccessors2,tmp.getAbsolutePath()+"\\"+p2+i+".ttl","TTL");
				
				System.out.println("Resource "+i+" of "+finalResources.size()+" (manual)");
				
				//Run Legato with partitionned source and target
				MapList map = runLegato(tmp.getAbsolutePath()+"\\"+p1+i+".ttl", tmp.getAbsolutePath()+"\\"+p2+i+".ttl", classname, propertyname, automatic);
				
				for(Map elem : map)
				{
					res.add(elem);
				}
				
				repo = new File(repo.getAbsolutePath()+"\\automatic");
				if(!repo.exists()) repo.mkdirs();
				
			}
			//Copy of stopWords file for current repository
			legato.setPath(repo.getAbsolutePath());
			File rewrite = new File(legato.getPath()+"\\stopWords.txt");
			copy(stopwords,rewrite);
			
			//save submodels
			writeModel(cbdSuccessors1,repo.getAbsolutePath()+"\\"+p1+i+".ttl","TTL");
			writeModel(cbdSuccessors2,repo.getAbsolutePath()+"\\"+p2+i+".ttl","TTL");
			
			System.out.println("Resource "+i+" of "+finalResources.size());
			
			//Run Legato with partitionned source and target
			MapList map = runLegato(repo.getAbsolutePath()+"\\"+p1+i+".ttl", repo.getAbsolutePath()+"\\"+p2+i+".ttl", classname, propertyname, true);

			
			
			//Concatenate results
			//finalResults.add(ModelManager.loadModel(legato.getPath()+"\\results.rdf"));
			for(Map elem : map)
			{
				res.add(elem);
			}
			
			//Saving final results
			//writeModel(finalResults, dir+"\\results.rdf", "RDF/XML");
			
			//try to suppress residuals files
			Matchifier.suppressAll();
		}
		legato.setPath(dir);
		Matchifier.deleteDoublons(res);
		Align.saveMappings(res);
	}
	
	public static void writeModel(Model model, String path, String lang) throws IOException
	{
		FileWriter out = new FileWriter(path);
		model.write(out, lang);
		out.close();
	}
	
	public static boolean copy(File source, File dest) { 
	    try (InputStream sourceFile = new java.io.FileInputStream(source);  
	            OutputStream destinationFile = new FileOutputStream(dest)) { 
	        // Lecture par segment de 0.5Mo  
	        byte buffer[] = new byte[512 * 1024]; 
	        int nbLecture; 
	        while ((nbLecture = sourceFile.read(buffer)) != -1){ 
	            destinationFile.write(buffer, 0, nbLecture); 
	        } 
	    } catch (IOException e){ 
	        e.printStackTrace(); 
	        return false; // Erreur 
	    } 
	    return true; // Résultat OK   
	}

	private static MapList runLegato(String src, String tgt, List<String> classname, List<String> properties, boolean automatic) throws Exception {
		
		
		
		legato.defaultMode = automatic ;
		
		/***
		 * RUN LEGATO
		 ***/
		
		PropList propList = new PropList(); 
		legato.setPropList(propList);
		try 
		{
			PropertyHandler.clean(src, tgt);
		} catch (IOException e1) { e1.printStackTrace(); }
	    
		MapList results = legato.buildDocuments();
		
	    return results;
	}

	private static Model extractNewModel(String resource, Model model) {
		Model res=null;
		
		System.out.println("Lancement de la requete sur "+resource+" !");
		
		String sparqlQueryString=sparqlPrefix;
		
		sparqlQueryString += sparqlQuery.replace("<X>", "<"+resource+">");
		
		/*
		sparqlQueryString+="DESCRIBE ?res "
				+ "WHERE { ?s a efrbroo:F28_Expression_Creation . "
				+ "?s ecrm:P9_consists_of ?p . "
				+ "?p ecrm:P14_carried_out_by <"+resource+"> . "
				//+ "?r rdfs:label \"Domenico Scarlatti\" . "
				+ "?s efrbroo:R17_created ?res }";
		*/
		QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(sparqlQueryString, model);
		res = qexec.execDescribe();
		qexec.close();
		
		return res;
	}

	private static ArrayList<String> extractResourcesWithType(String type, Model model) {
		
		ArrayList<String> resources = new ArrayList<String>();
		
		String sparqlQueryString=sparqlPrefix;
		
		sparqlQueryString += "SELECT DISTINCT ?res "
				+ "WHERE { ?res a "+type+" }";
		
		QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(sparqlQueryString, model);
		//Model cbdSuccessors = qexec.execDescribe();
		ResultSet res = qexec.execSelect();
		
		while(res.hasNext())
		{
			resources.add(res.next().getResource("?res").getURI());
		}
		
		qexec.close();
		
		return resources;
	}
}
