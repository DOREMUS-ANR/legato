package legato;

public class Main {
    
	private static LEGATO legato;
	
    public static void main(String[] args) throws Exception
    {
    	/********
    	 * Input = 2 RDF datasets
    	 * Output = build documents based on the CBD of the resources
    	 ********/
    	legato = LEGATO.getInstance();
    	legato.openGUI();
    }
    
}
