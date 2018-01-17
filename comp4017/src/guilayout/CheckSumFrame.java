package guilayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Base64;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import jpass.util.SpringUtilities;

import keyEncrypt.CheckSum;

public class CheckSumFrame extends JDialog{
	PVSFrame parent;
	JTextArea dataArea = new JTextArea();

	File datafile;
	String[] operation = {"MD5","SHA-1","SHA-256"};
	JComboBox operatioBox = new JComboBox(operation);
	String hmode = "MD5";
	public CheckSumFrame(final PVSFrame parent)
	{
		this.parent = parent;

		dataArea.setLineWrap(true);
		dataArea.setWrapStyleWord(true);
		
		JPanel filePanel = new JPanel();
		JLabel Title = new JLabel("Check Sum File: ");
		JTextField filenamefield = new JTextField();
		filenamefield.setPreferredSize(new Dimension(100, 25 ) );
		filenamefield.setEditable(false);
		JButton filebrose = new JButton("Browse");
		
		filebrose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				datafile =null;
				JFileChooser filefc = new JFileChooser("./INITIALFILE/");
				filefc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (filefc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					datafile = filefc.getSelectedFile();
					filenamefield.setText(filefc.getSelectedFile().getName());
				}
			}
		});
		filePanel.add(Title);
		filePanel.add(filenamefield);
		filePanel.add(filebrose);
		JPanel middlepanel = new JPanel();
		middlepanel.add(operatioBox);
		operatioBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				hmode = (String) operatioBox.getSelectedItem();
			}
		});
		JPanel topPanel = new JPanel();
		topPanel.add(filePanel);
		topPanel.add(middlepanel);

		topPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(topPanel,
				2, 1, //rows, columns
				5, 5, //initX, initY
				1, 1);


		getContentPane().add(topPanel, BorderLayout.NORTH);
		
		dataArea.setEditable(false);
		
		getContentPane().add(dataArea, BorderLayout.CENTER);
		
		
		JPanel buttonPanel = new JPanel();
		JButton check = new JButton("check");
		
		buttonPanel.add(check);
		
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				CheckSum cs = new CheckSum();
				try {

					String show = cs.checkSum(datafile,hmode);
					dataArea.setForeground(Color.RED);
					dataArea.setText(show);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		setLocationRelativeTo(parent);
		setSize(300,300);
		setMinimumSize(new Dimension(400, 350));
		setVisible(true);
	}
	
	
	
}
