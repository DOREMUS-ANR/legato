package legato.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
//import ca.pfv.spmf.tools.textprocessing.PorterStemmer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class Stemmer {
	
	public static String stem (String text) throws Exception
	{
	//	StanfordLemmatizer stanfordLemmatizer = new StanfordLemmatizer();
		
//		String stemText=null;
//		String[] terms = text.split(" ");
//		for (String term: terms)
//		{
//			stemText = stemText+ stemTerms(term)+" ";
//		}
		System.out.println(text + "   -->   "+ stemTerms(text));
		return stemTerms(text);
	}
	
//	static String stemTerm (String term) {
//	    PorterStemmer stemmer = new PorterStemmer();
//	    System.out.println(term + "   -->   "+stemmer.stem(term));
//	    return stemmer.stem(term);
//	}
	
	public static String stemTerms(String term) throws Exception {
	    Analyzer analyzer = new StandardAnalyzer();
	    TokenStream result = analyzer.tokenStream(null, term);
	    result = new PorterStemFilter(result);
	    result = new StopFilter(result, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
	    CharTermAttribute resultAttr = result.addAttribute(CharTermAttribute.class);
	    result.reset();

	    String tokens = null;
	    while (result.incrementToken()) {
	        tokens = tokens + resultAttr.toString()+ " ";
	    }
	    return tokens.trim();
	}

}
