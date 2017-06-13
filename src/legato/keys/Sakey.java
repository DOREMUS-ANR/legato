package legato.keys;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import legato.LEGATO;
import legato.keys.def.Key;
import legato.keys.def.KeyList;

public class Sakey {

	private static final Pattern KEY_REGEX = Pattern.compile("\\[(.+?)\\]");
	
	public static KeyList extractKeys(File file, KeyList keys) throws IOException {
		
		LEGATO legato = LEGATO.getInstance();
		ProcessBuilder pbSource = new ProcessBuilder("java", "-jar",legato.getPath()+File.separator+"store"+File.separator+"sakey.jar", file.toString(), "1");
		pbSource.directory(new File(legato.getPath()+File.separator+"store"));
		Process pSource = pbSource.start();
		
		/*******
		 * Parse the results
		 *******/
		BufferedReader reader = new BufferedReader(new InputStreamReader(pSource.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line = null;
		while ( (line = reader.readLine()) != null) {
			builder.append(line);
			builder.append(System.getProperty("line.separator"));
		}
		
		/*******
		 *  Parse the generated keys
		 *******/
		if(!builder.toString().contains("0-almost keys:[]"))
        {
			String[] tab1 = builder.toString().split("0-almost keys:");
			for(String k : getKeys(tab1[1].substring(1, tab1[1].length()-2)))
			{
				Key key = new Key();
				String[] tab2 = k.split(", ");
				for (String property : tab2) { //For each property
					key.addProperty(property);
				}
				keys.add(key);
			}
        }
		return keys;

	}

	private static List<String> getKeys (final String str) {
	    final List<String> keys = new ArrayList<String>();
	    final Matcher matcher = KEY_REGEX.matcher(str);
	    while (matcher.find()) {
	        keys.add(matcher.group(1));
	    }
	    return keys;
	}
}
