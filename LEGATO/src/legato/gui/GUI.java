package legato.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import legato.LEGATO;
import legato.rdf.PropList;
import legato.utils.PropertyHandler;
import legato.vocabularies.ConceptFinder;

import java.awt.Color;
import javax.swing.UIManager;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class GUI {

	public static JFrame frame;
	private JTextField sourceField;
	private JTextField targetField;
	private JTextField alignmentField;
    public static JTextArea descriptionArea;
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
		frame = new JFrame();
		frame.setTitle("LEGATO");
		frame.setSize(700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		frame.getContentPane().setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Description", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 70, 213)));
		panel_1.setBounds(15, 197, 648, 231);
		panel_1.setLayout(null);
		
		descriptionArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(descriptionArea);
		scrollPane.setBounds(15, 36, 616, 167);
		panel_1.add(scrollPane);
		frame.getContentPane().add(panel_1);
		
		JPanel panel = new JPanel();
		panel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Input Files", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 70, 213)));
		panel.setBounds(14, 12, 648, 169);
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
		sourceField.setBounds(138, 26, 417, 26);
		panel.add(sourceField);
		sourceField.setColumns(10);
		
		targetField = new JTextField();
		targetField.setColumns(10);
		targetField.setBounds(138, 60, 417, 26);
		panel.add(targetField);
		
		alignmentField = new JTextField();
		alignmentField.setColumns(10);
		alignmentField.setBounds(138, 96, 417, 26);
		panel.add(alignmentField);
		
		JButton sourceAdd = new JButton("Add");
		sourceAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DatasetFileChooser dfc = new DatasetFileChooser(sourceField);
			}
		});
		sourceAdd.setBounds(565, 27, 68, 24);
		panel.add(sourceAdd);
		
		JButton targetAdd = new JButton("Add");
		targetAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DatasetFileChooser dfc = new DatasetFileChooser(targetField);
			}
		});
		targetAdd.setBounds(565, 62, 68, 24);
		panel.add(targetAdd);
		
		JButton alignmentAdd = new JButton("Add");
		alignmentAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DatasetFileChooser dfc = new DatasetFileChooser(alignmentField);
			}
		});
		alignmentAdd.setBounds(565, 98, 68, 24);
		panel.add(alignmentAdd);
		
		JButton btnMatch = new JButton("Run");
		btnMatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LEGATO legato = LEGATO.getInstance();
				PropList propList = new PropList();
				legato.setPropList(propList);
				if (!alignmentField.getText().isEmpty()) legato.setRefAlign(new File(alignmentField.getText()));
					try {
						ConceptFinder.loadVocabularies();
						PropertyHandler.clean(sourceField.getText(), targetField.getText());
						legato.buildDocuments();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
			}
		});
		btnMatch.setBounds(306, 129, 68, 24);
		panel.add(btnMatch);
	}
}
