package guilayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import jpass.util.SpringUtilities;
import keyEncrypt.RSA;

public class RsaFrame  extends JDialog{
	PVSFrame parent;
	PublicKey publicKey;
	PrivateKey privateKey;
	String[] hashMethod = {"MD5","SHA1"};
	JComboBox hashBox = new JComboBox(hashMethod);
	JPanel abovePanel = new JPanel();
	JTextArea dataArea = new JTextArea();
	String hmode = "MD5";
	String display = null;
	String signfileName = null;
	JTextField newfilename = new JTextField();
	JPanel operationPanel = new JPanel();
	String[] operation = {"Generating the Digital Signature","Verify the Digital Signature"};
	JComboBox operatioBox = new JComboBox(operation);
	JPanel publicKeypanel = new JPanel();
	JPanel privateKeypanel = new JPanel();
	JPanel modepanel = new JPanel();
	JPanel specifyFilePanel = new JPanel();
	JLabel hash = new JLabel("Cryptographic Hash Mode:");
	JPanel buttonPanel = new JPanel();
	JTextField signfilename = new JTextField();
	JButton  generate = new JButton("Generating the Digital Signature");
	JButton  verify = new JButton("Verify the Digital Signature");
	String origiData = null;
	String signature = null;
	File origifile;
	File signfile;
	JTextField privateKeyfilefield = new JTextField();
	JButton privateKeybrowse = new JButton("Brose");


	public RsaFrame(final PVSFrame parent)
	{
		this.parent = parent;
		signfile=null;
		origifile=null;
		dataArea.setEditable(false);
		dataArea.setLineWrap(true);
		dataArea.setWrapStyleWord(true);

		operationPanel.add(new JLabel("Choose one operation"));
		operationPanel.add(operatioBox);
		operatioBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (operatioBox.getSelectedItem().equals("Generating the Digital Signature"))
				{
					modepanel.removeAll();
					modepanel.add(hash);
					modepanel.add(hashBox);
					modepanel.add(new JLabel("New File Name: "));
					modepanel.add(newfilename);
					modepanel.revalidate();
					modepanel.repaint();

					privateKeypanel.removeAll();
					privateKeypanel.add(new JLabel("Private Key File: "));
					privateKeypanel.add(privateKeyfilefield);
					privateKeypanel.add(privateKeybrowse);
					privateKeypanel.revalidate();
					privateKeypanel.repaint();
					
					buttonPanel.removeAll();
					buttonPanel.add(generate);
					buttonPanel.revalidate();
					buttonPanel.repaint();
					
					abovePanel.setLayout(new SpringLayout());
					SpringUtilities.makeCompactGrid(abovePanel,
							5, 1, //rows, columns
							5, 5, //initX, initY
							1, 1);
				}else
				{
//					specifyFilePanel.removeAll();
//					specifyFilePanel.revalidate();
//					specifyFilePanel.repaint();
					modepanel.removeAll();
					signfilename.setPreferredSize(new Dimension(180, 25 ) );
					JButton signfileButton = new JButton("Browse");
					signfileButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							JFileChooser signatureFc = new JFileChooser("./signature/");
							signatureFc.setFileSelectionMode(JFileChooser.FILES_ONLY);
							if (signatureFc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								signfile = signatureFc.getSelectedFile();
								JSONParser parser = new JSONParser();
								Object obj;
								try {
									signfilename.setText(signfile.getName());
									obj = parser.parse(new FileReader(signfile));
									JSONObject jsonObject = (JSONObject) obj;
									signature = (String) jsonObject.get("signature");
									hmode = (String) jsonObject.get("hmode");
									boolean check = false;
									RSA rsa = new RSA();
									 check = rsa.verify(origiData, signature, publicKey, hmode);
									 if(check == true)
									 {
										dataArea.setForeground(Color.BLUE);
										 dataArea.setText("File is not modified by others");
									 }else
									 {
											dataArea.setForeground(Color.RED);
										 dataArea.setText("File may be modified by others");
									 }
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					});
					buttonPanel.removeAll();
					buttonPanel.add(verify);
					buttonPanel.revalidate();
					buttonPanel.repaint();
					
					privateKeypanel.removeAll();
					
					modepanel.add(new JLabel("Signature File:"));
					modepanel.add(signfilename);
					modepanel.add(signfileButton);
					modepanel.revalidate();
					modepanel.repaint();
					abovePanel.setLayout(new SpringLayout());
					SpringUtilities.makeCompactGrid(abovePanel,
							5, 1, //rows, columns
							5, 5, //initX, initY
							1, 1);
				}
			}
		});

		JTextField publicKeyfilefield = new JTextField();
		publicKeyfilefield.setEditable(false);
		publicKeyfilefield.setPreferredSize(new Dimension(180, 25 ) );

		JButton publicKeybrowse = new JButton("Brose");
		publicKeybrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JFileChooser publickeyfc = new JFileChooser("./Key/RSA/");
				publickeyfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (publickeyfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					try {
						publicKey = new RSA().LoadPublicKey(publickeyfc.getSelectedFile());
						publicKeyfilefield.setText(publickeyfc.getSelectedFile().getName());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		publicKeypanel.add(new JLabel("Public Key File: "));
		publicKeypanel.add(publicKeyfilefield);
		publicKeypanel.add(publicKeybrowse);



		privateKeyfilefield.setEditable(false);
		privateKeyfilefield.setPreferredSize(new Dimension(180, 25 ) );


		privateKeybrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JFileChooser privatekeyfc = new JFileChooser("./Key/RSA/");
				privatekeyfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (privatekeyfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					try {
						privateKey = new RSA().LoadPrivateKey(privatekeyfc.getSelectedFile());
						privateKeyfilefield.setText(privatekeyfc.getSelectedFile().getName());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		privateKeypanel.add(new JLabel("Private Key File: "));

		privateKeypanel.add(privateKeyfilefield);
		privateKeypanel.add(privateKeybrowse);


		newfilename.setPreferredSize(new Dimension(130, 25 ) );
		modepanel.add(hash);
		modepanel.add(hashBox);
		modepanel.add(new JLabel("New File Name: "));
		modepanel.add(newfilename);

		hashBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				hmode = (String)hashBox.getSelectedItem();
			}
		});


		JTextField specifyFilefield = new JTextField();
		specifyFilefield.setEditable(false);
		specifyFilefield.setPreferredSize(new Dimension(180, 25 ) );
		JButton specifyFilebutton = new JButton("Brose");
		specifyFilePanel.add(new JLabel("Original File: "));
		specifyFilePanel.add(specifyFilefield);
		specifyFilePanel.add(specifyFilebutton);
		specifyFilebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JFileChooser privatekeyfc = new JFileChooser("./INITIALFILE/");
				privatekeyfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (privatekeyfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					dataArea.setText("");
					origifile = privatekeyfc.getSelectedFile();
					specifyFilefield.setText(origifile.getName());

					FileReader freader =null;
					try {
						freader = new FileReader(origifile);
						BufferedReader br = new BufferedReader(freader);  
						String s;  
						ArrayList<String> inputData = new ArrayList<String>();
						while((s = br.readLine()) != null) {  
							inputData.add(s);
						}

						for(int i = 0; i<inputData.size();i++)
						{
							if (display != null)
							{
								String olddisplay = display;
								display = olddisplay+"\n"+inputData.get(i);
							}else
							{
								display = inputData.get(i);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally
					{
						try {
							freader.close();
							dataArea.setForeground(Color.BLUE);
							dataArea.setText(display);
							origiData=display;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
					}
				}
			}
		});
		abovePanel.add(operationPanel);
		abovePanel.add(publicKeypanel);
		abovePanel.add(privateKeypanel);
		abovePanel.add(specifyFilePanel);

		abovePanel.add(modepanel);
		abovePanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(abovePanel,
				5, 1, //rows, columns
				5, 5, //initX, initY
				1, 1);



		

		buttonPanel.add(generate);

		getContentPane().add(this.abovePanel, BorderLayout.NORTH);
		getContentPane().add(dataArea, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		generate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				signfileName =newfilename.getText();
				if (!signfileName.equals(""))
				{
					RSA rsa = new RSA();
					try {
						rsa.sign(hmode, display, privateKey, signfileName);
						MessageDialog.showInformationMessage(parent, "Successfully generated a signature file");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Sign failed");
						MessageDialog.showErrorMessage(parent, "Failed to generate a signature file");
					}
				}else
				{
					MessageDialog.showWarningMessage(parent, "Please input new filename");
				}
			}
		});

		setLocationRelativeTo(parent);
		setSize(300,300);
		setMinimumSize(new Dimension(600, 350));
		setVisible(true);


	}
}
