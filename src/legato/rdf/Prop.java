package legato.rdf;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.rdf.model.Property;

public class Prop {
	
	private String name;
	private List<Property> path;

	public Prop(String newProp, List<Property> path) throws IOException {
		this.name = newProp;
		this.path = path;
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<Property> getPath()
	{
		return path;
	}
	
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(name + " = [");
		Iterator iter = path.iterator();
		while (iter.hasNext())
		{
			Property prop = (Property) iter.next();
			sb.append(prop.toString()+ ", ");
		}
		sb.deleteCharAt(sb.length()-2);
		sb.append("]");
		return sb.toString();
		
	}
}
