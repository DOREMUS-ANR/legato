package legato.match;

import java.net.URISyntaxException;

public class Map {
	
	String sURI;
	String tURI;
	Double sim;

	public Map (String sID, String tID, Double sim) throws URISyntaxException 
	{
		this.sURI = sID; //"http://data.doremus.org/expression/"+ sID;
		this.tURI = tID; //"http://data.doremus.org/expression/"+ tID;
		this.sim = +Math.floor(sim*100)/100; //Keep only 2 digits after decimal
    }
	
	public String getSourceURI()
	{
		return sURI;
	}
	
	public String getTargetURI()
	{
		return tURI;
	}
	
	public Double getSimValue()
	{
		return sim;
	}
	
	public boolean contains(String sURI)
	{
		boolean exist = false;
		if (this.getSourceURI().equals(sURI)) exist = true;
		if (this.getSourceURI() == sURI) exist = true;
		return exist;
	}
	
	public String getAlign()
	{
		String map = "<map>\n" +
					 "<Cell>\n" +
				     "<entity1 rdf:resource=\""+getSourceURI()+"\"/>\n" +
				     "<entity2 rdf:resource=\""+getTargetURI()+"\"/>\n" +
				     "<measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">"+ getSimValue() +"</measure>\n" +
				     "<relation> = </relation>\n"+
			         "</Cell>\n" +
				     "</map>\n";
		return map;
	}
	
}
