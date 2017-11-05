package legato;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

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

public class LEGATO {

	private static LEGATO legato = new LEGATO();
	private List<String> classResources;
	private List<String> selectedProperties;
	private HashMap<String, String> srcDocs;
	private HashMap<String, String> srcURIs;
	private HashMap<String, String> tgtDocs;
	private HashMap<String, String> tgtURIs; 
	private double threshold = 0.12;
	private long beginTime;
	private PropList propList; //List of new properties with their path (path = set of existing properties)
	public static Properties properties;
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
			loadProperties();
			dir = new File(properties.getProperty("storePath"));
		//	dir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		//	dir = dir.getParentFile();
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
	
	public void addClasses (String text)
	{
		classResources = new ArrayList<String>();
		if(!text.contains("\n")) this.classResources.add(text.trim());
		
		else for (String type : text.split("\n")) 
		{
			this.classResources.add(type.trim());
		}
	}
	
	public void addProperties (String text)
	{
		selectedProperties = new ArrayList<String>();
		if(!text.contains("\n")) this.selectedProperties.add(text);
		
		else for (String property : text.split("\n")) 
		{
			this.selectedProperties.add(property);
		}
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
			
		if (GUI.getMatchValue().equals("automatic"))
		{
		srcDocs = db.getDocuments(src.toString(), classResources, "source");
		GUI.resultsArea.append("\nSource dataset : "+srcDocs.size() +" resources");
		
		tgtDocs = db.getDocuments(tgt.toString(), classResources, "target"); 
		GUI.resultsArea.append("\nTarget dataset : "+tgtDocs.size() +" resources");
		}
		
		else if (GUI.getMatchValue().equals("manual"))
		{
			srcDocs = db.getDocuments(src.toString(), classResources, selectedProperties, "source");
			GUI.resultsArea.append("\nSource dataset : "+srcDocs.size() +" resources");
			
			tgtDocs = db.getDocuments(tgt.toString(), classResources, selectedProperties, "target"); 
			GUI.resultsArea.append("\nTarget dataset : "+tgtDocs.size() +" resources");
		}
		indexConfig();
	}
	
	public void setBeginTime(long beginTime){
		this.beginTime= beginTime;
	}
	
	public long getBeginTime(){
		return this.beginTime;
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
		DIR_TO_INDEX = getPath()+"docs"; //"C:/Users/Manel/Music/matching/DS1/Docs/";
		File dir = new File(getPath()+"index");
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
	
	  private static void loadProperties() {
		    properties = new Properties();
		    try {
		    	InputStream input = new FileInputStream("config.properties");
		    	properties.load(input);
		    	input.close();
		    } catch (IOException ex) { ex.printStackTrace(); }
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
