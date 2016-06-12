package music.db;

import java.io.*;
import java.text.*;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import main.Errors;

public class Song implements Serializable, Comparable<Object> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static public int caseCompare = 0;
	public int id;
    public String name;
    public String timeSt;
    public String group;
    public String album;
    public String tagTitle;
    public int trackNo;
    public float score;
    public String bitrate;
    public String brFormat;
    public File path;
    public File discPath;
    public Long time;
    public int discId;
    public boolean change;
    public boolean currentSong;
    
    
    /**
	  NUMBER OF COLUMN FOR TABLE FAVORITES
	 */
	public static final int COL_ID=0;
	public static final int COL_TITLE=1;
	public static final int COL_NO=2;
	public static final int COL_SCORE=3;
	public static final int COL_DISC_ID=4;
	
	

    public Song() {
    }
    //interface comparable, para ordenar primero por titulo

    public int compareTo(Object otroDisco) {
        int valor = 0;
        Collator comparador = Collator.getInstance();
        comparador.setStrength(Collator.PRIMARY);

        switch (caseCompare) {
            case 0:
                valor = -1 * comparador.compare(((Song) otroDisco).name, this.name);              
                break;
            case 1:          
                valor = this.name.compareTo(((Song) otroDisco).name);
                break;
          
            default:
                valor = this.name.compareTo(((Song) otroDisco).name);
                break;
        }
        return (valor);
    }


    public void convertTime(){
        if (this.time != null) {
            Long sec = this.time /1000000;
            Long min = sec / 60;
            sec = sec % 60;
            if (sec<10) timeSt= Long.toString(min) + ":0" + Long.toString(sec);
            else timeSt= Long.toString(min) + ":" + Long.toString(sec);
        }
    }
    
    /*   Available MP3 tag keys from http://www.javazoom.net/mp3spi/docs/doc1.9.4/index.html
	duration [Long], duration in microseconds.
	title [String], Title of the stream.
	author [String], Name of the artist of the stream.
	album [String], Name of the album of the stream.
	date [String], The date (year) of the recording or release of the stream.
	copyright [String], Copyright message of the stream.
	comment [String], Comment of the stream.

	And 
	MP3 parameters.
	mp3.version.mpeg [String], mpeg version : 1,2 or 2.5
	mp3.version.layer [String], layer version 1, 2 or 3
	mp3.version.encoding [String], mpeg encoding : MPEG1, MPEG2-LSF, MPEG2.5-LSF
	mp3.channels [Integer], number of channels 1 : mono, 2 : stereo.
	mp3.frequency.hz [Integer], sampling rate in hz.
	mp3.bitrate.nominal.bps [Integer], nominal bitrate in bps.
	mp3.length.bytes [Integer], length in bytes.
	mp3.length.frames [Integer], length in frames.
	mp3.framesize.bytes [Integer], framesize of the first frame. framesize is not constant for VBR streams.
	mp3.framerate.fps [Float], framerate in frames per seconds.
	mp3.header.pos [Integer], position of first audio header (or ID3v2 size).
	mp3.vbr [Boolean], vbr flag.
	mp3.vbr.scale [Integer], vbr scale.
	mp3.crc [Boolean], crc flag.
	mp3.original [Boolean], original flag.
	mp3.copyright [Boolean], copyright flag.
	mp3.padding [Boolean], padding flag.
	mp3.mode [Integer], mode 0:STEREO 1:JOINT_STEREO 2:DUAL_CHANNEL 3:SINGLE_CHANNEL
	mp3.id3tag.genre [String], ID3 tag (v1 or v2) genre.
	mp3.id3tag.track [String], ID3 tag (v1 or v2) track info.
	mp3.id3tag.encoded [String], ID3 tag v2 encoded by info.
	mp3.id3tag.composer [String], ID3 tag v2 composer info.
	mp3.id3tag.grouping [String], ID3 tag v2 grouping info.
	mp3.id3tag.disc [String], ID3 tag v2 track info.
	mp3.id3tag.publisher [String], ID3 tag v2 publisher info.
	mp3.id3tag.orchestra [String], ID3 tag v2 orchestra info.
	mp3.id3tag.length [String], ID3 tag v2 file length in seconds.
	mp3.id3tag.v2 [InputStream], ID3v2 frames.
	mp3.id3tag.v2.version [String], ID3v2 major version (2=v2.2.0, 3=v2.3.0, 4=v2.4.0).
	mp3.shoutcast.metadata.key [String], Shoutcast meta key with matching value. 
	For instance : 
	mp3.shoutcast.metadata.icy-irc=#shoutcast 
	mp3.shoutcast.metadata.icy-metaint=8192 
	mp3.shoutcast.metadata.icy-genre=Trance Techno Dance 
	mp3.shoutcast.metadata.icy-url=http://www.di.fm 
	and so on ...

	     */
    
    public void fillMP3Tags(){
        File file = this.path;
        String key = new String();
        AudioFileFormat baseFileFormat = null;
        try {
            baseFileFormat = AudioSystem.getAudioFileFormat(file);
            Map<?, ?> properties = baseFileFormat.properties();
            key = "duration";
            this.time = (Long) properties.get(key);
            this.convertTime();
            key = "author";
            this.group = (String) properties.get(key);
            key = "album";
            this.album = (String) properties.get(key);
            key = "title";
            this.tagTitle = (String) properties.get(key);
            key="mp3.bitrate.nominal.bps";
            Integer bitrate = (Integer) properties.get(key);
            bitrate=(bitrate/1000);
            this.bitrate=bitrate.toString()+" kbps";
            key="mp3.vbr";
            Boolean br = (Boolean) properties.get(key);
            if (br) this.brFormat="VBR"; else this.brFormat="CBR";
          
        } catch (Exception ex) {
        	Errors.writeError(Errors.FILE_IO_ERROR, "Error when reading MP3 file "+this.path);
            ex.printStackTrace();
        }
    }
   
}
