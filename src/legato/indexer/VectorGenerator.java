package legato.indexer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;

import legato.LEGATO;

public class VectorGenerator {
    DocVector[] docVector;
    Map allterms;
    Integer totalNoOfDocumentInIndex;
    IndexReader indexReader;
    
    
    
    public VectorGenerator() throws IOException
    {
        allterms = new HashMap<>();
        indexReader = IndexOpener.GetIndexReader();
        totalNoOfDocumentInIndex = IndexOpener.TotalDocumentInIndex();
        docVector = new DocVector[totalNoOfDocumentInIndex];
    }
    
    public void GetAllTerms() throws IOException
    {
        AllTerms allTerms = new AllTerms();
        allTerms.initAllTerms();
        allterms = allTerms.getAllTerms();
    }
    
    public DocVector[] GetDocumentVectors() throws IOException {
        for (int docId = 0; docId < totalNoOfDocumentInIndex; docId++) {
            Terms vector = indexReader.getTermVector(docId, LEGATO.getInstance().FIELD_CONTENT);
            TermsEnum termsEnum = null;
            //Verification integrité de vector
            try
            {
            	termsEnum = vector.iterator(termsEnum);
	            BytesRef text = null;            
	            docVector[docId] = new DocVector(allterms);            
	            while ((text = termsEnum.next()) != null) {
	                String term = text.utf8ToString();
	                int freq = (int) termsEnum.totalTermFreq();  // La fréquence des termes
	           //     System.out.println(term + "  "+freq);
	                /*************************************/
	                DefaultSimilarity similarity = new DefaultSimilarity();
	                
	                Term termInstance = new Term("contents", term);
	                long dcFreq = indexReader.docFreq(termInstance);
	                int docnum = indexReader.numDocs();
	                
	                double idf = similarity.idf(dcFreq, docnum);
	                docVector[docId].setEntry(term, freq*idf);
	              //  System.out.println("TF-IDF" + freq*idf);
	                /**************************************/
	            }
	            Document doc = indexReader.document(docId);
	  		    Iterator iter = doc.iterator();
	  		    while (iter.hasNext()) {
						Field f = (Field) iter.next();
							if (f.name().equals("id")){
								docVector[docId].setID(f.stringValue());
							}
							if (f.name().equals("filename")){
								docVector[docId].setDocName(f.stringValue());
							}
							if (f.name().equals("path")){
								docVector[docId].setAbsolutePath(f.stringValue());
							}
							if (f.name().equals("parent")){
								docVector[docId].setParentFolder(f.stringValue());
							}
							if (f.name().equals("contents")){
								docVector[docId].setContents(f.stringValue());
							}
	    		    }
	            docVector[docId].normalize();
            }catch(NullPointerException e)
            {
            	System.err.println("GetDocumentVectors() - docId="+docId+" : vector=null !");
            }
        }
            
        indexReader.close();
        return docVector;
    }
}