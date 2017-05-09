package legato.document;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import legato.gui.GUI;
import legato.rdf.ModelManager;
import legato.utils.StopWords;

public class DocumentBuilder {
	
	/***********************************************
	 * Build documents for resources in an RDF model 
	 ***********************************************/
	public static HashMap<String, String> getDocuments (String pathFile, List<String> classResources, String dataset){
		/****
		 * Load RDF model from the dataset 
		 ****/
		Model modelSource = ModelManager.loadModel(pathFile);
		HashMap<String, String> documents = new HashMap<String, String>(); //1st String = the docName. 2d String = its content   
		/****
		 * Documents creation based on the CBD of each resource 
		 ****/
		CBDBuilder.getResources(modelSource, classResources).forEach((resource)->{
			Model model = ModelFactory.createDefaultModel();
			model = CBDBuilder.getCBD(modelSource, resource);
			Model modelCBD = ModelFactory.createDefaultModel();
			modelCBD.add(model);
			/********
			 ** Options = CBD of direct Predecessors and/or Successors
			 ********/
		//	modelCBD.add(CBDBuilder.getCBDDirectPredecessors(modelSource, resource));
		//	modelCBD.add(CBDBuilder.getCBDDirectSuccessors(modelSource, resource));
			try {
			//	modelCBD.add(ModelManager.parseCBD(model));
				String docName= resource.toString().substring(resource.toString().lastIndexOf("/")+1, resource.toString().length()); //Last fragment of an URI
				/*****
				 * Preprocessing before documents creation
				 *****/
				String docContent = StopWords.clean(CBDBuilder.getLiterals(modelCBD));
				if (!docContent.equals("")&&!docContent.equals(null)&&!docContent.equals("\n")&&!docContent.equals(" "))
				{
					documents.put(docName, docContent); //Construct a document for each resource
					FileManager.create(docName, docContent, dataset);
				}
			} catch (IOException e) { e.printStackTrace(); }
		});
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
}
