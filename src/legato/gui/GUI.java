package legato.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import legato.LEGATO;
import legato.document.FileManager;
import legato.rdf.ModelManager;
import legato.rdf.PropList;
import legato.utils.PropertyHandler;

import java.awt.Color;
import java.awt.Component;

import javax.swing.UIManager;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class GUI {

	public static JFrame frame;
	private static JTextField sourceField;
	private static JTextField targetField;
	private static JTextField alignmentField;
    public static JTextArea resultsArea;
    public static JTextArea inputProp;
    private static String selectedProp;
    private static String propForDelete;
    private static String selectedClass;
    private static String classForDelete;
    private static JRadioButton rdbtnManuel;
    private static JRadioButton rdbtnAutomatic;
    private static String matching;
    
    /**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			frame = new JFrame();
			frame.setTitle("LEGATO");
			frame.setSize(890, 900);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
			frame.getContentPane().setLayout(null);
			
			/*****************************
			 * Description panel 
			 *****************************/
			JPanel panel_1 = new JPanel();
			panel_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
			panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Results", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 70, 213)));
			panel_1.setBounds(25, 677, 828, 151);
			panel_1.setLayout(null);
			
			resultsArea = new JTextArea();
			JScrollPane scrollPane = new JScrollPane(resultsArea);
			scrollPane.setBounds(15, 39, 798, 96);
			panel_1.add(scrollPane);
			frame.getContentPane().add(panel_1);
			
			JPanel panel_2 = new JPanel();
			panel_2.setFont(new Font("Tahoma", Font.PLAIN, 11));
			panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Properties", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 70, 213)));
			panel_2.setBounds(15, 298, 838, 384);
			panel_2.setLayout(null);
			
			inputProp = new JTextArea();
			inputProp.addMouseListener(new MouseAdapter(){
	            @Override
	            public void mouseClicked(MouseEvent e){
	            	int line;
					try {
						line = inputProp.getLineOfOffset( inputProp.getCaretPosition() );
						int start = inputProp.getLineStartOffset( line );
		            	int end = inputProp.getLineEndOffset( line );
		            	
		            	DefaultHighlighter highlighter =  (DefaultHighlighter)inputProp.getHighlighter();
		                DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter( Color.lightGray);
		                highlighter.setDrawsLayeredHighlights(false); // this is the key line
		                highlighter.addHighlight(start, end, painter );
		                
		            	selectedProp = inputProp.getDocument().getText(start, end - start);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
	            }
	        });
			
			JScrollPane scrollPane2 = new JScrollPane(inputProp);
			scrollPane2.setBounds(15, 36, 342, 330);
			panel_2.add(scrollPane2);
			frame.getContentPane().add(panel_2);
			
			
			JScrollPane scrollPane_1 = new JScrollPane((Component) null);
			scrollPane_1.setBounds(500, 36, 323, 330);
			panel_2.add(scrollPane_1);
			
			JTextArea outputProp = new JTextArea();
			scrollPane_1.setViewportView(outputProp);
			outputProp.addMouseListener(new MouseAdapter(){
	            @Override
	            public void mouseClicked(MouseEvent e){
	            	int line;
					try {
						line = outputProp.getLineOfOffset( outputProp.getCaretPosition() );
						int start = outputProp.getLineStartOffset( line );
		            	int end = outputProp.getLineEndOffset( line );
		            	
		            	DefaultHighlighter highlighter =  (DefaultHighlighter)outputProp.getHighlighter();
		                DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter( Color.lightGray);
		                highlighter.setDrawsLayeredHighlights(false); // this is the key line
		                highlighter.addHighlight(start, end, painter );
		                
		            	propForDelete = outputProp.getDocument().getText(start, end - start);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
	            }
	        });
			
			JPanel panel = new JPanel();
			panel.setFont(new Font("Tahoma", Font.BOLD, 12));
			panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Input Files", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 70, 213)));
			panel.setBounds(14, 12, 839, 132);
			frame.getContentPane().add(panel);
			panel.setLayout(null);
			JLabel lblSourceDataset = new JLabel("Source Dataset :");
			lblSourceDataset.setBounds(15, 29, 125, 20);
			panel.add(lblSourceDataset);
			
			JLabel lblTargetDataset = new JLabel("Target Dataset :");
			lblTargetDataset.setBounds(15, 63, 125, 20);
			panel.add(lblTargetDataset);
			
			JLabel lblAlignmentFile = new JLabel("Alignment File :");
			lblAlignmentFile.setBounds(15, 99, 125, 20);
			panel.add(lblAlignmentFile);
			
			sourceField = new JTextField();
			sourceField.setBounds(138, 26, 603, 26);
			panel.add(sourceField);
			sourceField.setColumns(10);
			
			targetField = new JTextField();
			targetField.setColumns(10);
			targetField.setBounds(138, 60, 603, 26);
			panel.add(targetField);
			
			alignmentField = new JTextField();
			alignmentField.setColumns(10);
			alignmentField.setBounds(138, 96, 603, 26);
			panel.add(alignmentField);
			
			
			
			JPanel panel_3 = new JPanel();
			panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Classes", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
			panel_3.setToolTipText("");
			panel_3.setBounds(15, 172, 838, 123);
			frame.getContentPane().add(panel_3);
			panel_3.setLayout(null);
			
			JScrollPane scrollPane_2 = new JScrollPane((Component) null);
			scrollPane_2.setBounds(15, 26, 342, 81);
			panel_3.add(scrollPane_2);
			
			JTextArea inputClasses = new JTextArea();
			scrollPane_2.setViewportView(inputClasses);
			inputClasses.addMouseListener(new MouseAdapter(){
	            @Override
	            public void mouseClicked(MouseEvent e){
	            	int line;
					try {
						line = inputClasses.getLineOfOffset( inputClasses.getCaretPosition() );
						int start = inputClasses.getLineStartOffset( line );
		            	int end = inputClasses.getLineEndOffset( line );
		            	
		            	DefaultHighlighter highlighter =  (DefaultHighlighter)inputClasses.getHighlighter();
		                DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter( Color.lightGray);
		                highlighter.setDrawsLayeredHighlights(false); // this is the key line
		                highlighter.addHighlight(start, end, painter );
		                
		            	selectedClass = inputClasses.getDocument().getText(start, end - start);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
	            }
	        });
			
			JScrollPane scrollPane_3 = new JScrollPane((Component) null);
			scrollPane_3.setBounds(496, 26, 327, 81);
			panel_3.add(scrollPane_3);
			
			JTextArea outputClasses = new JTextArea();
			outputClasses.addMouseListener(new MouseAdapter(){
	            @Override
	            public void mouseClicked(MouseEvent e){
	            	int line;
					try {
						line = outputClasses.getLineOfOffset( outputClasses.getCaretPosition() );
						int start = outputClasses.getLineStartOffset( line );
		            	int end = outputClasses.getLineEndOffset( line );
		            	
		            	DefaultHighlighter highlighter =  (DefaultHighlighter)outputClasses.getHighlighter();
		                DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter( Color.lightGray);
		                highlighter.setDrawsLayeredHighlights(false); // this is the key line
		                highlighter.addHighlight(start, end, painter );
		                
		            	classForDelete = outputClasses.getDocument().getText(start, end - start);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
	            }
	        });
			scrollPane_3.setViewportView(outputClasses);
			
			/**************************
			 * Buttons
			 **************************/
			JButton propAdd = new JButton("Add");
			propAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!selectedProp.equals(null))
					{
						if (!outputProp.getText().contains(selectedProp))
						{
							outputProp.setText((outputProp.getText()+"\n"+selectedProp).trim());
							selectedProp=null;
						}
					}
				}
			});
			propAdd.setBounds(372, 142, 115, 29);
			panel_2.add(propAdd);
			
			JButton propDelete = new JButton("Delete");
			propDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!propForDelete.equals(null))
					{
						if (outputProp.getText().contains(propForDelete))
						{
							outputProp.setText((outputProp.getText().replaceAll(propForDelete, "")).trim());
						}
					}
				}
			});
			propDelete.setBounds(372, 199, 115, 29);
			panel_2.add(propDelete);
			
			JButton btnRun = new JButton("Run");
			btnRun.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					long beginTime = System.currentTimeMillis()/1000;
					LEGATO legato = LEGATO.getInstance();
					legato.setBeginTime(beginTime);
					
					legato.addClasses(outputClasses.getText().trim());
					if (!alignmentField.getText().isEmpty()) legato.setRefAlign(new File(alignmentField.getText()));
					
					if (rdbtnAutomatic.isSelected())
					{
						matching = "automatic";
						PropList propList = new PropList(); 
						legato.setPropList(propList);
						try 
						{
							PropertyHandler.clean(sourceField.getText(), targetField.getText());
						} catch (IOException e1) { e1.printStackTrace(); }
					}
					else if (rdbtnManuel.isSelected())
					{
						matching = "manual";
						legato.addProperties (outputProp.getText().trim());
						Model srcModel = ModelManager.loadModel(sourceField.getText());
						Model tgtModel = ModelManager.loadModel(targetField.getText());
						try {
							legato.setSource(FileManager.getCreatedRDFile("source", srcModel));
							legato.setTarget(FileManager.getCreatedRDFile("target", tgtModel));
						} catch (IOException e1) { e1.printStackTrace(); }
					}
					try 
					{
						legato.buildDocuments();
					} catch (Exception e1) { e1.printStackTrace(); }
				}
			});
			btnRun.setBounds(372, 339, 115, 29);
			panel_2.add(btnRun);
			
			JButton sourceAdd = new JButton("Add");
			sourceAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					DatasetFileChooser dfc = new DatasetFileChooser(sourceField);
				}
			});
			sourceAdd.setBounds(756, 29, 68, 24);
			panel.add(sourceAdd);
			
			JButton targetAdd = new JButton("Add");
			targetAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					DatasetFileChooser dfc = new DatasetFileChooser(targetField);
				}
			});
			targetAdd.setBounds(756, 64, 68, 24);
			panel.add(targetAdd);
			
			JButton alignmentAdd = new JButton("Add");
			alignmentAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DatasetFileChooser dfc = new DatasetFileChooser(alignmentField);
				}
			});
			alignmentAdd.setBounds(756, 100, 68, 24);
			panel.add(alignmentAdd);
			
			JButton classAdd = new JButton("Add");
			classAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!selectedClass.equals(null))
					{
						if (!outputClasses.getText().contains(selectedClass))
						{
							outputClasses.setText((outputClasses.getText()+"\n"+selectedClass).trim());
						}
					}
				}
			});
			classAdd.setBounds(372, 26, 115, 29);
			panel_3.add(classAdd);
			
			JButton classDelete = new JButton("Delete");
			classDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!classForDelete.equals(null))
					{
						if (outputClasses.getText().contains(classForDelete))
						{
							outputClasses.setText((outputClasses.getText().replaceAll(classForDelete, "")).trim());
						}
					}
				}
			});
			classDelete.setBounds(372, 71, 115, 29);
			panel_3.add(classDelete);
			
			rdbtnManuel = new JRadioButton("Manual");
			rdbtnManuel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					rdbtnAutomatic.setSelected(false);
					Model srcModel = ModelManager.loadModel(sourceField.getText());
					Model tgtModel = ModelManager.loadModel(targetField.getText());
					
					List<Resource> classList = new ArrayList<Resource>();
					classList = ModelManager.getAllClassesFromModels(srcModel, tgtModel);
					inputClasses.setText(classList.toString().replaceAll(", ", "\n"));
							
					List<Property> propList = new ArrayList<Property>();
					propList = ModelManager.getAllPropFromModels(srcModel, tgtModel);
					inputProp.setText(propList.toString().replaceAll(", ", "\n"));
				}
			});
			rdbtnManuel.setBounds(465, 147, 155, 29);
			frame.getContentPane().add(rdbtnManuel);
			
			rdbtnAutomatic = new JRadioButton("Automatic");
			rdbtnAutomatic.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					rdbtnManuel.setSelected(false);
					inputProp.setText("");
					outputProp.setText("");
					
					Model srcModel = ModelManager.loadModel(sourceField.getText());
					Model tgtModel = ModelManager.loadModel(targetField.getText());
					
					List<Resource> classList = new ArrayList<Resource>();
					classList = ModelManager.getAllClassesFromModels(srcModel, tgtModel);
					inputClasses.setText(classList.toString().replaceAll(", ", "\n"));
				}
			});
			rdbtnAutomatic.setBounds(323, 147, 155, 29);
			frame.getContentPane().add(rdbtnAutomatic);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	/*	JButton btnMatch = new JButton("Run");
		btnMatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long beginTime = System.currentTimeMillis()/1000;
				LEGATO legato = LEGATO.getInstance();
				legato.setBeginTime(beginTime);
				PropList propList = new PropList();
				legato.setPropList(propList);
				if (!alignmentField.getText().isEmpty()) legato.setRefAlign(new File(alignmentField.getText()));
					try {
					//	ConceptFinder.loadVocabularies();
						PropertyHandler.clean(sourceField.getText(), targetField.getText());
						legato.buildDocuments();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
			}
		});
		btnMatch.setBounds(306, 129, 68, 24);
		panel.add(btnMatch);*/
	}
	
	public static String getMatchValue() {
		return matching;
	}
}
