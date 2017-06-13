package legato;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class Test {

	public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {	
		File file = new File("C:/Users/Manel/Downloads/trans.owl");
				InputStream in = new FileInputStream(file);
		Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.read(in, "TURTLE");
		try {
		    in.close();
		} catch (IOException e) {
		    System.err.println("Couldn't close the inputStream");
		}
		
		
			
/*			OntModel ontologyModel =  ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
			InputStream in =new FileInputStream(file);
			ontologyModel.read(in, null);
			Model m = ontologyModel.getBaseModel();
			FileManager.createRDFile("mano", m, "TTL");
					//System.out.println(m.);
			//		StmtIterator iter = m.listStatements();
					
			//		System.out.println("****************************");
			/*		while (iter.hasNext()){ 
						Statement stmt      = iter.nextStatement();  
						Resource subject = stmt.getSubject();
						Property prop = stmt.getPredicate();
						RDFNode   object    = stmt.getObject(); 
					//	System.out.println(object);
					}
					
		/*			ExtendedIterator<Individual> indivs = ontologyModel.listIndividuals();
					for (Individual indiv : indivs.toList())
					{
						System.out.println(indiv.);
					}*/
		
	/*	    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		    OWLOntology onto = manager.loadOntologyFromOntologyDocument(file);
		    
		 // create a file for the new format
			File output = new File("C:/Users/Manel/Downloads/trans.owl");
			// save the ontology in Turtle format
			OWLOntologyFormat format = manager.getOntologyFormat(onto);
			TurtleOntologyFormat turtleFormat = new TurtleOntologyFormat();
			if (format.isPrefixOWLOntologyFormat()) {
				turtleFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
			}
			manager.saveOntology(onto, turtleFormat, IRI.create(output.toURI()));
			
		    System.out.println("**************************");
/*		    
		    OWLDataFactory factory = manager.getOWLDataFactory(); 
			OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		
			Set<OWLNamedIndividual> indivs = onto.getIndividualsInSignature();
			for(OWLNamedIndividual i : indivs)
			{
				String indivUri = i.getIRI().toString();
				if(indivUri == null)
					continue;
				//Add it to the global list of URIs
				System.out.println("indiiiiiiiiiiiiiiiiiiv = "+ indivUri);
				String name = "";
				for(OWLAnnotation a : i.getAnnotations(onto))
				{
					if(a.getValue() instanceof OWLLiteral)
					{
						OWLLiteral val = (OWLLiteral) a.getValue();
						name = val.getLiteral();
						System.out.println("YES = "+ name);
						break;
					}
					else System.out.println("NOOO = "+a.getValue());
				}

				Map<OWLObjectPropertyExpression,Set<OWLIndividual>> o = i.getObjectPropertyValues(onto);
				System.out.println("SIZE = "+ o.toString());
				
				Map<OWLDataPropertyExpression,Set<OWLLiteral>> dataPropValues = i.getDataPropertyValues(onto);
				
				for(OWLDataPropertyExpression prop : dataPropValues.keySet())
				{
					System.out.println("---------------------------------------------------------------");
				//	if(prop.isAnonymous())
				//		continue;
					
					System.out.println(prop.asOWLDataProperty().getIRI().toString());
					
					for(OWLLiteral val : dataPropValues.get(prop))
						System.out.println(val.getLiteral());
				}	
			}
			
		/*	for(OWLNamedIndividual indiv : individuals)
			{
				String resource = indiv.getIRI().toString();
				System.out.println(resource);
				
				for(OWLAnnotation annotation : indiv.getAnnotations(onto))
				{ 
					System.out.println("Property = "+ annotation.getProperty().getIRI());
					System.out.println("Value = "+ annotation.getValue().toString());
					for (OWLAnonymousIndividual g : annotation.getAnonymousIndividuals())
					{
						System.out.println(g.toString());
					}
				}	
			} */
	}
}
