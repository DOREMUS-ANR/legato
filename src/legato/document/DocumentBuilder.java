package legato.document;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import legato.LEGATO;
import legato.gui.GUI;
import legato.rdf.ModelManager;
import legato.utils.Stemmer;
import legato.utils.StopWords;

public class DocumentBuilder {
	
	/***********************************************
	 * Build documents for resources in an RDF model 
	 ***********************************************/
	public static HashMap<String, String> getDocuments (String pathFile, List<String> classResources, String dataset) throws Exception{
		LEGATO legato = LEGATO.getInstance();
		/****
		 * Load RDF model from the dataset 
		 ****/
		File f = new File(pathFile);
		Model modelSource = ModelManager.loadModel(pathFile);
		HashMap<String, String> documents = new HashMap<String, String>(); //1st String = the docName. 2d String = its content   
		/****
		 * Documents creation based on the CBD of each resource 
		 ****/
		for (Resource resource : CBDBuilder.getResources(modelSource, classResources)){
			Model model = ModelFactory.createDefaultModel();
			model = CBDBuilder.getCBD(modelSource, resource);
			Model modelCBD = ModelFactory.createDefaultModel();
			modelCBD.add(model);
			try {			
				String docName = generateUUID(resource.getURI());
				String id = ModelManager.getID(modelCBD, resource, "");
				/*****
				 * Preprocessing before documents creation
				 *****/
				String docContent = StopWords.clean(CBDBuilder.getLiterals(modelCBD));
			//	docContent = Stemmer.stem(docContent);
				if (!docContent.equals("")&&!docContent.equals(null)&&!docContent.equals("\n")&&!docContent.equals(" "))
				{
					if (dataset.equals("source"))
						legato.setSrcUri(docName, resource.getURI());
					else if (dataset.equals("target"))
						legato.setTgtUri(docName, resource.getURI());
					documents.put(docName, docContent); //Construct a document for each resource
					FileManager.create(docName, docContent, dataset);
				}
			} catch (IOException e) { e.printStackTrace(); }
		}
		return documents;
	}
	
	/************************************************************
	 * Build documents for resources based on selected properties 
	 ************************************************************/
	public static HashMap<String, String> getDocuments (String pathFile, List<String> classResources, List<String> selectedProp, String dataset) throws Exception{
		LEGATO legato = LEGATO.getInstance();
		/****
		 * Load RDF model from the dataset 
		 ****/
		File f = new File(pathFile);
		Model modelSource = ModelManager.loadModel(pathFile);
		HashMap<String, String> documents = new HashMap<String, String>(); //1st String = the docName. 2d String = its content   
		/****
		 * Documents creation based on the selected properties for each resource 
		 ****/
		for (Resource resource : CBDBuilder.getResources(modelSource, classResources)){
			Model model = ModelFactory.createDefaultModel();
			String sparqlQueryString = "SELECT DISTINCT ?p ?o {<"+resource +"> ?p ?o }";
			Query query = QueryFactory.create(sparqlQueryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, modelSource);
			ResultSet queryResults = qexec.execSelect();
			while (queryResults.hasNext()) {
				QuerySolution qs = queryResults.nextSolution();
				Resource prop = qs.getResource("?p");
				if (selectedProp.contains(prop.toString()))
				{
					model.createResource(resource).addProperty(model.createProperty(prop.toString()), qs.get("?o").toString());
				}
			}
			qexec.close();
			String docName = generateUUID(resource.getURI());
			/*****
			 * Preprocessing before documents creation
			 *****/
			String docContent = StopWords.clean(CBDBuilder.getLiterals(model));
		//	docContent = Stemmer.stem(docContent);
			if (!docContent.equals("")&&!docContent.equals(null)&&!docContent.equals("\n")&&!docContent.equals(" "))
			{
				if (dataset.equals("source"))
					legato.setSrcUri(docName, resource.getURI());
				else if (dataset.equals("target"))
					legato.setTgtUri(docName, resource.getURI());
				documents.put(docName, docContent); //Construct a document for each resource
				FileManager.create(docName, docContent, dataset);
			}
		}
		return documents;
	}
	
	public static HashMap<String, String> getDocuments (String docsPath) throws IOException
	{
		HashMap<String, String> docs = new HashMap<>();
		File path = new File(docsPath);
		for (File file: path.listFiles())
		{
			docs.put(file.getName().substring(0, file.getName().length()-4), FileManager.getContent(file));
		}
		return docs;
	}
	
	private static String generateUUID(String seed) {
	    try {
	      String hash = DatatypeConverter.printHexBinary(MessageDigest.getInstance("SHA-1").digest(seed.getBytes("UTF-8")));
	      UUID uuid = UUID.nameUUIDFromBytes(hash.getBytes());
	      return uuid.toString();
	    } catch (Exception e) {
	      System.err.println("[ConstructURI.java]" + e.getLocalizedMessage());
	      return "";
	    }
	  }
}
