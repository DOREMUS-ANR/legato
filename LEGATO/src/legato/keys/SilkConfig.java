package legato.keys;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import legato.LEGATO;

public class SilkConfig {

	/*********
	 * SILK Configuration File Generation based on the Discovered Best Key
	 *********/
	
	public static void config (HashSet<String> bestKey, File configDIR, String srcFile, String tgtFile) throws IOException {
		
		/*******
		 * Config File Generation  
		 *******/
		String output =configDIR+File.separator+"configFile.xml";
	    FileWriter cf= new FileWriter (output);
	    BufferedWriter bw = new BufferedWriter (cf);
	    PrintWriter configFile = new PrintWriter (bw);
	    
	    configFile.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	    configFile.println("<Silk>");
	    configFile.println("<Prefixes>");
	    configFile.println("<Prefix id=\"rdf\" namespace=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"/>");
	    configFile.println("<Prefix id=\"prop\" namespace=\"http://model.org/\" />");
	    configFile.println("</Prefixes>");
	    configFile.println("<DataSources>");
	    configFile.println("<DataSource type=\"file\" id=\"ontoA\">");
	    configFile.println("<Param name=\"file\" value=\""+srcFile+"\" />");
	    configFile.println("<Param name=\"format\" value=\"N-TRIPLE\"/>");
	    configFile.println("</DataSource>");
	    configFile.println("<DataSource type=\"file\" id=\"ontoB\">");
	    configFile.println("<Param name=\"file\" value=\""+tgtFile+"\" />");
	    configFile.println("<Param name=\"format\" value=\"N-TRIPLE\"/>");
	    configFile.println("</DataSource>");
	    configFile.println("</DataSources>");
	    configFile.println("<Interlinks>");
	    configFile.println("<Interlink id=\"oeuvres\">");
	    configFile.println("<LinkType>owl:sameAs</LinkType>");
	    configFile.println("<SourceDataset dataSource=\"ontoA\" var=\"a\">");
	    configFile.println("</SourceDataset>");
	    configFile.println("<TargetDataset dataSource=\"ontoB\" var=\"b\">");
	    configFile.println("</TargetDataset>");
	    configFile.println("<LinkageRule>");
	    configFile.println("<Aggregate type=\"average\">");
	    
	    /******
	     * Create many <campare> as properties to compare 
	     ******/
	    for (String property : bestKey)
	    {
	    	property = property.toString().substring(property.toString().lastIndexOf("/")+1, property.toString().length()); //Last fragment of an URI
	    	configFile.println("<Compare metric=\"levenshtein\" threshold=\"1\" required=\"true\">");
	    	configFile.println("<Input path=\"?a/prop:"+property+"\" />");
	    	configFile.println("<Input path=\"?b/prop:"+property+"\" />");
	    	configFile.println("</Compare>");
	    }
	    configFile.println("</Aggregate>");
	    configFile.println("<Filter limit=\"1\" />");
	    configFile.println("</LinkageRule>");
	    configFile.println("<Outputs>");
	    configFile.println("<Output  type=\"file\" minConfidence=\"0.8\">");
	    configFile.println("<Param name=\"file\" value=\"links.rdf\" />");
	    configFile.println("<Param name=\"format\" value=\"alignment\" />");
	    configFile.println("</Output>");
	    configFile.println("</Outputs>");
	    configFile.println("</Interlink>");
	    configFile.println("</Interlinks>");
	    configFile.println("</Silk>");
	    configFile.close();
	}
}
