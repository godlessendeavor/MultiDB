package movies.db;

import javax.swing.JTextArea;
import java.sql.ResultSet;

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
	public static final String table=MultiDB.moviesTable;
	public static final int columns=9;

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
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						rs.next();
						movie = new Movie();
						movie.id = (Integer) rs.getObject(Movie.COL_ID + 1);
						movie.title = (String) rs.getObject(Movie.COL_TITLE + 1);
						if (rs.getObject(Movie.COL_YEAR + 1)!=null) movie.year = rs.getObject(Movie.COL_YEAR + 1).toString();
						movie.loc = (String) rs.getObject(Movie.COL_LOC + 1);
						movie.director = (String) rs.getObject(Movie.COL_DIR + 1);
						movie.other = (String) rs.getObject(Movie.COL_OTHER + 1);
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
		return movie;
	}
	
	public Movie getMovieByName(String name) {
		Movie movie = null;
		ResultSet rs;
		String mySelect = "select * from "+ table+ " where title=\"" +  name+ "\"";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					if (select(mySelect)>-1) {
						rs = getRs();
						rs.next();
						movie = new Movie();
						movie.id = (Integer) rs.getObject(Movie.COL_ID + 1);
						movie.title = (String) rs.getObject(Movie.COL_TITLE + 1);
						if (rs.getObject(Movie.COL_YEAR + 1)!=null) movie.year = rs.getObject(Movie.COL_YEAR + 1).toString();
						movie.loc = (String) rs.getObject(Movie.COL_LOC + 1);
						movie.director = (String) rs.getObject(Movie.COL_DIR + 1);
						movie.other = (String) rs.getObject(Movie.COL_OTHER + 1);
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
		return movie;
	}

	// INSERT MOVIE IN DATABASE AND TABLEMODEL
	public void insertNewMovie(Movie movie) {
		String myInsert = "insert into "+table+" (title,director,year,other,loc) values (\""
				+ movie.title + "\",\"" + movie.director + "\",\"" + movie.year + "\",\"" + movie.other + "\",\""+ movie.loc
				+ "\")";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					// System.out.println(myInsert);
					movie.check();
					if ((insert(myInsert)) != -1) {
						movie.id = lastInsertID();
						tabModel.addMovie(movie);
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
	public void deleteMovie(int row) {
		int id = (Integer) tabModel.getValueAt(row, Movie.COL_ID);
		String myDel="";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					
					myDel = "delete from movies where id=\"" + id + "\"";
					if (delete(myDel) != -1) {
						// System.out.println("Deleting succesful!!!");
						tabModel.deleteMovie(row);
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
	public void updateMovie(Movie movie,int selectedRow) {
		String myUpd = "";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					movie.check();
					myUpd = "update "+table+" set title=\"" + movie.title
							+"\",year=\"" + movie.year
							+ "\",loc=\"" + movie.loc 
							+ "\",director=\"" + movie.director
							+ "\",other=\"" + movie.other 
							+ "\" where id=\"" + movie.id + "\"";
					//System.out.println(movie.title);
					if (update(myUpd) != -1) {
						tabModel.setMovieAtRow(movie,selectedRow);
					} else {
						reviewView.append("Error updating fields\n");
					}
					close();
				}
			}

		} catch (Exception ex) {
			Errors.showError(Errors.DB_UPDATE);
			Errors.writeError(Errors.DB_UPDATE, myUpd + "\n");
		}
	}
	
	public void updateReviewOnly(Movie movie){
		String myUpd="";
		try {
            if (cargaControlador()>-1) {
                if (open("jdbc:mysql://localhost:"+port+"/"+database,user,pass)>-1) {
                	movie.check();
                    myUpd = "update "+table+" set review=\"" + movie.review + "\" where id=\"" + movie.id + "\"";
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

	
	/*public boolean makeBackup(String dirDest){
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
	}*/
	
	

}
