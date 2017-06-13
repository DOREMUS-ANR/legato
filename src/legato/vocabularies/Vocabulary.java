package legato.vocabularies;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import java.util.HashMap;

/**
 * Utility for Vocabulary referencing.
 */

public class Vocabulary {
  private Model vocabulary;
  private HashMap<String, Resource> substitutionMap;
  private String schemePath;

  public Vocabulary(String url) {
	  if (!url.equals("https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/genre-rameau.ttl")){
    vocabulary = ModelFactory.createDefaultModel();
    vocabulary.read(url, "TURTLE");

    // Save default path
    StmtIterator conceptSchemeIter =
      vocabulary.listStatements(new SimpleSelector(null, RDF.type, vocabulary.getResource(SKOS.ConceptScheme.toString())));
    if (conceptSchemeIter.hasNext()) {
      schemePath = conceptSchemeIter.nextStatement().getSubject().toString();
    }
    if (schemePath != null && !schemePath.endsWith("/") && !schemePath.endsWith("#"))
      schemePath += "/";

    // Build a map
    substitutionMap = new HashMap<>();
    StmtIterator conceptIter =
      vocabulary.listStatements(new SimpleSelector(null, RDF.type, vocabulary.getResource(SKOS.Concept.toString())));

    while (conceptIter.hasNext()) {
      Resource resource = conceptIter.nextStatement().getSubject();
      StmtIterator labelIterator = resource.listProperties(SKOS.prefLabel);
      while (labelIterator.hasNext()) {
        String value = labelIterator.nextStatement().getObject().toString();
        substitutionMap.put(value, resource);
      }
      labelIterator = resource.listProperties(SKOS.altLabel);
      while (labelIterator.hasNext()) {
        String value = labelIterator.nextStatement().getObject().toString();
        substitutionMap.put(value, resource);
      }
    }
  }
  }

  public Resource findConcept(String uriConcept) {
    Resource concept = vocabulary.getResource(uriConcept);
    if (vocabulary.contains(concept, null, (RDFNode) null)) {
      return concept;
    } else return null;
  }
}
