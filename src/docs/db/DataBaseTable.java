package docs.db;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JTextArea;

import main.AbstractDDBB;
import main.MultiDB;


public class DataBaseTable extends AbstractDDBB implements DataBaseLabels{
	
    ///DB CONSTRAINTS
	public static final int port=MultiDB.port;
	public static final String host=MultiDB.host;
	public static final String user=MultiDB.user;
	public static final String pass=MultiDB.pass;
	public static final String database=MultiDB.moviesDatabase;
	public static final String table=MultiDB.docsTable;
	public static final String mysqlPath=MultiDB.mysqlPath;
	public static final String nameCSV="dataCSV.csv";

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
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					if (select(mySelect)) {
						rs = getRs();
						rs.next();
						doc = new Doc();
						doc.id = (Integer) rs.getObject(COL_ID + 1);
						doc.title = (String) rs.getObject(COL_TITLE + 1);
						doc.loc = (String) rs.getObject(COL_LOC + 1);
						doc.setThemeByString((String) rs.getObject(COL_THEME + 1));
						doc.comments = (String) rs.getObject(COL_COMMENTS + 1);
					} else {
						System.out.println("Data searching in DB failed\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error while searching in the database\n");
		}
		return doc;
	}
	
	public Doc getDocByName(String name) {
		Doc doc = null;
		ResultSet rs;
		String mySelect = "select * from "+ table+ " where title=\"" +  name+ "\"";
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					if (select(mySelect)) {
						rs = getRs();
						rs.next();
						doc = new Doc();
						doc.id = (Integer) rs.getObject(COL_ID + 1);
						doc.title = (String) rs.getObject(COL_TITLE + 1);
						doc.loc = (String) rs.getObject(COL_LOC + 1);
						doc.setThemeByString((String) rs.getObject(COL_THEME + 1));
						doc.comments = (String) rs.getObject(COL_COMMENTS + 1);
					} else {
						reviewView.append("Data searching in DB failed\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			reviewView.append("Error while searching in the database\n");
		}
		return doc;
	}

	// INSERT MOVIE IN DATABASE AND TABLEMODEL
	public void insertNewDoc(Doc doc) {
		String myInsert = "insert into "+table+" (title,loc,theme,comments) values (\""
				+ doc.title + "\",\"" + doc.loc + "\",\"" + doc.theme + "\",\"" + doc.comments +"\")";
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					// System.out.println(myInsert);
					if ((insert(myInsert)) != -1) {
						doc.id = lastInsertID();
						tabModel.addDoc(doc);
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

	// DELETE MOVIE FROM DATABASE AND TABLEMODEL
	public void deleteDoc(int row) {
		int id = (Integer) tabModel.getValueAt(row, COL_ID);
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					String myDel;
					myDel = "delete from "+table+" where id=\"" + id + "\"";
					if (delete(myDel) != -1) {
						// System.out.println("Deleting succesful!!!");
						tabModel.deleteDoc(row);
					} else {
						reviewView.append("Error deleting fields\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			reviewView.append("An exception happened when trying to delete from database\n");
		}
	}

	// UPDATE DISC FROM DATABASE AND TABLEMODEL
	public void updateDoc(Doc doc,int selectedRow) {
		String myUpd = "";
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {

					myUpd = "update "+table+" set title=\"" + doc.title
							+"\",loc=\"" + doc.loc
							+ "\",theme=\"" + doc.theme 
							+ "\",comments=\"" + doc.comments
							+ "\" where id=\"" + doc.id + "\"";
					if (update(myUpd) != -1) {
						tabModel.setDocAtRow(doc,selectedRow);
					} else {
						reviewView.append("Error updating fields\n");
					}
					close();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			reviewView.append("An exception happened  when trying to update the database"+ myUpd + "\n");
		}
	}
	
	public void updateCommentsOnly(Doc doc){
		try {
            if (cargaControlador()) {
                if (open("jdbc:mysql://localhost:"+port+"/"+database,user,pass)) {
                    String myUpd="";
                    myUpd = "update "+table+" set comments=\"" + doc.comments + "\" where id=\"" + doc.id + "\"";
                    if (update(myUpd) != -1) {
                        //System.out.println("Updating succesful!!!");
                    } else {
                        reviewView.append("Error updating fields\n");
                    }
                    close();
                }
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
            reviewView.append("An exception happened when trying to update the database\n");
        }           
	}
	
	public boolean makeBackup(String dirDest){
		final int BUFFER = 32768;
		 try {
             Process run = Runtime.getRuntime().exec(mysqlPath+
                     "mysqldump --host=" + host + " --port=" + port +
                     " --user=" + user + " --password=" + pass +
                     " --compact --complete-insert --extended-insert " +
                     "--skip-comments --skip-triggers " + database);
             InputStream in = run.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in));
             StringBuffer temp = new StringBuffer();
             int count;
             char[] cbuf = new char[BUFFER];
             while ((count = br.read(cbuf, 0, BUFFER)) != -1) {
                 temp.append(cbuf, 0, count);
             }
             br.close();
             in.close();

             File filedst = new File(dirDest);
             FileOutputStream dest = new FileOutputStream(filedst);
             ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(dest));
             zip.setMethod(ZipOutputStream.DEFLATED);
             zip.setLevel(Deflater.BEST_COMPRESSION);
             zip.putNextEntry(new ZipEntry("data.sql"));
             zip.write(temp.toString().getBytes());
             zip.close();
             dest.close();
             return true;
         } catch (Exception ex) {
         	reviewView.append("Error while trying to make backup\n");
         	return false;
         }
	}
	
	public boolean makeBackupCSV(String dirDest){
		String myExport = new String("");
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					dirDest=dirDest.replace("\\","\\\\");
					myExport = "select * into outfile \""+dirDest+"\\\\"+nameCSV+
					"\" fields terminated by \",\" optionally enclosed by \"\\\"\""+
					" escaped by \"\\\\\" lines terminated by \"\\n\" from "+table+" where 1";
					if (select(myExport)) {
						return true;
					} else {
						reviewView.append("Error creating Backup\n");
						return false;
					}								
				}else return false;
			}else return false;

		} catch (Exception ex) {
			ex.printStackTrace();
			reviewView.append("An exception happened  when trying to backup the database "+ myExport + "\n");
			return false;
		}
		finally{
			
		}
	}
	
	public boolean restore(File filein) {
		try {

			/*
			 * String command = "mysql -u " + user + " -p " + pass + " " +
			 * database + " < " +fileIn.getAbsolutePath();
			 * Runtime.getRuntime().exec(command); File fileBackUp = new
			 * File(fileIn); FileInputStream from = new
			 * FileInputStream(fileBackUp); ZipInputStream zip = new
			 * ZipInputStream(from); ZipEntry entry; while ((entry =
			 * zip.getNextEntry())!=null){ while(zip.available()!=0){ } }
			 * zip.close(); from.close(); Process run =
			 * Runtime.getRuntime().exec(mysqlPath+ "mysql --host=" + host +
			 * " --port=" + port + " --user=" + user + " --password=" + pass +
			 * database);
			 */
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			reviewView.append("Error whilst restoring database\n");
			return false;
		}
	}

}
