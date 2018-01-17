package guilayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import jpass.util.SpringUtilities;


public class KeyList extends JDialog implements ActionListener{
	//private final JList<String> keyTitleList;
	private JScrollPane scrollPane;
	final File folder;
	//private final JScrollPane ButtonscrollPane;
	public KeyList(final PVSFrame parent)
	{

		JPanel topPanel = new JPanel();
		JLabel KeyP = new JLabel("Key");
		JLabel DescP = new JLabel("Descrption");
		JLabel DeleteP = new JLabel("Delete Button");
		topPanel.add(KeyP);
		topPanel.add(DescP);
		topPanel.add(DeleteP);
		topPanel.setLayout(new GridLayout(1, 3));
		getContentPane().add(topPanel, BorderLayout.NORTH);


		folder = new File("KEY");
		if(folder.isDirectory())
		{
			String[] filename = listFilesForFolder(folder);
			String[] filedescrption =listFilesDescrption(folder);
			JPanel listPanel = new JPanel();
			for(int i=0;i<filename.length;i++)
			{
				JLabel filenamelabel = new JLabel(filename[i]);
				JTextField filedesclabel = new JTextField(filedescrption[i]);

				//filedesclabel.setText(filedescrption[i]);
				filedesclabel.setEditable(false);
				JButton deleteButton = new JButton("Delete");
				deleteButton.setActionCommand(filename[i]);
				deleteButton.addActionListener(this);
				JPanel eachline = new JPanel();
				//eachline.setLayout(new GridLayout(1, 3));

				eachline.add(filenamelabel);
				eachline.add(filedesclabel);
				eachline.add(deleteButton);

				listPanel.add(eachline);
			}
			listPanel.setLayout(new SpringLayout());
			SpringUtilities.makeCompactGrid(listPanel,
					filename.length, 1, //rows, columns
					0, 0, //initX, initY
					1, 1);  

			scrollPane = new JScrollPane();
			scrollPane.setViewportView(listPanel);
			getContentPane().add(this.scrollPane, BorderLayout.CENTER);
		}
		JButton create = new JButton("Create new Key");
		JPanel createPanel = new JPanel();
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new KeyManagement(parent);
				dispose();
			}
		});
		createPanel.add(create);
		getContentPane().add(createPanel, BorderLayout.SOUTH);

		setLocationRelativeTo(parent);
		setSize(300,300);
		setMinimumSize(new Dimension(370, 300));
		setVisible(true);	
	}

	public String[] listFilesDescrption(final File folder) {
		ArrayList<String> filedescryption = new ArrayList<String>();

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if(!fileEntry.getName().equals(".DS_Store"))
				{
					JSONParser parser = new JSONParser();
					Object obj;
					try {
						obj = parser.parse(new FileReader(fileEntry));
						JSONObject jsonObject = (JSONObject) obj;
						String description = (String) jsonObject.get("description");
						filedescryption.add(description);
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		String[] filedescryptionOutput = new String[filedescryption.size()];
		for(int i=0;i<filedescryption.size();i++)
		{
			filedescryptionOutput[i]=filedescryption.get(i);
		}
		return filedescryptionOutput;
	}

	public String[] listFilesForFolder(final File folder) {
		int numofFiles = 0;
		ArrayList<String> filename = new ArrayList<String>();

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if(!fileEntry.getName().equals(".DS_Store"))
				{
					numofFiles++;
					filename.add(fileEntry.getName());
					//				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					BasicFileAttributes attr;
					try {
						attr = Files.readAttributes(fileEntry.toPath(),BasicFileAttributes.class);
						System.out.println("FileName: "+fileEntry.getName()+"  Created Time: "+attr.creationTime()+"  FileModified Time: "+attr.lastModifiedTime());

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		String[] filenameOutput = new String[filename.size()];
		for(int i=0;i<filename.size();i++)
		{
			filenameOutput[i]=filename.get(i);
		}
		return filenameOutput;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getActionCommand());
		try{

			File file = new File("KEY/"+e.getActionCommand());

			if(file.delete()){
				System.out.println(file.getName() + " is deleted!");
				refreshList();
			}else{
				System.out.println("Delete operation is failed.");
			}

		}catch(Exception error){
			error.printStackTrace();
		}
	}

	private void refreshList() {
		// TODO Auto-generated method stub
		scrollPane.getViewport().removeAll();
		revalidate();
		repaint();
		String[] filename = listFilesForFolder(folder);
		JPanel listPanel = new JPanel();
		for(int i=0;i<filename.length;i++)
		{
			JLabel filenamelabel = new JLabel(filename[i]);
			JButton deleteButton = new JButton("Delete");
			deleteButton.setActionCommand(filename[i]);
			deleteButton.addActionListener(this);
			JPanel eachline = new JPanel();
			eachline.add(filenamelabel);
			eachline.add(deleteButton);
			listPanel.add(eachline);
			//listPanel.setPreferredSize(new Dimension(370, 500));
		}
		listPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(listPanel,
				filename.length, 1, //rows, columns
				5, 5, //initX, initY
				5, 5);  

		scrollPane.setViewportView(listPanel);
		scrollPane.revalidate();
		scrollPane.repaint();


	}
}
