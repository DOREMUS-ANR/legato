package legato.match;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Evaluator;

import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import legato.LEGATO;
import legato.gui.GUI;

public class Align {
	
	/*********
	 ** Create and save the alignment file
	 *********/
	public static void saveMappings(MapList mapList) throws Exception {
		LEGATO legato = LEGATO.getInstance();
		FileWriter alignFile= new FileWriter (legato.getPath()+File.separator+"results.rdf");
		BufferedWriter bw = new BufferedWriter (alignFile);
		PrintWriter pw = new PrintWriter (bw);
		pw.println("<?xml version='1.0' encoding='utf-8'?>");
		pw.println("<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment'");
		pw.println("xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'");
		pw.println("xmlns:xsd='http://www.w3.org/2001/XMLSchema#'>");
		pw.println("<Alignment>");
		pw.println("<xml>yes</xml>");
		pw.println("<level>0</level>");
		pw.println("<type>??</type>");
		pw.println(mapList.getAlignments());
		pw.println("</Alignment>");
		pw.println("</rdf:RDF>");
		pw.close();
		GUI.resultsArea.append("\nRunning time = "+(System.currentTimeMillis()/1000 - legato.getBeginTime())+" seconds");
		evaluateMappings();
	}
	
	public static void evaluateMappings() throws AlignmentException
	{
		LEGATO legato = LEGATO.getInstance();
		if (legato.getRefAlign() == null) GUI.resultsArea.append("\nNo reference alignment file found !");
		else 
		{
			AlignmentParser parser = new AlignmentParser(0);
			Alignment refAlign = parser.parse((legato.refAlign).toURI());
			Alignment mapFile = parser.parse( new File(legato.getPath()+File.separator+"results.rdf").toURI() );
			Properties p = new Properties();
			Evaluator evaluator = new PRecEvaluator(refAlign, mapFile);
			evaluator.eval(p);
			GUI.resultsArea.append("\nEvaluation results:");
			GUI.resultsArea.append("\nF-Measure = "+Math.floor(((PRecEvaluator)evaluator).getFmeasure()*100)/100);
			GUI.resultsArea.append("\nPrecision = "+Math.floor(((PRecEvaluator)evaluator).getPrecision()*100)/100);
			GUI.resultsArea.append("\nRecall = "+Math.floor(((PRecEvaluator)evaluator).getRecall()*100)/100);
		}
	}
	
}

