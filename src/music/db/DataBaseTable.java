package music.db;

import java.io.File;
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
    public static final String table=MultiDB.musicTable;
    public static final int columns=10;
    //public static final String mysqlPath=MultiDB.mysqlPath;
    

    public DiscTableModel tabModel;
    public JTextArea reviewView;

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

	// GET DISC BY ID
	public Disc getDisc(int id) {
		Disc disc = null;
		ResultSet rs;
		int row;
		String mySelect = "select * from "+table+" where id=\"" + id + "\"";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						rs.next();
						disc = new Disc();
						disc.id = id;
						disc.group = (String) rs.getObject(Disc.COL_GROUP + 1);
						disc.title = (String) rs.getObject(Disc.COL_TITLE + 1);
						disc.style = (String) rs.getObject(Disc.COL_STYLE + 1);
						if ((Long)rs.getObject(Disc.COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(Disc.COL_YEAR+1)).toString();
						disc.loc = (String) rs.getObject(Disc.COL_LOC + 1);
						disc.copy = (String) rs.getObject(Disc.COL_COPY + 1);
						disc.type = (String) rs.getObject(Disc.COL_TYPE + 1);
						disc.mark = (String) rs.getObject(Disc.COL_MARK + 1);
						disc.review = (String) rs.getObject(Disc.COL_REVIEW + 1);
						row = tabModel.searchDisc(disc.id);
						disc.present = (String) tabModel.getValueAt(row,Disc.COL_PRESENT);
						disc.path = (File) tabModel.getValueAt(row, Disc.COL_PATH);
					} else {
						Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			Errors.showError(Errors.DB_SELECT);
			Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
		}
		return disc;
	}
	
	
	//GETDISCBYGROUPNAME:gets the first disc of group which name is name provided
	public Disc getDiscByGroupName(String name) {
		Disc disc = null;
		ResultSet rs;
		int row;
		String mySelect = "select * from "+table+" where groupName=\"" +  name+ "\"";
		//System.out.println(mySelect);
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						rs.next();
						disc = new Disc();
						disc.id = (Integer) rs.getObject(Disc.COL_ID + 1);
						disc.group = (String) rs.getObject(Disc.COL_GROUP + 1);
						disc.title = (String) rs.getObject(Disc.COL_TITLE + 1);
						disc.style = (String) rs.getObject(Disc.COL_STYLE + 1);
						if ((Long)rs.getObject(Disc.COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(Disc.COL_YEAR+1)).toString();
						disc.loc = (String) rs.getObject(Disc.COL_LOC + 1);
						disc.copy = (String) rs.getObject(Disc.COL_COPY + 1);
						disc.type = (String) rs.getObject(Disc.COL_TYPE + 1);
						disc.mark = (String) rs.getObject(Disc.COL_MARK + 1);
						disc.review = (String) rs.getObject(Disc.COL_REVIEW + 1);
						row = tabModel.searchDisc(disc.id);
						disc.present = (String) tabModel.getValueAt(row,Disc.COL_PRESENT);
						disc.path = (File) tabModel.getValueAt(row, Disc.COL_PATH);
					} else {
						Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			Errors.showError(Errors.DB_SELECT);
			Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
		}
		return disc;
	}
	
	//GETDISCBYGROUPNAME:gets the discography of group which name is name provided
	public ArrayList<Disc> getDiscsOfGroup(String name) {
		Disc disc = null;
		ArrayList<Disc> discList = new ArrayList<Disc>();
		ResultSet rs;
		int row;
		String mySelect = "select * from "+table+" where groupName=\"" +  name+ "\"";
		//System.out.println(mySelect);
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						while (rs.next()){
							disc = new Disc();
							disc.id = (Integer) rs.getObject(Disc.COL_ID + 1);
							disc.group = (String) rs.getObject(Disc.COL_GROUP + 1);
							disc.title = (String) rs.getObject(Disc.COL_TITLE + 1);
							disc.style = (String) rs.getObject(Disc.COL_STYLE + 1);
							if ((Long)rs.getObject(Disc.COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(Disc.COL_YEAR+1)).toString();
							disc.loc = (String) rs.getObject(Disc.COL_LOC + 1);
							disc.copy = (String) rs.getObject(Disc.COL_COPY + 1);
							disc.type = (String) rs.getObject(Disc.COL_TYPE + 1);
							disc.mark = (String) rs.getObject(Disc.COL_MARK + 1);
							disc.review = (String) rs.getObject(Disc.COL_REVIEW + 1);
							row = tabModel.searchDisc(disc.id);
							disc.present = (String) tabModel.getValueAt(row,Disc.COL_PRESENT);
							disc.path = (File) tabModel.getValueAt(row, Disc.COL_PATH);
							discList.add(disc);
						}
					} else {
						Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			Errors.showError(Errors.DB_SELECT);
			Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
		}
		return discList;
	}
	
	//GETDISCBYGROUPNAME:gets disc which group and title are provided
	public Disc getDiscByDG(String groupName, String discName) {
		Disc disc = null;
		ResultSet rs;
		int row;
		String mySelect = "select * from "+table+" where groupName=\"" + groupName+ "\" and title=\""+discName+"\"";
		//System.out.println(mySelect);
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						if (!rs.next()) return null;
						disc = new Disc();
						disc.id = (Integer) rs.getObject(Disc.COL_ID + 1);
						disc.group = (String) rs.getObject(Disc.COL_GROUP + 1);
						disc.title = (String) rs.getObject(Disc.COL_TITLE + 1);
						disc.style = (String) rs.getObject(Disc.COL_STYLE + 1);
						if ((Long)rs.getObject(Disc.COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(Disc.COL_YEAR+1)).toString();
						disc.loc = (String) rs.getObject(Disc.COL_LOC + 1);
						disc.copy = (String) rs.getObject(Disc.COL_COPY + 1);
						disc.type = (String) rs.getObject(Disc.COL_TYPE + 1);
						disc.mark = (String) rs.getObject(Disc.COL_MARK + 1);
						disc.review = (String) rs.getObject(Disc.COL_REVIEW + 1);
						row = tabModel.searchDisc(disc.id);
						disc.present = (String) tabModel.getValueAt(row,Disc.COL_PRESENT);
						disc.path = (File) tabModel.getValueAt(row, Disc.COL_PATH);
					} else {
						Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			Errors.showError(Errors.DB_SELECT);
			Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
		}
		return disc;
	}
	
	// INSERT DISC IN DATABASE AND TABLEMODEL
	public void insertNewDisc(Disc disc) {
		disc.check();
		String myInsert = "insert into "+table+" (groupName,title,year,style,loc,copy,type,mark,review) values (\""
				+ disc.group + "\",\"" + disc.title + "\",\"" + disc.year+ "\",\"" + disc.style + "\",\"" + disc.loc
				+ "\",\"" + disc.copy + "\",\"" + disc.type + "\",\"" + disc.mark + "\",\"" + disc.review
				+ "\")";
		int row;
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if ((row = insert(myInsert)) > -1) {
						disc.id = lastInsertID();
						row = tabModel.addDisc(disc);
						tabModel.setValueAt("?", row, Disc.COL_PRESENT);
						tabModel.setValueAt(disc.path, row, Disc.COL_PATH);
					} else {
						Errors.showError(Errors.DB_INSERT);
						Errors.writeError(Errors.DB_INSERT, myInsert + "\n");
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

	// DELETE DISC FROM DATABASE AND TABLEMODEL
	public void deleteDisc(int row) {
		int id = (Integer) tabModel.getValueAt(row, Disc.COL_ID);
		String myDel="";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					
					myDel = "delete from "+table+" where id=\"" + id + "\"";
					if (delete(myDel) > -1) {
						// System.out.println("Deleting succesful!!!");
						tabModel.deleteDisc(row);
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

	// UPDATE DISC FROM DATABASE AND TABLEMODEL
	public void updateDisc(Disc disc,int selectedRow) {
		String myUpd = "";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					disc.check();
					myUpd = "update "+table+" set groupName=\"" + disc.group
							+ "\",title=\"" + disc.title + "\",style=\""
							+ disc.style + "\",year=\"" + disc.year
							+ "\",loc=\"" + disc.loc + "\",type=\"" + disc.type
							+ "\",mark=\"" + disc.mark + "\",copy=\""
							+ disc.copy + "\",review=\"" + disc.review
							+ "\" where id=\"" + disc.id + "\"";
					if (update(myUpd) > -1) {
						tabModel.setDiscAtRow(disc,selectedRow);
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
	
	public void updateDiscWithoutReview(Disc disc,int selectedRow) {
		String myUpd = "";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {					
					disc.check();
					myUpd = "update "+table+" set groupName=\"" + disc.group
							+ "\",title=\"" + disc.title + "\",style=\""
							+ disc.style + "\",year=\"" + disc.year
							+ "\",loc=\"" + disc.loc + "\",type=\"" + disc.type
							+ "\",mark=\"" + disc.mark + "\",copy=\""
							+ disc.copy + "\" where id=\"" + disc.id + "\"";
					if (update(myUpd) > -1) {
						tabModel.setDiscAtRow(disc,selectedRow);
					} else {
						reviewView.append("Error updating fields\n");
					}
					close();
				}
			}

		} catch (Exception ex) {
			Errors.showError(Errors.DB_UPDATE,"An exception happened  when trying to update the database\n");
			Errors.writeError(Errors.DB_UPDATE,"An exception happened  when trying to update the database"+ myUpd + "\n");
		}
	}
	
	public void updateReviewOnly(Disc disc){
		String myUpd = "";
		try {
            if (cargaControlador()>-1) {
                if (open("jdbc:mysql://localhost:"+port+"/"+database,user,pass)>-1) {
                    disc.check();
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
        	Errors.showError(Errors.DB_UPDATE,"An exception happened  when trying to update the database\n");
        	Errors.writeError(Errors.DB_UPDATE,"An exception happened  when trying to update the database"+ myUpd + "\n");
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
	
//	public boolean makeBackup(String dirDest){
//		final int BUFFER = 32768;
//		 try {
//			 String exec = mysqlPath+
//             "mysqldump --host=" + host + " --port=" + port +
//             " --user=" + user + " --password=" + pass +
//             " --compact --complete-insert --extended-insert " +
//             "--skip-comments --skip-triggers " + database;
//             Process run = Runtime.getRuntime().exec(exec);
//             InputStream in = run.getInputStream();
//             BufferedReader br = new BufferedReader(new InputStreamReader(in));
//             StringBuffer temp = new StringBuffer() ;
//             int count;
//             char[] cbuf = new char[BUFFER];
//             while ((count = br.read(cbuf, 0, BUFFER)) != -1) {
//                 temp.append(cbuf, 0, count);
//             }
//             br.close();
//             in.close();
//             DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//             Date date = new Date();
//             
//             File filedst = new File(dirDest+"\\"+dateFormat.format(date)+table+".sql");
//             FileOutputStream dest = new FileOutputStream(filedst);
//             ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(dest));
//             zip.setMethod(ZipOutputStream.DEFLATED);
//             zip.setLevel(Deflater.BEST_COMPRESSION);
//             zip.putNextEntry(new ZipEntry(dirDest+"\\"+dateFormat.format(date)+table+".sql"));
//             zip.write(temp.toString().getBytes());
//             zip.close();
//             dest.close();
//             return true;
//         } catch (Exception ex) {
//        	 ex.printStackTrace();
//         	reviewView.append("Error while trying to make backup\n");
//         	return false;
//         }
//	}
	
	
}
