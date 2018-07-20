package legato;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import legato.rdf.ModelManager;

public class Concatener {
	
	public static String target = "..\\person";
	public static String source = "..\\rfPerson.ttl";
	
	public static void main(String args[]) throws IOException
	{
		concatenateFiles();
		//concatenateResults(new File("..\\partitionner"));
	}
	
	public static void concatenateFiles() throws IOException
	{
		Model model = ModelFactory.createDefaultModel();
		
		File repo = new File(target);
		
		if(repo.exists()&&repo.isDirectory())
		{
			for(File childFile : repo.listFiles())
			{
				model.add(ModelManager.loadModel(childFile.getAbsolutePath()));
			}
		}
		
		FileWriter out = new FileWriter(source);
		model.write(out,"TTL");
		
		out.close();
	}
	
	public static void concatenateResults(File repo) throws IOException
	{
		Model model = ModelFactory.createDefaultModel();
		//Model modtmp;
		boolean first = true;
		//LEGATO legato = LEGATO.getInstance();
		
		for(File childFile : repo.listFiles())
		{
			System.out.println("File : "+childFile.getAbsolutePath());
			if(childFile.isDirectory())
			{
				if(first)
				{
					model = ModelManager.loadModel(childFile+"\\results.rdf");
					first=false;
				}
				else
				{
					/*
					modtmp = ModelManager.loadModel(childFile+"\\results.rdf");
					model.add(modtmp.listStatements());*/
				}
				
				//model = ModelManager.loadModel(childFile+"\\results.rdf");
			}
		}
		
		Partitioner.writeModel(model, "..\\bnf2pp.rdf", "RDF/XML-ABBREV");
		
	}
	
	
	
}
