package legato.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ValueComparator<T> implements Comparator<T>{

	Map<T,String> base;
	
	public ValueComparator(Map<T,String> base) {
	this.base = base;
	}

	@Override
	public int compare(T a, T b) {
	if (Double.parseDouble(base.get(a)) >= Double.parseDouble(base.get(b))) {
	return -1;
	} else {
	return 1;
	} 
	}
	
}
