package legato.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

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
		
		srcModel = ModelManager.rewrite(srcModel);
	    tgtModel = ModelManager.rewrite(tgtModel);
	    
	    Model mergedModel = ModelFactory.createDefaultModel();
	    mergedModel.add(srcModel);
		mergedModel.add(tgtModel);
		
		List<Resource> properties = getDistinctProperties(mergedModel);
		
	//	System.out.println(legato.getPropList());
		
		HashMap<String,String> propScoreList = new HashMap<String,String>();
		
		properties.forEach((property) -> {
			propScoreList.put(property.toString(), String.valueOf(getScore(property, mergedModel)));
		});
		
		ValueComparator<String> comp = new ValueComparator<String>(propScoreList);
		TreeMap<String,String> mapTriee = new TreeMap<String,String>(comp);
	    mapTriee.putAll(propScoreList);
	    
	  //  String heterProp = mapTriee.firstEntry().getKey();
		Iterator iter = mapTriee.keySet().iterator();
		List<String> propList = new ArrayList<String>();
		int nb = 1;
		while (iter.hasNext())		
		{
			String prop = (String) iter.next();
			if (nb==1) {
			//	System.out.println(prop);
				propList.add(prop);
			}
			nb = nb+1;
		}
	  //  System.out.println(heterProp);
		
		
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
