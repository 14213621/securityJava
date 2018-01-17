package guilayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import jpass.util.SpringUtilities;
import keyEncrypt.AES;
import keyEncrypt.DesEncrypter;
import keyEncrypt.RSA;
import keyEncrypt.TripleDESTest;

public class encryptFrame extends JDialog implements ActionListener{

	private final JPanel fieldPanel;
	private final JPanel dataPanel;

	private final JPanel choseFilePanel;
	private final JPanel EmcryptMethodPanel;//DES,3DES,AES,RSA
	private final JPanel EmcryptModePanel; //
	private final JPanel ButtonPanel; //
	private String absoluteFilePath;
	private String sharedfolder;
	private JTextField encryptedFilednameField;
	String encryptedData;
	File file;
	File keyfile;
	private JTextField tf;
	String display = null;
	String encryptKeyMethod = null;
	String encryptfileMode = "ECB";
	String IVString = "";
	//  String[] encryMethod = {"DES","AES","3DES","RSA"};
	//  JComboBox encryMethodBox = new JComboBox(encryMethod);

	String[] encryMode = {"ECB","CBC","CFB","OFB","CTR"};
	JComboBox encryModBox = new JComboBox(encryMode);

	JFileChooser fc;
	private PVSFrame parent;
	private JTextArea dataArea;

	public encryptFrame(final PVSFrame parent)
	{
		file = null;
		keyfile = null;
		this.parent = parent;
		fc = new JFileChooser("./INITIALFILE/");

		this.fieldPanel = new JPanel();
		choseFilePanel = new JPanel();
		choseFilePanel.add(new JLabel("Choose file:"));
		tf = new JTextField();
		tf.setPreferredSize(new Dimension(100, 25 ) );
		tf.setEditable(false);
		choseFilePanel.add(tf);

		this.dataPanel = new JPanel(new BorderLayout(5, 5));



		dataArea = new JTextArea();
		dataArea.setEditable(false);

		dataArea.setSize(new Dimension(200,50));
		dataArea.setFont(TextComponentFactory.newTextField().getFont());
		dataArea.setLineWrap(true);
		dataArea.setWrapStyleWord(true);
		this.dataPanel.add(new JScrollPane(dataArea));

		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				tf.setText("");

				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					// System.out.println("getCurrentDirectory(): " +
					// fc.getCurrentDirectory());
					System.out.println("getSelectedFile() : " + fc.getSelectedFile());
					file = fc.getSelectedFile();
					tf.setText(file.getName());

					FileReader freader;
					try {
						freader = new FileReader(file);
						BufferedReader br = new BufferedReader(freader);  
						String s;  
						ArrayList<String> inputData = new ArrayList<String>();
						display = null;
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
						dataArea.setText(display);
						freader.close();  
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  

				}

			}
		});
		choseFilePanel.add(browse);
		//	    


		EmcryptModePanel = new JPanel();
		EmcryptModePanel.add(new JLabel("Mode:"));
		EmcryptModePanel.add(encryModBox);

		encryModBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				encryptfileMode = encryModBox.getSelectedItem().toString();
				System.out.println("Mode Changed to be:"+encryptfileMode);
				if(keyfile !=null && (file !=null))
				{
					System.out.println("Has key and data do encrypt");
					try {
						encryptFiledata();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else
				{
					System.out.println("No key or datafile");
				}
			}
		});

		EmcryptMethodPanel = new JPanel();
		EmcryptMethodPanel.add(new JLabel("Choose Key File:"));
		JTextField keyfield = new JTextField();
		keyfield.setPreferredSize(new Dimension(100, 25 ) );
		keyfield.setEditable(false);
		EmcryptMethodPanel.add(keyfield);
		JButton browsekey = new JButton("Browse");
		browsekey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(file == null)
				{
					dataArea.setForeground(Color.RED);
					dataArea.setText("Pleases choose one data file first");
				}else
				{
					JFileChooser keyfc = new JFileChooser("./Key/");


					keyfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

					if (keyfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

						EmcryptModePanel.add(new JLabel("Mode:"));
						EmcryptModePanel.add(encryModBox);
						EmcryptModePanel.revalidate();
						EmcryptModePanel.repaint();
						fieldPanel.setLayout(new SpringLayout());
						SpringUtilities.makeCompactGrid(fieldPanel,
								3, 1, //rows, columns
								5, 5, //initX, initY
								5, 5);  
						fieldPanel.revalidate();
						fieldPanel.repaint();
						// System.out.println("getCurrentDirectory(): " +
						// fc.getCurrentDirectory());
						System.out.println("getSelectedFile() : " + keyfc.getSelectedFile());
						keyfile = keyfc.getSelectedFile();
						keyfield.setText(keyfile.getName());
						try {
							//keyreader = new FileReader(keyfile);
							//						Path fileLocation = Paths.get(keyfile.getAbsolutePath());
							JSONParser parser = new JSONParser();
							Object obj;
							try {
								obj = parser.parse(new FileReader(keyfile));
								JSONObject jsonObject = (JSONObject) obj;
								String description = (String) jsonObject.get("description");
								String key = (String) jsonObject.get("key");
								System.out.println("Key:" + key);

								byte[] keybyte = Base64.getDecoder().decode(key);
								System.out.println(keybyte.length);
								if (keyfc.getSelectedFile().getAbsolutePath().endsWith(".des")){ //DES ENCRYPT
									SecretKey KEY = new SecretKeySpec(keybyte, "DES");
									System.out.println(KEY);
									try {
										DesEncrypter encrypt = new DesEncrypter(KEY,parent);
										encrypt.setMode(encryModBox.getSelectedItem().toString(),"encrypt");
										IVString = encrypt.getIV();
										encryptedData = encrypt.encrypt(display);
										System.out.println(encryptedData);
										encryptKeyMethod="des";
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}else if (keyfc.getSelectedFile().getAbsolutePath().endsWith(".aes"))  //AES ENCRYPT
								{
									SecretKey KEY = new SecretKeySpec(keybyte, "AES");
									System.out.println(KEY);
									try {
										AES encrypt = new AES(KEY,parent);
										encrypt.setMode(encryModBox.getSelectedItem().toString(),"encrypt");
										IVString = encrypt.getIV();
										System.out.println(IVString);
										encryptedData = encrypt.encrypt(display);
										System.out.println(encryptedData);
										encryptKeyMethod="aes";

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}else if (keyfc.getSelectedFile().getAbsolutePath().endsWith(".desede"))
								{
									//SecretKey KEY = new SecretKeySpec(keybyte, "DESede");
									encryptKeyMethod="desede";
									SecretKey KEY;
									try {
										KEY = TripleDESTest.readKey(keybyte);
										try {
											TripleDESTest encrypt = new TripleDESTest(KEY);
											encrypt.setMode(encryModBox.getSelectedItem().toString(),"encrypt");
											IVString = encrypt.getIV();
											encryptedData = encrypt.encrypt(display);
											System.out.println(encryptedData);
											encryptKeyMethod="desede";
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

								}else if  (keyfc.getSelectedFile().getAbsolutePath().endsWith(".rsa"))
								{
									System.out.println("It's rsa key");
									
									EmcryptModePanel.removeAll();
									EmcryptModePanel.revalidate();
									EmcryptModePanel.repaint();
									fieldPanel.setLayout(new SpringLayout());
									SpringUtilities.makeCompactGrid(fieldPanel,
											2, 1, //rows, columns
											5, 5, //initX, initY
											5, 5);  
									fieldPanel.revalidate();
									fieldPanel.repaint();
									
									RSA rsa = new RSA();
									rsa.LoadPrivateKey(keyfile);
								}else
								{
									System.out.println("Fail key");
								}
								dataArea.setForeground(Color.BLUE);
								dataArea.setText(encryptedData);
							} catch (ParseException | NoSuchAlgorithmException | InvalidKeySpecException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
		});
		EmcryptMethodPanel.add(browsekey);
		//		this.EmcryptModePanel.add(EmcryptMethodPanel);
		//getContentPane().add(this.EmcryptMethodPanel);

		getContentPane().add(this.dataPanel, BorderLayout.CENTER);

		//		JButton Generate =new JButton("Generate Key");
		//		Generate.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent event) {
		//				System.out.println("Generate a key");
		//		      //  new GeneratePasswordDialog(parent.getInstance());
		//			}
		//		});
		//		KeyPanel.add(Generate);



		ButtonPanel = new JPanel();

		JButton Emcrpt = new JButton("Emcrpt");
		Emcrpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//System.out.println("Method: "+ encryMethodBox.getSelectedItem());
				System.out.println("Mode: "+ encryModBox.getSelectedItem());
				System.out.println("encryptedFilednameField.getText():"+encryptedFilednameField.getText());
				if (encryptedFilednameField.getText().equals(""))
				{
					dataArea.setForeground(Color.RED);
					dataArea.setText("Pleases input the name of new encrypted file !!");
				}else if (file == null){
					dataArea.setForeground(Color.RED);
					dataArea.setText("Pleases choose a data file to encrypt !!");
				}else if (keyfile == null){
					dataArea.setForeground(Color.RED);
					dataArea.setText("Pleases choose a key file to use to encrypt !!");
				}
				else {
					System.out.println("Encrypt File");
					File newfile = new File("./ENCRPTED/"+encryptedFilednameField.getText()+"."+encryptKeyMethod);
					if (newfile.getParentFile().mkdir()) {
						try {
							newfile.createNewFile();
							System.out.println("new dir");

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						System.out.println("exited");
					}

					JSONObject obj = new JSONObject();
					System.out.println("IVString: "+IVString);
					System.out.println("encryptKeyMethod: "+encryptKeyMethod);

						System.out.println("encryptedData: "+encryptedData);

						obj.put("encryptedData", encryptedData);
					
					obj.put("IV",IVString);
					obj.put("MODE",encryModBox.getSelectedItem().toString());


					try (FileWriter writer = new FileWriter("./ENCRPTED/"+encryptedFilednameField.getText()+"."+encryptKeyMethod))
					{
						writer.write(obj.toString());
						writer.flush();
					}catch (IOException ex)
					{
						ex.printStackTrace();
					}
					System.out.println(obj);
					MessageDialog.showInformationMessage(parent, "Successfully encrypted");
				}
			}
		});

		JPanel encryptedFilePanel = new JPanel();
		encryptedFilednameField = new JTextField();
		encryptedFilednameField.setPreferredSize(new Dimension(150,30));
		JLabel encryptFiledNameLabel = new JLabel("Ecrypted File Name: ");
		encryptedFilePanel.add(encryptFiledNameLabel);
		encryptedFilePanel.add(encryptedFilednameField);
		ButtonPanel.add(encryptedFilePanel);
		ButtonPanel.add(Emcrpt);
		this.ButtonPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this.ButtonPanel,
				2, 1, //rows, columns
				5, 5, //initX, initY
				5, 5);  

		EmcryptModePanel.revalidate();
		EmcryptModePanel.repaint();
		
		fieldPanel.add(choseFilePanel);
		fieldPanel.add(EmcryptMethodPanel);
		//fieldPanel.add(KeyPanel);
		fieldPanel.add(EmcryptModePanel);

		this.fieldPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this.fieldPanel,
				3, 1, //rows, columns
				5, 5, //initX, initY
				5, 5);  


		getContentPane().add(this.fieldPanel, BorderLayout.NORTH);
		getContentPane().add(this.ButtonPanel, BorderLayout.SOUTH);

		setLocationRelativeTo(parent);
		setSize(300,300);
		setMinimumSize(new Dimension(370, 500));
		setVisible(true);

	}

	private void encryptFiledata() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new FileReader(keyfile));
			JSONObject jsonObject = (JSONObject) obj;
			String description = (String) jsonObject.get("description");
			String key = (String) jsonObject.get("key");
			System.out.println("Key:" + key);

			byte[] keybyte = Base64.getDecoder().decode(key);
			System.out.println(keybyte.length);
			if (keyfile.getAbsolutePath().endsWith(".des")){ //DES ENCRYPT
				System.out.println("Using DES KEY");
				SecretKey KEY = new SecretKeySpec(keybyte, "DES");
				try {
					DesEncrypter encrypt = new DesEncrypter(KEY,parent);
					encrypt.setMode(encryModBox.getSelectedItem().toString(),"encrypt");
					IVString = encrypt.getIV();
					encryptedData = encrypt.encrypt(display);
					System.out.println(encryptedData);
					encryptKeyMethod="des";
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (keyfile.getAbsolutePath().endsWith(".aes"))  //AES ENCRYPT
			{
				SecretKey KEY = new SecretKeySpec(keybyte, "AES");
				System.out.println("Using AES KEY");
				try {
					AES encrypt = new AES(KEY,parent);
					encrypt.setMode(encryModBox.getSelectedItem().toString(),"encrypt");
					IVString = encrypt.getIV();
					System.out.println(IVString);
					encryptedData = encrypt.encrypt(display);
					System.out.println(encryptedData);
					encryptKeyMethod="aes";

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (keyfile.getAbsolutePath().endsWith(".desede"))
			{
				System.out.println("Using 3DES KEY");
				encryptKeyMethod="desede";

				//SecretKey KEY = new SecretKeySpec(keybyte, "DESede");
				SecretKey KEY;
				try {
					KEY = TripleDESTest.readKey(keybyte);
					try {
						TripleDESTest encrypt = new TripleDESTest(KEY);
						encrypt.setMode(encryModBox.getSelectedItem().toString(),"encrypt");
						IVString = encrypt.getIV();
						encryptedData = encrypt.encrypt(display);
						System.out.println(encryptedData);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else if (keyfile.getAbsolutePath().endsWith(".rsa"))
			{
				System.out.println("Using RSA KEY");
				encryptKeyMethod="rsa";
			}
			dataArea.setForeground(Color.BLUE);
			dataArea.setText(encryptedData);


		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
