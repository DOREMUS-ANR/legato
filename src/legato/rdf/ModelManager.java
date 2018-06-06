package legato.rdf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntTools;
import org.apache.jena.ontology.OntTools.Path;
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
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.Filter;
import org.apache.jena.vocabulary.RDF;
import legato.LEGATO;
import legato.document.CBDBuilder;

/**
 * @author Manel Achichi
 **/

@SuppressWarnings("deprecation")
public class ModelManager {
	
	/***************************************
	 ***Get a value of a given property for a resource
	 ***************************************/
	public static String getID (Model model, Resource rsrc, String prop)
	{
		String id = null;
		StmtIterator iter = model.listStatements();
		while (iter.hasNext()){ 
			Statement stmt      = iter.nextStatement();  
			Property property = stmt.getPredicate();
			if (prop.equals(property)) 
				{
					RDFNode object = stmt.getObject();
					Path path = OntTools.findShortestPath(model, rsrc, object, Filter.any); // A filter which accepts statements whose predicate matches one of a collection of predicates held by the filter object.
					if (!(path==null))
					{	
						id = object.toString();
					}
				}
		}
		return id;
	}
	
	/***************************************
	 ***Get all properties from two models
	 ***************************************/
	public static List<Property> getAllPropFromModels(Model srcModel, Model tgtModel)
	{
		List<Property> propList = new ArrayList<Property>();
		StmtIterator iter1 = srcModel.listStatements();
		while (iter1.hasNext()){ 
			Statement stmt      = iter1.nextStatement();  
			Property prop = stmt.getPredicate();
			if (!propList.contains(prop)) propList.add(prop);
		}
		StmtIterator iter2 = tgtModel.listStatements();
		while (iter2.hasNext()){ 
			Statement stmt      = iter2.nextStatement();  
			Property prop = stmt.getPredicate();
			if (!propList.contains(prop)) propList.add(prop);
		}
		return propList;
	}
	
	/***************************************
	 ***Get all classes from two models
	 ***************************************/
	public static List<Resource> getAllClassesFromModels(Model srcModel, Model tgtModel)
	{
		List<Resource> classList = new ArrayList<Resource>();
		StmtIterator iter1 = srcModel.listStatements();
		while (iter1.hasNext()){ 
			Statement stmt      = iter1.nextStatement();  
			Property prop = stmt.getPredicate();
			RDFNode   object    = stmt.getObject();
			if (prop.equals(RDF.type))
				if (!classList.contains(object)) classList.add(object.asResource());
		}
		StmtIterator iter2 = tgtModel.listStatements();
		while (iter2.hasNext()){ 
			Statement stmt      = iter2.nextStatement();  
			Property prop = stmt.getPredicate();
			RDFNode   object    = stmt.getObject();
			if (prop.equals(RDF.type))
				if (!classList.contains(object)) classList.add(object.asResource());
		}
		return classList;
	}
	
	/***************************************
	 ***Load the RDF model from an RDF file
	 ***************************************/
	public static Model loadModel (String inputFile){	
		Model model = ModelFactory.createDefaultModel();
		try{
			InputStream in =new FileInputStream(inputFile);
			String ext = FilenameUtils.getExtension(inputFile);
			if (ext.equals("nt"))
			{

				Reader r = new InputStreamReader(in, Charset.forName("UTF-8"));
				model.read(r, null,"N-TRIPLES");
			}
			else if (ext.equals("ttl"))
			{
				model.read(in, null ,"TTL");
			}
			else if (ext.equals("rdf"))
			{
				model.read(in, null,"RDF/XML");
			}
			else if (ext.equals("owl"))
			{
				OntModel ontologyModel =  ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
				ontologyModel.read(in, "RDF/XML-ABBREV");
				model = ontologyModel.getBaseModel();
			}
		} catch(Exception e){ e.printStackTrace(); }
		return model;
	}
	
	/*****************************************************
	 * Parse a CBD to get the prefLabels from vocabularies 
	 *****************************************************/
	public static Model parseCBD (Model model) throws IOException{
		Model m = ModelFactory.createDefaultModel();
		StmtIterator iter = model.listStatements();
		while (iter.hasNext()){ 
			Statement stmt      = iter.nextStatement();  
			Resource subject = stmt.getSubject();
			Property prop = stmt.getPredicate();
			RDFNode   object    = stmt.getObject();      
//			if (!object.isLiteral()){ //
//				Resource uriConcept = (Resource) object;
//				if (uriConcept.toString().contains("iaml/genre")){
//					for (String label : ConceptFinder.getLabel(ConceptFinder.genreVocabulary, uriConcept.toString())){
//						m.add(subject, prop, label);
//					}
//				}
//				else if (uriConcept.toString().contains("diabolo/genre")){
//					for (String label : ConceptFinder.getLabel(ConceptFinder.genreVocabularyDiabolo, uriConcept.toString())){
//						m.add(subject, prop, label);
//					}
//				}
//			} //
		}
		return m;
	}
	
	/**********
	 ** Place all Literals (in resources CBD) to a distance = 1 
	 **********/
		public static Model rewrite (Model model, boolean ok) throws IOException
	{
		LEGATO legato = LEGATO.getInstance();
		Model finalModel = ModelFactory.createDefaultModel();  
		
		model.listSubjects().toSet().forEach((resource) -> { //Parse all resources 
		if (legato.hasType(resource) == true) //If the current resource belongs to a given "type"
	    {
				Model m = CBDBuilder.getCBD(model, resource);
				if (ok==true)
				{
				m.add(CBDBuilder.getCBDDirectPredecessors(model, resource));
			    m.add(CBDBuilder.getCBDDirectSuccessors(model, resource));
				}
				
				try {
					m.add(ModelManager.parseCBD(m));
				} catch (IOException e1) {e1.printStackTrace();}
				
				m.listStatements().toSet().forEach((stmt) -> { 
					Resource sub = stmt.getSubject();
					Property prop = stmt.getPredicate();
					RDFNode object = stmt.getObject();
					if (object.isLiteral()==true) //Parse all literals
					{
						Path path = OntTools.findShortestPath(m, resource, object, Filter.any); // A filter which accepts statements whose predicate matches one of a collection of predicates held by the filter object.
						if (!(path==null))
						{	
							List<Property> properties = getPropFromPath(path); //Get the successive properties from the path
							if (legato.getPropList().existProperty(properties)==false)
							{
								int indice = legato.getPropList().size();
								finalModel.createResource(resource.toString()).addProperty(finalModel.createProperty("http://model.org/property"+indice), object);
								try {
									legato.addToPropList("http://model.org/property"+indice, properties);
								} catch (IOException e) {
								}
							}
							else
							{
								finalModel.createResource(resource.toString()).addProperty(finalModel.createProperty(legato.getPropList().getPropertyName(properties)), object);
							}
						}
						else
						{
							String sparqlQueryString = "select ?predec where {"+ 
									   "?predec ?prop <"+ resource +">."+
									   "}";
							Query query = QueryFactory.create(sparqlQueryString);
							QueryExecution qexec = QueryExecutionFactory.create(query, model);
							ResultSet queryResults = qexec.execSelect();
							while (queryResults.hasNext()) {
								QuerySolution qs = queryResults.nextSolution();
								final PathManager.Path path2 = PathManager.findShortestPath(model, qs.getResource("?predec"), object, prop); 
								if (!(path2==null))
								{
									List<Property> properties = getPropFromPath(path2); //Get the successive properties from the path
									if (legato.getPropList().existProperty(properties)==false)
									{
										int indice = legato.getPropList().size();
										finalModel.createResource(resource.toString()).addProperty(finalModel.createProperty("http://model.org/property"+indice), object);
										try {
											legato.addToPropList("http://model.org/property"+indice, properties);
										} catch (IOException e) {
										}
									}
									else
									{
										finalModel.createResource(resource.toString()).addProperty(finalModel.createProperty(legato.getPropList().getPropertyName(properties)), object);
									}
								}
							}
							qexec.close();
						}
					}
					else if (prop.equals(RDF.type) && (legato.hasType(sub)))
					{
						finalModel.createResource(resource.toString()).addProperty(RDF.type, object);
					}
//					else
//						finalModel.createResource(resource.toString()).addProperty(prop, object);
				});
			} 
		});
		return finalModel;	
	}
	
	/**********
	 ** Get all resources having the value "object" in their path. 
	 **********/
	public static List<Resource> getObjResources (Model model, RDFNode object)
	{
		LEGATO legato = LEGATO.getInstance();
		List<Resource> resources = new ArrayList<Resource>();
		for (String className : legato.getClassResources())
		{	
			String sparqlQueryString = "prefix : <urn:ex:>"+
				   				   "SELECT DISTINCT ?resource WHERE {"+
				   				   "{?resource a <"+ className +"> ;"+
				   				   "(:|!:)* \""+object.toString()+"\"}"+
				   				   "}";
			Query query = QueryFactory.create(sparqlQueryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			ResultSet queryResults = qexec.execSelect();
			while (queryResults.hasNext()) {
				QuerySolution qs = queryResults.nextSolution();
				resources.add(qs.getResource("?resource"));
			}
			qexec.close();
		}
		return resources;
	}
	
	/********
	 * List all the properties of a path
	 ********/
	public static List<Property> getPropFromPath(PathManager.Path propPath)
	{
		List<Property> path = new ArrayList<Property>();
		Iterator iterPath = propPath.iterator();
		while (iterPath.hasNext())
		{
			Statement stmtPath = (Statement) iterPath.next();
			path.add((Property) stmtPath.getPredicate());
		}
		return path;
	}
	
	/********
	 * Filter triples from an RDF model with specific properties
	 ********/
	public static Model getFilteredTriples (Model model, List<Property> listProperties)
	{
		Model newModel = ModelFactory.createDefaultModel();
		StmtIterator iter = model.listStatements();
		while (iter.hasNext()){
			Statement stmt      = iter.nextStatement();
			Property  property = stmt.getPredicate();
			if (listProperties.contains(property)){
				newModel.add(stmt);
			}
		}
		return model;
	}
	
	/********
	 * Get all triples of a resource from an RDF model
	 ********/
/*	public static Model getAllTriples (Model model, Resource resource)
	{
		Model newModel = ModelFactory.createDefaultModel();
		StmtIterator iter = model.listStatements();
		while (iter.hasNext()){
			Statement stmt      = iter.nextStatement();
			Resource subject = stmt.getSubject();
			if (subject.toString().equals(resource.toString())){
				newModel.add(stmt);
			}
		}
		return model;
	} */
	
	/********
	 * List all the properties of a path
	 ********/
	public static List<Property> getPropFromPath(Path propPath)
	{
		List<Property> path = new ArrayList<Property>();
		Iterator iterPath = propPath.iterator();
		while (iterPath.hasNext())
		{
			Statement stmtPath = (Statement) iterPath.next();
			path.add((Property) stmtPath.getPredicate());
		}
		return path;
	}

}
