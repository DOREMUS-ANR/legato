package legato.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import legato.LEGATO;


/**
 * @author Manel Achichi
 **/

public class CBDBuilder {
	
	/*****************************************************
	 * Get all resource of a given class from an RDF model 
	 *****************************************************/
	public static List<Resource> getResources(Model model, List<String> classnames) {
		List<Resource> results = new ArrayList<Resource>();
		for (String classname : classnames)
		{
			String sparqlQueryString = "SELECT DISTINCT ?s { ?s a <"+classname+"> }";
			Query query = QueryFactory.create(sparqlQueryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			ResultSet queryResults = qexec.execSelect();
			while (queryResults.hasNext()) {
				QuerySolution qs = queryResults.nextSolution();
				results.add(qs.getResource("?s"));
			}
			qexec.close();
		}
		return results;
	}

	/*********************************************
	 * Get the CBD of a resource from an RDF model 
	 *********************************************/
	public static Model getCBD (Model model, Resource resource) {
		String sparqlQueryString = "DESCRIBE <" + resource + ">";
		QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(sparqlQueryString, model);
		Model cbd = qexec.execDescribe();
		qexec.close();
		return cbd;
	}
	
	/*********************************************
	 * Get the CBD of all resources from an RDF model 
	 *********************************************/
	public static Model getAllCBDs (Model model) { 
		LEGATO legato = LEGATO.getInstance();
		List<Resource> resources = new ArrayList<Resource>();
		resources = getResources(model, legato.getClassResources());
		Model allCBDs = ModelFactory.createDefaultModel();
		Iterator iter = resources.iterator();
		while (iter.hasNext())
		{
			Resource resource = (Resource) iter.next();
			String sparqlQueryString = "DESCRIBE <" + resource + ">";
			QueryFactory.create(sparqlQueryString);
			QueryExecution qexec = QueryExecutionFactory.create(sparqlQueryString, model);
			Model cbd = qexec.execDescribe();
			allCBDs.add(cbd);
			qexec.close();
		}
		return allCBDs;
	}
	
	
	/**************************************************
	 * Get the CBD of direct predecessors of a resource 
	 ***************************************************/
	public static Model getCBDDirectPredecessors(Model model, Resource resource){
		String sparqlQueryString = "DESCRIBE ?predecessor WHERE { ?predecessor ?relation <" + resource + ">.}";
		QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(sparqlQueryString, model);
		Model cbdPredecessors = qexec.execDescribe();
		qexec.close();
		return cbdPredecessors;
	}
	
	/**************************************************
	 * Get the CBD of direct successors of a resource 
	 ***************************************************/
	public static Model getCBDDirectSuccessors(Model model, Resource resource){
		String sparqlQueryString = "DESCRIBE ?successor WHERE { <" + resource + "> ?relation ?successor.}";
		QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(sparqlQueryString, model);
		Model cbdSuccessors = qexec.execDescribe();
		qexec.close();
		return cbdSuccessors;
	}
	
	/****************************************
	 * Get all the literals from an RDF model 
	 ****************************************/
	public static String getLiterals(Model model) {
		StringBuffer sb = new StringBuffer();
		Iterator iter = model.listStatements();
		while(iter.hasNext()){
			Statement stmt = (Statement) iter.next();
			RDFNode object = stmt.getObject();
			if (object.isLiteral())
				sb.append(stmt.getLiteral().getString()+" ");
			//else sb.append(object.toString());
		}
		return sb.toString();
	}

}