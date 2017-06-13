package legato.gui;

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import legato.LEGATO;

public class DatasetFileChooser extends JPanel {
    
	public DatasetFileChooser(JTextField textField) {
		
		super(new BorderLayout());
		JFileChooser fileChooser = new JFileChooser(new File(LEGATO.getInstance().getPath() + "store/"));
	
		/*****
		 * Filter on file extensions
		 *****/
		fileChooser.setFileFilter(new FileFilter() {
	        @Override
	        public boolean accept(File f) {
	            if (f.isDirectory()) {
	                return true;
	            }
	            final String name = f.getName();
	            return name.endsWith(".rdf") || name.endsWith(".ttl") || name.endsWith(".nt") || name.endsWith(".owl");
	        }

	        @Override
	        public String getDescription() {
	            return "*.rdf,*.ttl,*.nt";
	        }
	    });
		int returnVal = fileChooser.showOpenDialog(DatasetFileChooser.this);
	    
		/*******
		 * If a file is selected 
		 ********/
	    if (returnVal == JFileChooser.APPROVE_OPTION) { 
	        File file = fileChooser.getSelectedFile();	
	        textField.setText(file.getAbsolutePath());
	      }
	}
}
