package legato.document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.jena.rdf.model.Model;

import legato.LEGATO;
import legato.gui.GUI;

/**
 * @author Manel Achichi
 **/

public class FileManager {

	/*********************
	 * Create an RDF file  
	 *********************/
	public static void createRDFile(File dirCluster, String fileName, Model model, String ext) throws IOException{
		LEGATO legato = LEGATO.getInstance();
		FileWriter out = new FileWriter(dirCluster.getAbsolutePath()+File.separator+fileName+"."+ext);
		try {
			if (ext.equals("nt")) model.write( out, "N-TRIPLES");
			else model.write( out, "TTL");
        }
        finally {
           try {
               out.close();
           }
           catch (IOException closeException) {
           }
        }	
	}
	
	public static File getCreatedRDFile(String fileName, Model model) throws IOException{
		LEGATO legato = LEGATO.getInstance();
		FileWriter out = new FileWriter(legato.getPath()+File.separator+fileName+".rdf");
		model.write( out, "RDF/XML");
		File file = new File(legato.getPath()+File.separator+fileName+".rdf");
		out.close();
		return file;	
	}
	
	/*********************
	 * Create a text file  
	 *********************/
	public static void create(String fileName, String content, String dataset) throws IOException{
		File dir = new File(LEGATO.getInstance().getPath()+File.separator+"docs"+File.separator+dataset);
		if (!dir.exists()) dir.mkdirs();
		PrintStream ps = new PrintStream(dir.toString()+File.separator+fileName+".txt");
		ps.println(clean(content));
	}
	
	/*********************
	 * Create a text file  
	 *********************/
	public static void create(String fileName, String content) throws IOException{
		File dir = new File(LEGATO.getInstance().getPath());
		PrintStream ps = new PrintStream(dir.toString()+File.separator+fileName+".txt");
		ps.println(content);
	}
	
	/***************************************
	 * Remove all punctuation from a string  
	 ***************************************/
	public static String clean(String content){
		return content.replaceAll("\\p{Punct}", " ").trim();
	}
	
	/******************
	 * Get File Content  
	 * @throws IOException 
	 ******************/
	public static String getContent (File file) throws IOException{
		List<String> lines = Files.readAllLines(Paths.get(file.toString()), StandardCharsets.UTF_8); 
		StringBuilder sb = new StringBuilder(); 
		for (String line : lines) sb.append(line);
		return sb.toString();
	}
	
}
