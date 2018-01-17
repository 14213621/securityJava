package guilayout;

import java.awt.EventQueue;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

//import guiaction.MenuActionType;

import datahandle.DataModel;
//import guilayout.StatusPanel;
import guiaction.*;
import guiaction.MenuActionType;
import guilayout.SearchPanel;
import guilayout.StatusPanel;
import jpass.ui.helper.FileHelper;
import jpass.util.SpringUtilities;

public class PVSFrame extends JFrame{
	private static volatile PVSFrame INSTANCE;
	private JFrame frame;
	private final JMenu fileMenu;

	private final JMenu editMenu;
	private final JMenu toolsMenu;
	// private final JMenu encrptMenu;

	private final JPopupMenu popup;
	private final SearchPanel searchPanel;
	private final StatusPanel statusPanel;
	private final JPanel buttonContainerPanel;

	private JPanel topContainerPanel;
	private JMenuBar menuBar;
	private final JList entryTitleList;
	private final JScrollPane scrollPane;
	private final DefaultListModel entryTitleListModel;
	private final DataModel model = DataModel.getInstance();
	public static final String PROGRAM_NAME = "COMP4017";
	private volatile boolean processing = false;

	public String eMethod = "";
	//  String[] encryMethod = {"AES","DES","Method three","Method four","Method five"};
	//  JComboBox encryBox = new JComboBox(encryMethod);
	/**
	 * Launch the application.
	 */
	/**
	 * Create the application.
	 */
	public PVSFrame(String fileName) {
		this.popup = new JPopupMenu();
		this.topContainerPanel = new JPanel(new BorderLayout());
		this.buttonContainerPanel = new JPanel(new BorderLayout());
		this.buttonContainerPanel.setLayout(new GridLayout(1, 3));

		this.menuBar = new JMenuBar();

		this.fileMenu = new JMenu("File");
		this.fileMenu.setMnemonic(KeyEvent.VK_F);
		this.fileMenu.add(MenuActionType.NEW_FILE.getAction());
		this.fileMenu.add(MenuActionType.OPEN_FILE.getAction());
		this.fileMenu.add(MenuActionType.SAVE_FILE.getAction());
		this.fileMenu.add(MenuActionType.SAVE_AS_FILE.getAction());
		this.fileMenu.addSeparator();
		this.fileMenu.add(MenuActionType.CHANGE_PASSWORD.getAction());
		this.fileMenu.addSeparator();
		this.fileMenu.add(MenuActionType.EXIT.getAction());
		this.menuBar.add(this.fileMenu);

		this.editMenu = new JMenu("Edit");
		this.editMenu.setMnemonic(KeyEvent.VK_E);
		this.editMenu.add(MenuActionType.ADD_ENTRY.getAction());
		this.editMenu.add(MenuActionType.EDIT_ENTRY.getAction());
		this.editMenu.add(MenuActionType.DELETE_ENTRY.getAction());
		this.menuBar.add(this.editMenu);

		this.toolsMenu = new JMenu("Tools");
		this.toolsMenu.setMnemonic(KeyEvent.VK_T);
		this.toolsMenu.add(MenuActionType.GENERATE_PASSWORD.getAction());
		this.menuBar.add(this.toolsMenu);

		//        
		//        this.encrptMenu = new JMenu("Encrpt Method");
		//        this.encrptMenu.setMnemonic(KeyEvent.VK_E);
		//        this.encrptMenu.add(MenuActionType.ENCRPT_METHOD.getAction());
		//        this.menuBar.add(this.encrptMenu);
		//        	
		this.entryTitleListModel = new DefaultListModel();
		this.entryTitleList = new JList(this.entryTitleListModel);
		this.entryTitleList.setCellRenderer( new MyListRenderer() );

		this.entryTitleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.entryTitleList.addMouseListener(new ListListener());
		this.entryTitleList.setFont(new Font("Verdana", Font.BOLD, 12));
		
		this.entryTitleList.setBackground(Color.BLACK);
		this.scrollPane = new JScrollPane(this.entryTitleList);
		//this.entryTitleList.setBackground(Color.red);

		//        JPanel encrptMethodP =  new JPanel();
		//        JLabel encrptMethodL = new JLabel("Encrpt method:");
		//        encrptMethodP.add(encrptMethodL);
		//        encrptMethodP.add(this.encryBox);
		//        this.encryBox.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent event) {
		//			System.out.println(encryBox.getSelectedItem());
		//		    eMethod = (String) encryBox.getSelectedItem();
		//			System.out.println(eMethod);
		//			}
		//		});
		this.searchPanel = new SearchPanel(new Callback() {
			@Override
			public void call(boolean enabled) {
				if (enabled) {
					refreshEntryTitleList(null);
				}
			}
		});
		this.searchPanel.setEnabled(true);
		this.searchPanel.setVisible(true);

		this.topContainerPanel.add(this.searchPanel, BorderLayout.SOUTH);

		JPanel EmcryptPanel = new JPanel();
		JButton EmcryptButton = new JButton("Encrypt");
		EmcryptPanel.add(EmcryptButton);
		EmcryptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Encrypt GO");
				encryptFrame encry =  new encryptFrame(PVSFrame.getInstance());
				//      EntryDialog ed = new EntryDialog(null, "Add New user", null, true);
			}
		});

		JPanel DecryptPanel = new JPanel();
		JButton DecryptButton  = new JButton("Decrypt");
		DecryptPanel.add(DecryptButton);
		DecryptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Deycrpt");
				new DecryptFrame(PVSFrame.getInstance());
			}
		});

		JPanel KeyManagePanel = new JPanel();
		JButton KeyManageButton  = new JButton("Key Management");
		KeyManagePanel.add(KeyManageButton);
		KeyManageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Key Management");
				//new KeyManagement(PVSFrame.getInstance());
				new KeyList(PVSFrame.getInstance());

			}
		});

		JPanel RSAManagePanel = new JPanel();
		JButton RSA = new JButton("RSA Management");
		RSAManagePanel.add(RSA);
		DecryptPanel.add(DecryptButton);

		RSA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("RSA Management");
				new RsaFrame(PVSFrame.getInstance());
			}
		});

		JPanel CheckSum = new JPanel();
		JButton checkS = new JButton("Check Sum");
		CheckSum.add(checkS);
		checkS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new CheckSumFrame(PVSFrame.getInstance());
			}
		});
		JPanel firstOne = new JPanel();
		firstOne.add(EmcryptPanel);
		firstOne.add(DecryptPanel);
		firstOne.add(KeyManagePanel);
		JPanel secondOne = new JPanel();
		secondOne.add(RSAManagePanel);
		secondOne.add(CheckSum);
		buttonContainerPanel.add(firstOne);
		buttonContainerPanel.add(secondOne);


		buttonContainerPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(buttonContainerPanel,
				2, 1, //rows, columns
				5, 5, //initX, initY
				1, 1); 


		getContentPane().add(this.topContainerPanel, BorderLayout.NORTH);
		getContentPane().add(this.buttonContainerPanel, BorderLayout.SOUTH);

		this.statusPanel = new StatusPanel();
		//getContentPane().add(this.statusPanel, BorderLayout.SOUTH);

		getContentPane().add(this.scrollPane, BorderLayout.CENTER);

		setJMenuBar(this.menuBar);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(420, 400);
		setMinimumSize(new Dimension(420, 200));
		addWindowListener(new CloseListener());
		setLocationRelativeTo(null);
		setVisible(true);
		FileHelper.doOpenFile(fileName, this);

		// set focus to the list for easier keyboard navigation
		this.entryTitleList.requestFocusInWindow();
	}

	public JPopupMenu getPopup() {
		return this.popup;
	}

	public static void exitFrame() {
		// TODO Auto-generated method stub
		System.exit(0);

	}

	public JList getEntryTitleList() {
		return this.entryTitleList;
	}

	public static PVSFrame getInstance() {
		return getInstance(null);
	}

	public static PVSFrame getInstance(String fileName) {
		if (INSTANCE == null) {
			synchronized (PVSFrame.class) {
				if (INSTANCE == null) {
					INSTANCE = new PVSFrame(fileName);
				}
			}
		}
		return INSTANCE;
	}
	public DataModel getModel() {
		return this.model;
	}

	public boolean isProcessing() {
		return this.processing;
	}
	/**
	 * Initialize the contents of the frame.
	 */

	public void refreshFrameTitle() {
		setTitle((getModel().isModified() ? "*" : "")
				+ (getModel().getFileName() == null ? "Untitled" : getModel().getFileName()) + " - "
				+ "COMP4017");
	}

	public void refreshEntryTitleList(String selectTitle) {
		this.entryTitleListModel.clear();
		List<String> titles = this.model.getTitles();
		Collections.sort(titles, String.CASE_INSENSITIVE_ORDER);

		String searchCriteria = this.searchPanel.getSearchCriteria();
		for (String title : titles) {
			//          this.entryTitleListModel.addElement(title);
			if (searchCriteria.isEmpty() || title.toLowerCase().contains(searchCriteria.toLowerCase())) {
				this.entryTitleListModel.addElement(title);
			}
		}

		if (selectTitle != null) {
			this.entryTitleList.setSelectedValue(selectTitle, true);
		}

		//      if (searchCriteria.isEmpty()) {
		//          this.statusPanel.setText("Entries count: " + titles.size());
		//      } else {
		//          this.statusPanel.setText("Entries found: " + this.entryTitleListModel.size() + " / " + titles.size());
		//      }
	}

	public void refreshAll() {
		refreshFrameTitle();
		refreshEntryTitleList(null);
	}

	public void clearModel() {
		this.model.clear();
		this.entryTitleListModel.clear();
	}
	
    private class MyListRenderer extends DefaultListCellRenderer
    {
        private HashMap theChosen = new HashMap();
  
        public Component getListCellRendererComponent( JList list,
                Object value, int index, boolean isSelected,
                boolean cellHasFocus )
        {
            super.getListCellRendererComponent( list, value, index,
                    isSelected, cellHasFocus );
  
            setForeground( Color.RED );
            if( isSelected )
            {
                theChosen.put( value, "chosen" );
            }
  
            if( theChosen.containsKey( value ) )
            {
                setForeground( Color.RED );
            }
            else
            {
                setForeground( Color.RED );
            }
  
            return( this );
        }
    }
    

	public void setProcessing(boolean processing) {
		this.processing = processing;
		for (MenuActionType actionType : MenuActionType.values()) {
			actionType.getAction().setEnabled(!processing);
		}
		this.statusPanel.setProcessing(processing);
		this.searchPanel.setEnabled(!processing);
		this.entryTitleList.setEnabled(!processing);
	}
}
