package legato.document;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import legato.LEGATO;
import legato.gui.GUI;
import legato.rdf.ModelManager;
import legato.utils.StopWords;

public class DocumentBuilder {
	
	/***********************************************
	 * Build documents for resources in an RDF model 
	 ***********************************************/
	public static HashMap<String, String> getDocuments (String pathFile, List<String> classResources, String dataset){
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
				//String docName= resource.toString().substring(resource.toString().lastIndexOf("/")+1, resource.toString().length()); //Last fragment of an URI
				String docName = generateUUID(resource.getURI());
				/*****
				 * Preprocessing before documents creation
				 *****/
				String docContent = StopWords.clean(CBDBuilder.getLiterals(modelCBD));
				
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
