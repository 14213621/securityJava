package guilayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import keyEncrypt.AES;
import keyEncrypt.DesEncrypter;
import keyEncrypt.RSA;
import keyEncrypt.TripleDESTest;
import jpass.util.SpringUtilities;

public class KeyManagement  extends JDialog implements ActionListener {
	private final JPanel EmcryptMethodPanel;//DES,3DES,AES,RSA
	private final JPanel KeyPanel;
	private final JPanel ButtonPanel;
	private final JPanel topPanel;
	private final JPanel centerPanel;

	private  JTextField KeyNameFiled;
	String[] encryMethod = {"DES","AES","3DES","RSA"};
	JComboBox encryMethodBox = new JComboBox(encryMethod);
	
	JComboBox keylengthbox;
	private JTextField publicKeyName;
	private  JTextField privateKeyName;

	public KeyManagement(final PVSFrame parent)
	{
		topPanel = new JPanel();

		KeyPanel = new JPanel();

		
		publicKeyName = new JTextField();
		privateKeyName = new JTextField();

		KeyNameFiled = new JTextField();
		EmcryptMethodPanel = new JPanel();
		KeyNameFiled.setPreferredSize( new Dimension( 130, 20 ) );
		KeyPanel.add(new JLabel("Key Name:"));
		KeyPanel.add(KeyNameFiled);
		
		JLabel keylengthlabel = new JLabel("Key length(bit):");
		String[] keylength = {"56"};
		keylengthbox = new JComboBox(keylength);
		KeyPanel.add(keylengthlabel);
		KeyPanel.add(keylengthbox);
		KeyPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(KeyPanel,
				1, 4, //rows, columns
				5, 5, //initX, initY
				1, 1); 


		EmcryptMethodPanel.add(new JLabel("Encrytion Method:"));
		EmcryptMethodPanel.add(encryMethodBox);

		encryMethodBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//		System.out.println(encryMethodBox.getSelectedItem());
				if( (encryMethodBox.getSelectedItem()).equals("RSA"))
				{
					System.out.println("RSA");
					KeyPanel.removeAll();
					topPanel.removeAll();
					JLabel publickeyNameLabel = new JLabel("RSA Public Key Name:");
					publicKeyName = new JTextField();
					publicKeyName.setPreferredSize( new Dimension( 130, 20 ) );
					
					JPanel keylengthPanel = new JPanel();
					JLabel keylengthlabel = new JLabel("Key length(bit):");
					String[] keylength = {"1024","2048","4096"};
					keylengthbox = new JComboBox(keylength);
					keylengthPanel.add(keylengthlabel);
					keylengthPanel.add(keylengthbox);
					
					JPanel publicPanel = new JPanel();
					publicPanel.add(publickeyNameLabel);
					publicPanel.add(publicKeyName);

					JLabel privatekeyNameLabel = new JLabel("RSA Private Key Name:");
					privateKeyName = new JTextField();
					privateKeyName.setPreferredSize( new Dimension( 130, 20 ) );
					JPanel privatePanel = new JPanel();
					privatePanel.add(privatekeyNameLabel);
					privatePanel.add(privateKeyName);

					KeyPanel.add(keylengthPanel);
					KeyPanel.add(publicPanel);
					KeyPanel.add(privatePanel);
					//KeyPanel.setLayout(new GridLayout(2, 1));

					KeyPanel.setLayout(new SpringLayout());
					SpringUtilities.makeCompactGrid(KeyPanel,
							3, 1, //rows, columns
							5, 5, //initX, initY
							1, 1); 

					KeyPanel.revalidate();
					KeyPanel.repaint();

					topPanel.add(EmcryptMethodPanel);
					topPanel.add(KeyPanel);
					topPanel.setLayout(new SpringLayout());
					SpringUtilities.makeCompactGrid(topPanel,
							2, 1, //rows, columns
							5, 5, //initX, initY
							1, 1); 

					topPanel.revalidate();
					topPanel.repaint();
				}else
				{
					
					 String methodmode = (String) encryMethodBox.getSelectedItem();
					KeyPanel.removeAll();
					topPanel.removeAll();

					KeyNameFiled.setPreferredSize( new Dimension( 130, 20 ) );
					KeyPanel.add(new JLabel("Key Name:"));
					KeyPanel.add(KeyNameFiled);
					
					JLabel keylengthlabel = new JLabel("Key length(bit):");
					if (methodmode.equals("DES"))
					{
					String[] keylength = {"56"};
					keylengthbox = new JComboBox(keylength);
					}else if (methodmode.equals("3DES"))
					{
					String[] keylength = {"112","168"};
					keylengthbox = new JComboBox(keylength);
					}else if (methodmode.equals("AES"))
					{
					String[] keylength = {"128","192","256"};
					keylengthbox = new JComboBox(keylength);
					}
					KeyPanel.add(keylengthlabel);
					KeyPanel.add(keylengthbox);

					//					EmcryptMethodPanel.add(new JLabel("Encrytion Method:"));
					//					EmcryptMethodPanel.add(encryMethodBox);
					System.out.println("OTHERS");
					//	KeyPanel.setLayout(new GridLayout(2, 1));
					KeyPanel.setLayout(new SpringLayout());
					SpringUtilities.makeCompactGrid(KeyPanel,
							1, 4, //rows, columns
							5, 5, //initX, initY
							1, 1); 

					topPanel.add(EmcryptMethodPanel);
					topPanel.add(KeyPanel);
					topPanel.setLayout(new SpringLayout());
					SpringUtilities.makeCompactGrid(topPanel,
							2, 1, //rows, columns
							5, 5, //initX, initY
							1, 1); 
					topPanel.revalidate();
					topPanel.repaint();

				}
			}
		});


		topPanel.add(this.EmcryptMethodPanel);
		topPanel.add(this.KeyPanel);
		this.topPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this.topPanel,
				2, 1, //rows, columns
				5, 5, //initX, initY
				1, 1); 

		getContentPane().add(this.topPanel, BorderLayout.NORTH);
		//getContentPane().add(this.KeyPanel, BorderLayout.CENTER);
		centerPanel = new JPanel();

		JPanel Descriptionpaenl = new JPanel();
		JLabel description = new JLabel("Description of This Key:");
		Descriptionpaenl.add(description);

		JPanel desAreaPanel = new JPanel();
		desAreaPanel.setPreferredSize(new Dimension(300,200));
		JTextArea descriptionArea = new JTextArea();
		descriptionArea.setSize(new Dimension(300,200));
		descriptionArea.setFont(TextComponentFactory.newTextField().getFont());
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		desAreaPanel.add(descriptionArea);

		centerPanel.add(Descriptionpaenl);
		centerPanel.add(desAreaPanel);
		this.centerPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this.centerPanel,
				2, 1, //rows, columns
				5, 5, //initX, initY
				1, 1); 
		getContentPane().add(this.centerPanel, BorderLayout.CENTER);


		ButtonPanel = new JPanel();
		JButton save = new JButton("Save this key");
		ButtonPanel.add(save);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				if(KeyNameFiled.getText().equals("")&&publicKeyName.getText().equals(""))
				{
					System.out.println("No Name");
					MessageDialog.showWarningMessage(parent, "Pleases Insert Key Name");
				}else
				{
					System.out.println("Method: "+ encryMethodBox.getSelectedItem());

					System.out.println("Encrypt Key and Save");
					switch ((encryMethodBox.getSelectedItem().toString()))
					{
					case "DES":
						try {

							//new DES( keyFiled.getText(),KeyNameFiled.getText());
							KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
							//Init 128,192,256
							String lengthString = (String)keylengthbox.getSelectedItem();
							int length = Integer.parseInt(lengthString);
							keyGenerator.init(length);
							SecretKey secretKey= keyGenerator.generateKey();
							
							DesEncrypter DESKEY = new DesEncrypter(secretKey,parent);
							DESKEY.saveKey(KeyNameFiled.getText(),descriptionArea.getText());
							dispose();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;

					case "AES":
						System.out.println("Create AES KEY");
						try {
							KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
							//Init 128,192,256
							String lengthString = (String)keylengthbox.getSelectedItem();
							int length = Integer.parseInt(lengthString);
							System.out.println("KeylengthBox:"+length);

							keyGenerator.init(length);
							SecretKey secretKey= keyGenerator.generateKey();
							AES AESKEY = new AES(secretKey,parent);
							AESKEY.saveKey(KeyNameFiled.getText(),descriptionArea.getText());
							dispose();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;

					case "3DES":
						System.out.println("Create 3DES KEY");
						try {
							KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
							// keysize must be equal to 112 or 168 for this provider
							String lengthString = (String)keylengthbox.getSelectedItem();
							int length = Integer.parseInt(lengthString);
							System.out.println(length);
							keyGenerator.init(length);
							
							SecretKey secretKey= keyGenerator.generateKey();
							TripleDESTest TDES = new TripleDESTest(secretKey);
							TDES.saveKey(KeyNameFiled.getText(),descriptionArea.getText());
							dispose();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case "RSA":
						System.out.println("Create RSA KEY");
						RSA rsa = new RSA();
						try {
							String lengthString = (String)keylengthbox.getSelectedItem();
							int length = Integer.parseInt(lengthString);
							System.out.println(length);
							rsa.generateKeyPair(length);
							rsa.SaveKeyPair(publicKeyName.getText(), privateKeyName.getText(),descriptionArea.getText());
							dispose();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					}
				}
			}
		});
		getContentPane().add(this.ButtonPanel, BorderLayout.SOUTH);

		setLocationRelativeTo(parent);
		setSize(300,300);
		setMinimumSize(new Dimension(370, 300));
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
