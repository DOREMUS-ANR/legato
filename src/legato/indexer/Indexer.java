package legato.indexer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import legato.LEGATO;

public class Indexer {

    private final File sourceDirectory;
    private final File indexDirectory;
    private static String fieldName;

    public Indexer() {
        this.sourceDirectory = new File(LEGATO.getInstance().DIR_TO_INDEX);
        this.indexDirectory = new File(LEGATO.getInstance().INDEX_DIR);
        fieldName = LEGATO.getInstance().FIELD_CONTENT;
    }

    public void indexFiles() throws Exception {
        Directory dir = FSDirectory.open(indexDirectory);
        Analyzer analyzer = new StandardAnalyzer(StandardAnalyzer.STOP_WORDS_SET);  // using stop words
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);

        if (indexDirectory.exists()) {
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        } else {
            // Add new documents to an existing index:
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        }

        IndexWriter writer = new IndexWriter(dir, iwc);
        int id = 0;
        listeRepertoire(sourceDirectory, writer, id);
        writer.close();
    }
    
public static void listeRepertoire (File repertoire, IndexWriter writer, int id) throws Exception {
    	
		if ( repertoire.isDirectory ( ) ) {
			File[] list = repertoire.listFiles();
            if (list != null){
            	for (File file : list) {
            		if (file.isFile()){
            			if(file.length()>0){
            			Document doc = new Document();
                        FieldType fieldType = new FieldType();
                        fieldType.setIndexed(true);
                        fieldType.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
                        fieldType.setStored(true);
                        fieldType.setStoreTermVectors(true);
                        fieldType.setTokenized(true);
                        Field contentField = new Field(fieldName, getAllText(file), fieldType);
                        Field fileNameField = new Field(LEGATO.getInstance().FILE_NAME, file.getName(), Field.Store.YES,Field.Index.NOT_ANALYZED);
                        Scanner scanner = new Scanner(file);
                        Field contents = new Field("contents", scanner.useDelimiter("\\A").next(), Store.YES, Index.NO);  // you should actually close the scanner
                        scanner.close();
                        doc.add(new Field("id",""+id,Field.Store.YES,Field.Index.ANALYZED));
                        doc.add(new Field("path",""+file.getParentFile(),Field.Store.YES,Field.Index.ANALYZED));
                        doc.add(new Field("parent",""+file.getParentFile().getName(),Field.Store.YES,Field.Index.ANALYZED));
                        doc.add(fileNameField);
                        doc.add(contentField);
                        doc.add(contents);
                        writer.addDocument(doc);
                        id++;
            		}
            		}
            	}
            	for ( int i = 0; i < list.length; i++) {
                    listeRepertoire( list[i], writer, id);
		        } 
            }
		}
	}

    public static String getAllText(File f) throws FileNotFoundException, IOException {
        String textFileContent = "";

        for (String line : Files.readAllLines(Paths.get(f.getAbsolutePath()), StandardCharsets.ISO_8859_1)) {
            textFileContent += line;
        }
        return textFileContent;
    }
}