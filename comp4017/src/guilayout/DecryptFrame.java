package guilayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.print.attribute.AttributeSet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import jpass.util.SpringUtilities;
import keyEncrypt.AES;
import keyEncrypt.DesEncrypter;
import keyEncrypt.TripleDESTest;

import org.json.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class DecryptFrame  extends JDialog implements ActionListener {
	private final JPanel choseFilePanel;
	private final JPanel choseKeyPanel; //
	private final JPanel dataPanel;
	private final JPanel fieldPanel;

	String readyDecrptData;
	JFileChooser keyfc;
	boolean DecryptSuccess = false;

	File keyfile;
	File datafile;
	byte[] bytesArray = null;
	String IVString = "";
	String dataMode = "";

	public DecryptFrame(final PVSFrame parent)
	{
		datafile = null;
		keyfile = null;

		JTextField keyFileName = new JTextField();

		this.fieldPanel = new JPanel();

		this.dataPanel = new JPanel(new BorderLayout(5, 5));

		JTextArea dataArea = new JTextArea();
		dataArea.setEditable(false);
		dataArea.setSize(new Dimension(200,200));
		dataArea.setFont(TextComponentFactory.newTextField().getFont());
		dataArea.setLineWrap(true);
		dataArea.setWrapStyleWord(true);
		dataPanel.add(new JScrollPane(dataArea));

		choseFilePanel = new JPanel();
		choseFilePanel.add(new JLabel("Choose file:"));
		JTextField decrptFileName = new JTextField();
		decrptFileName.setPreferredSize(new Dimension(100, 25 ));
		decrptFileName.setEditable(false);
		choseFilePanel.add(decrptFileName);
		JButton BrowseFile = new JButton("Browse a Data File");
		choseFilePanel.add(BrowseFile);
		BrowseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				keyFileName.setText("");
				JFileChooser fc = new JFileChooser("./ENCRPTED/");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//				FileInputStream is = null;

				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					System.out.println("getSelectedFile() : " + fc.getSelectedFile());

					datafile = fc.getSelectedFile();
					decrptFileName.setText(datafile.getName());
					JSONParser parser = new JSONParser();
					Object obj;
					try {
						obj = parser.parse(new FileReader(datafile));
						JSONObject jsonObject = (JSONObject) obj;
						System.out.println("Received object:" +jsonObject);
						//						byte[] objecybyte = toByteArray(jsonObject.get("encryptedData"));
						//						String encrypteddata = new String(objecybyte);
						String encrypteddata = (String)jsonObject.get("encryptedData");
						//String encrypteddata = fixToNewline(""+jsonObject.get("encryptedData"));
						IVString = (String) jsonObject.get("IV");
						dataMode = (String) jsonObject.get("MODE");
						System.out.println("encrypteddata:"+encrypteddata);

						System.out.println("dataMode:" +dataMode);
						System.out.println("IVString:"+IVString);
						System.out.println("IVString.getBytes().length :"+IVString.getBytes("ISO-8859-1").length);
						dataArea.setForeground(Color.ORANGE);
						dataArea.setText(encrypteddata);

						StringWriter out = new StringWriter();
						jsonObject.writeJSONString(out);

						String jsonText = out.toString();
						System.out.print(jsonText);


						readyDecrptData = encrypteddata;
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						dataArea.setForeground(Color.RED);
						dataArea.setText("Wrong Key");
					}
				}
			}
		});

		choseKeyPanel = new JPanel();
		choseKeyPanel.add(new JLabel("Choose key:"));
		keyFileName.setPreferredSize(new Dimension(100, 25 ));
		keyFileName.setEditable(false);
		choseKeyPanel.add(keyFileName);

		JButton BrowseKey = new JButton("Browse a Key File");
		choseKeyPanel.add(BrowseKey);

		BrowseKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(datafile == null)
				{
					dataArea.setForeground(Color.RED);
					dataArea.setText("Pleases choose one data file first");
				}else
				{
					//tf.setText("");
					keyfc = new JFileChooser("./KEY/");

					keyfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

					if (keyfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						keyfile = keyfc.getSelectedFile();
						keyFileName.setText(keyfile.getName());
						System.out.println(keyfile.getName());
						FileReader keyreader;
						Font font = new Font("Segoe Script", Font.BOLD, 20);
						dataArea.setFont(font);
						try {

							//
							JSONParser parser = new JSONParser();
							try {
								Object obj =parser.parse(new FileReader(keyfile));
								JSONObject jsonObject = (JSONObject) obj;
								String description = (String) jsonObject.get("description");
								String key = (String) jsonObject.get("key");
								byte[] decodedKey = Base64.getDecoder().decode(key);

								if (checkCorrectMethod(datafile,keyfile)) {
									if (keyfile.getAbsolutePath().endsWith(".des")){
										System.out.println("Using des decrept");  
										SecretKey DESKEY = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES"); 
										try {
											DesEncrypter decrpt = new DesEncrypter(DESKEY,parent);
											decrpt.setMode(dataMode, IVString);
											String decrpteddata = decrpt.decrypt(readyDecrptData);
											System.out.println("decrpteddata: "+decrpteddata);
											System.out.println(decrpt.decrptSuccess());
											if (decrpt.decrptSuccess()==true)
											{
												dataArea.setForeground(Color.BLUE);
												dataArea.setText(decrpteddata);
												DecryptSuccess=true;
											}
											else
											{
												DecryptSuccess=false;
												dataArea.setForeground(Color.RED);
												dataArea.setText("Wrong Key!!!");
											}

										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}else if (keyfile.getAbsolutePath().endsWith(".aes")){
										System.out.println("Using aes decrept");  
										SecretKey DESKEY = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
										try {
											AES decrpt = new AES(DESKEY,parent);
											decrpt.setMode(dataMode, IVString);
											String decrpteddata = decrpt.decrypt(readyDecrptData);
											System.out.println("decrpteddata: "+decrpteddata);
											System.out.println(decrpt.decrptSuccess());
											if (decrpt.decrptSuccess()==true)
											{
												dataArea.setForeground(Color.BLUE);
												dataArea.setText(decrpteddata);
												DecryptSuccess=true;
											}
											else
											{
												DecryptSuccess=false;
												dataArea.setForeground(Color.RED);
												dataArea.setText("Wrong Key!!!");
											}
											if (DecryptSuccess=false)
											{
												dataArea.setForeground(Color.RED);
												dataArea.setText("Wrong Key!!!");
											}
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}else if (keyfile.getAbsolutePath().endsWith(".desede")){
										System.out.println("Using 3DES decrept");  
										//									SecretKey KEY = new SecretKeySpec(decodedKey, "DESede");
										SecretKey KEY;
										try {
											KEY = TripleDESTest.readKey(decodedKey);
											try {
												TripleDESTest decrpt = new TripleDESTest(KEY);
												decrpt.setMode(dataMode, IVString);

												String decrpteddata =  decrpt.decrypt(readyDecrptData);
												System.out.println("decrpteddata: "+decrpteddata);
												System.out.println(decrpt.decrptSuccess());
												if (decrpt.decrptSuccess()==true)
												{
													DecryptSuccess=true;
													dataArea.setForeground(Color.BLUE);
													dataArea.setText(decrpteddata);
												}
												else
												{
													DecryptSuccess=false;
													dataArea.setForeground(Color.RED);
													dataArea.setText("Wrong Key!!!");
												}
												
												if (DecryptSuccess=false)
												{
													dataArea.setForeground(Color.RED);
													dataArea.setText("Wrong Key!!!");
												}
											} catch (Exception e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
												dataArea.setForeground(Color.RED);
												dataArea.setText("Wrong Key!!!");
											}

										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}

								}else
								{
									dataArea.setForeground(Color.RED);
									dataArea.setText("Wrong Key!!!");
								}
							} catch (ParseException e1) {
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
				if (DecryptSuccess=false)
				{
					dataArea.setForeground(Color.RED);
					dataArea.setText("Wrong Key!!!");
				}
			}
		});


		fieldPanel.add(choseFilePanel);
		fieldPanel.add(choseKeyPanel);
		this.fieldPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this.fieldPanel,
				2, 1, //rows, columns
				5, 5, //initX, initY
				5, 5);  
		getContentPane().add(this.fieldPanel, BorderLayout.NORTH);

		getContentPane().add(dataPanel, BorderLayout.CENTER);

		setLocationRelativeTo(parent);
		setSize(300,300);
		setMinimumSize(new Dimension(370, 300));
		setVisible(true);


	}

	public static byte[] toByteArray(Object obj) throws IOException {
		byte[] bytes = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
		} finally {
			if (oos != null) {
				oos.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
		return bytes;
	}

	protected boolean checkCorrectMethod(File inputdatafile, File inputkeyfile) {
		// TODO Auto-generated method stub
		String inputdatafileName = inputdatafile.getName();
		String inputdataExs =null;
		if(inputdatafileName.lastIndexOf(".") != -1 && inputdatafileName.lastIndexOf(".") != 0)
		{
			inputdataExs= inputdatafileName.substring(inputdatafileName.lastIndexOf(".")+1);
		}
		String inputkeyExs =null;
		String inputkeyfileName = inputkeyfile.getName();

		if(inputkeyfileName.lastIndexOf(".") != -1 && inputkeyfileName.lastIndexOf(".") != 0)
		{
			inputkeyExs= inputkeyfileName.substring(inputkeyfileName.lastIndexOf(".")+1);
		}
		if(inputdataExs.equals(inputkeyExs))
		{
			return true;
		}

		return false;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
