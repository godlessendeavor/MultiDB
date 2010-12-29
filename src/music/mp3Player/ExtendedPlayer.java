/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package music.mp3Player;

import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.advanced.PlaybackEvent;

/**
 *
 * @author thrasher
 */
public class ExtendedPlayer extends javazoom.jl.player.advanced.AdvancedPlayer {

    protected boolean playing = true;
    
    public final int sampleFrec = 44100;
    public final int samplesPerFrame = 1152;
    
    protected boolean skipped = false;
    public int startPos=0; 
    public float[] equalizer = new float[32];

    public ExtendedPlayer(InputStream arg0) throws JavaLayerException {
        super(arg0);
    }

    @Override
    public boolean play(int frames) throws JavaLayerException {
    	
        boolean ret = true;
        // report to listener
        if (listener != null) listener.playbackStarted(createEvent(PlaybackEvent.STARTED));

        while (frames-- > 0 && ret) {
        	if (this.skipped) return false;
            if (this.playing) {
                ret = decodeFrame();
            } else {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }
        
        if (!ret) {
            // last frame, ensure all data flushed to the audio device.
            AudioDevice out = audio;
            if (out != null) {
                out.flush();
                synchronized (this) {
                    complete = (!closed);
                    close();
                }
                // report to listener
                if (listener != null)  listener.playbackFinished(createEvent(out, PlaybackEvent.STOPPED));
            }
        }
        
        return ret;
    }

	/**
	 * Plays a range of MPEG audio frames
	 * @param start	The first second in the song to play
	 * @param end The last frame to play
	 * 
	 */
	public void jump(final int start, final int end) throws JavaLayerException{
		boolean ret = true;
		long offset = start*sampleFrec/samplesPerFrame;
		startPos=start*1000;
		while (offset-- > 0 && ret) ret = skipFrame();
		play(end-start);
	}
    
	/**
	 * Retrieves the position in milliseconds of the current audio
	 * sample being played. This method delegates to the <code>
	 * AudioDevice</code> that is used by this player to sound
	 * the decoded audio samples. 
	 */
	public long getPosition(){
		long position = lastPosition;
		
		AudioDevice out = audio;
		if (out!=null) position = out.getPosition();
		long suma=position+startPos;
		return (suma);
	}	
	
	public boolean isComplete(){
		return complete;
	}
    
    public void pauseResume() {
    	playing=!playing;
    }

    @Override
    public void stop() {
        AudioDevice out = audio;
        if (out != null) {
            if (listener != null)  listener.playbackFinished(createEvent(PlaybackEvent.STOPPED));
            playing = false;
            close();
        }
    }
    
    public void setEq(int band,float value){
		equalizer[band]=value;
		if (decoder.filter1!=null) decoder.filter1.setEQ(equalizer);
		if (decoder.filter2!=null) decoder.filter2.setEQ(equalizer);
	
    }
  
}
