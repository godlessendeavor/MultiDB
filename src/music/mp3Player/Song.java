package music.mp3Player;

import java.io.*;
import java.text.*;

public class Song implements Serializable, Comparable<Object> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static public int caseCompare = 0;
    public String name;
    public String timeSt;
    public String group;
    public String tagTitle;
    public String bitrate;
    public String brFormat;
    public File path;
    public Long time;  
    public boolean change;
    public boolean currentSong;
    

    public Song() {
    }
    //interface comparable, para ordenar primero por t�tulo y luego por a�o

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
