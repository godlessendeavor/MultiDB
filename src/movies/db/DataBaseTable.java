package movies.db;

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
	public static final String table=MultiDB.moviesTable;
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
	public Movie getMovie(int id) {
		Movie movie = null;
		ResultSet rs;
		String mySelect = "select * from "+ table+ " where id=\"" + id + "\"";
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					if (select(mySelect)) {
						rs = getRs();
						rs.next();
						movie = new Movie();
						movie.id = (Integer) rs.getObject(COL_ID + 1);
						movie.title = (String) rs.getObject(COL_TITLE + 1);
						if (rs.getObject(COL_YEAR + 1)!=null) movie.year = rs.getObject(COL_YEAR + 1).toString();
						movie.loc = (String) rs.getObject(COL_LOC + 1);
						movie.director = (String) rs.getObject(COL_DIR + 1);
						movie.other = (String) rs.getObject(COL_OTHER + 1);
					} else {
						reviewView.append("Data searching in DB failed\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			reviewView.append("Error while searching in the database\n");
		}
		return movie;
	}
	
	public Movie getMovieByName(String name) {
		Movie movie = null;
		ResultSet rs;
		String mySelect = "select * from "+ table+ " where title=\"" +  name+ "\"";
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					if (select(mySelect)) {
						rs = getRs();
						rs.next();
						movie = new Movie();
						movie.id = (Integer) rs.getObject(COL_ID + 1);
						movie.title = (String) rs.getObject(COL_TITLE + 1);
						if (rs.getObject(COL_YEAR + 1)!=null) movie.year = rs.getObject(COL_YEAR + 1).toString();
						movie.loc = (String) rs.getObject(COL_LOC + 1);
						movie.director = (String) rs.getObject(COL_DIR + 1);
						movie.other = (String) rs.getObject(COL_OTHER + 1);
					} else {
						reviewView.append("Data searching in DB failed\n");
					}
					close();
				}
			}
		} catch (Exception ex) {
			reviewView.append("Error while searching in the database\n");
		}
		return movie;
	}

	// INSERT MOVIE IN DATABASE AND TABLEMODEL
	public void insertNewMovie(Movie movie) {
		String myInsert = "insert into "+table+" (title,director,year,other,loc) values (\""
				+ movie.title + "\",\"" + movie.director + "\",\"" + movie.year + "\",\"" + movie.other + "\",\""+ movie.loc
				+ "\")";
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					// System.out.println(myInsert);
					if ((insert(myInsert)) != -1) {
						movie.id = lastInsertID();
						tabModel.addMovie(movie);
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
	public void deleteMovie(int row) {
		int id = (Integer) tabModel.getValueAt(row, COL_ID);
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					String myDel;
					myDel = "delete from movies where id=\"" + id + "\"";
					if (delete(myDel) != -1) {
						// System.out.println("Deleting succesful!!!");
						tabModel.deleteMovie(row);
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

	// UPDATE DISC FROM DATABASE AND TABLEMODEL
	public void updateMovie(Movie movie,int selectedRow) {
		String myUpd = "";
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {

					myUpd = "update "+table+" set title=\"" + movie.title
							+"\",year=\"" + movie.year
							+ "\",loc=\"" + movie.loc 
							+ "\",director=\"" + movie.director
							+ "\",other=\"" + movie.other 
							+ "\" where id=\"" + movie.id + "\"";
					if (update(myUpd) != -1) {
						tabModel.setMovieAtRow(movie,selectedRow);
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
	
	public void updateReviewOnly(Movie movie){
		try {
            if (cargaControlador()) {
                if (open("jdbc:mysql://localhost:"+port+"/"+database,user,pass)) {
                    String myUpd="";
                    myUpd = "update "+table+" set review=\"" + movie.review + "\" where id=\"" + movie.id + "\"";
                    if (update(myUpd) != -1) {
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
			reviewView.append("Error whilst restoring database\n");
			return false;
		}
	}

}
