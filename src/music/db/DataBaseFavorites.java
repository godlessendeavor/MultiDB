package music.db;

import java.sql.ResultSet;
import java.sql.SQLException;
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
		String mySelect = "select * from "+database+"."+table+" where id=\"" + id + "\"";		
		return getSongSelect(mySelect);
	}
	
	// GET FAVORITE SONGS FROM DISC BY DISC ID
	public ArrayList<Song> getSongsFromDiscByDiscId(int id) throws com.mysql.jdbc.exceptions.jdbc4.CommunicationsException{
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
							//System.out.println(id);
							disc = musicDatabase.getDisc(id);
							song.group = disc.group;
							//System.out.println(song.group);
							song.tagTitle = (String) rs.getObject(Song.COL_TITLE + 1);
							//System.out.println(song.tagTitle);
							song.album = disc.title;
							//System.out.println(song.album);
							if ((Long)rs.getObject(Song.COL_NO+1)!=null) song.trackNo=((Long)rs.getObject(Song.COL_NO+1)).intValue();
							//System.out.println(song.trackNo);
							song.score=(Float)rs.getObject(Song.COL_SCORE+1);
							//System.out.println(song.score);
							songList.add(song);
						}
					} else {
						Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
					}
					close();
				}
			}
		} catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException ex) {
			Errors.showError(Errors.DB_SELECT, ""+ ex);
			ex.printStackTrace();
			Errors.writeError(Errors.DB_SELECT, mySelect + " "+ ex);
			throw ex;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return songList;
	}
	
	public Song getRandomSongGreaterThanScore(Double score){
		/*String mySelect = "SELECT * FROM "+database+"."+table+" AS r1 join "
				+ "(SELECT CEIL(RAND() * "
		        + "(SELECT MAX(id) FROM "+database+"."+table+")) AS id) "
		        + "AS r2 WHERE r1.id >= r2.id AND r1.score > " + score+" " 
		        + "ORDER BY r1.id ASC LIMIT 1";*/
		String mySelect = "SELECT * FROM "+database+"."+table+" AS r WHERE r.score > " + score+" "+ "ORDER BY RAND() LIMIT 1";
		return getSongSelect(mySelect);
	}
	
	private Song getSongSelect(String selectQuery){
		Song song = null;
		Disc disc = null;
		//System.out.println(selectQuery);
		try {
			ResultSet rs;
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(selectQuery)>-1) {
						rs = getRs();
						if (rs.next()){
							song = new Song();
							song.id = ((Long) rs.getObject(Song.COL_ID + 1)).intValue();
							int discId = ((Long) rs.getObject(Song.COL_DISC_ID + 1)).intValue();
							disc = musicDatabase.getDisc(discId);
							song.group = disc.group;
							song.tagTitle = (String) rs.getObject(Song.COL_TITLE + 1);
							song.album = disc.title;
							if ((Long)rs.getObject(Song.COL_NO+1)!=null) song.trackNo=((Long)rs.getObject(Song.COL_NO+1)).intValue();
							song.score=(Float)rs.getObject(Song.COL_SCORE+1);
							song.discPath = disc.getPath();
						}else{
							Errors.writeError(Errors.DB_SELECT, selectQuery + "\n");
						}
					} else {
						Errors.writeError(Errors.DB_SELECT, selectQuery + "\n");
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
	
	public boolean insertNewSongIfNotExistent(Song song){
		ArrayList<Song> songList = new ArrayList<Song>();
		try
		{
			//retrieve all discs from discID
			songList = getSongsFromDiscByDiscId(song.discId);
			//and check if it's already added
			if (songList!=null){
				for (int ind=0; ind<songList.size(); ind++){
					Song currentSongInList = songList.get(ind);
					if (currentSongInList.tagTitle.compareTo(song.tagTitle)==0){
						Errors.showError(Errors.DB_INSERT, "This song is already in the favorites list: "+song.group+", "+song.tagTitle);
						return false;
					}
				}
				insertNewSong(song);
			}
			return true;
		}
		catch(com.mysql.jdbc.exceptions.jdbc4.CommunicationsException ex){
			Errors.writeError(Errors.DB_INSERT, "Error trying to communicate with database when trying to add favorite song "+ song.tagTitle+ " from band " + song.group + " and disc "+song.album+"\n");			
			return false;
		}
	}
	
	
	
	// INSERTS SONG IN DATABASE 
	public void insertNewSong(Song song) {
		String myInsert = "insert into "+database+"."+table+" (track_title,track_no,score,disc_id) values (\""
				+ song.tagTitle + "\",\"" + song.trackNo + "\",\"" + song.score+ "\",\"" + song.discId + "\")";
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
