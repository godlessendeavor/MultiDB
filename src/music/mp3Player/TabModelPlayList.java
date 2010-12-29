/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package music.mp3Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.table.AbstractTableModel;

import main.exceptions.MP3FilesNotFound;

/**
 *
 * @author thrasher
 */
public class TabModelPlayList extends AbstractTableModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int COL_NAME=0;
	public static final int COL_TIME=1;
	public static final int COL_GROUP=2;
	public static final int COL_TITLE=3;
	public static final int COL_BR=4;
	public static final int COL_SF=5;
	public static final int COL_T=6;
	public static final int COL_P=7;
	public static final int COL_CHANGE=8;
	public static final int COL_CURRENT_SONG=9;
	
	public File pathFolder;
	public String group="";
	public String album="";
	private String[] labels;
    private ArrayList<Song> data;
    private Song song;
    public int numSongs=0;

    public TabModelPlayList(){
    	labels = new String[10];// Se obtiene cada una de las etiquetas para cada columna
        labels[0] = "File name";
        labels[1] = "Time";
        labels[2] = "Group";
        labels[3] = "Tag title";
        labels[4] = "Bitrate";
        labels[5] = "Sampling Format";
        labels[6] = "t";
        labels[7] = "p";
        labels[8] = "change";
        labels[9] = "currentSong";
        
        data = new ArrayList<Song>();
    }

    public void searchFiles(File pathFolder) throws MP3FilesNotFound{
    	
    	this.pathFolder=pathFolder;
    	String[] files = pathFolder.list();
        int tam = files.length;
        if (tam == 0) { //no files
        } else { //for every file in this folder
            int index=0;
            for (int j = 0; j < tam; j++) {
            	if (files[j].matches("(?i).*.mp3")){
                    numSongs++;
                    song = new Song();
                    index=files[j].lastIndexOf(".");
                    song.name = files[j].substring(0, index);
                    song.time = new Long(0);
                    song.path = new File(pathFolder.getAbsolutePath() + "\\" + files[j]);
                    song.change=false;
                    tagReader(song);
                    data.add(song);
                }else{
                	File posFolder = new File(pathFolder+"\\"+files[j]);
                	if (posFolder.isDirectory()) searchFiles(posFolder);
                }
            }	
        }
        
        if (numSongs==0) {
			System.out.println("No mp3 files found");
			throw (new MP3FilesNotFound());
		}
    }
    
    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return labels.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object ob = new Object();
        if (columnIndex == 0) ob = data.get(rowIndex).name;
        if (columnIndex == 1) ob = data.get(rowIndex).timeSt;
        if (columnIndex == 2) ob = data.get(rowIndex).group;
        if (columnIndex == 3) ob = data.get(rowIndex).tagTitle;
        if (columnIndex == 4) ob = data.get(rowIndex).bitrate;
        if (columnIndex == 5) ob = data.get(rowIndex).brFormat;
        if (columnIndex == 6) ob = data.get(rowIndex).path;
        if (columnIndex == 7) ob = data.get(rowIndex).time;
        if (columnIndex == 8) ob = data.get(rowIndex).change;
        if (columnIndex == 9) ob = data.get(rowIndex).currentSong;
        return ob;
    }

    public Song getSongAtRow(int row) {
        song = data.get(row);
        return song;
    }
   

    public void setSongAtRow(Song song, int row) {
        this.setValueAt(song.name, row, 0);
        this.setValueAt(song.timeSt, row, 1);
        this.setValueAt(song.group, row, 2);
        this.setValueAt(song.tagTitle, row, 3);
        this.setValueAt(song.bitrate, row, 4);
        this.setValueAt(song.brFormat, row, 5);
        this.setValueAt(song.path, row, 6);
        this.setValueAt(song.time, row, 7);
        this.setValueAt(song.change, row, 8);
        this.setValueAt(song.currentSong, row, 9);
        this.fireTableRowsUpdated(row, row);
    }

    public int addSong(Song song) {
        data.add(song);
        this.fireTableRowsInserted(data.size(), data.size());
        return (data.size() - 1);
    }

    public void deleteSong(int row) {
        data.remove(row);
        this.fireTableRowsDeleted(row, row);
    }

    public void removeAllRows(){
    	int plLength = data.size();
    	for(int row=0;row<plLength;row++){
    		data.remove(0);
    	}
    }
    
    public void sort() {
        Collections.sort(data);
        this.fireTableDataChanged();
    }

    private void tagReader(Song song) {
        File file = song.path;
        String key = new String();
        AudioFileFormat baseFileFormat = null;
        try {
            baseFileFormat = AudioSystem.getAudioFileFormat(file);
            Map<?, ?> properties = baseFileFormat.properties();
            key = "duration";
            song.time = (Long) properties.get(key);
            song.convertTime();
            key = "author";
            song.group = (String) properties.get(key);
            key = "title";
            song.tagTitle = (String) properties.get(key);
            key="mp3.bitrate.nominal.bps";
            Integer bitrate = (Integer) properties.get(key);
            bitrate=(bitrate/1000);
            song.bitrate=bitrate.toString()+" kbps";
            key="mp3.vbr";
            Boolean br = (Boolean) properties.get(key);
            if (br) song.brFormat="VBR"; else song.brFormat="CBR";
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    /*   
     * "author" (String): Name of the author of the file
     * "title" (String): Title of the file
     * "copyright" (String): Copyright message
     * "comment" (String): Arbitrary text
     */
    }


    ///OVERRIDEN METHODS
    @Override
    public String getColumnName(int col) {
        return labels[col];
    }

      @Override
      public Class<? extends Object> getColumnClass(int c) {
         return String.class;
     }
    @Override
    public boolean isCellEditable(int row, int col) {
    	if (col==0) return true; else  return false;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {

        if (columnIndex == COL_NAME) {
            data.get(rowIndex).name = (String) value;
        }
        if (columnIndex == COL_CHANGE) {
            data.get(rowIndex).change = (Boolean) value;
        }
        if (columnIndex == COL_CURRENT_SONG) {
            data.get(rowIndex).currentSong = (Boolean) value;
        }

        if (columnIndex == COL_T) {
            if (value instanceof Long) {
                data.get(rowIndex).time = (Long) value;
            } else if (value instanceof String) {
                if (((String) value).compareTo("") != 0) {
                    data.get(rowIndex).time = new Long((String) value);
                } else {
                    data.get(rowIndex).time = new Long(0);
                }
            }
        }

        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }
}

