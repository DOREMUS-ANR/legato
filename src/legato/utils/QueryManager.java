package legato.utils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import legato.match.MapList;

public class QueryManager {

	public static List<Resource> selectQuery(Model model, String prefixes, String sparqlQuery, String sub)
	{
		List<Resource> results = new ArrayList<Resource>();
		String sparqlQueryString = prefixes+sparqlQuery;
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet queryResults = qexec.execSelect();
		while(queryResults.hasNext())
		{
			QuerySolution qs = queryResults.nextSolution();
			results.add(qs.getResource(sub));
		}
		qexec.close();
		
		return results;
	}
	
	public static MapList selectAlignQuery(Model model, String prefixes, String sparqlQuery, String sub1, String sub2) throws URISyntaxException
	{
		MapList results = new MapList();
		String sparqlQueryString = prefixes+sparqlQuery;
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet queryResults = qexec.execSelect();
		while(queryResults.hasNext())
		{
			QuerySolution qs = queryResults.nextSolution();
			results.add(qs.getResource(sub1).toString(), qs.getResource(sub2).toString(), 1.0);
		}
		qexec.close();
		
		return results;
	}
	
	public static Model describeQuery(Model model, String prefixes, String sparqlQuery)
	{
		String sparqlQueryString = prefixes+sparqlQuery;
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		Model queryResults = qexec.execDescribe();
		qexec.close();
		
		return queryResults;
	}
}
