package docs.db;

import java.sql.ResultSet;
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
	public static final String database=MultiDB.moviesDatabase;
	public static final String table=MultiDB.docsTable;
	public static final String mysqlPath=MultiDB.mysqlPath;
	public static final String nameCSV="dataCSV.csv";
	public static final int columns=5;

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

	// GET MOVIE BY ID
	public Doc getDoc(int id) {
		Doc doc = null;
		ResultSet rs;
		String mySelect = "select * from "+ table+ " where id=\"" + id + "\"";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						rs.next();
						doc = new Doc();
						doc.id = (Integer) rs.getObject(Doc.COL_ID + 1);
						doc.title = (String) rs.getObject(Doc.COL_TITLE + 1);
						doc.loc = (String) rs.getObject(Doc.COL_LOC + 1);
						doc.setThemeByString((String) rs.getObject(Doc.COL_THEME + 1));
						doc.comments = (String) rs.getObject(Doc.COL_COMMENTS + 1);
					} else {
						Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Errors.showError(Errors.DB_SELECT);
			Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
		}
		return doc;
	}
	
	public Doc getDocByName(String name) {
		Doc doc = null;
		ResultSet rs;
		String mySelect = "select * from "+ table+ " where title=\"" +  name+ "\"";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						rs.next();
						doc = new Doc();
						doc.id = (Integer) rs.getObject(Doc.COL_ID + 1);
						doc.title = (String) rs.getObject(Doc.COL_TITLE + 1);
						doc.loc = (String) rs.getObject(Doc.COL_LOC + 1);
						doc.setThemeByString((String) rs.getObject(Doc.COL_THEME + 1));
						doc.comments = (String) rs.getObject(Doc.COL_COMMENTS + 1);
					} else {
						Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Errors.showError(Errors.DB_SELECT);
			Errors.writeError(Errors.DB_SELECT, mySelect + "\n");
		}
		return doc;
	}

	// INSERT MOVIE IN DATABASE AND TABLEMODEL
	public void insertNewDoc(Doc doc) {
		String myInsert = "insert into "+table+" (title,loc,theme,comments) values (\""
				+ doc.title + "\",\"" + doc.loc + "\",\"" + doc.theme + "\",\"" + doc.comments +"\")";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					// System.out.println(myInsert);
					if ((insert(myInsert)) != -1) {
						doc.id = lastInsertID();
						tabModel.addDoc(doc);
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

	// DELETE MOVIE FROM DATABASE AND TABLEMODEL
	public void deleteDoc(int row) {
		int id = (Integer) tabModel.getValueAt(row, Doc.COL_ID);
		String myDel="";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {					
					myDel = "delete from "+table+" where id=\"" + id + "\"";
					if (delete(myDel) != -1) {
						// System.out.println("Deleting succesful!!!");
						tabModel.deleteDoc(row);
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
	public void updateDoc(Doc doc,int selectedRow) {
		String myUpd = "";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {

					myUpd = "update "+table+" set title=\"" + doc.title
							+"\",loc=\"" + doc.loc
							+ "\",theme=\"" + doc.theme 
							+ "\",comments=\"" + doc.comments
							+ "\" where id=\"" + doc.id + "\"";
					if (update(myUpd) != -1) {
						tabModel.setDocAtRow(doc,selectedRow);
					} else {
						Errors.showError(Errors.DB_UPDATE);
						Errors.writeError(Errors.DB_UPDATE, myUpd + "\n");
					}
					close();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			Errors.showError(Errors.DB_UPDATE);
			Errors.writeError(Errors.DB_UPDATE, myUpd + "\n");
		}
	}
	
	// UPDATE DISC FROM DATABASE AND TABLEMODEL
	public void updateDocWithoutComments(Doc doc,int selectedRow) {
		String myUpd = "";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {

					myUpd = "update "+table+" set title=\"" + doc.title
							+"\",loc=\"" + doc.loc
							+ "\",theme=\"" + doc.theme 
							+ "\" where id=\"" + doc.id + "\"";
					if (update(myUpd) != -1) {
						tabModel.setDocAtRow(doc,selectedRow);
					} else {
						Errors.showError(Errors.DB_UPDATE);
						Errors.writeError(Errors.DB_UPDATE, myUpd + "\n");
					}
					close();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			Errors.showError(Errors.DB_UPDATE);
			Errors.writeError(Errors.DB_UPDATE, myUpd + "\n");
		}
	}
	
	
	public void updateCommentsOnly(Doc doc){
        String myUpd="";
		try {
            if (cargaControlador()>-1) {
                if (open("jdbc:mysql://localhost:"+port+"/"+database,user,pass)>-1) {
                    myUpd = "update "+table+" set comments=\"" + doc.comments + "\" where id=\"" + doc.id + "\"";
                    if (update(myUpd) != -1) {
                        //System.out.println("Updating succesful!!!");
                    } else {
                    	Errors.showError(Errors.DB_UPDATE);
						Errors.writeError(Errors.DB_UPDATE, myUpd + "\n");
                    }
                    close();
                }
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        	Errors.showError(Errors.DB_UPDATE);
			Errors.writeError(Errors.DB_UPDATE, myUpd + "\n");
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
