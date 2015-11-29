package music.db;

import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JTextArea;

import main.AbstractDDBB;
import main.Errors;
import main.MultiDB;


public class DataBaseFavorites extends AbstractDDBB {
	
    ///DB CONSTRAINTS
    public static final int port=MultiDB.port;
    public static final String host=MultiDB.host;
    public static final String user=MultiDB.user;
    public static final String pass=MultiDB.pass;
    public static final String database=MultiDB.musicDatabase;
    public static final String table=MultiDB.musicFavoritesTable;
    public static final int columns=5;


    public DiscTableModel tabModel;
    public JTextArea reviewView;
    public music.db.DataBaseTable musicDatabase;
    
    public DataBaseFavorites(music.db.DataBaseTable musicDatabase){
    	this.musicDatabase = musicDatabase;
    }

	public DiscTableModel getTabModel() {
		return tabModel;
	}

	public void setTabModel(DiscTableModel tabModel) {
		this.tabModel = tabModel;
	}

	public JTextArea getReviewView() {
		return reviewView;
	}

	public void setReviewView(JTextArea reviewView) {
		this.reviewView = reviewView;
	}

	// GET SONG BY ID
	public Song getSong(int id) {
		Song song = null;
		Disc disc = null;
		ResultSet rs;
		String mySelect = "select * from "+database+"."+table+" where id=\"" + id + "\"";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						rs.next();
						song = new Song();
						song.id = id;
						disc = musicDatabase.getDisc(((Integer)rs.getObject(Song.COL_DISC_ID + 1)).intValue());
						song.group = disc.group;
						song.tagTitle = (String) rs.getObject(Song.COL_TITLE + 1);
						song.album = disc.title;
						if ((Long)rs.getObject(Song.COL_NO+1)!=null) song.trackNo=((Long)rs.getObject(Song.COL_NO+1)).intValue();
						song.score=(Float)rs.getObject(Song.COL_SCORE+1);
					} else {
						Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			Errors.showError(Errors.DB_SELECT);
			Errors.writeError(Errors.DB_SELECT, ex.getMessage() + "\n");
		}
		return song;
	}
	
	// GET FAVORITE SONGS FROM DISC BY DISC ID
	public ArrayList<Song> getSongsFromDiscByDiscId(int id) {
		Song song = null;
		Disc disc = null;
		ArrayList<Song> songList = new ArrayList<Song>();
		ResultSet rs;
		String mySelect = "select * from "+database+"."+table+" where disc_id=\"" + id + "\"";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						while (rs.next()){	
							song = new Song();
							disc = musicDatabase.getDisc(((Long)rs.getObject(Song.COL_DISC_ID + 1)).intValue());
							song.group = disc.group;
							song.tagTitle = (String) rs.getObject(Song.COL_TITLE + 1);
							song.album = disc.title;
							if ((Long)rs.getObject(Song.COL_NO+1)!=null) song.trackNo=((Long)rs.getObject(Song.COL_NO+1)).intValue();
							song.score=(Float)rs.getObject(Song.COL_SCORE+1);
							songList.add(song);
						}
					} else {
						Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			Errors.showError(Errors.DB_SELECT);
			Errors.writeError(Errors.DB_SELECT, ex.getMessage() + "\n");
		}
		return songList;
	}
	
	public boolean insertNewSongIfNotExistent(Song song, int discId){
		ArrayList<Song> songList = new ArrayList<Song>();
		songList = getSongsFromDiscByDiscId(discId);
		if (songList!=null){
			for (int ind=0;ind<songList.size();ind++){
				Song currentSongInList = songList.get(ind);
				if (currentSongInList.tagTitle.compareTo(song.tagTitle)==0){
					Errors.writeError(Errors.DB_INSERT, "Random favorite song already added: "+ song.tagTitle+ " from band " + song.group + " and disc "+song.album+"\n");
					return false;
				}
			}
			insertNewSong(song,discId);
		}
		return true;
	}
	
	
	
	// INSERTS SONG IN DATABASE 
	public void insertNewSong(Song song, int discId) {
		String myInsert = "insert into "+database+"."+table+" (track_title,track_no,score,disc_id) values (\""
				+ song.tagTitle + "\",\"" + song.trackNo + "\",\"" + song.score+ "\",\"" + discId + "\")";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (insert(myInsert) > -1) {
						song.id = lastInsertID();
					} else {
						Errors.showError(Errors.DB_INSERT);
						Errors.writeError(Errors.DB_INSERT, myInsert +"\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Errors.showError(Errors.DB_INSERT);
			Errors.writeError(Errors.DB_INSERT, myInsert + "\n");
		}
	}

	// DELETE SONG FROM DATABASE
	public void deleteSong(int id) {
		String myDel="";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					
					myDel = "delete from "+table+" where id=\"" + id + "\"";
					if (delete(myDel) > -1) {
					} else {
						Errors.showError(Errors.DB_DELETE);
						Errors.writeError(Errors.DB_DELETE, myDel + "\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			Errors.showError(Errors.DB_DELETE);
			Errors.writeError(Errors.DB_DELETE, myDel + "\n");
		}
	}

	// UPDATE SONG FROM DATABASE 
	public void updateSong(Song song, int discId) {
		String myUpd = "";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					myUpd = "update "+table+" set track_title=\"" + song.tagTitle + "\",track_no=\""
							+ song.trackNo + "\",mark=\"" + song.score + "\",disc_id=\""
							+ discId + "\" where id=\"" + song.id + "\"";
					//System.out.println(myUpd);
					if (update(myUpd) > -1) {
					} else {
						Errors.showError(Errors.DB_UPDATE);
						Errors.writeError(Errors.DB_UPDATE, myUpd + "\n");
					}
					close();
				}
			}

		} catch (Exception ex) {
			Errors.showError(Errors.DB_UPDATE);
			Errors.writeError(Errors.DB_UPDATE,myUpd + "\n");
		}
	}
	
	
	public int makeBackup(String nameFile){
		if (makeBackupCSV(nameFile,database,table)>-1) return 0;
		else return Errors.DB_BUP;
		
	}
	
	public int restoreBackup(String nameFile){
		if (restoreBackupCSV(nameFile,database,table)>-1) return 0;
		else return Errors.DB_RESTORE;
	}
	
	
}
