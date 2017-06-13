package legato.keys;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import legato.LEGATO;

public class SILK {
	public static void link(String dirCluster) throws IOException, InterruptedException {
		LEGATO legato = LEGATO.getInstance();
		File file = new File (dirCluster+File.separator+"configFile.xml");		
		try {

			Process process = Runtime.getRuntime().exec("java -DconfigFile="+file+" -jar "+legato.getPath()+"store"+File.separator+"silk.jar"); 
		    InputStream stderr = process.getErrorStream ();
		    BufferedReader reader = new BufferedReader (new InputStreamReader(stderr));
		    String line = reader.readLine();
		    while (line != null && ! line.trim().equals("--EOF--")) {
		        line = reader.readLine();
		    }
		}
		catch (IOException ex){
		    ex.printStackTrace();
		}
		return;
	}
}
