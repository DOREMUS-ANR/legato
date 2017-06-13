package legato.keys.def;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class KeyList {

private final List<Key> keys;
	
	public KeyList()
	{
		keys = new ArrayList<Key>();
	}
	
	public void add(Key key) 
	{ 
		keys.add(key);
	}
	
	public Key get(int index) 
	{
		return keys.get(index);
	}
	
	public Iterator<Key> iterator() 
	{
		return keys.iterator();
	}
	
	public int size() 
	{
		return keys.size(); 
	}
	
	/******
	 * Cartesian product between 2 sets of keys
	 ******/
	public KeyList merge(HashSet<HashSet<Key>> keySets)
	{
		HashSet<Key> keySet1 = null;
        HashSet<Key> keySet2 = null;
        for (HashSet<Key> keySet : keySets) {
            if (keySet1 == null) {
                keySet1 = keySet;
            } else {
                keySet2 = keySet;
            }
            if (keySet1 != null && keySet2 != null) {
                keySet2 = cartesianProduct(keySet1, keySet2);
                keySet1 = null;
            }
        }
        if (keySet2 == null) {
            keySet2 = keySet1;
        }
        
        HashSet<Key> k = new HashSet<Key>();
        k = simplifyKeySet(keySet2);
       
        KeyList mergedKeys = new KeyList();
        Iterator iter = k.iterator();
        while (iter.hasNext())
        {
        	mergedKeys.add((Key) iter.next()); 
        }
        return mergedKeys;
	}
	
	public HashSet<Key> cartesianProduct(HashSet<Key> keySet1, HashSet<Key> keySet2) {
        HashSet<Key> mergedKeys = new HashSet<Key>();
        for (Key key1 : keySet1) {
            for (Key key2 : keySet2) {
                Key mergedKey = new Key();
                mergedKey.addListProperties(key1);
                mergedKey.addListProperties(key2);
                mergedKeys.add(mergedKey);
            }
        }
        return mergedKeys;
    }
	
	public HashSet<Key> simplifyKeySet(HashSet<Key> keySet) {
        HashSet<Key> newKeySet = new HashSet<Key>();
        newKeySet.addAll(keySet);
        for (Key key1 : keySet) {
            for (Key key2 : keySet) {
                if ((equals(key1, key2)==false) && key1.containsAllProperties(key2)==true) {
                    newKeySet.remove(key1);
                }
            }
        }
        return newKeySet;
    }
	
	public Boolean equals(Key key1, Key key2) 
	{
		Boolean equal = false;
		if (key1.containsAllProperties(key2)==true && key2.containsAllProperties(key1)==true) equal=true;
		return equal;	
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (Key key : keys) {
			sb.append(key.toString());
			sb.append("\n");
		}
		return sb.toString().trim();
	}
	
}
