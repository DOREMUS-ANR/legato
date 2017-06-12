package legato;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntTools;
import org.apache.jena.ontology.OntTools.Path;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.Filter;

import legato.document.CBDBuilder;
import legato.document.FileManager;

public class A {

	public static void main(String[] args) throws FileNotFoundException {
		
		Model model = ModelFactory.createDefaultModel();
		Model finalModel = ModelFactory.createDefaultModel();
		InputStream in =new FileInputStream("C:/Users/Manel/Music/DS_HT/source.ttl");
		model.read(in, null,"TTL");
		
		List<String> id = new ArrayList<String>();
		id.add("http://data.doremus.org/expression/fe8c27ea-1dd6-36ac-bdaf-375acecfc3f1");
		
		id.add("http://data.doremus.org/expression/f96f8a54-45d3-3d39-9e98-72a74d9c32a6");
		
		id.add("http://data.doremus.org/expression/41c60f7e-2965-3dd9-a6c6-2a36f61ccd70");
		
		id.add("http://data.doremus.org/expression/1f8bbcac-c5fa-38d5-bd5b-602dc76a1bda");
		
		id.add("http://data.doremus.org/expression/cedd10d8-974f-3328-a36f-2a7fb336c683");
		
		id.add("http://data.doremus.org/expression/aed0815c-d793-359e-afe7-758216b1a90f");
		
		id.add("http://data.doremus.org/expression/1fe5e42d-ce7b-368e-83a4-90994d68c8c1");
		
		id.add("http://data.doremus.org/expression/0f242b66-f2d2-3c65-9b14-e2bf320eae37");
		LEGATO legato = LEGATO.getInstance();
		
		model.listSubjects().toSet().forEach((resource) -> { //Parse all resources 
			if (legato.hasType(resource) == true) //If the current resource belongs to a given "type"
		    {
				if (id.contains(resource.toString())){
					finalModel.add(CBDBuilder.getCBD(model, resource));
					finalModel.add(CBDBuilder.getCBDDirectPredecessors(model, resource));
					finalModel.add(CBDBuilder.getCBDDirectSuccessors(model, resource));
				}
				try {
					FileManager.createRDFile(new File("C:/Users/Manel/Downloads/"), "PP", finalModel, "TTL");
				} catch (IOException e) {
					e.printStackTrace();
				}
			/*	model.listStatements().toSet().forEach((stmt) -> { 	
					Property p = stmt.getPredicate();
					RDFNode object = stmt.getObject();
					if (object.isLiteral()){
						if (p.toString().contains("R33_has_content")){
						if (id.contains(object.asLiteral().getInt()))
						{
							Path path = OntTools.findShortestPath(model, resource, object, Filter.any); // A filter which accepts statements whose predicate matches one of a collection of predicates held by the filter object.
							if (!(path==null))
							{
								System.out.println(object.toString());
								finalModel.add(CBDBuilder.getCBD(model, resource));
								finalModel.add(CBDBuilder.getCBDDirectPredecessors(model, resource));
								finalModel.add(CBDBuilder.getCBDDirectSuccessors(model, resource));
							}
						}
					}
					}
			
				});*/
		    }});

	}

}
