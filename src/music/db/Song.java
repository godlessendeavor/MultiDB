package music.db;

import java.io.*;
import java.text.*;

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
   
}
