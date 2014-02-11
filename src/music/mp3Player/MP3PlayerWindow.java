package music.mp3Player;

import image.ImageDealer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import main.Errors;
import main.utils.LevenshteinDistance;
import music.db.Disc;
import music.db.Song;
import music.lyrics.LyricsFrame;
import musicmovies.db.Video;


public class MP3PlayerWindow {

    public static final Dimension PLAYER_DIM = new Dimension(910,630);
    public static final Dimension COVER_DIM = new Dimension(250,250);
    public static final int EQ_NUM_BANDS = 10;
    public final String LYR_PLAYER_NAME="playerLyricsMenu";
    
    public static final Rectangle PIC_PANEL_BOUNDS = new Rectangle(634, 53, COVER_DIM.width, COVER_DIM.height);
    public static final Rectangle INFO_PANEL_BOUNDS = new Rectangle( 634, 315, 249, 154);
    public static final Rectangle PLAYLIST_TABLE_BOUNDS = new Rectangle(10, 104, 614, 374);
    public static final Rectangle EQ_SLIDERS_BOUNDS= new Rectangle(20, 490, 41, 101);
    public static final Rectangle RESET_BOUNDS= new Rectangle(342, 487, 89, 23);
    public static final Rectangle SLIDER_BOUNDS= new Rectangle(10, 55, 614, 47);
    
    
	  //PLAYER ELEMENTS
    public MP3Player mp3Player;
    public TabModelPlayList playList;
    public JFrame playerFrame;
    public JButton pauseResumeButton,stopButton,resetEqButton,nextButton,previousButton;
    public JTable playListTable;
    public JSlider songSlider;
    public JSlider[] equalizer;
    public JScrollPane spPlay,spLyrics;
    public JPanel picPanel, infoPanel;
    public JLabel picLabel, infoLabel;
    public JPopupMenu popupSong;
    public PlayerTableRenderer playerTableRenderer;
    public TimerThread timerThread;
    public RandomPlayThread randomPlayThread; 
    public LyricsFrame lyricsFrame;
    public boolean playFirstTime = true;
    private music.db.TabMod musicTabModel;
    private ImageDealer imageDealer;
  
    public int selectedViewRowPlayer = -1,selectedModelRowPlayer=-1;  
    
    public MP3PlayerWindow(music.db.TabMod mTabModel, music.mp3Player.MP3Player  mp3Player){
    	setMusicTabModel(mTabModel);
    	setPlayer(mp3Player);
    	imageDealer = new ImageDealer();
    	this.initWindow();
    }
    
    public MP3PlayerWindow(){
    	this(null,null);
    }
    
    
    public void setMusicTabModel(music.db.TabMod mTabModel){
    	this.musicTabModel = mTabModel;
    }
    
    public void setPlayer(music.mp3Player.MP3Player  mp3Player){
    	this.mp3Player = mp3Player;
    	   //handlers to manage the slider of mp3player     
        //controls the time elapsed
    	if (mp3Player!=null){
	        PlayingHandler playingHandler = new PlayingHandler();
	        mp3Player.setPlayingListener(playingHandler);
    	}
    }
    
//////////////////////////LAYOUT//////////////////////////////////////////////
//////////////////////////LAYOUT//////////////////////////////////////////////
//////////////////////////LAYOUT//////////////////////////////////////////////
	
	private void initWindow(){
		
		//frames
	    playerFrame=new JFrame("MP3Player");
	    playerFrame.setBounds(100, 100, PLAYER_DIM.width, PLAYER_DIM.height);
	    playerFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	    playerFrame.getContentPane().setLayout(null);
	    //playerFrame.setSize(PLAYER_DIM);
	    lyricsFrame = new LyricsFrame();
	    
	    
	    randomPlayThread = new RandomPlayThread();
	 	    
	    //Buttons
	    pauseResumeButton=new JButton("Pause/Resume");
	    pauseResumeButton.setBounds(127, 11, 103, 23);
	    PauseResumeHandler pauseResumeHandler = new PauseResumeHandler();
	    pauseResumeButton.addActionListener(pauseResumeHandler);
	    stopButton=new JButton("Stop");
	    stopButton.setBounds(265, 11, 89, 23);
	    StopButtonHandler stopButtonHandler = new StopButtonHandler();
	    stopButton.addActionListener(stopButtonHandler);
	    
		nextButton = new JButton("Next");
		nextButton.setBounds(386, 11, 89, 23);
		
		previousButton = new JButton("Previous");
		previousButton.setBounds(513, 11, 89, 23);
	    
	    playerFrame.getContentPane().add(pauseResumeButton);
	    playerFrame.getContentPane().add(stopButton);
	    playerFrame.getContentPane().add(nextButton);
	    playerFrame.getContentPane().add(previousButton);
	    
	    
	    //slider
	    songSlider = new JSlider(JSlider.HORIZONTAL,0,0,0);
	    songSlider.setMajorTickSpacing(60);
	    songSlider.setMinorTickSpacing(1);
	    songSlider.setPaintLabels(true);
	    songSlider.setPaintTicks(true);
	    songSlider.setBounds(SLIDER_BOUNDS);
	    playerFrame.getContentPane().add(songSlider);
	    
	    //equalizer
	    equalizer = new JSlider[EQ_NUM_BANDS];
	    int i;
	    Rectangle sliderBounds;  
	    for(i=0;i<EQ_NUM_BANDS;i++){  	
	    	equalizer[i]=new JSlider(JSlider.VERTICAL,-100,100,0); 
	    	equalizer[i].setMinorTickSpacing(10);
	    	equalizer[i].setPaintLabels(true);
	    	equalizer[i].setPaintTicks(true);
	    	equalizer[i].setName(((Integer)i).toString());
	    	sliderBounds = new Rectangle(EQ_SLIDERS_BOUNDS);
	    	sliderBounds.x = sliderBounds.x + 30*i;
	    	equalizer[i].setBounds(sliderBounds);
	    	playerFrame.getContentPane().add(equalizer[i]);
	    }
	    resetEqButton=new JButton("Reset");
	    ResetEqHandler resetEqHandler = new ResetEqHandler();
	    resetEqButton.addActionListener(resetEqHandler);
	    resetEqButton.setBounds(RESET_BOUNDS);
	    playerFrame.getContentPane().add(resetEqButton);
	    
	    //songs table        
	    playListTable=new JTable();
	    
	    playList = new TabModelPlayList();
	    playerTableRenderer = new PlayerTableRenderer();
	    playListTable.setDefaultRenderer(Object.class,playerTableRenderer);
	    //playListTable.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
	    spPlay = new JScrollPane();
	    spPlay.setBounds(PLAYLIST_TABLE_BOUNDS);
	    //spPlay.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    spPlay.setViewportView(playListTable);
	    playListTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	    playerFrame.getContentPane().add(spPlay);  
	    
		picPanel = new JPanel();
		picPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		picPanel.setBounds(PIC_PANEL_BOUNDS);
		playerFrame.getContentPane().add(picPanel);
		picPanel.setLayout(null);
		
		picLabel = new JLabel("Cover");
		picLabel.setToolTipText("Cover");
		picLabel.setBounds(-1, 0, COVER_DIM.width, COVER_DIM.height);
		picPanel.add(picLabel);
		picLabel.setBackground(new Color(102, 204, 102));
		
		infoPanel = new JPanel();
		infoPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		infoPanel.setBounds(INFO_PANEL_BOUNDS);
		playerFrame.getContentPane().add(infoPanel);
		infoPanel.setLayout(null);
		
		infoLabel = new JLabel("Info");
		infoLabel.setBounds(0, 0, 249, 154);
		infoPanel.add(infoLabel);
		infoLabel.setToolTipText("Info");
		infoLabel.setBackground(new Color(255, 240, 245));
		infoLabel.setFont(new Font("Tahoma", Font.PLAIN, 9));
  
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
	    
	    //handler to play the song selected wit doubleclick on list
        PlayThisSongHandler playThisSongHandler = new PlayThisSongHandler();
        playListTable.addMouseListener(playThisSongHandler); 
	    
	    ClosePlayerHandler closePlayerHandler = new ClosePlayerHandler();
        playerFrame.addWindowListener(closePlayerHandler);
        
        //handler to edit name of played files
        SongEditorHandler songEditorHandler = new SongEditorHandler();
        DefaultCellEditor dcePlayer = (DefaultCellEditor) playListTable.getDefaultEditor(Object.class);
        dcePlayer.addCellEditorListener(songEditorHandler);       
        
        //manages the skipping of the song played
        SongSliderHandler songSliderHandler = new SongSliderHandler();
        songSlider.addMouseListener(songSliderHandler);        
        
        //Equalization slider handler
        EqSliderHandler eqSliderHandler = new EqSliderHandler();
        for (i=0;i<10;i++){
        	equalizer[i].addChangeListener(eqSliderHandler);
        }
        
        //handler to replace components after resizing the window
        ResizerHandler resizerHandler = new ResizerHandler();
        playerFrame.addComponentListener(resizerHandler);        
	}
	
	 //method to manage layout of playlist table  
	private void managePlayListTable(){
		///managing layout
	   TableColumn c;
	   try{
		   c = playListTable.getColumn("t");
		   playListTable.removeColumn(c);
		   c = playListTable.getColumn("p");
		   playListTable.removeColumn(c);
		   c = playListTable.getColumn("change");
		   playListTable.removeColumn(c);
		   c = playListTable.getColumn("currentSong");
		   playListTable.removeColumn(c);
		   c = playListTable.getColumn("discPath");
		   playListTable.removeColumn(c);
	   }catch(IllegalArgumentException ex){}
       c = playListTable.getColumn("File name");
       c.setMinWidth(100);
       c.setPreferredWidth(180);
       c = playListTable.getColumn("Length");
       c.setMinWidth(30);
       c.setPreferredWidth(35);
       c = playListTable.getColumn("Group");
       c.setMinWidth(80);
       c.setPreferredWidth(100);
       c = playListTable.getColumn("Album");
       c.setMinWidth(100);
       c.setPreferredWidth(100);
       c = playListTable.getColumn("Tag title");
       c.setMinWidth(100);
       c.setPreferredWidth(100);
       c = playListTable.getColumn("Bitrate");
       c.setMinWidth(40);
       c.setPreferredWidth(40);
       c = playListTable.getColumn("Sampling Format");
       c.setMinWidth(30);
       c.setPreferredWidth(30); 
	}
	   
	 
	 public final class ScrollingTableFix implements ComponentListener {
		  private final JTable table;

		  public ScrollingTableFix(JTable table, JScrollPane scrollPane) {
		    assert table != null;

		    this.table = table;
		    table.getModel().addTableModelListener(new ColumnAddFix(table, scrollPane));
		  }

		  public void componentHidden(final ComponentEvent event) {}

		  public void componentMoved(final ComponentEvent event) {}

		  public void componentResized(final ComponentEvent event) {
		    // turn off resize and let the scroll bar appear once the component is smaller than the table

		    if (event.getComponent().getWidth() < table.getPreferredSize().getWidth()) {
		        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		    }
		    // otherwise resize new columns in the table
		    else {
		        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		    }
		  }

		  public void componentShown(final ComponentEvent event) {}

		  // similar behavior is needed when columns are added to the table
		  private final class ColumnAddFix implements TableModelListener {
		    private final JTable table;
		    private final JScrollPane scrollPane;

		    ColumnAddFix(JTable table, JScrollPane scrollPane) {
		      this.table = table;
		      this.scrollPane = scrollPane;
		     }

		    @Override
		    public void tableChanged(TableModelEvent e) {
		      if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
		        if (scrollPane.getViewport().getWidth() < table.getPreferredSize().getWidth()) {
		          table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		        }
		        else {
		          table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		        }
		      }
		     }
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
	
	
	public void openAndStartPlaying(double mark, boolean fav){
		randomPlayThread.fav = fav;
		randomPlayThread.mark = mark;
		randomPlayThread.start();
		playerFrame.setVisible(true);
	}
	
	public void openAndStartPlaying(TabModelPlayList playList){
		this.playList = playList; 
        playListTable.setModel(playList);
		mp3Player.playList(playList);
        timerThread = new TimerThread();
        timerThread.setDaemon(true);
        timerThread.start();

        playFirstTime=false;
        managePlayListTable();
        spPlay.addComponentListener(new ScrollingTableFix(playListTable, spPlay));
        playerFrame.setVisible(true);
	}
	
	
	public void openAndStartPlaying(File pathDisc, String group, String album){
		playList.removeAllRows();
        playList.numSongs=0;
        playList.searchFiles(pathDisc,true,group,album);
        if (playList.numSongs!=0){
            openAndStartPlaying(playList);
        }
	}
	
	public void openAndStartPlaying(File pathDisc){
		openAndStartPlaying(pathDisc, "undefined", "undefined");
	}

	
    private void setCurrentCover(){
    	File pathDisc = playList.getSongAtRow(mp3Player.currentSong).discPath;
		if (!imageDealer.showImage(pathDisc, picLabel,ImageDealer.FRONT_COVER, COVER_DIM)){
			picLabel.setIcon(null);
			picLabel.setText("Image not found");
		}
    }
	
//////////////////////////HANDLERS//////////////////////////////////////////////
//////////////////////////HANDLERS//////////////////////////////////////////////
//////////////////////////HANDLERS//////////////////////////////////////////////

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
	                        	JOptionPane.showMessageDialog(frame, "File renamed succesfully: " + song.name+"\n");
	                        } else  JOptionPane.showMessageDialog(frame,"Can write but not rename file: " + song.name+"\n");
	                   } else JOptionPane.showMessageDialog(frame,"Could not rename file: " + song.name+"\n");
	        	   }
	           }
	          if (timerThread!=null) timerThread.closed=true;
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
	   }
	   
	   
	   
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
	   }
	   
	   private class PlayingHandler extends PlayingListener{

	        @Override
	       public void playingStarted(PlayingEvent evt){
	        	songSlider.setMaximum((int)evt.getTime()/1000000);
	        	playList.fireTableDataChanged();
	        	setCurrentCover();
	       }
	        @Override
	       public void playingNextSong(PlayingEvent evt){
	        	playList.fireTableDataChanged();
	        	setCurrentCover();
	       }
	        
	   
	   }
	   
	   
	   private class SongSliderHandler extends MouseAdapter {
			private Dimension dim = new Dimension(0,0);
		   @Override
			public void mousePressed(MouseEvent arg0) {
				timerThread.paused=true;
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				timerThread.paused=false;
				
				int width=((JSlider)arg0.getSource()).getSize(dim).width;
				int point = arg0.getX()*((JSlider)arg0.getSource()).getMaximum()/width;
				if (point > ((JSlider)arg0.getSource()).getMaximum()) point = ((JSlider)arg0.getSource()).getMaximum();
				if (point < 0) point=0;
				mp3Player.jump(point);
			}
		}
	   
	   private class EqSliderHandler implements ChangeListener {

			@Override
			public void stateChanged(ChangeEvent evt) {
				JSlider currentBand =(JSlider)evt.getSource();
				if (currentBand.getValueIsAdjusting()) {return;}
				int band = Integer.parseInt(currentBand.getName());
				float value;
				if (currentBand.getValue()==0) value=0; else value=currentBand.getValue()/(new Float(100));
				mp3Player.setEq(band,value);
			}

		}
	   
	   ////////////////////////////////songs table popup////////////////
	   private class PopupSongListener extends MouseAdapter{
	       @Override     
	       public void mousePressed(MouseEvent e) {
	           if (SwingUtilities.isRightMouseButton(e)) {
	               Point p = e.getPoint();// get the coordinates of the mouse click
	               selectedViewRowPlayer = playListTable.rowAtPoint(p);
	               selectedModelRowPlayer = playListTable.convertRowIndexToModel(selectedViewRowPlayer);
	               playListTable.changeSelection(selectedViewRowPlayer, selectedModelRowPlayer, false, false);
	           }
	           if (e.isPopupTrigger()) {
	        	   popupSong.show(e.getComponent(),e.getX(), e.getY());
	           }
	       }
	  }
	   
	   private class PopupMenuViewLyricsHandler implements ActionListener{
		   
	       public void actionPerformed(ActionEvent e) {
	    	   lyricsFrame.open(playList.getSongAtRow(selectedModelRowPlayer).discPath,playList.getSongAtRow(selectedModelRowPlayer).group,playList.getSongAtRow(selectedModelRowPlayer).album);
	    	  
	       }
	   }
	   
	   private class ResizerHandler extends ComponentAdapter{
		   
		   @Override
			public void componentResized(ComponentEvent e)
			{
				Rectangle compBounds = new Rectangle(); 
				playerFrame.getBounds(compBounds);
				int widthDiff = compBounds.width - PLAYER_DIM.width;
				int heightDiff = compBounds.height - PLAYER_DIM.height;
				if (widthDiff > 0){
					compBounds = new Rectangle(PIC_PANEL_BOUNDS);
				    compBounds.x = compBounds.x + widthDiff;
					picPanel.setBounds(compBounds);
					compBounds = new Rectangle(SLIDER_BOUNDS);
				    compBounds.width = compBounds.width + widthDiff;
					songSlider.setBounds(compBounds);
					compBounds = new Rectangle(INFO_PANEL_BOUNDS);
				    compBounds.x = compBounds.x + widthDiff;
					infoPanel.setBounds(compBounds);
					compBounds = new Rectangle(PLAYLIST_TABLE_BOUNDS);
				    compBounds.width = compBounds.width + widthDiff;
				    compBounds.height = compBounds.height + heightDiff;
					spPlay.setBounds(compBounds);
					for(int i=0;i<EQ_NUM_BANDS;i++){  
						compBounds = new Rectangle(EQ_SLIDERS_BOUNDS);
						compBounds.y = compBounds.y + heightDiff;
					    compBounds.x = compBounds.x + 30*i;
					    equalizer[i].setBounds(compBounds);
					}
					compBounds = new Rectangle(RESET_BOUNDS);
				    compBounds.y = compBounds.y + heightDiff;
					resetEqButton.setBounds(compBounds);
				}else{
					picPanel.setBounds(PIC_PANEL_BOUNDS);
					infoPanel.setBounds(INFO_PANEL_BOUNDS);
					spPlay.setBounds(PLAYLIST_TABLE_BOUNDS);
					resetEqButton.setBounds(RESET_BOUNDS);
					songSlider.setBounds(SLIDER_BOUNDS);
					for(int i=0;i<EQ_NUM_BANDS;i++){  
						compBounds = new Rectangle(EQ_SLIDERS_BOUNDS);
					    compBounds.x = compBounds.x + 30*i;
					    equalizer[i].setBounds(compBounds);
					}
				}
			
			}
		}
	 
	   //RANDOM PLAY/////////////////////////////////////////////////////////////////////////  
	   public class RandomPlayThread extends Thread {
		   
	       public boolean fav;
	       public Double mark=0.0;
	       private File pathDisc;
	       private Random rand = new Random();
	       private int randomDisc=0,randomSong=0;
	       private List<Integer> selectedDiscs = new LinkedList<Integer>();
	       private List<Integer> favSongs = new LinkedList<Integer>();
	       private List<Song> songsInPath = new LinkedList<Song>();
	       private Song currentSong;
	       private String currentGroup, currentAlbum;
	       private int numSongs=0;
		   
			public RandomPlayThread() {
				super();
			}

			@Override
			public void run() {

				selectedDiscs=getListOfDiscsByMark(mark);
				/*System.out.println(selectedDiscs.size());
	        	
			    for(int j=0;j<selectedDiscs.size();j++){
		            	playList.removeAllRows();
		                playList.numSongs=0;
		                currentGroup=(String)musicTabModel.getValueAt(selectedDiscs.get(j),Disc.COL_GROUP);
	                    currentAlbum=(String)musicTabModel.getValueAt(selectedDiscs.get(j),Disc.COL_TITLE);
	                    pathDisc = (File) musicTabModel.getValueAt(selectedDiscs.get(j),Disc.COL_PATH);
	                    try {
	                    	songsInPath=playList.searchFiles(pathDisc,false,currentGroup,currentAlbum);
	                    } catch (MP3FilesNotFound ex) {
	                    	System.out.println(currentGroup+ " "+currentAlbum);
		                }
		            } */
				
				
	            do{
	            	playList.removeAllRows();
	            	this.numSongs=0;
	            	do {
	            		randomDisc=rand.nextInt(selectedDiscs.size());
	            		favSongs.clear();
	            		pathDisc = (File) musicTabModel.getValueAt(selectedDiscs.get(randomDisc),Disc.COL_PATH);
	            		songsInPath.clear();
	                    currentGroup=(String)musicTabModel.getValueAt(selectedDiscs.get(randomDisc),Video.COL_GROUP);
	                    currentAlbum=(String)musicTabModel.getValueAt(selectedDiscs.get(randomDisc),Video.COL_TITLE);
	                    songsInPath=playList.searchFiles(pathDisc,false,currentGroup,currentAlbum);
	                    if (songsInPath.size()!=0){
		                     if (!fav){
			                      randomSong=rand.nextInt(songsInPath.size());
			                      currentSong=songsInPath.get(randomSong);
			                      playList.addSong(currentSong);
			                      this.numSongs++;
			                 }else{
			                  	 seekFavSongs(randomDisc,songsInPath);
			                   	 if (favSongs.size()>0){
				                   	 randomSong=rand.nextInt(favSongs.size());
				                     currentSong=songsInPath.get(favSongs.get(randomSong));
				                     playList.addSong(currentSong);
				                     this.numSongs++;
			                   	 }
			                 }
	                    }
	            	}while(this.numSongs<10);
	                
	               
	                playListTable.setModel(playList);
	                //handler to play the song selected wit doubleclick on list
	                PlayThisSongHandler playThisSongHandler = new PlayThisSongHandler();
	                playListTable.addMouseListener(playThisSongHandler);   
	                
	                if (playFirstTime) {
	                	playFirstTime=false;
	                	managePlayListTable();               
	                }
	                
	                mp3Player.playList(playList);
	                timerThread = new TimerThread();
	                timerThread.setDaemon(true);
	                timerThread.start();
	                playerFrame.setVisible(true);

	            	synchronized(MP3Player.lock) {
	                   try {
	                		MP3Player.lock.wait();
	                   } catch (InterruptedException ex) {
	                        ex.printStackTrace();
	                   }
	                   
	            	}
	            } while(true);
			}
			
			 //method to return indexes of discs which mark is over than provided
			public List<Integer> getListOfDiscsByMark(Double mark){
			   List<Integer> list = new LinkedList<Integer>();
			   Double currentMark=new Double(0.0);
			   String sMark=new String("");
			   for(int currentIndex=0;currentIndex<musicTabModel.getRowCount();currentIndex++){
				   sMark=(String)musicTabModel.getValueAt(currentIndex, Disc.COL_MARK);
				   try{
					   currentMark=Double.parseDouble(sMark);
					   if (currentMark>=mark) list.add(currentIndex);
				   }catch(NumberFormatException e){
					   //nothing to do
				   }
			   }
			   return list;	   
			}
			
			public void seekFavSongs(int index,List<Song> songList){
				String rev=musicTabModel.getDiscAtRow(selectedDiscs.get(index)).review;
				String currFav="";
				boolean added;
				LevenshteinDistance dist = new LevenshteinDistance();
				dist.setThreshold(LevenshteinDistance.MED_THRESHOLD);
				
				//////////WITH NO REGEX/////////////////
				/*while (rev.indexOf("\"") > -1) {
					rev = rev.substring(rev.indexOf("\"")+"\"".length(),rev.length());
					if (rev.indexOf("\"")>-1) {
						currFav= rev.substring(0,rev.indexOf("\""));
						rev = rev.substring(rev.indexOf("\"")+"\"".length(),rev.length());
					}*/
				
				
				Pattern pattern = Pattern.compile("\"[^\"]*?\"");
				Matcher matcher = pattern.matcher(rev);			
				
				while (matcher.find()){
					currFav=matcher.group();
					currFav=currFav.substring(1,currFav.length()-1);//removing double quotes
					
					for (int ind=0;ind<songList.size();ind++){
						//System.out.println("probando "+currFav);
						added=false;
						if (songList.get(ind).name!=null){		
							if (dist.compare(currFav, songList.get(ind).name)){
							//if ((Pattern.compile(Pattern.quote(currFav), Pattern.CASE_INSENSITIVE).matcher(songList.get(ind).name).find())){
								favSongs.add(ind);
								added=true;
								break;
							}
						}
						if (!added){
							if (dist.compare(currFav, songList.get(ind).tagTitle)){
							//if (songList.get(ind).tagTitle!=null){							
								if (Pattern.compile(Pattern.quote(currFav), Pattern.CASE_INSENSITIVE).matcher(songList.get(ind).tagTitle).find()){
									favSongs.add(ind);
									break;
								}
							}
						}
					}
					
				}
			}
	  }

	   
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
					            	infoLabel.setText("Playing: "+ titleShown+"  "+ timeSt);
					            }
							}
							Thread.sleep(1000);
						} while (!closed);
					}
				} catch (Exception e) {
					Errors.showError(Errors.GENERIC_ERROR,e.toString());
				}
			}
	   }
	   
	   
	
   
}
