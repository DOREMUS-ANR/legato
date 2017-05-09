package legato.keys;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import legato.LEGATO;
import legato.keys.def.Key;
import legato.keys.def.KeyList;
import legato.rdf.ModelManager;
import legato.utils.ValueComparator;

public class SupportMergedKeys {
	
public static HashSet<String> allInstances = new HashSet<>();
public static HashMap<HashSet<String>,String> keysSource = new HashMap<HashSet<String>,String>();
public static HashMap<HashSet<String>,String> keysTarget = new HashMap<HashSet<String>,String>();
public static boolean cib = false;
public static int NBs,NBt; //le nombre d'instances

public static HashSet<String> rank(KeyList mKeys, File srcFile, File tgtFile) throws IOException {
	
	LEGATO legato = LEGATO.getInstance();
	
	/******
	 * Get All Merged Keys
	 ******/
	HashSet<HashSet<String>> keys = new HashSet<>(); //will contain all merged keys
	Iterator iter = mKeys.iterator();
	while (iter.hasNext()) //For each merged key
	{ 
		HashSet<String> properties = new HashSet<>();
		Key key = (Key) iter.next();
		Iterator iterProp = key.iterator();
		while (iterProp.hasNext()) //For each property
		{
			String property = (String) iterProp.next();
			properties.add(property);
		}
		keys.add(properties);
	}
	
	/******
	 * Support computing
	 ******/
	HashMap<String, HashSet<String>> srcResources = fileParsing(srcFile.toString());
    computeSupport(keys, srcResources);
    NBs = 100;
    
    allInstances.clear();
    cib=true;
	HashMap<String, HashSet<String>> tgtResources = fileParsing(tgtFile.toString());
    computeSupport(keys, tgtResources);
    NBt = 100;
    
    ValueComparator<HashSet<String>> compSource = new ValueComparator<HashSet<String>>(keysSource);
    TreeMap<HashSet<String>,String> mapTrieeSource = new TreeMap<HashSet<String>,String>(compSource);
    mapTrieeSource.putAll(keysSource);
    
    ValueComparator<HashSet<String>> compTarget = new ValueComparator<HashSet<String>>(keysTarget);
    TreeMap<HashSet<String>,String> mapTrieeTarget = new TreeMap<HashSet<String>,String>(compTarget);
    mapTrieeTarget.putAll(keysTarget);
    
    /******
     * Keys Ranking
     ******/
    HashMap<HashSet<String>,String> mergedKeys = new HashMap<HashSet<String>,String>();
    Iterator iterSource = mapTrieeSource.entrySet().iterator();
    while (iterSource.hasNext()){
    	Map.Entry keySource = (Map.Entry) iterSource.next();
        Iterator iterTarget = mapTrieeTarget.entrySet().iterator();
	    while (iterTarget.hasNext()){
	    	Map.Entry keyTarget = (Map.Entry) iterTarget.next();
	    	if (keySource.getKey().equals(keyTarget.getKey())){
	    		float s = Float.valueOf((String) keySource.getValue());
	    		float t = Float.valueOf((String) keyTarget.getValue());
	    		float rankValue = s*t;
	    		mergedKeys.put((HashSet<String>) keySource.getKey(), String.valueOf(rankValue));
	    	}
	    }
    }
    ValueComparator<HashSet<String>> compMerg = new ValueComparator<HashSet<String>>(mergedKeys);
    TreeMap<HashSet<String>,String> mapTrieeMerg = new TreeMap<HashSet<String>,String>(compMerg);
    mapTrieeMerg.putAll(mergedKeys);
    /******
     * Return the first key (with the highest score)
     ******/
    HashSet<String> res;
    if (mapTrieeMerg.isEmpty()) res=null;
    else res = mapTrieeMerg.firstEntry().getKey();
	return res;
}

private static void computeSupport(HashSet<HashSet<String>> keys, HashMap<String, HashSet<String>> instancesPerProperty) throws IOException {
    for (HashSet<String> key : keys) {
        HashSet<String> commonInstances = new HashSet<>();
        commonInstances.addAll(allInstances);
        for (String property : key) {
        	if(instancesPerProperty.get(property)!=null)
            commonInstances.retainAll(instancesPerProperty.get(property));
        }
        float percent = (commonInstances.size() * 100.0f) / allInstances.size();
            	if (cib==false)
            		keysSource.put(key, String.valueOf(percent));
            	else keysTarget.put(key, String.valueOf(percent));
    }
}

private static HashMap<String, HashSet<String>> fileParsing(String file) throws IOException {
    HashMap<String, HashSet<String>> instancesPerProperty = new HashMap<>();
    Model model = ModelManager.loadModel(file);
    
    StmtIterator iter = model.listStatements();
	while (iter.hasNext()){ 
		Statement stmt      = iter.nextStatement();  
		Resource subject = stmt.getSubject();
		Property prop = stmt.getPredicate();
		String instance = subject.toString();
        String property = prop.toString();
        allInstances.add(instance);
        if (instancesPerProperty.containsKey(property)) {
            instancesPerProperty.get(property).add(instance);
        } else {
            HashSet<String> instances = new HashSet<>();
            instances.add(instance);
            instancesPerProperty.put(property, instances);
        }
	}
	return instancesPerProperty;
}
}