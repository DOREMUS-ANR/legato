package legato.vocabularies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.SKOS;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;

public class ConceptFinder {
	
	private static ArrayList<Vocabulary> vocabularies;
	public static Vocabulary genreVocabulary;
	public static Vocabulary genreVocabularyDiabolo;
	public static Vocabulary keyVocabulary;

	/*********************************
	 *** Load vocabularies from github
	 *********************************/
	public static void loadVocabularies() throws IOException {
		
		final String vocabularyRoot = "https://raw.githubusercontent.com/DOREMUS-ANR/knowledge-base/master/vocabularies/";
	    vocabularies = new ArrayList<>();

	    GitHubClient gitHubClient = new GitHubClient();
		String authenticationToken = "bae77c1ed1092519a71e9e294b2cc584564b2ddb";
		gitHubClient.setOAuth2Token( authenticationToken );
		RepositoryService repositoryService = new RepositoryService( gitHubClient );
		
		Repository repo = repositoryService.getRepository("DOREMUS-ANR", "knowledge-base");
		
	    
//	    String token = "b05857f6f2585ee3f3973449cc363ca193c0c171";
//	    RepositoryService service = new RepositoryService();
//	    if (token != null && !token.isEmpty())
//	      service.getClient().setCredentials("manoach", "vincie36");//.setOAuth2Token(token);
//	    Repository repo = service.getRepository("DOREMUS-ANR", "knowledge-base");
	   
	    ContentsService contentsService = new ContentsService();
	    for (RepositoryContents content : contentsService.getContents(repo, "/vocabularies/")) {
	      if (!content.getName().endsWith(".ttl")) continue;
	      String url = vocabularyRoot + content.getName();
	      Vocabulary vocabulary = new Vocabulary(url);
	      vocabularies.add(vocabulary);
	      if (content.getName().equals("genre-iaml.ttl")) {
	        genreVocabulary = vocabulary;
	      }
	      if (content.getName().equals("genre-diabolo.ttl")) {
		        genreVocabularyDiabolo = vocabulary;
		      }
	      if (content.getName().equals("key.ttl")) {
		        keyVocabulary = vocabulary;
		  }
	    }
	  }
	
	/******************************* 
	 * Get "prefLabels" of a concept 
	 *******************************/
	public static List<String> getLabel(Vocabulary vocabulary, String uriConcept){
		List<String> labels = new ArrayList<String>();
		Iterator iter = vocabulary.findConcept(uriConcept).asResource().listProperties();
		while(iter.hasNext()){
			Statement stmt = (Statement) iter.next();
			Property prop = stmt.getPredicate();
		if (prop.equals(SKOS.prefLabel)){
				labels.add(stmt.getLiteral().getString());
			}
		}
		return labels;
	}
}
