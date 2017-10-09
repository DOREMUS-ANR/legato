package legato;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import legato.document.DocumentBuilder;
import legato.gui.GUI;
import legato.indexer.Indexer;
import legato.match.Matchifier;
import legato.rdf.PropList;
import legato.vocabularies.ConceptFinder;

public class LEGATO {

	private static LEGATO legato = new LEGATO();
	private List<String> classResources;
	private HashMap<String, String> srcDocs;
	private HashMap<String, String> srcURIs;
	private HashMap<String, String> tgtDocs;
	private HashMap<String, String> tgtURIs; 
	private double threshold = 0.12;
	private PropList propList; //List of new properties with their path (path = set of existing properties)
	private File dir;
	public String DIR_TO_INDEX;
	public String INDEX_DIR;
	public String FIELD_CONTENT;
	public String FILE_NAME;
	public String ABS_PATH;
	public String PARENT_FOLDER;
	public File src, tgt, refAlign;
	
	private LEGATO ()
	{
		try
		{
			dir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			dir = dir.getParentFile();
			classResources = new ArrayList<String>();
		 //   classResources.add("http://www.tomtom.com/ontologies/traces#Trace");	
		//	classResources.add("http://data.doremus.org/ontology/Self_Contained_Expression");
			classResources.add("http://erlangen-crm.org/efrbroo/F22_Self-Contained_Expression");
		//	classResources.add("http://www.bbc.co.uk/ontologies/creativework/Programme");
		//	classResources.add("http://www.bbc.co.uk/ontologies/creativework/NewsItem");
		//	classResources.add("http://www.bbc.co.uk/ontologies/creativework/BlogPost");
		    srcURIs = new HashMap<String, String>();
		    tgtURIs = new HashMap<String, String>();
		}
		catch(Exception e) {}
	}
	
	public static LEGATO getInstance()
	{
		return legato;
	}
	
	public List<String> getClassResources()
	{
		return classResources;
	}
	
	public double getThreshold()
    {
    	return threshold;
    }
	
	public boolean hasType (Resource resource)
	{
		boolean is = false;
		for (int i = 0; i < legato.getClassResources().size(); i++) {
			Model model = ModelFactory.createDefaultModel();
			Resource classResource = model.createResource(legato.getClassResources().get(i));
			if (resource.hasProperty(RDF.type, classResource))
			{
				is = true;
			}
		}
		return is;
	}
	
	public RDFNode getType (Resource resource, Model model)
	{
		return model.getResource(resource.toString()).getProperty(RDF.type).getObject();
	}
	
	public PropList getPropList()
	{
		return propList;
	}
	
	public void setPropList(PropList propList)
	{
		this.propList = propList;
	}
	
	public void addToPropList(String newProp, List<Property> path) throws IOException
	{
		this.propList.add(newProp, path);
	}
	
	public void setSource (File src)
	{
		this.src = src;
	}
	
	public void setTarget (File tgt)
	{
		this.tgt = tgt;
	}
	
	public void setRefAlign (File refAlign)
	{
		this.refAlign = refAlign;
	}
	
	public File getRefAlign()
	{
		return refAlign;
	}
	
	public void buildDocuments() throws Exception
	{
		DocumentBuilder db = new DocumentBuilder();
		
	//	GUI.descriptionArea.append("\nResources type : "+classResources);
		
		long srcTime = System.currentTimeMillis()/1000;
		srcDocs = db.getDocuments(src.toString(), classResources, "source");
		srcTime = System.currentTimeMillis()/1000 - srcTime;
		GUI.descriptionArea.append("\nSource dataset : "+srcDocs.size() +" resources");
		GUI.descriptionArea.append("\nSource documents created in " + srcTime + " seconds");
		
		long tgtTime = System.currentTimeMillis()/1000;
		tgtDocs = db.getDocuments(tgt.toString(), classResources, "target");
		tgtTime = System.currentTimeMillis()/1000 - tgtTime;
		GUI.descriptionArea.append("\nTarget dataset : "+tgtDocs.size() +" resources");
		GUI.descriptionArea.append("\nTarget documents created in " + tgtTime + " seconds");
		
		indexConfig();
	}
	
	public HashMap<String, String> getSRCdocs()
	{
		return srcDocs;
	}
	
	public void setSrcUri(String docName, String srcUri)
	{
		srcURIs.put(docName, srcUri);
	}
	
	public HashMap<String, String> getSrcURIs()
	{
		return srcURIs;
	}
	
	public HashMap<String, String> getTGTdocs()
	{
		return tgtDocs;
	}
	
	public void setTgtUri (String docName, String tgtUri)
	{
		tgtURIs.put(docName, tgtUri);
	}
	
	public HashMap<String, String> getTgtURIs()
	{
		return tgtURIs;
	}
	
	public void indexConfig() throws Exception
    {
		DIR_TO_INDEX = getPath()+"store"+File.separator+"docs"; //"C:/Users/Manel/Music/matching/DS1/Docs/";
		File dir = new File(getPath()+"store"+File.separator+"index");
		if (!dir.exists()) dir.mkdirs();
		INDEX_DIR = dir.getAbsolutePath();
	    FIELD_CONTENT = "contents";
	    FILE_NAME="filename";
	    ABS_PATH="path";
	    PARENT_FOLDER="parent";
	    
	    Indexer index = new Indexer();
	    index.indexFiles();
	    Matchifier matchifier = new Matchifier();
	    matchifier.match();
    }
	
	public String getPath()
	{
		return dir.getAbsolutePath()+File.separator;
	}
	
	public void openGUI()
	{
		GUI window = new GUI();
		window.frame.setVisible(true);
	}
	
}
