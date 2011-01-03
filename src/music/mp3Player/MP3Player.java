/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package music.mp3Player;
import java.io.FileInputStream;
import java.io.InputStream;

import javazoom.jl.decoder.Equalizer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 *
 * @author thrasher
 */
public class MP3Player {

    public ExtendedPlayer player;
    public InputStream is;
    public int currentSong=0,skipStart,skipEnd;
    public TabModelPlayList list;
    public boolean seqPlaying=true;
    public boolean skipping=false;
    public PlayBackHandler playBackListener= new PlayBackHandler();
    public PlayingListener playingListener;
    public MP3Player mp3Player;
    public PlayerThread pt;
    public boolean randomPlay=false;
    public float[] equalizer = new float[32];
    public Equalizer eq = new Equalizer();
    public static Object lock= new Object();
    
    
    public MP3Player(String fileName) {
        this.setFile(fileName);
    }
    public MP3Player() {
    	mp3Player=this;
    	for (int i=0;i<32;i++) equalizer[i]=0.0f; 
    	eq.setFrom(equalizer);
    }
    
    public void setEq(int band,float value){
		equalizer[band]=value;
		eq.setFrom(equalizer);
    	player.decoder.setEqualizer(eq);
    }

    public void setFile(String fileName){
        try {
             is = new FileInputStream(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playFile() {
        try {    
            pt = new PlayerThread();
            pt.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseResume(){
        player.pauseResume();
    }
   
    public void close(){
        currentSong=list.getRowCount()+1;
        player.close();
        if (player!=null) player=null;
    }
    public void playThisSong(int numSong){
        currentSong=numSong-1;
        player.stop();
    }

    public void playList(TabModelPlayList list){
        this.list=list;
        currentSong=0;
        playList();
    }
    public void playList(){
        try {
        	//System.out.println("num of Rows: "+list.getRowCount());
        	//System.out.println("currentsong: "+currentSong);
        	setFile(list.getSongAtRow(currentSong).path.getAbsolutePath());
            player = new ExtendedPlayer(is);
            player.setPlayBackListener(playBackListener);
            playFile();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }

	public long getPosition(){
		if (player!=null) return player.getPosition();
		else return 0;
	}

    public void jump(int time) {
		seqPlaying = false;
		player.stop();
		seqPlaying = true;
		skipStart = time;
		skipEnd = Integer.MAX_VALUE;
		setFile(list.getSongAtRow(currentSong).path.getAbsolutePath());
		try {
			player = new ExtendedPlayer(is);
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
        player.setPlayBackListener(playBackListener);
		skipping = true;
		pt = new PlayerThread();
		pt.start();
	}

	protected PlayingEvent createEvent(int id){
		
		if (player!=null) {
			return new PlayingEvent(id,list.getSongAtRow(currentSong).time);
		}else return new PlayingEvent(id,0);
	}
	
	public void setPlayingListener(PlayingListener listener){
		this.playingListener = listener;
	}

	public PlayingListener getPlayBackListener(){
		return playingListener;
	}
	

    private class PlayerThread extends Thread {

        public PlayerThread() {
			super();
		}

		@Override
        public void run() {
            try {
            	eq.setFrom(equalizer);
            	player.decoder.setEqualizer(eq);
                if (!skipping) player.play();
                else {
                	skipping=false;
                	player.jump(skipStart,skipEnd);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
 
   
    private class PlayBackHandler extends PlaybackListener{

        @Override
        public void playbackFinished(PlaybackEvent evt) {
            if ((currentSong < list.getRowCount())&&(seqPlaying)) {
                currentSong++;
                playList();
                
            }
            if ((randomPlay)&&(seqPlaying)&&(currentSong == list.getRowCount())){
            	currentSong=0;
            	System.out.println("Now currentSong is "+currentSong);
            	synchronized (lock) {
            		lock.notify();
				}
            }
        }
        
        @Override
        public void playbackStarted(PlaybackEvent evt){
        	for (int i=0;i<list.getRowCount();i++){
        		if (i==currentSong) list.setValueAt(true,i,TabModelPlayList.COL_CURRENT_SONG);
        		else list.setValueAt(false,i,TabModelPlayList.COL_CURRENT_SONG);
        	}
        	list.fireTableDataChanged();
        	if (playingListener != null) playingListener.playingStarted(mp3Player.createEvent(PlayingEvent.STARTED));
        }
    }


}
