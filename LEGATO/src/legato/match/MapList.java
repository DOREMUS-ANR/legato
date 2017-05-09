package legato.match;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class MapList implements Iterable<Map> {

	private final List<Map> mapList = new CopyOnWriteArrayList<Map>();
	
	public void add(Map map) { 
		mapList.add(map);
	}
	
	public void add (String sID, String tID, Double sim) throws URISyntaxException
	{
		Map map = new Map (sID, tID, sim);
		mapList.add(map);
	}
	
	public Map get(int index) {
		return mapList.get(index);
	}
	
	public boolean contains(String sURI)
	{
		boolean exist = false;
		for (Map map: mapList)
		{
			if (map.contains(sURI)) exist = true;
		}
		return exist;
	}
	
	public void removeMap (String sURI, String tURI)
	{
		Iterator<Map> it = mapList.iterator();
		while(it.hasNext()){
			Map map = (Map) it.next();
		    if(map.getSourceURI().equals(sURI) && map.getTargetURI().equals(tURI))
		    {
		    	mapList.remove(map);
		    }
		}
	}
	
	public String getTargetURI (String sURI)
	{
		String tURI = null;
		for (Map map: mapList)
		{
			if (map.contains(sURI)) tURI = map.getTargetURI();
		}
		return tURI;
	}
	
	@Override
	public Iterator<Map> iterator() {
		return mapList.iterator();
	}

	public int size() {
		return mapList.size(); 
	}
	
	public String getAlignments() {
		StringBuilder sb = new StringBuilder();
		for (Map map : mapList) {
			sb.append(map.getAlign());
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
