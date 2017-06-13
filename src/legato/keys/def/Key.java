package legato.keys.def;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Key {

	private List<String> keyProperties;

	public Key () {
		keyProperties = new ArrayList<String>();
	}
	
	public void addProperty(String property)
	{
		keyProperties.add(property);
	}
	
	public void addListProperties(Key key)
	{
		Iterator iter = key.keyProperties.iterator();
		while (iter.hasNext())
		{
			String property = (String) iter.next();
			if (!keyProperties.contains(property)) addProperty(property);
		}
	}
	
	public List<String> getKey()
	{
		return keyProperties;
	}
	
	public Boolean containsAllProperties(Key key)
	{
		Boolean exist = true;
		for (String prop : key.keyProperties)
		{
			if (!keyProperties.contains(prop)) exist = false;
		}
		return exist;
	}
	
	public Iterator<String> iterator() 
	{
		return keyProperties.iterator();
	}
	
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Iterator iter = keyProperties.iterator();
		while (iter.hasNext())
		{
			String prop = (String) iter.next();
			sb.append(prop+ ", ");
		}
		sb.deleteCharAt(sb.length()-2);
		sb.append("]");
		return sb.toString();
		
	}

}
