package legato.keys;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import legato.LEGATO;
import legato.document.FileManager;
import legato.keys.def.Key;
import legato.keys.def.KeyList;
import legato.rdf.ModelManager;
import legato.rdf.PropList;

public class KeysClassifier {
	
	    /**********************************************
	     *** Input = 2 RDF models
	     *** output = Ranked keys valid for both models 
	     **********************************************/

	private static LEGATO legato;
	
	KeysClassifier(){
	}
	
	public static HashSet<String> getBestKey (Model srcModel, Model tgtModel, File dirCluster) throws  IOException {
		
		legato = LEGATO.getInstance();
		/*******************************************************************************************
		 ** Place all Literals (in resources CBD) to a distance = 1
		 * Reasons : 
		 ***********+ SAKey considers blank nodes as "Strings"
		 ***********+ SILK gives different results when comparing property values whose distance > 1
		 *******************************************************************************************/	
		
	//	srcModel = ModelManager.rewrite(srcModel);
	//	tgtModel = ModelManager.rewrite(tgtModel);
		
		/**********
		 * Filter triples whose properties are common for both datasets
		 **********/
		List<Property> commonProperties = getCommonProperties(srcModel, tgtModel);
		srcModel = ModelManager.getFilteredTriples(srcModel, commonProperties);
		tgtModel = ModelManager.getFilteredTriples(tgtModel, commonProperties);
		
		/**********
		 * Save the 2 models temporarily in 2 RDF files in "N-TRIPLES" (The only format accepted by SAKey)
		 **********/
		FileManager.createRDFile(dirCluster, "source", srcModel, "nt");
		FileManager.createRDFile(dirCluster, "target", tgtModel, "nt");
		
		/*******
		 * The keys of the "source" and "target" datasets are saved in "srcKeys" and "tgtKeys" respectively
		 *******/
		KeyList srcKeys = new KeyList();
		KeyList tgtKeys = new KeyList();
		
		File srcFile = new File(dirCluster.getAbsolutePath()+File.separator+"source.nt");
		File tgtFile = new File(dirCluster.getAbsolutePath()+File.separator+"target.nt");
				
		srcKeys = Sakey.extractKeys(srcFile, srcKeys);
		tgtKeys = Sakey.extractKeys(tgtFile, tgtKeys);
		
		/*********
		 * Merge the 2 sets of keys
		 *********/
		HashSet<Key> keySet1 = new HashSet(); //keySet1 = all the keys of "srcKeys"
		HashSet<Key> keySet2 = new HashSet(); //keySet2 = all the keys of "tgtKeys"

		Iterator iter1 = srcKeys.iterator();
		while (iter1.hasNext()) keySet1.add((Key) iter1.next());
		Iterator iter2 = tgtKeys.iterator();
		while (iter2.hasNext()) keySet2.add((Key) iter2.next());

		HashSet<HashSet<Key>> keySets = new HashSet<HashSet<Key>>();
		keySets.add(keySet1);
        keySets.add(keySet2);
        
        KeyList mergedKeys = new KeyList();
        mergedKeys = mergedKeys.merge(keySets);
		
        /********
         * Keys Ranking
         ********/
        HashSet<String> bestKey = SupportMergedKeys.rank(mergedKeys, srcFile, tgtFile);
		
        return bestKey;
		}
	
public static HashSet<String> getHeterKey (Model srcModel, File dirCluster) throws  IOException {
		
		legato = LEGATO.getInstance();
		PropList propList = new PropList();
		legato.setPropList(propList);
		/*******************************************************************************************
		 ** Place all Literals (in resources CBD) to a distance = 1
		 * Reasons : 
		 ***********+ SAKey considers blank nodes as "Strings"
		 ***********+ SILK gives different results when comparing property values whose distance > 1
		 *******************************************************************************************/	
		
		srcModel = ModelManager.rewrite(srcModel);
		
		/**********
		 * Save the 2 models temporarily in 2 RDF files in "N-TRIPLES" (The only format accepted by SAKey)
		 **********/
		FileManager.createRDFile(dirCluster, "source", srcModel, "nt");
		
		/*******
		 * The keys of the "source" and "target" datasets are saved in "srcKeys" and "tgtKeys" respectively
		 *******/
		KeyList srcKeys = new KeyList();
		File srcFile = new File(dirCluster.getAbsolutePath()+File.separator+"source.nt");
		srcKeys = Sakey.extractKeys(srcFile, srcKeys);
		
		/*********
		 * Merge the 2 sets of keys
		 *********/
        KeyList mergedKeys = new KeyList();
       
        Iterator iter = srcKeys.iterator();
        while (iter.hasNext())
        {
        	mergedKeys.add((Key) iter.next()); 
        }
		
        /********
         * Keys Ranking
         ********/
        HashSet<String> bestKey = SupportMergedKeys.rank(mergedKeys, srcFile, srcFile);
		
        return bestKey;
		}
	
	public static List<Property> getCommonProperties(Model srcModel, Model tgtModel)
	{
		/****
		 * Get All Predicates of the source model 
		 ****/
		List<Property> propSRC = new ArrayList<Property>();
		StmtIterator iterSRCModel = srcModel.listStatements();
		while (iterSRCModel.hasNext()){
			Statement stmt      = iterSRCModel.nextStatement();
			Property  prop = stmt.getPredicate();
			if (!(propSRC.contains(prop))){
				propSRC.add(prop);
			}
		}
		/****
		 * Get All Common Predicates for both models 
		 ****/
		List<Property> commonProperties = new ArrayList<Property>();
		StmtIterator iterTGTModel = tgtModel.listStatements();
		while (iterTGTModel.hasNext()){
			Statement stmt      = iterTGTModel.nextStatement();
			Property  prop = stmt.getPredicate();
			if (!(commonProperties.contains(prop))&&(propSRC.contains(prop))){ //If "prop" is a common property for both datasets
				commonProperties.add(prop);
			}
		}
		return commonProperties;
	}
}
		
