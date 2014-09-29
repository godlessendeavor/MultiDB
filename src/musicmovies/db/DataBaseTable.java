package musicmovies.db;

import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JTextArea;

import main.AbstractDDBB;
import main.Errors;
import main.MultiDB;


public class DataBaseTable extends AbstractDDBB {
	
    ///DB CONSTRAINTS
    public static final int port=MultiDB.port;
    public static final String host=MultiDB.host;
    public static final String user=MultiDB.user;
    public static final String pass=MultiDB.pass;
    public static final String database=MultiDB.musicDatabase;
    public static final String table=MultiDB.musicMoviesTable;
    public static final int columns=10;

    public TabMod tabModel;
    public JTextArea reviewView;

	public TabMod getTabModel() {
		return tabModel;
	}

	public void setTabModel(TabMod tabModel) {
		this.tabModel = tabModel;
	}

	public JTextArea getReviewView() {
		return reviewView;
	}

	public void setReviewView(JTextArea reviewView) {
		this.reviewView = reviewView;
	}

	// GET DISC BY ID
	public Video getVideo(int id) {
		Video vid = null;
		ResultSet rs;
		String mySelect = "select * from "+table+" where id=\"" + id + "\"";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						rs.next();
						vid = new Video();
						vid.id = new Long(id);
						vid.group = (String) rs.getObject(Video.COL_GROUP + 1);
						vid.title = (String) rs.getObject(Video.COL_TITLE + 1);
						vid.style = (String) rs.getObject(Video.COL_STYLE + 1);
						if ((Long)rs.getObject(Video.COL_YEAR+1)!=null) vid.year=((Long)rs.getObject(Video.COL_YEAR+1)).toString();
						vid.loc = (String) rs.getObject(Video.COL_LOC + 1);
						vid.copy = (String) rs.getObject(Video.COL_COPY + 1);
						vid.type = (String) rs.getObject(Video.COL_TYPE + 1);
						vid.mark = (String) rs.getObject(Video.COL_MARK + 1);
						vid.review = (String) rs.getObject(Video.COL_REVIEW + 1);
					} else {
						reviewView.append("Data searching in DB failed\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			reviewView.append("Error while searching in the database\n");
		}
		return vid;
	}
	
	
	//GETDISCBYGROUPNAME:gets the first disc of group which name is name provided
	public Video getVideoByGroupName(String name) {
		Video disc = null;
		ResultSet rs;
		String mySelect = "select * from "+table+" where groupName=\"" +  name+ "\"";
		//System.out.println(mySelect);
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						rs.next();
						disc = new Video();
						disc.id = (Long) rs.getObject(Video.COL_ID + 1);
						disc.group = (String) rs.getObject(Video.COL_GROUP + 1);
						disc.title = (String) rs.getObject(Video.COL_TITLE + 1);
						disc.style = (String) rs.getObject(Video.COL_STYLE + 1);
						if ((Long)rs.getObject(Video.COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(Video.COL_YEAR+1)).toString();
						disc.loc = (String) rs.getObject(Video.COL_LOC + 1);
						disc.copy = (String) rs.getObject(Video.COL_COPY + 1);
						disc.type = (String) rs.getObject(Video.COL_TYPE + 1);
						disc.mark = (String) rs.getObject(Video.COL_MARK + 1);
						disc.review = (String) rs.getObject(Video.COL_REVIEW + 1);
					} else {
						reviewView.append("Data searching in DB failed\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			reviewView.append("Error while searching in the database\n");
		}
		return disc;
	}
	
	//GETDISCBYGROUPNAME:gets the discography of group which name is name provided
	public ArrayList<Video> getVideosOfGroup(String name) {
		Video vid = null;
		ArrayList<Video> discList = new ArrayList<Video>();
		ResultSet rs;
		String mySelect = "select * from "+table+" where groupName=\"" +  name+ "\"";
		//System.out.println(mySelect);
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						while (rs.next()){
							vid = new Video();
							vid.id = (Long) rs.getObject(Video.COL_ID + 1);
							vid.group = (String) rs.getObject(Video.COL_GROUP + 1);
							vid.title = (String) rs.getObject(Video.COL_TITLE + 1);
							vid.style = (String) rs.getObject(Video.COL_STYLE + 1);
							if ((Long)rs.getObject(Video.COL_YEAR+1)!=null) vid.year=((Long)rs.getObject(Video.COL_YEAR+1)).toString();
							vid.loc = (String) rs.getObject(Video.COL_LOC + 1);
							vid.copy = (String) rs.getObject(Video.COL_COPY + 1);
							vid.type = (String) rs.getObject(Video.COL_TYPE + 1);
							vid.mark = (String) rs.getObject(Video.COL_MARK + 1);
							vid.review = (String) rs.getObject(Video.COL_REVIEW + 1);
							discList.add(vid);
						}
					} else {
						reviewView.append("Data searching in DB failed\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			reviewView.append("Error while searching in the database\n");
		}
		return discList;
	}
	
	//GETDISCBYGROUPNAME:gets disc which group and title are provided
	public Video getVideosByDG(String groupName, String discName) {
		Video vid = null;
		ResultSet rs;
		String mySelect = "select * from "+table+" where groupName=\"" + groupName+ "\" and title=\""+discName+"\"";
		//System.out.println(mySelect);
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						rs.next();
						vid = new Video();
						vid.id = (Long) rs.getObject(Video.COL_ID + 1);
						vid.group = (String) rs.getObject(Video.COL_GROUP + 1);
						vid.title = (String) rs.getObject(Video.COL_TITLE + 1);
						vid.style = (String) rs.getObject(Video.COL_STYLE + 1);
						if ((Long)rs.getObject(Video.COL_YEAR+1)!=null) vid.year=((Long)rs.getObject(Video.COL_YEAR+1)).toString();
						vid.loc = (String) rs.getObject(Video.COL_LOC + 1);
						vid.copy = (String) rs.getObject(Video.COL_COPY + 1);
						vid.type = (String) rs.getObject(Video.COL_TYPE + 1);
						vid.mark = (String) rs.getObject(Video.COL_MARK + 1);
						vid.review = (String) rs.getObject(Video.COL_REVIEW + 1);
					} else {
						reviewView.append("Data searching in DB failed\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			reviewView.append("Error while searching in the database\n");
		}
		return vid;
	}
	
	// INSERT VIDEO IN DATABASE AND TABLEMODEL
	public void insertNewVideo(Video vid) {
		String myInsert = "insert into "+table+" (groupName,title,year,style,loc,copy,type,mark,review) values (\""
				+ vid.group + "\",\"" + vid.title + "\",\"" + vid.year+ "\",\"" + vid.style + "\",\"" + vid.loc
				+ "\",\"" + vid.copy + "\",\"" + vid.type + "\",\"" + vid.mark + "\",\"" + vid.review
				+ "\")";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (insert(myInsert) > -1) {
						vid.id = new Long(lastInsertID());
						tabModel.addVideo(vid);
					} else {
						reviewView.append("Data inserting in DB failed\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			reviewView.append("Trouble with insert\n");
		}
	}

	// DELETE VIDEO FROM DATABASE AND TABLEMODEL
	public void deleteVideo(int row) {
		int id = (Integer) tabModel.getValueAt(row, Video.COL_ID);
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					String myDel;
					myDel = "delete from "+table+" where id=\"" + id + "\"";
					if (delete(myDel) > -1) {
						// System.out.println("Deleting succesful!!!");
						tabModel.deleteVideo(row);
					} else {
						reviewView.append("Error deleting fields\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			reviewView.append("An exception happened when trying to delete from database\n");
		}
	}

	// UPDATE VIDEO FROM DATABASE AND TABLEMODEL
	public void updateVideo(Video vid,int selectedRow) {
		String myUpd = "";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {

					myUpd = "update "+table+" set groupName=\"" + vid.group
							+ "\",title=\"" + vid.title + "\",style=\""
							+ vid.style + "\",year=\"" + vid.year
							+ "\",loc=\"" + vid.loc + "\",type=\"" + vid.type
							+ "\",mark=\"" + vid.mark + "\",copy=\""
							+ vid.copy + "\",review=\"" + vid.review
							+ "\" where id=\"" + vid.id + "\"";
					//System.out.println(myUpd);
					if (update(myUpd) > -1) {
						tabModel.setVideoAtRow(vid,selectedRow);
					} else {
						reviewView.append("Error updating fields\n");
					}
					close();
				}
			}

		} catch (Exception ex) {
			reviewView.append("An exception happened  when trying to update the database"+ myUpd + "\n");
		}
	}
	
	public void updateReviewOnly(Video disc){
		try {
            if (cargaControlador()>-1) {
                if (open("jdbc:mysql://localhost:"+port+"/"+database,user,pass)>-1) {
                    String myUpd;
                    myUpd = "update "+table+" set review=\"" + disc.review + "\" where id=\"" + disc.id + "\"";
                    //System.out.println(myUpd);
                    if (update(myUpd) > -1) {
                        //System.out.println("Updating succesful!!!");
                    } else {
                        reviewView.append("Error updating fields\n");
                    }
                    close();
                }
            }
        } catch (Exception ex) {
            reviewView.append("An exception happened when trying to update the database\n");
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
