package legato.cluster;

import org.apache.commons.math.linear.OpenMapRealVector;
import org.apache.commons.math.linear.RealVectorFormat;

public class DocVec {

	public OpenMapRealVector vector;
	private String id;
	
	public DocVec(String id, double[] vec) {
		this.id = id;
		this.vector = new OpenMapRealVector(vec.length);  
		for (int i = 0; i < vec.length; i++) {
			this.vector.setEntry(i, vec[i]); 
		}
	}
	
	public String getID()
	{
		return id;
	}
	
	public double[] getVector()
	{
		return vector.getData();
	}
}
