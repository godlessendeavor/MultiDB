package music.lyrics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import main.Errors;
import music.dealfiles.DealMusicFiles;
import music.mp3Player.MP3PlayerWindow.TimerThread;
import music.web.WebMusicInfoExtractor;



public class LyricsFrame {
	
    public final Dimension LYRICS_DIM = new Dimension(500,700);
    
	  //LYRICS ELEMENTS
    public JFrame lyricsFrame;
    public JTextArea lyricsView;
    public JButton saveLyricsButton;
    public JButton downloadLyricsButton;
    public String lyricsGroup;
    public String lyricsAlbum;
    public File lyricsPath;
    public Font font;
    public TimerThread timerThread;
    public JScrollPane spLyrics;
    public File lyricsFile;  
    
    
    public LyricsFrame(){
    	this.initFrame();    	
    }

	
	public void initFrame(){
		
	////////////layout for lyrics frame////////////////////////////////////////////////////////////////////////
	    lyricsFrame=new JFrame("Lyrics");
	    lyricsFrame.setSize(LYRICS_DIM);
	    //lyricsFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
	    
	    //handler to save when closing frame
	    CloseLyricsFrameHandler closeLyricsFrameHandler = new CloseLyricsFrameHandler();
	    lyricsFrame.addWindowListener(closeLyricsFrameHandler);
    
	}
	
	public void open(File path, String group, String album){
		lyricsPath = path;
		lyricsGroup = group;
		lyricsAlbum = album;
		lyricsView.setText("");
  	    if ((lyricsFile=DealMusicFiles.searchLyricsFile(lyricsPath))!=null){
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
  			   Errors.showError(Errors.WRITING_IOERROR,"File: "+ lyricsFile);
  		   }
  	    }
  	    lyricsView.setCaretPosition(0); //to place the scroll on the top
  	    lyricsFrame.setVisible(true);
  	   
	}
	
	
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
	   
	   
	   public void saveCurrentLyrics(){
	    	File fileToWrite;
			if (lyricsFile==null){    	
				fileToWrite=new File (lyricsPath.getAbsolutePath()+File.separator+"lyrics.txt");
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
				Errors.showError(Errors.COPYING_IOERROR,ex.toString());
			}
	    	
	    }
	   
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
	
	 //GET LYRICS/////////////////////////////////////////////////////////////////////////  
	   public class GetLyricsThread extends Thread {
		   
			public GetLyricsThread() {
				super();
			}

			@Override
			public void run() {
				lyricsView.setText(WebMusicInfoExtractor.getLyricsOfDisc(lyricsGroup,lyricsAlbum,"webEM"));	
			}
	   }
	   
}
