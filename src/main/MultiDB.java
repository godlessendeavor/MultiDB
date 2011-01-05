package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpinnerListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.undo.UndoManager;

import sun.awt.shell.ShellFolder;

import main.exceptions.MP3FilesNotFound;
import main.ftp.FTPManager;
import movies.db.Movie;
import music.db.DataBaseTable;
import music.db.Disc;
import music.db.NewDiscTabMod;
import music.db.TabMod;
import music.mp3Player.MP3Player;
import music.mp3Player.PlayingEvent;
import music.mp3Player.PlayingListener;
import music.mp3Player.Song;
import music.mp3Player.TabModelPlayList;
import music.web.WebReader;
import docs.db.Doc;
import docs.db.DocTheme;

public class MultiDB extends JFrame implements music.db.DataBaseLabels{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//CONSTRAINTS

    public final Dimension COVERS_DIM = new Dimension(400,400);
    public final Dimension MAX_COVERS_DIM = new Dimension(1200,800);
    public final Dimension PLAYER_DIM = new Dimension(1000,600);
    public final Dimension LYRICS_DIM = new Dimension(500,700);
    public final Dimension MAIN_DIM = new Dimension(1230, 700);
    public final Dimension PANE_DIM = new Dimension(1200, 600);
    public final JLabel COVERS_NOT_FOUND_MSG = new JLabel("Covers not available");
    public final JLabel COVERS_NOT_NAMED_PROP_MSG = new JLabel("Covers found but not named properly");
    public final int EQ_NUM_BANDS = 10;
    public final String configFileName="multiDB.config";
    public final String LYR_PLAYER_NAME="playerLyricsMenu";
    public final String LYR_MENU_NAME="menuViewLyrics"; 
    public final int IND_MUSIC_TAB = 0;
    public final int IND_MOVIES_TAB = 1;
    public final int IND_DOCS_TAB = 2;
    public final int SAVE_REVIEW=0;
    public final int PASTE_IN_TABLE=1;
    //VARS
    
    //STATIC VARS
    public static int port;
    public static String host;
    public static String user;
    public static String pass;
    public static String musicDatabase;
    public static String moviesDatabase;
    public static String mysqlPath;
    public static String webEMSearch;
    public static String webEMBand;
    public static String webEMRelease;
    public static String webEMLyrics;
    public static String webMBSearch;
    public static String webMBBand;
    public static String ftpUser;
    public static String ftpPassword;
    public static String ftpHost;
    public static String ftpDirectory;
    public static String blogUser;
    public static String blogPswd;
    public static String musicUpload;
    public static String moviesUpload;
    public static String docsUpload;
    public static String musicTable="music";
    public static String moviesTable="movies";
    public static String docsTable="docs";
    
    
    
    /////MULTIVARS
    
    ///common
    public int selectedViewColumn = -1,selectedModelColumn=-1;
    public int selectedViewRow = -1,lastSelectedViewRow = -1,selectedModelRow=-1,lastSelectedModelRow=-1;   
    public FTPManager ftpManager;
    public List<Integer> selectedView = new LinkedList<Integer>();//selected Rows in table (View)
    public List<Integer> selectedModel = new LinkedList<Integer>();//selected Rows in table (Model)   
    public TimerThread timerThread;
    public RandomPlayThread randomPlayThread;
    public Clipboard sysClipboard;
    public long lastTime;
    public int currentCharPos;
    
    
    ///n-tuplas
    public music.db.TabMod musicTabModel;
    public music.db.DataBaseTable musicDataBase;
    public TableRowSorter<music.db.TabMod> musicTableSorter; //trouble with tablerowsorter, used only for filtering words
    
    public movies.db.TabMod moviesTabModel;     
    public movies.db.DataBaseTable moviesDataBase;
    public TableRowSorter<movies.db.TabMod> moviesTableSorter;
    
    public docs.db.TabMod docsTabModel;     
    public docs.db.DataBaseTable docsDataBase;
    public TableRowSorter<docs.db.TabMod> docsTableSorter;
    
    
    //MUSIC VARS
   
    public List<Disc> disCover = new LinkedList<Disc>();
    public UndoManager undoManager = new UndoManager();  //undo/redo manager
    public WebReader webReader;
    public NewDiscTabMod newDiscsTabMod;
    public boolean backUpConnected = false,currentFrontCover = false;
    public SpinnerListModel spinnerCoversM;
    public File backUpPath,lyricsFile,auxPath;
    public Dimension bigCoverDim;


    //MAIN VIEWING ELEMENTS
    
    //common
    public JMenuBar menuBar;
    //menu items
    public JMenu menuDataBase;
    public JMenuItem menuRelDBBU;
    public JMenuItem menuAddItem;
    public JMenuItem menuDelItem;
    public JMenuItem menuMakeBUP;
    public JMenuItem menuAddBUDB;
    public JMenuItem menuUploadBackup;
    public JMenu menuOpciones;
    public JMenuItem menuOpcionesCovers;
    public JMenuItem menuOpcionesGuardar;
    public JMenuItem menuOpcionesCopiarPortadas;
    public JMenuItem menuOpcionesCoverBackup;
    public JMenuItem menuCopyReviews2BUP;
    public JMenu menuViewNewDiscsViaWeb;    
    public JMenuItem menuPlayRandom;
    public JMenuItem menuViaEM;
    public JMenuItem menuViaMB;
    public JTabbedPane multiPane;
    public MultiDB f;
    public JPopupMenu popupTable;
    public JMenuItem menuPlay;
    public JMenuItem menuViewLyrics;
    public MusicTableRenderer coloredTableRenderer;
    public JFileChooser fc = new JFileChooser(new NewFileSystemView());
    
        
    //n-tuplas
    public JTable musicJTable,moviesJTable,docsJTable;           //main tables
    public JScrollPane musicTableSp,moviesTableSp,docsTableSp;
        
    //music specific
    public JLabel coversView,selectCoversView,bigCoversView;  //covers labels
    public JTextArea reviewView,infoText;
    public JScrollPane spRev,splitLeft,newDiscsSp,bigCoversScroll, infoScroll;
    public JSplitPane splitRight,mainSplit;
    public ImageIcon origIcon, scaledIcon, bigIcon;
    public JPopupMenu popupCover,popupReview;
    public JFrame selectCoverFrame,bigCoversFrame,infoFrame,newDiscsFrame;   
    public JSpinner spinnerCovers;
    public JButton spinnerCoversButton;
    public Image imagen;
    public JTable newDiscsTab;        //table for discographies searched in the internet
      
    //PLAYER ELEMENTS
    public MP3Player mp3Player;
    public TabModelPlayList playList;
    public JFrame playerFrame;
    //public JButton playButton;
    public JButton pauseResumeButton,stopButton,resetEqButton;
    public JTable playListTable;
    public JSlider songSlider;
    public JSlider[] equalizer;
    public JTextField songInformation;
    public JSplitPane splitEq;
    public JInternalFrame playerIntFrame,equalizerFrame;
    public JSplitPane splitPlayer;
    public JScrollPane spPlay,spLyrics;
    public JPopupMenu popupSong;
    public PlayerTableRenderer playerTableRenderer;
    //LYRICS ELEMENTS
    public JFrame lyricsFrame;
    public JTextArea lyricsView;
    public JButton saveLyricsButton;
    public JButton downloadLyricsButton;
    public String lyricsGroup;
    public String lyricsAlbum;
    public File lyricsPath;
       
        ///////////////////////////////////HERE WE GO!!!!\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    	///////////////////////////////////HERE WE GO!!!!\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    	///////////////////////////////////HERE WE GO!!!!\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
    public MultiDB() {
        super("MultiDB");
        f=this;
    }

    public static void main(String[] args) {
        MultiDB aplicacion = new MultiDB();
        aplicacion.initApi();     
    }

  
//////////////////////METODOS/////////////////////////////////////////////////////////////
//////////////////////METODOS/////////////////////////////////////////////////////////////
//////////////////////METODOS/////////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////
//////////////////////VIEWING METHODS/////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////
    
    public void initApi(){
    	
    	//initializing vars
    	this.setSize(MAIN_DIM);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	Toolkit.getDefaultToolkit().setDynamicLayout(true);
    	System.setProperty("sun.awt.noerasebackground", "true");
    	JFrame.setDefaultLookAndFeelDecorated(true);
    	JDialog.setDefaultLookAndFeelDecorated(true);
    	try {
			UIManager.setLookAndFeel("com.easynth.lookandfeel.EaSynthLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	
        processConfig(configFileName);  //reading parameters from config file
        
        //creating main objects
        
        
        mp3Player = new MP3Player();
        //common
        webReader = new WebReader();
        ftpManager = new FTPManager();
        //n-tuplas
        musicTabModel = new music.db.TabMod();  
    	musicDataBase = new music.db.DataBaseTable();
        musicDataBase.setTabModel(musicTabModel);
        moviesTabModel = new movies.db.TabMod(); 
    	moviesDataBase = new movies.db.DataBaseTable();
        moviesDataBase.setTabModel(moviesTabModel);
        docsTabModel = new docs.db.TabMod(); 
    	docsDataBase = new docs.db.DataBaseTable();
        docsDataBase.setTabModel(docsTabModel);
               
        sysClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        lastTime=System.currentTimeMillis();
        currentCharPos=0;
    	
        /////////////////////////////////////////////creating menu
        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        
        //menu DATA BASE
        menuDataBase = new JMenu("Data Base ");
        menuDataBase.setMnemonic('D');
        //item REL DATABASE AND BACKUP
        menuRelDBBU = new JMenuItem("Relate DataBase and Backup");
        menuRelDBBU.setMnemonic('r');
        RelDBBUHandler relHandler = new RelDBBUHandler();
        menuRelDBBU.addActionListener(relHandler);
        menuDataBase.add(menuRelDBBU);
        //item ADD DISC TO THE DATABASE
        menuAddItem = new JMenuItem("Add item to the data base");
        menuAddItem.setMnemonic('a');
        AddItemHandler addItemHandler = new AddItemHandler();
        menuAddItem.addActionListener(addItemHandler);
        menuDataBase.add(menuAddItem);
        //item DELETE DISC FROM DATABASE
        menuDelItem = new JMenuItem("Delete item from data base");
        menuDelItem.setMnemonic('d');
        DelItemHandler delItemHandler = new DelItemHandler();
        menuDelItem.addActionListener(delItemHandler);
        menuDataBase.add(menuDelItem);
        //item ADD DISCS TO BACKUP AND DATABASE
        menuAddBUDB = new JMenuItem("Add discs from folder to backup and database");
        menuDelItem.setMnemonic('a');
        AddBUDBHandler addBUDBHandler = new AddBUDBHandler();
        menuAddBUDB.addActionListener(addBUDBHandler);
        menuDataBase.add(menuAddBUDB); 
          //item MAKE BACKUP OF DATABASE
        menuMakeBUP = new JMenuItem("Make backup of database");
        menuMakeBUP.setMnemonic('m');
        DBBUPHandler makeBUPHandler = new DBBUPHandler();
        menuMakeBUP.addActionListener(makeBUPHandler);
        menuDataBase.add(menuMakeBUP);
        //item UPLOAD BACKUP TO WEB
        menuUploadBackup = new JMenuItem("Upload backup to Webdatabase");
        menuUploadBackup.setMnemonic('u');
        UploadBUPHandler uploadBUPHandler = new UploadBUPHandler();
        menuUploadBackup.addActionListener(uploadBUPHandler);
        menuDataBase.add(menuUploadBackup);
      /*    //item RESTORE BACKUP OF DATABASE
        JMenuItem menuRestoreBUP = new JMenuItem("Restore backup of database");
        menuRestoreBUP.setMnemonic('r');
        RestoreDBBUPHandler restoreBUPHandler = new RestoreDBBUPHandler(this);
        menuRestoreBUP.addActionListener(restoreBUPHandler);
        menuDataBase.add(menuRestoreBUP);*/

        //menu EDIT
        JMenu menuEdit = new JMenu("Edit ");
        menuEdit.setMnemonic('E');
        //item UNDO
        JMenuItem menuUndo = new JMenuItem("Undo");
        menuUndo.setMnemonic('u');
        menuUndo.setAccelerator(KeyStroke.getKeyStroke('Z',Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        MenuUndoHandler menuUndoHandler = new MenuUndoHandler();
        menuUndo.addActionListener(menuUndoHandler);
        menuEdit.add(menuUndo);
        //item REDO
        JMenuItem menuRedo = new JMenuItem("Redo");
        menuRedo.setMnemonic('r');
        MenuRedoHandler menuRedoHandler = new MenuRedoHandler();
        menuRedo.addActionListener(menuRedoHandler);
        menuEdit.add(menuRedo);
        //item PASTE
        JMenuItem menuPaste = new JMenuItem("Paste");
        menuPaste.setMnemonic('v');
        MenuPasteHandler menuPasteHandler = new MenuPasteHandler();
        menuPaste.setAccelerator(KeyStroke.getKeyStroke('V',Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        menuPaste.addActionListener(menuPasteHandler);
        menuEdit.add(menuPaste);
        //item FILTER
        JMenuItem menuFilter = new JMenuItem("Search word");
        menuRedo.setMnemonic('f');
        menuFilter.setAccelerator(KeyStroke.getKeyStroke('F',Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        MenuFilterHandler menuFilterHandler = new MenuFilterHandler();
        menuFilter.addActionListener(menuFilterHandler);
        menuEdit.add(menuFilter);
        
        //MENU OPCIONES
        menuOpciones = new JMenu("Music DB Options ");
        menuOpciones.setMnemonic('O');
        //item VER LISTA COVERS
        menuOpcionesCovers = new JMenuItem("Open found covers list");
        menuOpcionesCovers.setMnemonic('c');
        CoverHandler coverHandler = new CoverHandler();
        menuOpcionesCovers.addActionListener(coverHandler);
        menuOpciones.add(menuOpcionesCovers);
        //item GUARDAR LISTA DISCOS SIN COVER
        menuOpcionesGuardar = new JMenuItem("Save list of discs without cover");
        menuOpcionesGuardar.setMnemonic('g');
        NoCoverDiscHandler listaHandler = new NoCoverDiscHandler();
        menuOpcionesGuardar.addActionListener(listaHandler);
        menuOpciones.add(menuOpcionesGuardar);
        //item COPIAR PORTADAS A DESTINO
        menuOpcionesCopiarPortadas = new JMenuItem("Move covers to backup");
        menuOpcionesCopiarPortadas.setMnemonic('p');
        MoveCoversHandler copiarPortadasHandler = new MoveCoversHandler();
        menuOpcionesCopiarPortadas.addActionListener(copiarPortadasHandler);
        menuOpciones.add(menuOpcionesCopiarPortadas);
        //item COVER BACKUP
        menuOpcionesCoverBackup = new JMenuItem("Cover backup");
        menuOpcionesCoverBackup.setMnemonic('b');
        CoverBackupHandler coverBackupHandler = new CoverBackupHandler();
        menuOpcionesCoverBackup.addActionListener(coverBackupHandler);
        menuOpciones.add(menuOpcionesCoverBackup);
        //item COPYREVIEW2BUP
        menuCopyReviews2BUP = new JMenuItem("Copy reviews to database");
        menuCopyReviews2BUP.setMnemonic('m');
        CopyReviewsHandler copyReviewsHandler = new CopyReviewsHandler();
        menuCopyReviews2BUP.addActionListener(copyReviewsHandler);
        menuOpciones.add(menuCopyReviews2BUP);
        //item VIEWNEWDISCS
        menuViewNewDiscsViaWeb = new JMenu("Search new discs via web");
        menuViewNewDiscsViaWeb.setMnemonic('v');
        ViewNewDiscsHandler viewNewDiscsHandlerEM = new ViewNewDiscsHandler("webEM");
        ViewNewDiscsHandler viewNewDiscsHandlerMB = new ViewNewDiscsHandler("webMB");
        menuViaEM = new JMenuItem("Via Encyclopedia Metallum");
        menuViaEM.addActionListener(viewNewDiscsHandlerEM);
        menuViewNewDiscsViaWeb.add(menuViaEM);
        menuViaMB = new JMenuItem("Via Musicbrainz");
        menuViaMB.addActionListener(viewNewDiscsHandlerMB);
        menuViewNewDiscsViaWeb.add(menuViaMB);
        menuOpciones.add(menuViewNewDiscsViaWeb);
        //item PLAYRANDOM
        menuPlayRandom = new JMenuItem("Play files at random");
        menuPlayRandom.setMnemonic('p');
        PlayRandomHandler playRandomHandler = new PlayRandomHandler();
        menuPlayRandom.addActionListener(playRandomHandler);
        menuPlayRandom.setEnabled(false);
        menuOpciones.add(menuPlayRandom);
            

        menuBar.add(menuDataBase);
        menuBar.add(menuEdit);
        menuBar.add(menuOpciones);
        
              

////////////////////////////////////popupmenus//////////////////////////////////

		popupTable = new JPopupMenu();
		JMenuItem menuDefaultOrder = new JMenuItem("Default sorting");
		PopupMenuSortDefaultHandler popupSortMenuDefaultHandler = new PopupMenuSortDefaultHandler();
		menuDefaultOrder.addActionListener(popupSortMenuDefaultHandler);
		JMenuItem menuOrderByField = new JMenuItem("Sort by this field");
		PopupMenuSortByFieldHandler popupMenuSortByFieldHandler = new PopupMenuSortByFieldHandler();
		menuOrderByField.addActionListener(popupMenuSortByFieldHandler);
		menuPlay = new JMenuItem("Play this item");
		PopupMenuPlayHandler popupMenuPlayHandler = new PopupMenuPlayHandler();
		menuPlay.addActionListener(popupMenuPlayHandler);
		menuPlay.setEnabled(false);
		menuViewLyrics = new JMenuItem("View Lyrics");
		PopupMenuViewLyricsHandler popupMenuViewLyrics = new PopupMenuViewLyricsHandler();
		menuViewLyrics.addActionListener(popupMenuViewLyrics);	
		menuViewLyrics.setEnabled(false);
		menuViewLyrics.setName(LYR_MENU_NAME);
		JMenuItem menuPastePopup = new JMenuItem("Paste");
		PopupMenuPasteHandler popupMenuPasteHandler = new PopupMenuPasteHandler();
		menuPastePopup.addActionListener(popupMenuPasteHandler);
		menuPastePopup.setMnemonic('v');
		menuPastePopup.setAccelerator(KeyStroke.getKeyStroke('V',Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
	       

		popupTable.add(menuDefaultOrder);
		popupTable.add(menuOrderByField);
		popupTable.add(menuPlay);
		popupTable.add(menuPastePopup);
		popupTable.add(menuViewLyrics);

		popupCover = new JPopupMenu();
		JMenuItem menuViewBigCover = new JMenuItem("View cover in bigger frame");
		PopupMenuShowBigCoverHandler showBigCoverHandler = new PopupMenuShowBigCoverHandler();
		menuViewBigCover.addActionListener(showBigCoverHandler);

		popupCover.add(menuViewBigCover);
			
		popupReview = new JPopupMenu();
        JMenuItem menuSaveReview = new JMenuItem("Save review in database");
        menuSaveReview.setMnemonic('s');
        SaveReviewHandler saveReviewHandler = new SaveReviewHandler();
        menuSaveReview.addActionListener(saveReviewHandler);
        
		popupReview.add(menuSaveReview);
		
		
		////////////////////////////////TABLES LAYOUT\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
		////////////////////////////////TABLES LAYOUT\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
		////////////////////////////////TABLES LAYOUT\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
		
	////////////////////////////////////////music table layout\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        
        musicJTable = new JTable(musicTabModel);
        musicJTable.setCellSelectionEnabled(true); //no se puede seleccionar solo una celda
        musicJTable.setColumnSelectionAllowed(true); //no se pueden seleccionar columnas
        musicJTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      
        musicTableSorter = new TableRowSorter<TabMod>(musicTabModel);
        musicJTable.setRowSorter(musicTableSorter);
        for (int i=0;i<musicTabModel.getColumnCount();i++){ 
        	musicTableSorter.setSortable(i,false); //issue with TableRowSorter, disabling sorting, using only for filtering
        }
        
        MusicTableRenderer musicTableRenderer = new MusicTableRenderer();
        musicJTable.setDefaultRenderer(Object.class,musicTableRenderer);
        
        //sizing cols
        TableColumn col = musicJTable.getColumn("groupName");
        col.setMinWidth(130);
        col.setPreferredWidth(130);
        col = musicJTable.getColumn("title");
        col.setMinWidth(180);
        col.setPreferredWidth(180);
        col = musicJTable.getColumn("style");
        col.setMinWidth(120);
        col.setPreferredWidth(120);
        col = musicJTable.getColumn("year");
        col.setMinWidth(40);
        col.setPreferredWidth(40);
        col = musicJTable.getColumn("loc");
        col.setMinWidth(80);
        col.setPreferredWidth(80);
        col = musicJTable.getColumn("copy");
        col.setMinWidth(60);
        col.setPreferredWidth(60);
        col = musicJTable.getColumn("type");
        col.setMinWidth(40);
        col.setPreferredWidth(40);
        col = musicJTable.getColumn("mark");
        col.setMinWidth(30);
        col.setPreferredWidth(30);
        col = musicJTable.getColumn("present");
        col.setMinWidth(30);
        col.setPreferredWidth(30);
        //removing cols not needed
        col = musicJTable.getColumn("review");
        musicJTable.removeColumn(col);
        col = musicJTable.getColumn("path");
        musicJTable.removeColumn(col);
        col = musicJTable.getColumn("Id");
        musicJTable.removeColumn(col);
        
	////////////////////////////////////////movies table layout\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        
        moviesJTable = new JTable(moviesTabModel);
        moviesJTable.setCellSelectionEnabled(true); //no se puede seleccionar solo una celda
        moviesJTable.setColumnSelectionAllowed(true); //no se pueden seleccionar columnas
        moviesJTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      
        moviesTableSorter = new TableRowSorter<movies.db.TabMod>(moviesTabModel);
        moviesJTable.setRowSorter(moviesTableSorter);
        for (int i=0;i<moviesTabModel.getColumnCount();i++){ 
        	moviesTableSorter.setSortable(i,false); //issue with TableRowSorter, disabling sorting, using only for filtering
        }
        
        MoviesTableRenderer moviesTableRenderer = new MoviesTableRenderer();
        moviesJTable.setDefaultRenderer(Object.class,moviesTableRenderer);
        
        //sizing cols
        col = moviesJTable.getColumn("title");
        col.setMinWidth(180);
        col.setPreferredWidth(180);
        col = moviesJTable.getColumn("director");
        col.setMinWidth(120);
        col.setPreferredWidth(120);
        col = moviesJTable.getColumn("year");
        col.setMinWidth(40);
        col.setPreferredWidth(40);
        col = moviesJTable.getColumn("loc");
        col.setMinWidth(80);
        col.setPreferredWidth(80);
        col = moviesJTable.getColumn("other");
        col.setMinWidth(60);
        col.setPreferredWidth(60);
      
        //removing cols not needed
     
        col = moviesJTable.getColumn("Id");
        moviesJTable.removeColumn(col);
        col = moviesJTable.getColumn("path");
        moviesJTable.removeColumn(col);
        col = moviesJTable.getColumn("present");
        moviesJTable.removeColumn(col);
        moviesTableSp = new JScrollPane(moviesJTable);
               
	////////////////////////////////////////docs table layout\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        
        docsJTable = new JTable(docsTabModel);
        docsJTable.setCellSelectionEnabled(true); //no se puede seleccionar solo una celda
        docsJTable.setColumnSelectionAllowed(true); //no se pueden seleccionar columnas
        docsJTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      
        docsTableSorter = new TableRowSorter<docs.db.TabMod>(docsTabModel);
        docsJTable.setRowSorter(docsTableSorter);
        for (int i=0;i<docsTabModel.getColumnCount();i++){ 
        	docsTableSorter.setSortable(i,false); //issue with TableRowSorter, disabling sorting, using only for filtering
        }
        
        DocsTableRenderer docsTableRenderer = new DocsTableRenderer();
        docsJTable.setDefaultRenderer(Object.class,docsTableRenderer);
        
        //sizing cols
        col = docsJTable.getColumn("title");
        col.setMinWidth(500);
        col.setPreferredWidth(500);
        col = docsJTable.getColumn("comments");
        col.setMinWidth(350);
        col.setPreferredWidth(350);
        col = docsJTable.getColumn("theme");
        col.setMinWidth(60);
        col.setPreferredWidth(60);
        col = docsJTable.getColumn("loc");
        col.setMinWidth(40);
        col.setPreferredWidth(40);
      
        col = docsJTable.getColumn("id");
        docsJTable.removeColumn(col);
        
        //adding combobox for theme column		
		ComboCellEditor comboCellEditor = new ComboCellEditor();
		docsJTable.setDefaultEditor(DocTheme.class, comboCellEditor);
		ComboTableCellRenderer comboCellRenderer = new ComboTableCellRenderer();
		docsJTable.setDefaultRenderer(DocTheme.class,comboCellRenderer);
		
        docsTableSp = new JScrollPane(docsJTable);  
        
        
       
 
//////////////////////////////////tab music splits layout\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\  
        
        coversView = new JLabel();
        //dimensions for covers
        coversView.setMinimumSize(COVERS_DIM);
        COVERS_NOT_FOUND_MSG.setMinimumSize(COVERS_DIM);
        COVERS_NOT_NAMED_PROP_MSG.setMinimumSize(COVERS_DIM);
        reviewView = new JTextArea();
        Font font = new Font("Verdana", Font.BOLD, 11);
        reviewView.setFont(font);
        reviewView.setForeground(Color.BLACK);
        reviewView.setBackground(Color.CYAN);
        reviewView.setLineWrap(true);
        reviewView.setWrapStyleWord(true);
               
        
        spRev = new JScrollPane(reviewView);
        //scrollbar policies
        spRev.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spRev.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //creating splits
        splitLeft = new JScrollPane(musicJTable);
        splitRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT,coversView,spRev);        
        splitRight.setContinuousLayout(true);
        splitRight.setOneTouchExpandable(true);
        splitRight.setDividerLocation(400);
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitLeft,splitRight );
        mainSplit.setContinuousLayout(true);
        mainSplit.setOneTouchExpandable(true);
        mainSplit.setDividerLocation(800);
        mainSplit.setPreferredSize(PANE_DIM);
        musicTableSp = new JScrollPane(mainSplit);
        

        /////////////////////////////////adding tabs to tabbedpane\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        multiPane = new JTabbedPane();
        ///being careful with the orders
        multiPane.addTab("music",musicTableSp);
        multiPane.addTab("movies",moviesTableSp);
        multiPane.addTab("documentaries",docsTableSp); 
        TabbedPaneListener tabbedPaneListener = new TabbedPaneListener();
        multiPane.addChangeListener(tabbedPaneListener);
        f.add(multiPane);
        
        
        ///////////setting icons for pictures
        origIcon= new ImageIcon();
        scaledIcon = new ImageIcon();
        bigIcon = new ImageIcon();
        
        musicDataBase.setReviewView(reviewView);
        moviesDataBase.setReviewView(reviewView);
        docsDataBase.setReviewView(reviewView);
        
        
        
/////////////////////////creating layout for frame selectCover//////////////////////////////

        selectCoverFrame = new JFrame("Select a picture");
        selectCoverFrame.setSize(500, 500);
        //selectCoversView = new JLabel();
        //selectCoversView.setMinimumSize(COVERS_DIM);
        selectCoverFrame.getContentPane().setLayout(new BoxLayout(selectCoverFrame.getContentPane(),BoxLayout.Y_AXIS));
        spinnerCoversM = new SpinnerListModel();
        spinnerCovers = new JSpinner(spinnerCoversM);
        spinnerCoversButton = new JButton("Cambiar a nomenclatura oficial");
        ChangeNameHandler changeNameHandler = new ChangeNameHandler();
        spinnerCoversButton.addActionListener(changeNameHandler);
        selectCoverFrame.getContentPane().add(spinnerCovers);
        selectCoverFrame.getContentPane().add(spinnerCoversButton);
        //selectCoverFrame.getContentPane().add(selectCoversView);
        
        
        
////////////////////////////////table new Discs layout////////////////////////////
		newDiscsTab = new JTable();        
		newDiscsTab.setCellSelectionEnabled(true); //no se puede seleccionar solo una celda
		newDiscsTab.setColumnSelectionAllowed(true); //no se pueden seleccionar columnas
		newDiscsTab.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); 
		
		coloredTableRenderer = new MusicTableRenderer();
		newDiscsTab.setDefaultRenderer(Object.class,coloredTableRenderer);
		
		newDiscsTabMod = new NewDiscTabMod();   
		newDiscsTab.setModel(newDiscsTabMod);
		//sizing cols
		col = newDiscsTab.getColumn("Group");
		col.setMinWidth(130);
		col.setPreferredWidth(130);
		col = newDiscsTab.getColumn("Title");
		col.setMinWidth(180);
		col.setPreferredWidth(180);
		col = newDiscsTab.getColumn("Style");
		col.setMinWidth(120);
		col.setPreferredWidth(120);
		col = newDiscsTab.getColumn("Year");
		col.setMinWidth(40);
		col.setPreferredWidth(40);
		col = newDiscsTab.getColumn("Location");
		col.setMinWidth(220);
		col.setPreferredWidth(220);
		col = newDiscsTab.getColumn("Id");
		newDiscsTab.removeColumn(col);
		newDiscsSp = new JScrollPane(newDiscsTab);  
        
///////////////////////////////Frames layout///////////////////////////////////////////////
        
        //bigcover frame
        bigCoversFrame=new JFrame("Cover");
        bigCoversView = new JLabel();
        bigCoversScroll = new JScrollPane(bigCoversView);
        bigCoversFrame.add(bigCoversScroll);
         
        //copyinginfo frame
		infoFrame = new JFrame("Info");
		infoFrame.setSize(500, 80);
		infoText = new JTextArea();
		infoScroll = new JScrollPane(infoText);
		infoFrame.add(infoScroll);
		
		//newDiscs Frame
		newDiscsFrame = new JFrame("New Discs");
        newDiscsFrame.add(newDiscsSp);
        
//////////////////////layout for playerFrame////////////////////////////////////////////////////////
        
		//frames
        playerFrame=new JFrame("MP3Player");
        playerFrame.setSize(PLAYER_DIM);
        playerIntFrame = new JInternalFrame();
        equalizerFrame = new JInternalFrame();
        playerIntFrame.setTitle("Player");
        equalizerFrame.setTitle("Equalizer");
        playerIntFrame.setVisible(true);
        equalizerFrame.setVisible(true);

        //layouts
        BoxLayout blay1=new BoxLayout(playerIntFrame.getContentPane(),BoxLayout.Y_AXIS);
        playerIntFrame.getContentPane().setLayout(blay1);
        
        BoxLayout blay2=new BoxLayout(equalizerFrame.getContentPane(),BoxLayout.X_AXIS);
        equalizerFrame.getContentPane().setLayout(blay2);        
        
        
        //Buttons
        
   /*     playButton=new JButton("Play");
        PlayButtonHandler playButtonHandler = new PlayButtonHandler();
        playButton.addActionListener(playButtonHandler);*/
        pauseResumeButton=new JButton("Pause/Resume");
        PauseResumeHandler pauseResumeHandler = new PauseResumeHandler();
        pauseResumeButton.addActionListener(pauseResumeHandler);
        stopButton=new JButton("Stop");
        StopButtonHandler stopButtonHandler = new StopButtonHandler();
        stopButton.addActionListener(stopButtonHandler);
        //playerFrame.add(playButton);
        
        
        //slider
        songSlider= new JSlider(JSlider.HORIZONTAL,0,0,0);
        songSlider.setMajorTickSpacing(60);
        songSlider.setMinorTickSpacing(1);
        songSlider.setPaintLabels(true);
        songSlider.setPaintTicks(true);
        songInformation = new JTextField("");      
        
        playerIntFrame.add(pauseResumeButton);
        playerIntFrame.add(stopButton);
        playerIntFrame.add(songSlider);
        playerIntFrame.add(songInformation);
        
        //equalizer
        equalizer = new JSlider[EQ_NUM_BANDS];
        int i;
        for(i=0;i<EQ_NUM_BANDS;i++){  	
        	equalizer[i]=new JSlider(JSlider.VERTICAL,-100,100,0); 
        	equalizer[i].setMinorTickSpacing(1);
        	equalizer[i].setPaintLabels(true);
        	equalizer[i].setPaintTicks(true);
        	equalizer[i].setName(((Integer)i).toString());
        	equalizerFrame.add(equalizer[i]);
        }
        resetEqButton=new JButton("Reset");
        ResetEqHandler resetEqHandler = new ResetEqHandler();
        resetEqButton.addActionListener(resetEqHandler);
        equalizerFrame.add(resetEqButton);
        
        //splits
        splitPlayer = new JSplitPane(JSplitPane.VERTICAL_SPLIT,playerIntFrame,equalizerFrame);
        splitPlayer.setDividerLocation(400);
        playerFrame.add(splitPlayer);
        
        //songs table
        
        playListTable=new JTable();
        playList = new TabModelPlayList();
        spPlay = new JScrollPane(playListTable);
        playerTableRenderer = new PlayerTableRenderer();
        playListTable.setDefaultRenderer(Object.class,playerTableRenderer);
        playerIntFrame.add(spPlay);
        
        
        
        //popupmenu
        
        popupSong = new JPopupMenu();
		JMenuItem seeLyricsMenu = new JMenuItem("View Lyrics");
		PopupMenuViewLyricsHandler popupMenuSeeLyrics = new PopupMenuViewLyricsHandler();
		seeLyricsMenu.addActionListener(popupMenuSeeLyrics);
		seeLyricsMenu.setName(LYR_PLAYER_NAME);
		popupSong.add(seeLyricsMenu);
        
        //popupmenulistener
        PopupSongListener popupSongListener = new PopupSongListener();
        playListTable.addMouseListener(popupSongListener);
        
        
////////////layout for lyrics frame////////////////////////////////////////////////////////////////////////
        lyricsFrame=new JFrame("Lyrics");
        lyricsFrame.setSize(LYRICS_DIM);
        lyricsFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        lyricsView = new JTextArea();
        lyricsView.setFont(font);
        lyricsView.setForeground(Color.BLACK);
        lyricsView.setBackground(Color.CYAN);
        lyricsView.setLineWrap(true);
        lyricsView.setWrapStyleWord(true);
        spLyrics = new JScrollPane(lyricsView);
        saveLyricsButton=new JButton("Save Lyrics");
        SaveLyricsButtonHandler saveLyricsButtonHandler = new SaveLyricsButtonHandler();
        saveLyricsButton.addActionListener(saveLyricsButtonHandler);
        downloadLyricsButton=new JButton("Download Lyrics");
        DownloadLyricsButtonHandler downloadLyricsButtonHandler = new DownloadLyricsButtonHandler();
        downloadLyricsButton.addActionListener(downloadLyricsButtonHandler);
        BoxLayout blay=new BoxLayout(lyricsFrame.getContentPane(),BoxLayout.Y_AXIS);
        lyricsFrame.getContentPane().setLayout(blay);
        lyricsFrame.add(spLyrics);
        lyricsFrame.add(saveLyricsButton);
        lyricsFrame.add(downloadLyricsButton);
  
       
///////////////////////ADDING LISTENER HANDLERS//////////////////////////////////////////////

        
        ///////////////////////////for every table/////////////////////////////////////
        //n-tuplas where needed
        ListSelectionModel rowSM = musicJTable.getSelectionModel();
        //selecting row, shows review and covers in case of BackUpConnected
        SelectItemHandler selectHandler = new SelectItemHandler();
        rowSM.addListSelectionListener(selectHandler);
        rowSM = moviesJTable.getSelectionModel();
        rowSM.addListSelectionListener(selectHandler);
        rowSM = docsJTable.getSelectionModel();
        rowSM.addListSelectionListener(selectHandler);
        
        //to edit cells and save in the database
        CellEditorHandler edHandler = new CellEditorHandler();
        musicJTable.getDefaultEditor(Object.class).addCellEditorListener(edHandler);
        moviesJTable.getDefaultEditor(Object.class).addCellEditorListener(edHandler);
        docsJTable.getDefaultEditor(Object.class).addCellEditorListener(edHandler);
        
        //popuptablelistener
        PopupTableListener popupTableListener = new PopupTableListener();
        musicJTable.addMouseListener(popupTableListener);
        moviesJTable.addMouseListener(popupTableListener);
        docsJTable.addMouseListener(popupTableListener); 
        
        //key listener to select row by letter
        TableKeyListener tableKeyListener = new TableKeyListener();
        musicJTable.addKeyListener(tableKeyListener);
        moviesJTable.addKeyListener(tableKeyListener);
        docsJTable.addKeyListener(tableKeyListener);
        
        ////////////////////specific handlers for music\\\\\\\\\\\\\\\\\\\\\\\\\

        //popupreviewlistener
        PopupReviewListener popupReviewListener = new PopupReviewListener();
        reviewView.addMouseListener(popupReviewListener);
        
        //diferent method to add keystroke to the reviewView due to the fact that the other method doesn't work if the popup isn't showed 
        reviewView.getInputMap().put(KeyStroke.getKeyStroke("control S"), "saveReview");
        AbstractActionsHandler saveReviewKHandler = new AbstractActionsHandler(SAVE_REVIEW);
        // Add the action to the component
        reviewView.getActionMap().put("saveReview",saveReviewKHandler);
        
        
        //pastelisteners Ctrl+V
        musicJTable.getInputMap().put(KeyStroke.getKeyStroke("control V"), "pasteInTable");
        moviesJTable.getInputMap().put(KeyStroke.getKeyStroke("control V"), "pasteInTable");
        docsJTable.getInputMap().put(KeyStroke.getKeyStroke("control V"), "pasteInTable");
        
        // Add the action to the component
        AbstractActionsHandler pasteHandler = new AbstractActionsHandler(PASTE_IN_TABLE);
        musicJTable.getActionMap().put("pasteInTable",pasteHandler);
        moviesJTable.getActionMap().put("pasteInTable",pasteHandler);
        docsJTable.getActionMap().put("pasteInTable",pasteHandler);
      
        

        /////////////////////////////covers//////////////////////////////////////////
        
        //mouselistener to change the coverview for the backcoverview
        ChangeCoverListener changeCoverListener = new ChangeCoverListener();
        coversView.addMouseListener(changeCoverListener);
        
        //handler to view covers on selectFrameCover
        ViewCoverHandler viewHandler = new ViewCoverHandler();
        spinnerCovers.addChangeListener(viewHandler);
        
        
        /////////////////////////////player//////////////////////////////////////////
        //handler to close de player when closing the window
        ClosePlayerHandler closePlayerHandler = new ClosePlayerHandler();
        playerFrame.addWindowListener(closePlayerHandler);
        
        //handler to edit name of played files
        SongEditorHandler songEditorHandler = new SongEditorHandler();
        DefaultCellEditor dcePlayer = (DefaultCellEditor) playListTable.getDefaultEditor(Object.class);
        dcePlayer.addCellEditorListener(songEditorHandler);
        
        //handlers to manage the slider of mp3player     
        //controls the time elapsed
        PlayingHandler playingHandler = new PlayingHandler();
        mp3Player.setPlayingListener(playingHandler);
        
        //manages the skipping of the song played
        SongSliderHandler songSliderHandler = new SongSliderHandler();
        songSlider.addMouseListener(songSliderHandler);
        
        
        //Equalization slider handler
        EqSliderHandler eqSliderHandler = new EqSliderHandler();
        for (i=0;i<10;i++){
        	equalizer[i].addChangeListener(eqSliderHandler);
        }
        
        /////////////////////////////////lyrics/////////////////////////////////////////
        //handler to close de player when closing the window
        CloseLyricsFrameHandler closeLyricsFrameHandler = new CloseLyricsFrameHandler();
        lyricsFrame.addWindowListener(closeLyricsFrameHandler);
             
        this.setVisible(true);       
    }


///////////////////////////////////////////////////////////////////////////////////
//////////////////////ERRORS/////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////

	private void errorDir(String dir, JFrame f) {
		JOptionPane.showMessageDialog(f,"El directorio especificado no es correcto: " + dir);
	}

	private void errorSint(String dir) {
		reviewView.append("El directorio no responde a las especificaciones habituales: "+ dir + "\n");
	}

	private void errorFileNF(String dir, JFrame f) {
		JOptionPane.showMessageDialog(f,"El fichero especificado no se ha encontrado o no es correcto: "+ dir);
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////METODOS DE BUSQUEDA DE FICHEROS////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
    

    //METODO QUE DEVUELVE PATH DE UN DISCO DETERMINADO
    private String buscarDisco(String nombreGrupo, String nombreDisco, String pathDisco) {
        String resul = "";
        File fPathDisc;
        String currentGroup, currentDisc;
        int posGuion = -1, longNombre = 0, group = -1, disc = -1;


        fPathDisc = new File(pathDisco);
        if (fPathDisc.isDirectory() == false) {
        } else {
            String[] grupos = fPathDisc.list();
            Integer tam = grupos.length;
            if (tam == 0) {
            } else {

                //para todos los grupos de la carpeta

                for (int j = 0; j < tam; j++) {
                    currentGroup = grupos[j];

                    File discosGrupoF = new File(pathDisco + "\\" + nombreGrupo);
                    if (discosGrupoF.isDirectory() == false) {
                    } else {
                        String[] discosGrupo = discosGrupoF.list();
                        Integer numeroDiscos = discosGrupo.length;

                        //para todos los discos de este grupo

                        for (int k = 0; k < numeroDiscos; k++) {
                            File discoF = new File(pathDisco + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
                            if (discoF.isDirectory() == false) {
                            } else {
                                posGuion = discosGrupo[k].indexOf("-");

                                if (posGuion < 0) {
                                } else {
                                    longNombre = discosGrupo[k].length();
                                    currentDisc = discosGrupo[k].substring(posGuion + 1, longNombre);
                                    Collator comparador = Collator.getInstance();
                                    comparador.setStrength(Collator.PRIMARY);
                                    group = comparador.compare(currentGroup, nombreGrupo);
                                    disc = comparador.compare(currentDisc, nombreDisco);
                                    if ((group == 0) && (disc == 0)) {
                                        resul = discoF.getAbsolutePath();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return resul;
    }

    
    //METODO QUE DEVUELVE EL DIRECTORIO DE UN GRUPO DETERMINADO
    private File buscarGrupo(String nombreGrupo, File pathDisco) {
        File resul = null;
 
        String currentGroup;
        int group = -1;

        if (pathDisco.isDirectory() == false) {
            errorDir(pathDisco.toString(), f);
        } else {
            String[] grupos = pathDisco.list();
            Integer tam = grupos.length;
            if (tam == 0) {
                errorDir(pathDisco.toString(), f);
            } else {
                for (int j = 0; j < tam; j++) {
                    currentGroup = grupos[j];
                    File discosGrupoF = new File(pathDisco + "\\" + nombreGrupo);                
                    Collator comparador = Collator.getInstance();
                    comparador.setStrength(Collator.PRIMARY);
                    group = comparador.compare(currentGroup, nombreGrupo);
                    if (group == 0) {
                        resul = discosGrupoF.getAbsoluteFile();
                        break;
                    }
                }
            }
        }
        return resul;
    }
    
    
    /////////method that searches for lyrics file in music folder
    private File searchLyricsFile(){
    	File lyrics = null;
      	String[] files = null;

 		if (lyricsPath!=null) files=lyricsPath.list();
 	    if (files!=null){
	        int tam = files.length;
	        if (tam == 0) { //no files
	        } else { //for every file in this folder
	            for (int j = 0; j < tam; j++) {
	                if ((files[j].indexOf("lyrics.txt")) > -1) {
	                	lyrics=new File(lyricsPath+"\\"+files[j]);
	                	break;
	                }
	            }	
	        }
 	    }
    	return lyrics;
    }
    
    //método que procesa las variables de configuración
    public void processConfig(String fileName) {
    	String appPath = System.getProperties().getProperty("user.dir"); 
    	File fi=new File(appPath+"\\"+fileName);
		if (fi!=null){
 		   try{
 			   FileReader fr = new FileReader(fi);
 			   BufferedReader bf = new BufferedReader(fr);
 			   String cad;
 			   String param;
 			   int pos;
 			   while ((cad=bf.readLine())!=null) {
 				   pos=0;
 				   if ((pos=cad.indexOf(">"))>-1){
 					   param=cad.substring(pos+1).trim();
	 				   if (cad.indexOf("<port>")>-1){
	 					   port=Integer.parseInt(param);
	 				   }else if (cad.indexOf("<host>")>-1){
	 					   host=param;
	 				   }else if (cad.indexOf("<user>")>-1){
	 					   user=param;
	 				   }else if (cad.indexOf("<password>")>-1){
	 					   pass=param;
	 				   }else if (cad.indexOf("<musicDatabase>")>-1){
	 					   musicDatabase=param;
	 				   }else if (cad.indexOf("<moviesDatabase>")>-1){
	 					   moviesDatabase=param;
	 				   }else if (cad.indexOf("<mysqlPath>")>-1){
	 					   mysqlPath=param;
	 				   }else if (cad.indexOf("<webEMSearch>")>-1){
	 					   webEMSearch=param;
	 				   }else if (cad.indexOf("<webEMBand>")>-1){
	 					   webEMBand=param;
	 				   }else if (cad.indexOf("<webEMRelease>")>-1){
	 					   webEMRelease=param;
	 				   }else if (cad.indexOf("<webEMLyrics>")>-1){
	 					   webEMLyrics=param;
	 				   }else if (cad.indexOf("<webMBSearch>")>-1){
	 					   webMBSearch=param;
	 				   }else if (cad.indexOf("<webMBBand>")>-1){
	 					   webMBBand=param;
	 				   }else if (cad.indexOf("<ftpUser>")>-1){
	 					   ftpUser=param;
				   	   }else if (cad.indexOf("<ftpPswd>")>-1){
	 					   ftpPassword=param;
				   	   }else if (cad.indexOf("<ftpHost>")>-1){
				   		   ftpHost=param;
 				   	   }else if (cad.indexOf("<ftpDirectory>")>-1){
				   		   ftpDirectory=param;
 				   	   }else if (cad.indexOf("<blogUser>")>-1){
 				   		   blogUser=param;
 				   	   }else if (cad.indexOf("<blogPswd>")>-1){
 				   		   blogPswd=param;
 				   	   }else if (cad.indexOf("<musicUpload>")>-1){
 				   		   musicUpload=param;
 				   	   }else if (cad.indexOf("<moviesUpload>")>-1){
 				   		   moviesUpload=param;
 				   	   }else if (cad.indexOf("<docsUpload>")>-1){
 				   		   docsUpload=param;
 				   	   }
 				   }
 			   }  			  
 			   bf.close();
 			   fr.close();
 		   }catch(Exception ex){
 			   ex.printStackTrace();
 			   errorFileNF(fileName,f);
 		   }
 	    }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////METHODS TO COPY FILES//////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //copy files from one folder to another 
    public boolean copyFiles(File fromFile, File toFile){
        //System.out.println("recibimos los paths"+fromFile.getAbsolutePath()+" y "+toFile.getAbsolutePath());
        File currentFile;
        File newDir;
        if (fromFile.isDirectory()) {
            String [] files = fromFile.list();
            for (int i = 0; i < files.length; i++) {
                currentFile=new File(fromFile.getAbsolutePath()+"\\"+files[i]);
                if (currentFile.isDirectory()){
                    newDir = new File(toFile+"//"+currentFile.getName());
                    if (newDir.exists()) 
                    	{
                    		reviewView.append("File already exists: "+newDir.getAbsolutePath()+"\n");
                    		return true;
                    	}
                    else {
                        if (!newDir.mkdir()) errorFileNF(newDir.getName(),this);
                           else copyFiles(currentFile,newDir);
                    }
                }else{
                    newDir=new File(toFile.getAbsolutePath()+"\\"+files[i]);
                    //System.out.println("copying file "+currentFile.getAbsolutePath()+"\n into "+newDir.getAbsolutePath());
                    fileCopy(currentFile,newDir);
                }
            }
        } else {
            newDir= new File(toFile.getAbsolutePath()+"\\"+fromFile.getName());
            //System.out.println("(2) copying file "+fromFile.getAbsolutePath()+"\n into "+newDir.getAbsolutePath());
            fileCopy(fromFile,newDir);
        }
        return false;
    }

    //copy folder and files inside folder to another folder already created
    public boolean copyFolder(File fromFile, File toFile){

        File newDir = new File(toFile+"//"+fromFile.getName());
        if (!newDir.mkdir()){
        	errorFileNF(newDir.getName(),this);
        	return true;
        }
        else return copyFiles(fromFile,newDir);
    }

    //copy file
    public void fileCopy(File fromFile, File toFile){
        FileInputStream fromStream = null;
        FileOutputStream toStream = null;
        try {
            fromStream = new FileInputStream(fromFile);
            toStream = new FileOutputStream(toFile);
            byte[] buffer = new byte[32768];
            int bytesRead;

            while ((bytesRead = fromStream.read(buffer)) != -1) {
                toStream.write(buffer, 0, bytesRead); // write
            }
        } catch (IOException ex) {
        	reviewView.append("Could not copy file "+fromFile+ " into "+toFile+"\n");
        }
        finally {
            if (fromStream != null) {
                try {
                    fromStream.close();
                } catch (IOException e) {}
            }
            if (toStream != null) {
                try {
                    toStream.close();
                } catch (IOException e) {}
            }
        }
    }
  
    


////////////////////////////////////////other methods////////////////////////////////////////////////////
////////////////////////////////////////other methods////////////////////////////////////////////////////    
////////////////////////////////////////other methods////////////////////////////////////////////////////
    
    ////////method to paste in multiple cells in the table
    public void multiPasteInTable(){
		String data;
		try {
			data = (String) sysClipboard.getData(DataFlavor.stringFlavor);
			selectedModel=transformViewSelectedToModel(selectedView);
			for (int i=0;i<selectedModel.size();i++){
				switch(multiPane.getSelectedIndex()){
					case IND_MUSIC_TAB:
						musicTabModel.setValueAt(data, selectedModel.get(i), selectedModelColumn);
						Disc newDisc=musicTabModel.getDiscAtRow(selectedModel.get(i));
				        Disc previousDisc=musicDataBase.getDisc(newDisc.id);
				        //adding the undo/redo effect object
				        undoManager.addEdit(new music.db.UnReUpdate(musicDataBase,newDisc,previousDisc,selectedModel.get(i)));		        
				        musicDataBase.updateDisc(newDisc,selectedModel.get(i));	
						break;
					case IND_MOVIES_TAB:
						moviesTabModel.setValueAt(data, selectedModel.get(i), selectedModelColumn);
						Movie newMovie=moviesTabModel.getMovieAtRow(selectedModel.get(i));
				        Movie previousMovie=moviesDataBase.getMovie(newMovie.id);
				        //adding the undo/redo effect object 
				        undoManager.addEdit(new movies.db.UnReUpdate(moviesDataBase,newMovie,previousMovie,selectedModel.get(i)));		        
				        moviesDataBase.updateMovie(newMovie,selectedModel.get(i));	
						break;
					case IND_DOCS_TAB:
						docsTabModel.setValueAt(data, selectedModel.get(i), selectedModelColumn);
						Doc newDoc=docsTabModel.getDocAtRow(selectedModel.get(i));
				        Doc previousDoc=docsDataBase.getDoc(newDoc.id);
				        //adding the undo/redo effect object 
				        undoManager.addEdit(new docs.db.UnReUpdate(docsDataBase,newDoc,previousDoc,selectedModel.get(i)));		        
				        docsDataBase.updateDoc(newDoc,selectedModel.get(i));	
						break;
				}

			}
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void showCover(String type){
        boolean present=false;
        int numArchivos, numImageFiles, found, indexCover = 0;
        File pathDisc;

        if (((String) musicTabModel.getValueAt(selectedModelRow, COL_PRESENT)).compareTo("YES") == 0) present = true;

		if (backUpConnected && present) {
			pathDisc = (File) musicTabModel.getValueAt(selectedModelRow, COL_PATH);
			String[] listaArchivos = pathDisc.list();
			numArchivos = listaArchivos.length;
			found = 0;
			numImageFiles = 0;
			for (int i = 0; i < numArchivos; i++) {
				listaArchivos[i] = listaArchivos[i].toLowerCase();
				if (((listaArchivos[i].indexOf(".jpg") > -1) || (listaArchivos[i].indexOf(".gif")) > -1)) {
					numImageFiles++;// covers found!!
					if (listaArchivos[i].indexOf(type) > -1) {
						found = 1;// front cover found!!
						indexCover = i;
						break;
					} else
						found = 2;// covers found but not named front cover
				}
			}
			if (found == 1) {
				if (type.compareTo("front") == 0) currentFrontCover = true;
				putImage(coversView,pathDisc + "\\" + listaArchivos[indexCover]);
				splitRight.setTopComponent(coversView);
			} else if (found == 2) {
				splitRight.setTopComponent(COVERS_NOT_NAMED_PROP_MSG);

				List<String> imageFiles = new LinkedList<String>();
				int currentImage = 0;
				for (int i = 0; i < numArchivos; i++) {
					if (((listaArchivos[i].indexOf(".jpg") > -1) || (listaArchivos[i].indexOf(".gif")) > -1)) {
						imageFiles.add(listaArchivos[i]);
						currentImage++;
					}
				}

				spinnerCoversM.setList(imageFiles);
				putImage(coversView,pathDisc + "\\" + imageFiles.get(0));
				selectCoverFrame.getContentPane().add(coversView);
				selectCoverFrame.setVisible(true);

			} else {
				currentFrontCover = false;
				splitRight.setTopComponent(COVERS_NOT_FOUND_MSG);
			}
		} else {
			currentFrontCover = false;
			splitRight.setTopComponent(COVERS_NOT_FOUND_MSG);
		}
		System.gc();
	}
//METHOD TO SHOW AN IMAGEFILE IN COVERSVIEW
    public void putImage(JLabel labelFrom,String file) {
        origIcon = new ImageIcon(file);
        imagen = origIcon.getImage();
        imagen = imagen.getScaledInstance(400, 400, Image.SCALE_FAST);
        scaledIcon.setImage(imagen);
        labelFrom.setIcon(scaledIcon);
        labelFrom.repaint();
    }

//method to save current review in the database
    public void saveCurrentReview(){
    	String review = reviewView.getText();
    	review=review.replace("\"","\\\"");
    	musicTabModel.setValueAt(review,selectedModelRow,COL_REVIEW);
    	musicDataBase.updateReviewOnly(musicTabModel.getDiscAtRow(selectedModelRow));
    }

//method to save current lyrics in a file  
    public void saveCurrentLyrics(){
    	File fileToWrite;
		if (lyricsFile==null){    	
			fileToWrite=new File (lyricsPath.getAbsolutePath()+"\\lyrics.txt");
			try{
				fileToWrite.createNewFile();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}else fileToWrite=lyricsFile;
		
		try{
			FileWriter fw = new FileWriter(fileToWrite);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(lyricsView.getText());
			bw.close();
			fw.close();
			JOptionPane.showMessageDialog(lyricsFrame, "File saved successfully");
		}catch(Exception ex){
			ex.printStackTrace();
		}
    	
    }
    
    //method to transform Rows selected in View to Model
    public List<Integer> transformViewSelectedToModel(List<Integer> selectedView){
    	selectedModel.clear();
    	for (int i=0;i<selectedView.size();i++){
    		switch(multiPane.getSelectedIndex()){
			case IND_MUSIC_TAB:
				selectedModel.add(new Integer(musicJTable.convertRowIndexToModel(selectedView.get(i))));
				break;
			case IND_MOVIES_TAB:
				selectedModel.add(new Integer(moviesJTable.convertRowIndexToModel(selectedView.get(i))));
				break;
			case IND_DOCS_TAB:
				selectedModel.add(new Integer(docsJTable.convertRowIndexToModel(selectedView.get(i))));
				break;	
    		}
    	}	
    	return selectedModel;
    }
  //method to manage layout of playlist table  
   public void managePlayListTable(){
	   ////managing layout
       try{
       	TableColumn c = playListTable.getColumn("t");
       	playListTable.removeColumn(c);
       	c = playListTable.getColumn("p");
       	playListTable.removeColumn(c);
       	c = playListTable.getColumn("change");
       	playListTable.removeColumn(c);
       	c = playListTable.getColumn("currentSong");
       	playListTable.removeColumn(c);
           c = playListTable.getColumn("File name");
           c.setMinWidth(180);
           c.setPreferredWidth(180);
           c = playListTable.getColumn("Length");
           c.setMinWidth(35);
           c.setPreferredWidth(35);
           c = playListTable.getColumn("Group");
           c.setMinWidth(130);
           c.setPreferredWidth(130);
           c = playListTable.getColumn("Album");
           c.setMinWidth(200);
           c = playListTable.getColumn("Tag title");
           c.setMinWidth(200);
           c.setPreferredWidth(200);
           c = playListTable.getColumn("Bitrate");
           c.setMinWidth(40);
           c.setPreferredWidth(40);
           c = playListTable.getColumn("Sampling Format");
           c.setMinWidth(30);
           c.setPreferredWidth(30);
       	
       }catch(IllegalArgumentException ex)
       {
       //	System.out.println("Error pintando tabla de reproducción!!");
       	//ex.printStackTrace();
       }
       
   }
   
   //method to return indexes of discs which mark is over than provided
   public List<Integer> getListOfDiscsByMark(Double mark){
	   List<Integer> list = new LinkedList<Integer>();
	   Double currentMark=new Double(0.0);
	   String sMark=new String("");
	   for(int currentIndex=0;currentIndex<musicTabModel.getRowCount();currentIndex++){
		   sMark=(String)musicTabModel.getValueAt(currentIndex, COL_MARK);
		   try{
			   currentMark=Double.parseDouble(sMark);
			   if (currentMark>=mark) list.add(currentIndex);
		   }catch(NumberFormatException e){
			   //nothing to do
		   }
	   }
	   return list;	   
   }
    

    ///////////////////////////////////HANDLERS///////////////////////////////////////////
    ///////////////////////////////////HANDLERS///////////////////////////////////////////
    ///////////////////////////////////HANDLERS///////////////////////////////////////////

    ///////////////////////////////////ACTION HANDLERS///////////////////////////////////////////
 
    ////////////////////////////////MENU DATABASE HANDLERS///////////////////////////////////////////////
    ////////////////////////////////MENU DATABASE HANDLERS///////////////////////////////////////////////
    
   private class SaveReviewHandler implements ActionListener {

       public void actionPerformed(ActionEvent event) {
    	   saveCurrentReview();
       }
   } //FIN HANDLER SAVEREVIEW


  private class DelItemHandler implements ActionListener {

       public void actionPerformed(ActionEvent evento) {
           if (selectedModelRow>-1) {
        	   switch(multiPane.getSelectedIndex()){
   					case IND_MUSIC_TAB:
   		        	   Disc disc=musicTabModel.getDiscAtRow(selectedModelRow);
   		               undoManager.addEdit(new music.db.UnReDelete(musicDataBase,disc,musicTabModel));
   		        	   musicDataBase.deleteDisc(selectedModelRow);
   					break;
   					case IND_MOVIES_TAB:
   						Movie movie=moviesTabModel.getMovieAtRow(selectedModelRow);
   						undoManager.addEdit(new movies.db.UnReDelete(moviesDataBase,movie,moviesTabModel));
   		        	    moviesDataBase.deleteMovie(selectedModelRow);
   					break;	
   					case IND_DOCS_TAB:
   						Doc doc=docsTabModel.getDocAtRow(selectedModelRow);
   						undoManager.addEdit(new docs.db.UnReDelete(docsDataBase,doc,docsTabModel));
   		        	    docsDataBase.deleteDoc(selectedModelRow);
   					break;	
       			}
           }
           else reviewView.append("Please select an item\n");
       }
   } //FIN HANDLER DELITEM

  private class AddItemHandler implements ActionListener {
	   
      public void actionPerformed(ActionEvent evento) {
    	  switch(multiPane.getSelectedIndex()){
			case IND_MUSIC_TAB:
				Disc disc = new Disc();
		        disc.reset();
		        disc.year="0";
		        musicDataBase.insertNewDisc(disc);
				break;
			case IND_MOVIES_TAB:
				Movie movie = new Movie();
				movie.reset();
				movie.year="0";
		        moviesDataBase.insertNewMovie(movie);
				break;
			case IND_DOCS_TAB:
				Doc doc = new Doc();
				doc.reset();
		        docsDataBase.insertNewDoc(doc);
				break;	
  		}          
      }
  } //FIN HANDLER ADDDISC
  
   private class RelDBBUHandler implements ActionListener {

       int posGuion = -1, longNombre = -1, numberFiles = 0;
       String[] files;
      
       public void actionPerformed(ActionEvent evento) {
           fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           int status = fc.showOpenDialog(f);
           if (status == JFileChooser.APPROVE_OPTION){  
	           backUpPath = fc.getSelectedFile();
	    	   if (backUpPath.isDirectory() == false) {
	    		   errorDir(backUpPath.toString(), f);
	    	   } else {
	           String[] grupos = backUpPath.list();
	           Integer tam = grupos.length;
	           if (tam == 0) {
	               errorDir(backUpPath.toString(), f);
	           } else{              //para todos los grupos de la carpeta
	               for (int j = 0; j < tam; j++) {
	                   String nombreGrupo = grupos[j];
	
	                   File discosGrupoF = new File(backUpPath + "\\" + nombreGrupo);
	                   if (discosGrupoF.isDirectory() == false) {
	                   	errorSint(backUpPath + "\\" + nombreGrupo);
	                   } else {
	                       String[] discosGrupo = discosGrupoF.list();
	                       Integer numeroDiscos = discosGrupo.length;
	
	                       //para todos los discos de este grupo
	
	                       for (int k = 0; k < numeroDiscos; k++) {
	                           File discoF = new File(backUpPath + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
	                           if (discoF.isDirectory() == false) {
	                           } else {
	                               posGuion = discosGrupo[k].indexOf("-");
	
	                               if (posGuion < 0) {
	                                   errorSint(backUpPath + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
	                               } else {
	                                   Disc disco = new Disc();
	                                   String anho = discosGrupo[k].substring(0, posGuion);
	                                   anho = anho.trim();
	                                   try {
	                                       //Long anhoLong = Long.decode(anho);
	                                       longNombre = discosGrupo[k].length();
	                                       String nombreDisco = discosGrupo[k].substring(posGuion + 1, longNombre);
	                                       //creamos nuevo disco con los datos leidos
	                                       disco.title = nombreDisco.trim();
	                                       disco.group = nombreGrupo;
	                                       disco.year = anho;
	                                       disco.path = discoF;
	                                       //OPCIONAL!!
	                                       //busqueda de carpetas llamadas cover para avisar que las portadas estan ahi
	                                       files = discoF.list();
	                                       numberFiles = files.length;
	                                       for (int i = 0; i < numberFiles; i++) {
	                                           File fileArchivo = new File(backUpPath + "//" + nombreGrupo + "//" + discosGrupo[k] + "//" + files[i]);
	                                           if (fileArchivo.isDirectory()) {
	                                               files[i] = files[i].toLowerCase();
	
	                                               if (files[i].indexOf("cover") > -1) {
	                                                   disCover.add(disco);
	                                               }
	                                           }
	                                       }
	                                       int pos;
	                                       if((pos=musicTabModel.searchDisc(disco.group,disco.title))!=-1){
	                                           musicTabModel.setValueAt("YES",pos,COL_PRESENT);
	                                           musicTabModel.setValueAt(disco.path, pos,COL_PATH);
	                                       }else{
	                                           reviewView.append("Disc not found in Database:"+nombreGrupo+"::"+discosGrupo[k]+"\n");
	                                       }
	                                       backUpConnected=true;
	                                       menuPlay.setEnabled(true);
	                                       menuPlayRandom.setEnabled(true);
	                                       menuViewLyrics.setEnabled(true);
	                                   } catch (NumberFormatException e) {
	                                       errorSint(backUpPath + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
	                                   }
		
		                             }
		                          }
		                       }
		                   }
		               }
		           }
		       }
           }
       }
   } //FIN HANDLER RELDBBU

   private class AddBUDBHandler implements ActionListener {

       public void actionPerformed(ActionEvent evento) {
           fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           fc.setDialogTitle("Path for music files");
           int status = fc.showOpenDialog(f);
           if (status == JFileChooser.APPROVE_OPTION){  
	           auxPath = fc.getSelectedFile();
	           reviewView.setText("");
	
	           if (auxPath.isDirectory() == false) {
	               errorDir(auxPath.getAbsolutePath(), f);
	           } else {
	               String[] grupos = auxPath.list();
	               Integer tam = grupos.length;
	               if (tam == 0) {
	                   errorDir(auxPath.getAbsolutePath(), f);
	               } else {
	            	   CopyThread copyThread = new CopyThread();
	            	   copyThread.path=auxPath.getAbsolutePath();
	            	   copyThread.folders=grupos;
	            	   copyThread.size=tam;
	            	   copyThread.start();
	               }
	           }
           }
       }
   } //FIN HANDLER ADDBUDB


    private class DBBUPHandler implements ActionListener {

       public void actionPerformed(ActionEvent evento) {           
           fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           fc.setDialogTitle("Path for Backup destination");
           int status = fc.showOpenDialog(f);
           if (status == JFileChooser.APPROVE_OPTION){  
        	   auxPath = fc.getSelectedFile();
        	   if (musicDataBase.makeBackup(auxPath.getAbsolutePath())) JOptionPane.showMessageDialog(f, "File created succesfully: "+auxPath.getAbsolutePath());
           }
       }
   }  //FIN HANDLER DBBUP
    
    
    private class UploadBUPHandler implements ActionListener {

        public void actionPerformed(ActionEvent evento) {           
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle("Path for Backup destination");
            int status = fc.showOpenDialog(f);
            if (status == JFileChooser.APPROVE_OPTION){  
         	   auxPath = fc.getSelectedFile();
         	   boolean ret=false;
         	   switch(multiPane.getSelectedIndex()){
         	   		case IND_MUSIC_TAB:
         	   			ret=musicDataBase.makeBackupCSV(auxPath.getAbsolutePath());
         	   			break;
         	   		case IND_MOVIES_TAB:
         	   			ret=moviesDataBase.makeBackupCSV(auxPath.getAbsolutePath());
         	   			break;
         	   		case IND_DOCS_TAB:
         	   			ret=docsDataBase.makeBackupCSV(auxPath.getAbsolutePath());
         	   			break;	
         	   }
         	   if (ret) {
         		   	File fbup=new File(auxPath.getAbsolutePath()+"\\"+DataBaseTable.nameCSV);
         	        try{         	            
         	        	ftpManager.upload(fbup);
         	        	ret = false;
                  	    switch(multiPane.getSelectedIndex()){
            	   			case IND_MUSIC_TAB:
            	   				ret=webReader.uploadBackup(DataBaseTable.nameCSV,musicUpload);
            	   				break;
            	   			case IND_MOVIES_TAB:
            	   				ret=webReader.uploadBackup(DataBaseTable.nameCSV,moviesUpload);
            	   				break;
            	   			case IND_DOCS_TAB:
            	   				ret=webReader.uploadBackup(DataBaseTable.nameCSV,docsUpload);
            	   				break;	
                  	    }
         	        	if (ret){
         	        		JOptionPane.showMessageDialog(f, "Backup Upload succesful");
         	        	}else JOptionPane.showMessageDialog(f, "Couldn't Insert data once backup loaded");
         	        }catch(IOException e){
         	        	e.printStackTrace();
         	        	JOptionPane.showMessageDialog(f, "Couldn't upload Backup");
         	        }
         	        fbup.delete();
         	        
         		}else JOptionPane.showMessageDialog(f, "Couln't create Backup");         		
            }
        }
    }  //FIN HANDLER UPLPOADBUP

 /*private class RestoreDBBUPHandler implements ActionListener {

       File fileIn;

     public void actionPerformed(ActionEvent evento) {

         JFileChooser fc = new JFileChooser();
         int op = fc.showOpenDialog(f);
         if (op == JFileChooser.APPROVE_OPTION) {
             fileIn = fc.getSelectedFile();
         }
         if (dataBase.restore(fileIn)) JOptionPane.showMessageDialog(f, "Table recovered succesfully");
     }
   }  //FIN HANDLER DBRESTORE
*/
///////////////////////////////////////////////MENU EDIT HANDLERS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
///////////////////////////////////////////////MENU EDIT HANDLERS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
    private class MenuUndoHandler implements ActionListener {

       public void actionPerformed(ActionEvent e) {
           if(undoManager.canUndo()){
               undoManager.undo();
           }else JOptionPane.showMessageDialog(f,"Nothing to undo");
       }

    }//MenuUndo Handler END

  private class MenuRedoHandler implements ActionListener {

       public void actionPerformed(ActionEvent e) {
           if (undoManager.canRedo()) {
               undoManager.redo();
           }else JOptionPane.showMessageDialog(f,"Nothing to redo");
       }
   }//MenuUndo Handler END
  
  
  private class MenuFilterHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String word = JOptionPane.showInputDialog(f,"Write the word you want to filter");
			switch(multiPane.getSelectedIndex()){
  	   		case IND_MUSIC_TAB:
  	   			musicTableSorter.setRowFilter(RowFilter.regexFilter(word));
  	   			break;
  	   		case IND_MOVIES_TAB:
  	   			moviesTableSorter.setRowFilter(RowFilter.regexFilter(word));
  	   			break;
  	   		case IND_DOCS_TAB:
  	   			docsTableSorter.setRowFilter(RowFilter.regexFilter(word));
  	   			break;	
			}
			
		}
	}//END OF FILTER HANDLER
  
  private class MenuPasteHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {			
			multiPasteInTable();
		}
	}//END OF PASTE HANDLER

  
///////////////////////////////////////////COVER MENU HANDLERS///////////////////////////////
///////////////////////////////////////////COVER MENU HANDLERS///////////////////////////////
///////////////////////////////////////////COVER MENU HANDLERS///////////////////////////////
  
   private class ChangeNameHandler implements ActionListener {

       String archivo,rutaArch,type;
       File file;
       Disc disc;

       public void actionPerformed(ActionEvent evento) {
           disc=musicTabModel.getDiscAtRow(selectedModelRow);
           archivo = (String) spinnerCovers.getValue();
           rutaArch = disc.path.getAbsolutePath();
           file = new File(rutaArch + "//" + archivo);
           if (file.canWrite()) {
               if (currentFrontCover) type="front"; else type="back";
               if (file.renameTo(new File(rutaArch + "//" + disc.group + " - " + disc.title + " - " + type+".jpg"))) {
                   JOptionPane.showMessageDialog(selectCoverFrame, "File renamed succesfully");
               } else {
                   JOptionPane.showMessageDialog(selectCoverFrame, "Could not rename file");
               }
           } else {
               JOptionPane.showMessageDialog(selectCoverFrame, "Could not rename file");
           }
           selectCoverFrame.dispose();
       }
   } //FIN HANDLER CHANGE NAME

   private class NoCoverDiscHandler implements ActionListener {

       int posGuion = -1, longNombre = -1, numberFiles = 0, found = 0;
       String[] files;

       public void actionPerformed(ActionEvent evento) {

          
           fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           fc.setDialogTitle("Path for music files");
           int status = fc.showOpenDialog(f);
           if (status == JFileChooser.APPROVE_OPTION){  
	           auxPath = fc.getSelectedFile();
	           String name = JOptionPane.showInputDialog(f, "File name for text with list without cover");
	           
	           try {
	               FileWriter fout = new FileWriter(name + ".txt");
	               BufferedWriter buffer = new BufferedWriter(fout);
	               PrintWriter printer = new PrintWriter(buffer);
	
	               if (auxPath.isDirectory() == false) {
	                   errorDir(auxPath.getAbsolutePath(), f);
	               } else {
	                   String[] grupos = auxPath.list();
	                   Integer tam = grupos.length;
	                   if (tam == 0) {
	                       errorDir(auxPath.getAbsolutePath(), f);
	                   } else {
	
	                       //para todos los grupos de la carpeta
	                       for (int j = 0; j < tam; j++) {
	                           String nombreGrupo = grupos[j];
	
	                           File discosGrupoF = new File(auxPath.getAbsolutePath() + "\\" + nombreGrupo);
	                           if (discosGrupoF.isDirectory() == false) {
	                               errorSint(auxPath.getAbsolutePath() + "\\" + nombreGrupo);
	                           } else {
	                               String[] discosGrupo = discosGrupoF.list();
	                               Integer numeroDiscos = discosGrupo.length;
	
	                               //para todos los discos de este grupo
	
	                               for (int k = 0; k < numeroDiscos; k++) {
	                                   File discoF = new File(auxPath.getAbsolutePath() + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
	                                   if (discoF.isDirectory() == false) {
	                                       errorSint(auxPath.getAbsolutePath() + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
	                                   } else {
	                                       posGuion = discosGrupo[k].indexOf("-");
	
	                                       if (posGuion < 0) {
	                                           errorSint(auxPath.getAbsolutePath() + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
	                                       } else {
	                                           longNombre = discosGrupo[k].length();
	                                           String nombreDisco = discosGrupo[k].substring(posGuion + 1, longNombre);
	                                           files = discoF.list();
	                                           numberFiles = files.length;
	                                           found = 0;
	                                           for (int i = 0; i < numberFiles; i++) {
	                                               files[i] = files[i].toLowerCase();
	                                               if ((((files[i].indexOf(".jpg") > -1) || (files[i].indexOf(".gif")) > -1)) && (files[i].indexOf("front") > -1)) {
	                                                   found = 1;
	                                                   break;
	                                               }
	                                           }
	                                           if (found == 0) {
	                                               printer.println(nombreGrupo + " - " + nombreDisco);
	                                           }
	                                       }
	                                   }
	                               }
	                           }
	                       }
	                   }
	               }
	               printer.close();
	               buffer.close();
	               fout.close();
	           } catch (IOException e) {
	               JOptionPane.showMessageDialog(f, "Cannot write in file");
	           }
           }
       }
   }  //FIN HANDLER LISTA COVERS
   
   private class CoverHandler implements ActionListener {


       String[][] listaCover;
       int numCovers = 0;

       private CoverHandler() {
       }

       public void actionPerformed(ActionEvent evento) {
           if (disCover.size() == 0) {
               JOptionPane.showMessageDialog(f, "No ha sido cargada una lista de covers");
           } else {
               numCovers = disCover.size();
               listaCover = new String[numCovers][2];
               for (int i = 0; i < numCovers; i++) {
                   listaCover[i][0] = disCover.get(i).group;
                   listaCover[i][1] = disCover.get(i).title;
               }
               String[] columnas = {"Disco", "Anho"};
               JTable table = new JTable(listaCover, columnas);
               JScrollPane scrollPane = new JScrollPane(table);
               splitRight.setTopComponent(scrollPane);
           }
       }
   } //FIN HANDLER COVER

   private class MoveCoversHandler implements ActionListener {

       int posGuion1 = -1, posGuion2 = -1;
       String[] portadas;
       String dirCovers, dirDisc, nombreGrupo, nombreDisco, pathDisc;

       public void actionPerformed(ActionEvent evento) {

    	   fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           fc.setDialogTitle("Path for music files");
           int status = fc.showOpenDialog(f);
           if (status == JFileChooser.APPROVE_OPTION){  
	           auxPath = fc.getSelectedFile();
	           dirDisc=auxPath.getAbsolutePath();
	           fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	           fc.setDialogTitle("Path for cover files");
	           status = fc.showOpenDialog(f);
	           if (status == JFileChooser.APPROVE_OPTION){  
		           auxPath = fc.getSelectedFile();
		           dirCovers=auxPath.getAbsolutePath();		           
		           
		           if (auxPath.isDirectory() == false) {
		               errorDir(dirCovers, f);
		           } else {
		               portadas = auxPath.list();
		               int tam = portadas.length;
		               if (tam == 0) {
		                   errorDir(dirCovers, f);
		               } else { //para todas las portadas
		                   for (int i = 0; i < tam; i++) {
		                       File currentCover = new File(dirCovers + "\\" + portadas[i]);
		                       posGuion1 = portadas[i].indexOf("-");
		                       if (posGuion1 < 0) {
		                           errorSint(dirCovers + "\\" + portadas[i]);
		                       } else {
		                           nombreGrupo = portadas[i].substring(0, posGuion1);
		                           nombreGrupo = nombreGrupo.trim();
		                           posGuion2 = portadas[i].lastIndexOf("-");
		                           if (posGuion1 >= posGuion2) {
		                               errorSint(dirCovers + "\\" + portadas[i]);
		                           } else {
		                               nombreDisco = portadas[i].substring(posGuion1 + 1, posGuion2);
		                               nombreDisco = nombreDisco.trim();
		                               pathDisc = buscarDisco(nombreGrupo, nombreDisco, dirDisc);
		                               if (pathDisc.compareTo("") != 0) {
		                                   if (currentCover.canWrite()) {
		                                       if (currentCover.renameTo(new File(pathDisc + "//" + portadas[i]))) {
		                                           reviewView.append("File moved succesfully: " + portadas[i]+"\n");
		                                       } else {
		                                           reviewView.append("Can write but not rename file: " + portadas[i]+ "to "+pathDisc + "//" + portadas[i]+"\n");
		                                       }
		                                   } else {
		                                       reviewView.append("Could not rename file: " + portadas[i]+"\n");
		                                   }
		                               }else reviewView.append(nombreGrupo+"/"+nombreDisco+" not found on folder "+dirDisc+"\n");
		                           }
		                       }
		
		                   }
		               }
		           }
		        }
           }
       }
   }  //FIN HANDLER COPIARPORTADAS
   

   private class CoverBackupHandler implements ActionListener {

       int tam = 0, posGuion;
       String dirDest, dirDisc;


       public void actionPerformed(ActionEvent evento) {

           fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           fc.setDialogTitle("Path for backup destination");
           int status = fc.showOpenDialog(f);
           if (status == JFileChooser.APPROVE_OPTION){  
	           auxPath = fc.getSelectedFile();
	           dirDest=auxPath.getAbsolutePath();
	           fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	           fc.setDialogTitle("Path for music files");
	           status = fc.showOpenDialog(f);
	           if (status == JFileChooser.APPROVE_OPTION){  
		           auxPath = fc.getSelectedFile();
		           dirDisc=auxPath.getAbsolutePath();
		           
		        
		           if (auxPath.isDirectory() == false) {
		               errorDir(dirDisc, f);
		           } else {
		
		               String[] grupos = auxPath.list();
		               tam = grupos.length;
		               if (tam == 0) {
		                   errorDir(dirDisc, f);
		               } else { //para todos los grupos de la carpeta
		                   for (int j = 0; j < tam; j++) {
		                       String nombreGrupo = grupos[j];
		                       File discosGrupoF = new File(dirDisc + "\\" + nombreGrupo);
		                       if (discosGrupoF.isDirectory() == false) {
		                           errorSint(dirDisc + "\\" + nombreGrupo);
		                       } else {
		                           String[] discosGrupo = discosGrupoF.list();
		                           int numeroDiscos = discosGrupo.length; //para todos los discos de este grupo
		                           for (int k = 0; k < numeroDiscos; k++) {
		                               File discoF = new File(dirDisc + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
		                               if (discoF.isDirectory() == false) {
		                                   errorSint(dirDisc + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
		                               } else {
		                                   posGuion = discosGrupo[k].indexOf("-");
		                                   if (posGuion < 0) {
		                                       errorSint(dirDisc + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
		                                   } else {
		                                       String[] listaArchivos = discoF.list();
		                                       int numArchivos = listaArchivos.length;
		                                       for (int i = 0; i < numArchivos; i++) {
		                                           listaArchivos[i] = listaArchivos[i].toLowerCase();
		                                           if (((listaArchivos[i].indexOf(".jpg") > -1) || (listaArchivos[i].indexOf(".gif")) > -1)) {
		                                               if (listaArchivos[i].indexOf("front") > -1) {
		                                                   //front cover found!!
		                                                   try {
		                                                       //copiamos portada a directorio destino
		                                                       File fsrc = new File(dirDisc + "\\" + nombreGrupo + "\\" + discosGrupo[k] + "\\" + listaArchivos[i]);
		                                                       File fdest = new File(dirDest + "\\" + nombreGrupo + " - " + discosGrupo[k] + " - front.jpg");
		                                                       fileCopy(fsrc,fdest);
		                                                       
		                                                   } catch (Exception e) {
		                                                       errorSint(dirDisc + "\\" + nombreGrupo + "\\" + discosGrupo[k]);
		                                                   }
		                                                   break;
		                                               } else {
		                                                   //covers found but not named front cover
		                                               }
		                                           //covers not found
		                                           }
		                                       }
		                                   }
		                               }
		                           }
		                       }
		                   }
		               }
		           }
		       }
	       }
       }
   }  //FIN HANDLER COVERBACKUP
   
///////////////////////////////////////////OTHER OPTION HANDLERS///////////////////////////////
///////////////////////////////////////////OTHER OPTION HANDLERS///////////////////////////////
///////////////////////////////////////////OTHER OPTION HANDLERS///////////////////////////////  
   
   private class CopyReviewsHandler implements ActionListener {

       int posGuion1 = -1, posGuion2 = -1, row=0;
       String[] reviews;
       String dirReviews, nombreGrupo, nombreDisco, nota, review;
       File currentReview;

       @Override
       public void actionPerformed(ActionEvent evento) {

           fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           fc.setDialogTitle("Path for review files");
           int status = fc.showOpenDialog(f);
           if (status == JFileChooser.APPROVE_OPTION){  
	           auxPath = fc.getSelectedFile();
	           dirReviews=auxPath.getAbsolutePath();
	
	           if (auxPath.isDirectory() == false) {
	               errorDir(dirReviews, f);
	           } else {
	               reviews = auxPath.list();
	               int tam = reviews.length;
	               if (tam == 0) {
	                   errorDir(dirReviews, f);
	               } else { //para todas las portadas
	                   for (int i = 0; i < tam; i++) {
	                	   //System.out.println(i);
	                	   if (reviews[i].indexOf(".txt") > -1){
		                       currentReview = new File(dirReviews + "\\" + reviews[i]);
		                       posGuion1 = reviews[i].indexOf("-");
		                       if (posGuion1 < 0) {
		                           errorSint(dirReviews + "\\" + reviews[i]);
		                       } else {
		                           nombreGrupo = reviews[i].substring(0, posGuion1);
		                           nombreGrupo = nombreGrupo.trim();
		                           posGuion2 = reviews[i].lastIndexOf("-");
		                           if (posGuion1 >= posGuion2) {
		                               errorSint(dirReviews + "\\" + reviews[i]);
		                           } else {
		                               nombreDisco = reviews[i].substring(posGuion1 + 1, posGuion2);
		                               nombreDisco = nombreDisco.trim();
		                               nota = reviews[i].substring(posGuion2 + 1, reviews[i].lastIndexOf("."));
		                               nota = nota.trim();
		                               review="";
		                        	   try{
		                        	       FileReader fr = new FileReader(currentReview);
		                        		   BufferedReader bf = new BufferedReader(fr);
		                        		   String cad;
		                        		   while ((cad=bf.readLine())!=null) {
		                        			   review=review+cad+"\n";
		                        		   } 
		                        		   bf.close();
		                        		   fr.close();
		                        		   Disc disc= new Disc();
			                        	   disc=musicDataBase.getDiscByDG(nombreGrupo,nombreDisco);
			                        	   row = musicTabModel.searchDisc(disc.id);
			                        	   disc.review=review;
			                        	   disc.mark=nota;
			                        	   if (disc.id!=0) musicDataBase.updateDisc(disc, row);
			                        	   else reviewView.append("Failed to save review "+nombreGrupo+ " "+nombreDisco+"\n");
		                        		}catch(Exception ex){
		                        		   ex.printStackTrace();
		                        		   reviewView.append("Failed to save review "+nombreGrupo+ " "+nombreDisco+"\n");
		                        	   }
		                           }
		                       }
	                   		}
	                   }
	               }
	           }

           }
       }
   }  //FIN HANDLER COPYRREVIEWS
   
   private class ViewNewDiscsHandler implements ActionListener {
	   
	private Look4NewDiscsThread lookThread;
	
	ViewNewDiscsHandler(String web){
		   lookThread = new Look4NewDiscsThread();
		   lookThread.web=web;
	   }
	@Override
	public void actionPerformed(ActionEvent arg0) {
		lookThread.start();
	}
   
   }
   
   
   
////////////////////////////////////OTHER BUTTONS HANDLERS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
////////////////////////////////////OTHER BUTTONS HANDLERS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   private class SaveLyricsButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			saveCurrentLyrics();
		}
  }//END OF SAVELYRICSHANDLER
   
   private class DownloadLyricsButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			GetLyricsThread getLyricsThread = new GetLyricsThread();
			getLyricsThread.start();
		}
 }//END OF SAVELYRICSHANDLER
   

///////////////////////////////////////POPUPMENUS ACTIONLISTENERS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
///////////////////////////////////////POPUPMENUS ACTIONLISTENERS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
   
   private class PopupMenuSortDefaultHandler implements ActionListener{

        public void actionPerformed(ActionEvent e) {
			switch(multiPane.getSelectedIndex()){
  	   		case IND_MUSIC_TAB:
	  	   		Disc.caseCompare=0;
	            musicTabModel.sort();         
  	   			break;
  	   		case IND_MOVIES_TAB:
  	   			Movie.caseCompare=0;
  	   			moviesTabModel.sort(); 
  	   			break;
  	   		case IND_DOCS_TAB:
  	   			Doc.caseCompare=0;
  	   			docsTabModel.sort(); 
  	   			break;
			}
               
        }
    } //ORDERING MENU HANDLER END
   
   private class PopupMenuSortByFieldHandler implements ActionListener{

        public void actionPerformed(ActionEvent e) {
			switch(multiPane.getSelectedIndex()){
  	   		case IND_MUSIC_TAB:
  	            Disc.caseCompare=selectedModelColumn;
  	            musicTabModel.sort();       
  	   			break;
  	   		case IND_MOVIES_TAB:
  	   			Movie.caseCompare=selectedModelColumn;
	            moviesTabModel.sort(); 
  	   			break;
  	   		case IND_DOCS_TAB:
  	   			Doc.caseCompare=selectedModelColumn;
	            docsTabModel.sort(); 
  	   			break;
			}

        }
    } //ORDERING MENU HANDLER END

   private class PopupMenuPlayHandler implements ActionListener{

       File pathDisc;
        public void actionPerformed(ActionEvent e) {
            try {
            	
                pathDisc = (File) musicTabModel.getValueAt(selectedModelRow,COL_PATH);
                playList.removeAllRows();
                playList.numSongs=0;
                playList.searchFiles(pathDisc,true,(String)musicTabModel.getValueAt(selectedModelRow,COL_GROUP),(String)musicTabModel.getValueAt(selectedModelRow,COL_TITLE));
                playListTable.setModel(playList);
                //handler to play the song selected wit doubleclick on list
                PlayThisSongHandler playThisSongHandler = new PlayThisSongHandler();
                playListTable.addMouseListener(playThisSongHandler);   
                
                managePlayListTable();
                
                mp3Player.playList(playList);
                timerThread = new TimerThread();
                timerThread.setDaemon(true);
                timerThread.start();
                playerFrame.setVisible(true);
                int divLoc=Math.min(400, playListTable.getHeight()+ pauseResumeButton.getHeight()+stopButton.getHeight()+songSlider.getHeight()+songInformation.getHeight()+60);
                splitPlayer.setDividerLocation(divLoc);

            } catch (MP3FilesNotFound ex) {
                reviewView.append("Cannot find playable files\n");
            }
        }
    } //Playing disc
   
   private class PlayRandomHandler implements ActionListener{

       private Double mark=-1.0;

        public void actionPerformed(ActionEvent e) {
            	mp3Player.randomPlay=true;
            	while ((mark<=0)||(mark>=10)){
	            	String sMark = JOptionPane.showInputDialog("Please insert the minimum mark of discs which to play");
	            	try{
	            		mark=Double.valueOf(sMark).doubleValue();
	            	}catch(NumberFormatException ex){	            		
	            	}
	            	if ((mark<=0)||(mark>=10)) JOptionPane.showMessageDialog(f,"Mark must be between 0 and 10");
            	}
            	randomPlayThread = new RandomPlayThread();
            	randomPlayThread.mark=mark;
            	randomPlayThread.start();
        }
    } //Playing disc
   
   private class PopupMenuPasteHandler implements ActionListener{

        public void actionPerformed(ActionEvent e) {
        	multiPasteInTable();
        }      
    } 
      
   private class PopupMenuViewLyricsHandler implements ActionListener{
	   
       public void actionPerformed(ActionEvent e) {
    	   lyricsView.setText("");
    	   if (((JMenuItem)e.getSource()).getName().compareTo(LYR_PLAYER_NAME)==0) {
    	    	lyricsPath = playList.pathFolder;
    	    	lyricsGroup=playList.group;
    	    	lyricsAlbum=playList.album;
    	    }
    	    else if (((JMenuItem)e.getSource()).getName().compareTo(LYR_MENU_NAME)==0)  {
    	    	lyricsPath = musicTabModel.getDiscAtRow(selectedModelRow).path;
    	    	lyricsGroup=musicTabModel.getDiscAtRow(selectedModelRow).group;
    	    	lyricsAlbum=musicTabModel.getDiscAtRow(selectedModelRow).title; 	    	
    	    }
    	   if ((lyricsFile=searchLyricsFile())!=null){
    		   try{
    			   FileReader fr = new FileReader(lyricsFile);
    			   BufferedReader bf = new BufferedReader(fr);
    			   String text="";
    			   String cad;
    			   while ((cad=bf.readLine())!=null) {
    				   text=text+"\n"+cad;
    			   } 
    			   lyricsView.setText(text);
    			   bf.close();
    			   fr.close();
    		   }catch(Exception ex){
    			   ex.printStackTrace();
    		   }
    	   }
    	   lyricsView.setCaretPosition(0); //to place the scroll on the top
    	   lyricsFrame.setVisible(true);
       }
   }
   
   private class PopupMenuShowBigCoverHandler implements ActionListener{

		@Override
			public void actionPerformed(ActionEvent arg0) {		
				imagen = origIcon.getImage();
				int width=origIcon.getIconWidth();
				int height=origIcon.getIconHeight();
				if ((width>MAX_COVERS_DIM.width)&&(height>MAX_COVERS_DIM.height)){
					width=MAX_COVERS_DIM.width;
					height=MAX_COVERS_DIM.height;
				}
				imagen = imagen.getScaledInstance(width,height, Image.SCALE_FAST);
				bigIcon.setImage(imagen);
				bigCoversView.setIcon(bigIcon);
				bigCoversView.setMinimumSize(new Dimension(width,height));
				//bigCoversView.setMaximumSize(bigCoversView.getMinimumSize());
				//bigCoverFrame.setMinimumSize(new Dimension(width,height));			
				bigCoversFrame.setSize(width,height);
				//bigCoverFrame.setMaximumSize(bigCoverFrame.getMinimumSize());
		        
				bigCoversFrame.setVisible(true);
			}
	   }//END OF PopupMenuShowBigCoverHandler
   
   
      
   ////// OTHER EVENT HANDLERS ///////////////////////////////////////////////////////////////////////////////
   ////// OTHER EVENT HANDLERS ///////////////////////////////////////////////////////////////////////////////
   ////// OTHER EVENT HANDLERS ///////////////////////////////////////////////////////////////////////////////
 
     
   private class ViewCoverHandler implements ChangeListener {

       File pathDisco;

       // manejar evento de cambio en lista
       public void stateChanged(ChangeEvent e) {
           JSpinner spinner = (JSpinner) e.getSource();
           String nameFile = (String) spinner.getValue();
           pathDisco=(File)musicTabModel.getValueAt(selectedModelRow,COL_PATH);
           putImage(coversView,pathDisco + "\\" + nameFile);
       }
   } //FIN HANDLER VIEW COVERS
   
   private class CellEditorHandler implements CellEditorListener {

     
       public void editingStopped(ChangeEvent e) {
    	   switch(multiPane.getSelectedIndex()){
 	   		case IND_MUSIC_TAB:
 	   			Disc newDisc=musicTabModel.getDiscAtRow(selectedModelRow);
 	   			Disc previousDisc=musicDataBase.getDisc(newDisc.id);
 	   			//adding the undo/redo effect object
 	   			undoManager.addEdit(new music.db.UnReUpdate(musicDataBase,newDisc,previousDisc,selectedModelRow));
 	   			musicDataBase.updateDisc(newDisc,selectedModelRow);       
 	   			break;
 	   		case IND_MOVIES_TAB:
 	   			Movie newMovie=moviesTabModel.getMovieAtRow(selectedModelRow);
 	   			Movie previousMovie=moviesDataBase.getMovie(newMovie.id);
 	   			//adding the undo/redo effect object
 	   			undoManager.addEdit(new movies.db.UnReUpdate(moviesDataBase,newMovie,previousMovie,selectedModelRow));
 	   			moviesDataBase.updateMovie(newMovie,selectedModelRow);
 	   			break;		
 	   		case IND_DOCS_TAB:
 	   			Doc newDoc=docsTabModel.getDocAtRow(selectedModelRow);
 	   			Doc previousDoc=docsDataBase.getDoc(newDoc.id);
 	   			//adding the undo/redo effect object
 	   			undoManager.addEdit(new docs.db.UnReUpdate(docsDataBase,newDoc,previousDoc,selectedModelRow));
 	   			docsDataBase.updateDoc(newDoc,selectedModelRow);
 	   			break;	
			}
           
       }

       public void editingCanceled(ChangeEvent e) {}
   }//END OF CELLEDITOR HANDLER
   
  
   //EDITOR HANDLER FOR COMBOBOX IN DOCSTABLE 

  public class ComboCellEditor extends AbstractCellEditor implements TableCellEditor{

	private static final long serialVersionUID = 1L;
	JComboBox combo;
		
	   public ComboCellEditor() {
		   this.combo = new JComboBox();
		   for (DocTheme docTheme : DocTheme.values()){
			   combo.addItem(docTheme);
		   }
		   ComboActionListener comboAction = new ComboActionListener();
		   combo.addItemListener(comboAction);
	   }	
	   @Override
	   public Object getCellEditorValue() {
		   return combo.getSelectedItem();
	   }
	   
	  
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		selectedViewRow = row;       
        lastSelectedViewRow = row; 
        selectedViewColumn = column;
        selectedView.clear();
 		selectedModelRow = table.convertRowIndexToModel(selectedViewRow);
 		lastSelectedModelRow = table.convertRowIndexToModel(lastSelectedViewRow);
 		selectedModelColumn = table.convertColumnIndexToModel(selectedViewColumn);
		combo.setSelectedItem(value);
		return combo;
	}
	   
   }

   //EVENT STATE HANDLER FOR COMBOBOX IN DOCSTABLE
  private class ComboActionListener implements ItemListener{

		@Override
	public void itemStateChanged(ItemEvent e) {
		DocTheme docTheme;
		docTheme=(DocTheme)((JComboBox)e.getSource()).getSelectedItem();
		if (e.getStateChange()==ItemEvent.SELECTED){
			Doc newDoc=docsTabModel.getDocAtRow(selectedModelRow);		
			newDoc.setTheme(docTheme);
		   	Doc previousDoc=docsDataBase.getDoc(newDoc.id);
		   	//adding the undo/redo effect object
		   	undoManager.addEdit(new docs.db.UnReUpdate(docsDataBase,newDoc,previousDoc,selectedModelRow));
		   	docsDataBase.updateDoc(newDoc,selectedModelRow);
		}	
		
	}

   }
     
  //SELECTING CELLS IN EACH TABLE
   private class SelectItemHandler implements ListSelectionListener {

       public void valueChanged(ListSelectionEvent e) {

          if(e.getValueIsAdjusting()){return;} //to avoid double triggering when deselecting/selecting cells

           ListSelectionModel lsm = (ListSelectionModel) e.getSource();
           if (!lsm.isSelectionEmpty()){
               selectedViewRow = lsm.getMinSelectionIndex();       
               lastSelectedViewRow = lsm.getMaxSelectionIndex(); 
               
               JTable currentTable=null;
               switch(multiPane.getSelectedIndex()){
               	case IND_MUSIC_TAB:
    	   			currentTable=musicJTable;
    	   			break;
    	   		case IND_MOVIES_TAB:
    	   			currentTable=moviesJTable;
    	   			break;
    	   		case IND_DOCS_TAB:
    	   			currentTable=docsJTable;
    	   			break;
   				}
               selectedViewColumn = currentTable.getSelectedColumn();
               selectedView.clear();
	   		   selectedModelRow = currentTable.convertRowIndexToModel(selectedViewRow);
	   		   lastSelectedModelRow = currentTable.convertRowIndexToModel(lastSelectedViewRow);
	   		   selectedModelColumn = currentTable.convertColumnIndexToModel(selectedViewColumn);
               
             //only way to find wich rows are selected in multiple selection mode (for a DefaultListSelectionModel)
               for (int i = selectedViewRow; i <= lastSelectedViewRow; i++) { 
                   if (lsm.isSelectedIndex(i)) {
                	   selectedView.add(new Integer(i)); 
                   }
               }
               if (multiPane.getSelectedIndex()==IND_MUSIC_TAB){
            	   String review=(String)musicTabModel.getValueAt(selectedModelRow, COL_REVIEW);
            	   review=review.replace("\\\"","\"");
            	   reviewView.setText(review);
            	   showCover("front");
               }
           }   
       }      
   } //FIN HANDLER SELECCION DE DISCO
   
  //////////////////////////////////KEYBOARD LISTENERS///////////////////////////////////////
   
   //LISTENER FOR KEYBOARD USED FOR SEARCHING 
   private class TableKeyListener extends KeyAdapter {

       char letter;
       long currentTime;
       int startingRow;
       
       @Override
       public void keyTyped(KeyEvent e) {
           //this function selects the first cell (in the selected column) that starts with the key typed
    	   letter=e.getKeyChar();
    	   currentTime=System.currentTimeMillis();
    	   if (currentTime-lastTime<1500){
    		   currentCharPos++;
    		   startingRow=selectedModelRow;
    	   }
    	   else{ 
    		   currentCharPos=0;
    		   startingRow=0;
    	   }
    	   lastTime=currentTime;
    	   if ((letter >= 'a' && letter <= 'z') || (letter>='A' && letter<='Z')||(letter==' ')) {
    		   switch(multiPane.getSelectedIndex()){
	   	   		case IND_MUSIC_TAB:
		   	   		if (musicJTable.getCellEditor()!=null) {
	    		   		musicJTable.getCellEditor().cancelCellEditing();
						int row = musicTabModel.searchFirstElementWithLetter(letter,selectedModelColumn,currentCharPos,startingRow);
						if (selectedViewColumn < 0) selectedViewColumn = 0;
						if (row>-1) musicJTable.changeSelection(musicJTable.convertRowIndexToView(row),selectedViewColumn, false, false);
	    		   	}
	   	   			break;
	   	   		case IND_MOVIES_TAB:
		   	   		if (moviesJTable.getCellEditor()!=null) {
	    		   		moviesJTable.getCellEditor().cancelCellEditing();
						int row = moviesTabModel.searchFirstElementWithLetter(letter,selectedModelColumn,currentCharPos,startingRow);
						if (selectedViewColumn < 0) selectedViewColumn = 0;
						if (row>-1) moviesJTable.changeSelection(moviesJTable.convertRowIndexToView(row),selectedViewColumn, false, false);
	    		   	}
	   	   			break;
	   	   		case IND_DOCS_TAB:
		   	   		if (docsJTable.getCellEditor()!=null) {
	    		   		docsJTable.getCellEditor().cancelCellEditing();
						int row = docsTabModel.searchFirstElementWithLetter(letter,selectedModelColumn,currentCharPos,startingRow);
						if (selectedViewColumn < 0) selectedViewColumn = 0;
						if (row>-1) docsJTable.changeSelection(docsJTable.convertRowIndexToView(row),selectedViewColumn, false, false);
	    		   	}
	   	   			break;	
	  			}
    		   	
    		}
       }
   } //TABLEKEYLISTENERHANDLER END
   
   
   
   private class CloseLyricsFrameHandler extends WindowAdapter {

       JFrame frame;

       @Override
       public void windowClosing(WindowEvent e) {
          frame = (JFrame) e.getSource();
          int option = JOptionPane.showConfirmDialog(frame,"Do you want to save before closing?");
          if (option==JOptionPane.YES_OPTION){
        	  saveCurrentLyrics();
        	  frame.dispose();
          }
          else if(option==JOptionPane.NO_OPTION){
        	  frame.dispose();
          }      
       }
   } //CLOSEPLAYERHANDLER
   
   private class TabbedPaneListener implements ChangeListener{

	@Override
	public void stateChanged(ChangeEvent e) {
		
		switch(((JTabbedPane)e.getSource()).getSelectedIndex()){
				case IND_MUSIC_TAB:
					menuOpciones.setEnabled(true);
	   	   			menuRelDBBU.setEnabled(true);
	   	   			menuAddBUDB.setEnabled(true);
	   	   			if (backUpConnected) menuPlay.setEnabled(true);
	   	   		    if (backUpConnected) menuViewLyrics.setEnabled(true);
	   	   		    if (backUpConnected) menuPlayRandom.setEnabled(true);
	   	   			break;
	   	   		case IND_MOVIES_TAB:
	   	   			menuOpciones.setEnabled(false);
	   	   			menuRelDBBU.setEnabled(false);
	   	   			menuAddBUDB.setEnabled(false);
	   	   			menuPlay.setEnabled(false);
	   	   			menuViewLyrics.setEnabled(false);
	   	   		    menuPlayRandom.setEnabled(false);
	   	   			break;
	   	   		case IND_DOCS_TAB:
	   	   			menuOpciones.setEnabled(false);
	   	   			menuRelDBBU.setEnabled(false);
	   	   			menuAddBUDB.setEnabled(false);
	   	   			menuPlay.setEnabled(false);
	   	   			menuViewLyrics.setEnabled(false);
	   	   			menuPlayRandom.setEnabled(false);
	   	   			break;	
	  			}
	}
	   
   }

   
   public class AbstractActionsHandler extends AbstractAction{

		private static final long serialVersionUID = 1L;
		private int mode=-1;
		
		public AbstractActionsHandler(int mode){
			this.mode=mode;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (mode){
				case PASTE_IN_TABLE:     //PASTE IN TABLE
					multiPasteInTable();
					break;
				case SAVE_REVIEW:     //SAVE REVIEW
					saveCurrentReview();
					break;					
				default:
					break;							
			}			
		}
   	
   }

   /////////////////////POPUPS LISTENERS/////////////////////////////////////////////////////////////////////
   
   ///////////////////main table popup/////////////////////////////////
   private class PopupTableListener extends MouseAdapter {

       @Override
       public void mousePressed(MouseEvent e) {
           if (SwingUtilities.isRightMouseButton(e)) {
               Point p = e.getPoint();// get the coordinates of the mouse click
               JTable currentTable = null;
               switch(multiPane.getSelectedIndex()){
	   	   		case IND_MUSIC_TAB:
	   	   			currentTable=musicJTable;
	   	   			break;
	   	   		case IND_MOVIES_TAB:
	   	   			currentTable=moviesJTable;
	   	   			break;
	   	   		case IND_DOCS_TAB:
	   	   			currentTable=docsJTable;
	   	   			break;	
	  			}
                selectedViewRow = currentTable.rowAtPoint(p);
  	   			selectedModelRow = currentTable.convertRowIndexToModel(selectedViewRow);
  	   			selectedViewColumn = currentTable.columnAtPoint(p);
  	   			selectedModelColumn = currentTable.convertColumnIndexToModel(selectedViewColumn);
  	   			currentTable.changeSelection(selectedViewRow, selectedViewColumn, false, false);              
           }
       }

       @Override
       public void mouseReleased(MouseEvent e) {
           if (e.isPopupTrigger()) {
               popupTable.show(e.getComponent(),e.getX(), e.getY());
           }
       }
   } //POPUPLISTENER HANDLER END

   ////////////////////////////////review popup////////////////////////////////
   private class PopupReviewListener extends MouseAdapter{
       @Override
      public void mousePressed(MouseEvent e) {
    	   if (SwingUtilities.isRightMouseButton(e)){
          	popupReview.show(e.getComponent(),e.getX(), e.getY());         	
          }
      }
  }
   
   //this class shows the popup for showing the cover in big frame, it also changes the cover to front and back
   private class ChangeCoverListener extends MouseAdapter{
       @Override
      public void mousePressed(MouseEvent e) {
          if (SwingUtilities.isLeftMouseButton(e)) {
             if (currentFrontCover) {
                 currentFrontCover=false;
                 showCover("back");
             }
             else {
                 showCover("front");
             }           
          } else if (SwingUtilities.isRightMouseButton(e)){
          	popupCover.show(e.getComponent(),e.getX(), e.getY());         	
          }
      }
  }
   
   ////////////////////////////////songs table popup////////////////
   private class PopupSongListener extends MouseAdapter{
       @Override     
       public void mousePressed(MouseEvent e) {
           if (SwingUtilities.isRightMouseButton(e)) {
               Point p = e.getPoint();// get the coordinates of the mouse click
               int selectedViewRowPlayer = playListTable.rowAtPoint(p);
               int selectedModelRowPlayer = playListTable.convertRowIndexToModel(selectedViewRowPlayer);
               playListTable.changeSelection(selectedViewRowPlayer, selectedModelRowPlayer, false, false);
           }
       }
       @Override
       public void mouseReleased(MouseEvent e) {
           if (e.isPopupTrigger()) {
        	   popupSong.show(e.getComponent(),e.getX(), e.getY());
           }
       }
  }
   


///////////////////PLAYERFRAME HANDLERS//////////////////////////////////////////////////////////////////
///////////////////PLAYERFRAME HANDLERS//////////////////////////////////////////////////////////////////
///////////////////PLAYERFRAME HANDLERS//////////////////////////////////////////////////////////////////
   private class PauseResumeHandler implements ActionListener {

       public void actionPerformed(ActionEvent e) {
           mp3Player.pauseResume();
           if (timerThread!=null) timerThread.paused=!timerThread.paused;
       }
   }

   private class StopButtonHandler implements ActionListener {

       public void actionPerformed(ActionEvent e) {
    	   if (timerThread!=null) timerThread.closed=true;
           mp3Player.close();
       }
   }
   
   private class ResetEqHandler implements ActionListener {

       public void actionPerformed(ActionEvent e) {
           for (int i=0;i<EQ_NUM_BANDS;i++){
        	   equalizer[i].setValue(0);
        	   mp3Player.setEq(i,0);
           }
       }
   }

   private class ClosePlayerHandler extends WindowAdapter {

       JFrame frame;

       @Override
       public void windowClosing(WindowEvent e) {
           frame = (JFrame) e.getSource();
           mp3Player.close();
           Song song;
           String pathSt;
           int index;
           for(int i=0;i<playList.getRowCount();i++){
        	   song=playList.getSongAtRow(i);
        	   if (song.change==true){
                   if (song.path.canWrite()) {
						index = song.path.getAbsolutePath().indexOf(song.path.getName());
						pathSt = song.path.getAbsolutePath().substring(0, index);
                        if (song.path.renameTo(new File(pathSt + song.name+".mp3"))) {
                            reviewView.append("File renamed succesfully: " + song.name+"\n");
                        } else  reviewView.append("Can write but not rename file: " + song.name+"\n");
                   } else reviewView.append("Could not rename file: " + song.name+"\n");
        	   }
           }
          if (timerThread!=null) timerThread.closed=true;
          if (randomPlayThread!=null) randomPlayThread=null;
          frame.dispose();
          frame=null;
       }
   } //CLOSEPLAYERHANDLER

   private class PlayThisSongHandler extends MouseAdapter {

       @Override
       public void mousePressed(MouseEvent e) {
           if (e.getClickCount() == 2) {
               Point p = e.getPoint();// get the coordinates of the mouse click
               int selViewRow = playListTable.rowAtPoint(p);
               int selModelRow = playListTable.convertRowIndexToModel(selViewRow);
               int selViewCol = playListTable.columnAtPoint(p);
               if (selViewCol!=0) mp3Player.playThisSong(selModelRow);
           }
       }
   }//PlayThisSongListener
   
   private class SongEditorHandler implements CellEditorListener {
   
       public void editingStopped(ChangeEvent e) {
    	   ListSelectionModel lsm = playListTable.getSelectionModel();
           if (!lsm.isSelectionEmpty()){
               int selViewRow = lsm.getMinSelectionIndex();
               int selModelRow = playListTable.convertRowIndexToModel(selViewRow);
               playList.getSongAtRow(selModelRow).change=true;
           }   
       }

       public void editingCanceled(ChangeEvent e) {}
   }//END OF CELLEDITOR HANDLER
   
   private class PlayingHandler extends PlayingListener{

        @Override
       public void playingStarted(PlayingEvent evt){
        	songSlider.setMaximum((int)evt.getTime()/1000000);
       }
   }//END OF PLAYINGTIME HANDLER
   
   private class SongSliderHandler extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent arg0) {
			timerThread.paused=true;
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			timerThread.paused=false;
			mp3Player.jump(songSlider.getValue());
		}
	}
   
   private class EqSliderHandler implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent evt) {
			JSlider currentBand =(JSlider)evt.getSource();
			if (currentBand.getValueIsAdjusting()) {return;}
			int band = Integer.parseInt(currentBand.getName());
			float value;
			if (currentBand.getValue()==0) value=0; else value=currentBand.getValue()/new Float(100);
			mp3Player.setEq(band,value);
		}

	}
   
   
//////////////////////////////////////END OF HANDLERS/////////////////////////////////////////////////////////////// 
   
   
   ////////////////////////////////////////////////////////////////////////////////////////////////
   ////////////////////////////////TABLECELLRENDERER\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
   ////////////////////////////////////////////////////////////////////////////////////////////////

   
   public class MusicTableRenderer extends JLabel implements TableCellRenderer {
	   
	private static final long serialVersionUID = 1L;

	public MusicTableRenderer() {
		super();
	}

	public MusicTableRenderer(String arg0) {
		super(arg0);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean isFocused, int row, int col) {
		
		Font font;		
		int modelCol=table.convertColumnIndexToModel(col);
		
		switch (modelCol){
			case music.db.DataBaseLabels.COL_GROUP:
				this.setBackground(Color.CYAN);
				font= new Font("Arial",Font.BOLD,12);
				break;
			case music.db.DataBaseLabels.COL_TITLE:
				this.setBackground(Color.YELLOW);
				font= new Font("Lucida Console",Font.BOLD,11);
				break;
			case music.db.DataBaseLabels.COL_YEAR:
				this.setBackground(Color.getHSBColor(50,36,1215));
				font= new Font("Arial",Font.BOLD,11);
				break;	
			case music.db.DataBaseLabels.COL_STYLE:
				this.setBackground(Color.GREEN);
				font= new Font("Century Gothic",Font.PLAIN,11);
				break;	
			case music.db.DataBaseLabels.COL_LOC:
				this.setBackground(Color.PINK);
				font= new Font("Book Antiqua",Font.PLAIN,11);
				break;		
			case music.db.DataBaseLabels.COL_COPY:
				this.setBackground(Color.ORANGE);
				font= new Font("Arial",Font.BOLD,11);
				break;		
			case music.db.DataBaseLabels.COL_TYPE:
				this.setBackground(Color.LIGHT_GRAY);
				font= new Font("Arial",Font.PLAIN,11);
				break;	
			case music.db.DataBaseLabels.COL_MARK:
				this.setBackground(Color.RED);
				font= new Font("Arial",Font.PLAIN,11);
				break;	
			case music.db.DataBaseLabels.COL_PRESENT:
				this.setBackground(Color.MAGENTA);
				font= new Font("Arial",Font.BOLD,11);
				break;		
			default:
				this.setBackground(Color.getHSBColor(50,147,1635));
				font= new Font("Garamond",Font.BOLD,13);
				break;
		}
		this.setFont(font);
		this.setOpaque(true);
		if (isSelected){
			Border loweredBevel = BorderFactory.createLoweredBevelBorder();
			this.setBorder(loweredBevel); 
			this.setBackground(Color.getHSBColor(50,147,1635));
		}
		else this.setBorder(BorderFactory.createEmptyBorder());
		if (value!=null) this.setText(value.toString());
		return this;
	} 
	   
   }
   
   public class MoviesTableRenderer extends JLabel implements TableCellRenderer {

		   
		private static final long serialVersionUID = 1L;

		public MoviesTableRenderer() {
			super();
		}

		public MoviesTableRenderer(String arg0) {
			super(arg0);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean isFocused, int row, int col) {
			
			Font font;			
			int modelCol=table.convertColumnIndexToModel(col);

			switch (modelCol){
				case movies.db.DataBaseLabels.COL_TITLE:
					this.setBackground(Color.YELLOW);
					font= new Font("Lucida Console",Font.BOLD,11);
					break;
				case movies.db.DataBaseLabels.COL_DIR:
					this.setBackground(Color.GREEN);
					font= new Font("Century Gothic",Font.PLAIN,11);
					break;	
				case movies.db.DataBaseLabels.COL_YEAR:
					this.setBackground(Color.ORANGE);
					font= new Font("Tahoma",Font.BOLD,11);
					break;	
				case movies.db.DataBaseLabels.COL_LOC:
					this.setBackground(Color.PINK);
					font= new Font("Book Antiqua",Font.PLAIN,11);
					break;		
				case movies.db.DataBaseLabels.COL_OTHER:
					this.setBackground(Color.RED);
					font= new Font("Arial",Font.PLAIN,11);
					break;	
				case movies.db.DataBaseLabels.COL_PRESENT:
					this.setBackground(Color.MAGENTA);
					font= new Font("Arial",Font.BOLD,11);
					break;		
				default:
					font= new Font("Garamond",Font.BOLD,13);
					break;
			}
			this.setFont(font);
			this.setOpaque(true);
			if (isSelected){
				Border loweredBevel = BorderFactory.createLoweredBevelBorder();
				this.setBorder(loweredBevel); 
				this.setBackground(Color.getHSBColor(50,147,1635));
			}
			else this.setBorder(BorderFactory.createEmptyBorder());
			this.setText((String)value);
			return this;
		} 
   }
   
   
   
   public class DocsTableRenderer extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		public DocsTableRenderer() {
			super();
		}

		public DocsTableRenderer(String arg0) {
			super(arg0);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean isFocused, int row, int col) {
			
			Font font;
			int modelCol=table.convertColumnIndexToModel(col);

			switch (modelCol){
				case docs.db.DataBaseLabels.COL_TITLE:
					this.setBackground(Color.YELLOW);
					font= new Font("Lucida Console",Font.BOLD,11);
					break;
				case docs.db.DataBaseLabels.COL_LOC:
					this.setBackground(Color.PINK);
					font= new Font("Book Antiqua",Font.PLAIN,11);
					break;		
				case docs.db.DataBaseLabels.COL_COMMENTS:
					this.setBackground(Color.GREEN);
					font= new Font("Century Gothic",Font.PLAIN,11);
					break;	
				default:
					font= new Font("Garamond",Font.BOLD,13);
					break;
			}
			this.setFont(font);
			this.setOpaque(true);
			if (isSelected){
				Border loweredBevel = BorderFactory.createLoweredBevelBorder();
				this.setBorder(loweredBevel); 
				this.setBackground(Color.getHSBColor(50,147,1635));
			}
			else this.setBorder(BorderFactory.createEmptyBorder());
			this.setText((String)value);
			return this;
		} 
  }
   
  public class ComboTableCellRenderer implements TableCellRenderer {
	   DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
	   DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();

	 
	   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column) {
	     
		 tableRenderer = (DefaultTableCellRenderer) tableRenderer.getTableCellRendererComponent(table,value, isSelected, hasFocus, row, column);
	     Font font= new Font("Tahoma",Font.BOLD,11);
	     tableRenderer.setBackground(Color.ORANGE);
	     tableRenderer.setFont(font);
	     tableRenderer.setOpaque(true);	     
		 if (isSelected){
			Border loweredBevel = BorderFactory.createLoweredBevelBorder();
			tableRenderer.setBorder(loweredBevel); 
			tableRenderer.setBackground(Color.getHSBColor(50,147,1635));
		 }else tableRenderer.setBorder(BorderFactory.createEmptyBorder());
		 tableRenderer.setText((DocTheme.getStringTheme((DocTheme)value)));
	     return tableRenderer;
	   }
	 }

   
   public class PlayerTableRenderer extends JLabel implements TableCellRenderer {
   
		private static final long serialVersionUID = 1L;

		public PlayerTableRenderer() {
			super();
		}

		public PlayerTableRenderer(String arg0) {
			super(arg0);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean isFocused, int row, int col) {
			
			Font font;
			Border loweredBevel = BorderFactory.createLoweredBevelBorder();
			//Border raisedBevel = BorderFactory.createRaisedBevelBorder();
			//Border border = BorderFactory.createCompoundBorder(loweredBevel,raisedBevel);
			int modelRow=playListTable.convertRowIndexToModel(row);
			if ((Boolean)playList.getValueAt(modelRow,TabModelPlayList.COL_CURRENT_SONG)){
				this.setBackground(Color.CYAN);
				font= new Font("Arial",Font.BOLD,12);
				this.setFont(font);
			} else {
				this.setBackground(Color.YELLOW);
				font= new Font("Arial",Font.PLAIN,12);
				this.setFont(font);
			}
			
			if (isSelected){
				this.setBorder(loweredBevel); 
				this.setBackground(Color.getHSBColor(50,147,1635));
			}
			else this.setBorder(BorderFactory.createEmptyBorder());
			this.setOpaque(true);
			this.setText((String)value);
			return this;
		} 
		   
	   }

   
   /////////////////////////////THREADS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
   /////////////////////////////THREADS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\   
   /////////////////////////////THREADS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
   
   public class TimerThread extends Thread {

	   public boolean closed = false;
	   public boolean paused = false;

		public TimerThread() {
			super();
		}

		@Override
		public void run() {
			try {
				if (mp3Player != null) {
					do {
						if (!paused) {
							songSlider.setValue((int)mp3Player.getPosition()/1000);
							Long sec = mp3Player.getPosition()/1000;
				            Long min = sec / 60;
				            sec = sec % 60;
				            String timeSt;
				            if (sec<10) timeSt= Long.toString(min) + ":0" + Long.toString(sec);
				            else timeSt= Long.toString(min) + ":" + Long.toString(sec);
				            if (mp3Player.currentSong<mp3Player.list.getRowCount()){
				            	String titleShown;
				              	if (mp3Player.list.getSongAtRow(mp3Player.currentSong).tagTitle==null)
				              			titleShown=mp3Player.list.getSongAtRow(mp3Player.currentSong).name;
				            	else {
				              		if (mp3Player.list.getSongAtRow(mp3Player.currentSong).tagTitle.compareTo("")==0)
				              			titleShown=mp3Player.list.getSongAtRow(mp3Player.currentSong).name;
				              		else titleShown=mp3Player.list.getSongAtRow(mp3Player.currentSong).tagTitle;
				            	}
				            	songInformation.setText("Playing: "+ titleShown+"  "+ timeSt);
				            }
						}
						Thread.sleep(1000);
					} while (!closed);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	/*	public void waitThis(){
       	try {
       		synchronized(this){
           		wait();
           	}
   		} catch (InterruptedException e) {
   			e.printStackTrace();
   		}
       }*/
   }
  
   public class CopyThread extends Thread {

	   public boolean end = false;
	   public String[] folders;
	   public String path;
	   public int size;
       int posGuion = -1, longNombre = -1;
       String[] files;
       File currentDisc;
       boolean exists;

		public CopyThread() {
			super();
		}

		@Override
		public void run() {

			 infoFrame.setVisible(true);
       
			 for (int j = 0; j < size; j++) {
                 String nombreGrupo = folders[j];

                 File discosGrupoF = new File(path + "\\" + nombreGrupo);
                 if (discosGrupoF.isDirectory() == false) {
                      errorSint(path + "\\" + nombreGrupo);
                 } else {
                     String[] discosGrupo = discosGrupoF.list();
                     Integer numeroDiscos = discosGrupo.length;
                     File groupBUpPath=buscarGrupo(nombreGrupo,backUpPath);
                     exists=false;
                     if (groupBUpPath!=null){
                        //group folder already exists in backup
                         exists=copyFiles(discosGrupoF.getAbsoluteFile(),groupBUpPath.getAbsoluteFile());
                         if (!exists){
                        	 infoText.setText("Added folder "+discosGrupoF.getAbsolutePath()+" to "+groupBUpPath.getAbsolutePath()+"\n");
                        	 reviewView.append("Added folder "+discosGrupoF.getAbsolutePath()+" to "+groupBUpPath.getAbsolutePath()+"\n");
                         }
                     }else {
                         exists=copyFolder(discosGrupoF.getAbsoluteFile(),backUpPath.getAbsoluteFile());
                         if (!exists){
                        	 infoText.setText("Added folder "+discosGrupoF.getAbsolutePath()+" to "+backUpPath.getAbsolutePath()+"\n");
                        	 reviewView.append("Added folder "+discosGrupoF.getAbsolutePath()+" to "+backUpPath.getAbsolutePath()+"\n");
                         }
                     }
                     reviewView.revalidate();
                    //for every disc in the group folder we insert an entry in the database

                     if (!exists) {
							for (int k = 0; k < numeroDiscos; k++) {
								currentDisc = new File(path + "\\"+ nombreGrupo + "\\"+ discosGrupo[k]);
								if (currentDisc.isDirectory() == false) {errorSint(currentDisc.getAbsolutePath());
								} else {
									posGuion = discosGrupo[k].indexOf("-");
									if (posGuion < 0) {
										errorSint(currentDisc.getAbsolutePath());
									} else {
										Disc disco = new Disc();
										String anho = discosGrupo[k].substring(0, posGuion);
										anho = anho.trim();
										try {
											//Long anhoLong = Long.decode(anho);
											longNombre = discosGrupo[k].length();
											String nombreDisco = discosGrupo[k].substring(posGuion + 1,longNombre);
											// creamos nuevo disco con los datos leidos
											disco.reset();
											disco.title = nombreDisco.trim();
											disco.group = nombreGrupo;
											disco.year = anho;
											disco.review=" ";
											disco.path = currentDisc;
											if (groupBUpPath!=null){
												//copiamos la procedencia del grupo de algun elemento ya existente de ese grupo
												disco.loc=musicDataBase.getDiscByGroupName(nombreGrupo).loc;												
											}
											// anhadimos el disco tanto al backup como a la base de datos
											musicDataBase.insertNewDisc(disco);
										} catch (NumberFormatException e) {
											errorSint(currentDisc.getAbsolutePath());
										}
									}
								}
							}
						}
					}
             }
			 JOptionPane.showMessageDialog(f, "Files copied successfully");
			 infoFrame.dispose();
		}
   }
   
   public class Look4NewDiscsThread extends Thread {

	   public String web="webEM";
	   public String groupName;	   
	   private Disc discDB=new Disc(),discWeb=new Disc();
	   private boolean found;
	   private ArrayList<Disc> discListWeb = new ArrayList<Disc>(),discListDB = new ArrayList<Disc>();
	   private ArrayList<Disc> finalList = new ArrayList<Disc>();
	   private Set<String> groupList= new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	   private int already=0;  //number of discs already in db
	   
		public Look4NewDiscsThread() {
			super();
		}

		@Override
		public void run() {

			infoFrame.setVisible(true);
			selectedModel=transformViewSelectedToModel(selectedView);	 
			discListWeb.clear();
			finalList.clear();
			infoText.setText("");
			
			for (int i=0;i<selectedModel.size();i++){				
				groupName=musicTabModel.getDiscAtRow(selectedModel.get(i)).group;
				if (!groupList.contains(groupName)){
					groupList.add(groupName);
					discListDB.clear();
					discListDB = musicDataBase.getDiscsOfGroup(groupName);
					infoFrame.setSize(500, 400);
					infoFrame.setVisible(true);						
					infoText.append("Looking for new discs of "+groupName+"\n");
					discListWeb=webReader.getGroupInfo(groupName,web);
					if ((discListWeb!=null)&&(discListWeb.size()!=0)){
						already=0;
						for (int indWeb=0;indWeb<discListWeb.size();indWeb++){
							//System.out.println(discList.get(disc).title);
							found=false;							
							for (int indDB=0;indDB<discListDB.size();indDB++){
								discDB=discListDB.get(indDB);
								discWeb=discListWeb.get(indWeb);
								if (String.CASE_INSENSITIVE_ORDER.compare(discWeb.title,discDB.title)==0){
									found=true;
									already++;
									break;
								}									
							}
							if (!found){
								finalList.add(discWeb);
							}
						}
						infoText.append("Found "+discListWeb.size()+" albums in their discography\n");
						if (already>0) infoText.append(already+" albums already on list\n");
					}else infoText.append("Group not found or whithout album releases\n");
					infoText.append("-----------------------------------------\n");
				}				
			}
			newDiscsTabMod.setData(finalList);
			if (finalList.size()>0) {
				newDiscsFrame.setSize(700,finalList.size()*18+60);
				newDiscsFrame.setVisible(true);
			} else infoText.append("No new discs found\n");
			infoText.append("Done!");		
		}
   	}

   public class GetLyricsThread extends Thread {
	   
		public GetLyricsThread() {
			super();
		}

		@Override
		public void run() {
			lyricsView.setText(webReader.getLyricsOfDisc(lyricsGroup,lyricsAlbum));	
		}
   }
   
   public class RandomPlayThread extends Thread {
	   
       private File pathDisc;
       private Random rand = new Random();
       private int randomDisc=0,randomSong=0;
       private List<Integer> selectedDiscs = new LinkedList<Integer>();
       private List<Song> songsInPath = new LinkedList<Song>();
       private Song currentSong;
       public Double mark=0.0;
       private String currentGroup, currentAlbum;
	   
		public RandomPlayThread() {
			super();
		}

		@Override
		public void run() {

			selectedDiscs=getListOfDiscsByMark(mark);
        	
            do{
            	playList.removeAllRows();
            	for (int numSong=1;numSong<10;numSong++){
            		randomDisc=rand.nextInt(selectedDiscs.size());
            		pathDisc = (File) musicTabModel.getValueAt(selectedDiscs.get(randomDisc),COL_PATH);
            		try {
            			 songsInPath.clear();
                         playList.numSongs=0;
                         currentGroup=(String)musicTabModel.getValueAt(selectedDiscs.get(randomDisc),COL_GROUP);
                         currentAlbum=(String)musicTabModel.getValueAt(selectedDiscs.get(randomDisc),COL_TITLE);
                         songsInPath=playList.searchFiles(pathDisc,false,currentGroup,currentAlbum);
                         randomSong=rand.nextInt(songsInPath.size());
                         currentSong=songsInPath.get(randomSong);
                         playList.addSong(currentSong);
            		} catch (MP3FilesNotFound ex) {
                 }
            	}
                
               
                playListTable.setModel(playList);
                //handler to play the song selected wit doubleclick on list
                PlayThisSongHandler playThisSongHandler = new PlayThisSongHandler();
                playListTable.addMouseListener(playThisSongHandler);   
                
                managePlayListTable();               
                
                mp3Player.playList(playList);
                timerThread = new TimerThread();
                timerThread.setDaemon(true);
                timerThread.start();
                playerFrame.setVisible(true);
                int divLoc=Math.min(400, playListTable.getHeight()+ pauseResumeButton.getHeight()+stopButton.getHeight()+songSlider.getHeight()+songInformation.getHeight()+60);
                splitPlayer.setDividerLocation(divLoc);

            	synchronized(MP3Player.lock) {
                   try {
                		MP3Player.lock.wait();
                   } catch (InterruptedException ex) {
                        ex.printStackTrace();
                   }
                   
            	}
            } while(true);
		}
  }

   //////////////////////////////////////WORKARAOUND FOR BUGS IN XP AND VISTA FOR JFILECHOOSER///////////////
   private static class NewFileSystemView extends FileSystemView {
       public File createNewFolder(File containingDir) throws IOException {
           return FileSystemView.getFileSystemView().createNewFolder(containingDir);
       }

       public File[] getRoots() {
           List<File> result = new ArrayList<File>();

           for (File file : (File[]) ShellFolder.get("fileChooserComboBoxFolders")) {
               if (!(file instanceof ShellFolder) || !((ShellFolder) file).isLink()) {
                   result.add(file);
               }
           }

           return result.toArray(new File[result.size()]);
       }
   }

	
   
}
	