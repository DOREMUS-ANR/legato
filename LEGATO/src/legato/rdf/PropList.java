package legato.rdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.rdf.model.Property;

public class PropList {
	
	private final List<Prop> properties;
	
	public PropList()
	{
		properties = new ArrayList<Prop>();
	}

	public void add (String newProp,List<Property> path) throws IOException 
	{
		Prop prop = new Prop (newProp, path);
		add(prop);
	}
	
	public void add(Prop prop) 
	{ 
		properties.add(prop);
	}
	
	public Prop get(int index) 
	{
		return properties.get(index);
	}
	
	public Iterator<Prop> iterator() 
	{
		return properties.iterator();
	}
	
	public int size() 
	{
		return properties.size(); 
	}
	
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		for (Prop prop : properties) {
			sb.append(prop.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public Boolean existProperty(List<Property> path)
	{
		Boolean exist = false;
		for (Prop prop : properties) {
			if (prop.getPath().equals(path)) exist=true;
		}
		return exist;
	}
	
	public String getPropertyName(List<Property> path)
	{
		String propName = null;
		for (Prop prop : properties) {
			if (prop.getPath().equals(path))  propName=prop.getName();
		}
		return propName;
	}
	
	public String getProperties(){
		StringBuilder sb = new StringBuilder();
		for (Prop prop : properties) {
			sb.append(prop.getName());
			sb.append("\n");
		}
		return sb.toString().trim();
	}
}
