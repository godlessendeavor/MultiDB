package music.db;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public static final String database=MultiDB.musicDatabase;
    public static final String table=MultiDB.musicTable;
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

	// GET DISC BY ID
	public Disc getDisc(int id) {
		Disc disc = null;
		ResultSet rs;
		int row;
		String mySelect = "select * from "+table+" where id=\"" + id + "\"";
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					if (select(mySelect)) {
						rs = getRs();
						rs.next();
						disc = new Disc();
						disc.id = id;
						disc.group = (String) rs.getObject(COL_GROUP + 1);
						disc.title = (String) rs.getObject(COL_TITLE + 1);
						disc.style = (String) rs.getObject(COL_STYLE + 1);
						if ((Long)rs.getObject(COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(COL_YEAR+1)).toString();
						disc.loc = (String) rs.getObject(COL_LOC + 1);
						disc.copy = (String) rs.getObject(COL_COPY + 1);
						disc.type = (String) rs.getObject(COL_TYPE + 1);
						disc.mark = (String) rs.getObject(COL_MARK + 1);
						disc.review = (String) rs.getObject(COL_REVIEW + 1);
						row = tabModel.searchDisc(disc.id);
						disc.present = (String) tabModel.getValueAt(row,COL_PRESENT);
						disc.path = (File) tabModel.getValueAt(row, COL_PATH);
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
	
	
	//GETDISCBYGROUPNAME:gets the first disc of group which name is name provided
	public Disc getDiscByGroupName(String name) {
		Disc disc = null;
		ResultSet rs;
		int row;
		String mySelect = "select * from "+table+" where groupName=\"" +  name+ "\"";
		//System.out.println(mySelect);
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					if (select(mySelect)) {
						rs = getRs();
						rs.next();
						disc = new Disc();
						disc.id = (Integer) rs.getObject(COL_ID + 1);
						disc.group = (String) rs.getObject(COL_GROUP + 1);
						disc.title = (String) rs.getObject(COL_TITLE + 1);
						disc.style = (String) rs.getObject(COL_STYLE + 1);
						if ((Long)rs.getObject(COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(COL_YEAR+1)).toString();
						disc.loc = (String) rs.getObject(COL_LOC + 1);
						disc.copy = (String) rs.getObject(COL_COPY + 1);
						disc.type = (String) rs.getObject(COL_TYPE + 1);
						disc.mark = (String) rs.getObject(COL_MARK + 1);
						disc.review = (String) rs.getObject(COL_REVIEW + 1);
						row = tabModel.searchDisc(disc.id);
						disc.present = (String) tabModel.getValueAt(row,
								COL_PRESENT);
						disc.path = (File) tabModel.getValueAt(row, COL_PATH);
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
	public ArrayList<Disc> getDiscsOfGroup(String name) {
		Disc disc = null;
		ArrayList<Disc> discList = new ArrayList<Disc>();
		ResultSet rs;
		int row;
		String mySelect = "select * from "+table+" where groupName=\"" +  name+ "\"";
		//System.out.println(mySelect);
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					if (select(mySelect)) {
						rs = getRs();
						while (rs.next()){
							disc = new Disc();
							disc.id = (Integer) rs.getObject(COL_ID + 1);
							disc.group = (String) rs.getObject(COL_GROUP + 1);
							disc.title = (String) rs.getObject(COL_TITLE + 1);
							disc.style = (String) rs.getObject(COL_STYLE + 1);
							if ((Long)rs.getObject(COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(COL_YEAR+1)).toString();
							disc.loc = (String) rs.getObject(COL_LOC + 1);
							disc.copy = (String) rs.getObject(COL_COPY + 1);
							disc.type = (String) rs.getObject(COL_TYPE + 1);
							disc.mark = (String) rs.getObject(COL_MARK + 1);
							disc.review = (String) rs.getObject(COL_REVIEW + 1);
							row = tabModel.searchDisc(disc.id);
							disc.present = (String) tabModel.getValueAt(row,
									COL_PRESENT);
							disc.path = (File) tabModel.getValueAt(row, COL_PATH);
							discList.add(disc);
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
	public Disc getDiscByDG(String groupName, String discName) {
		Disc disc = null;
		ResultSet rs;
		int row;
		String mySelect = "select * from "+table+" where groupName=\"" + groupName+ "\" and title=\""+discName+"\"";
		//System.out.println(mySelect);
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					if (select(mySelect)) {
						rs = getRs();
						rs.next();
						disc = new Disc();
						disc.id = (Integer) rs.getObject(COL_ID + 1);
						disc.group = (String) rs.getObject(COL_GROUP + 1);
						disc.title = (String) rs.getObject(COL_TITLE + 1);
						disc.style = (String) rs.getObject(COL_STYLE + 1);
						if ((Long)rs.getObject(COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(COL_YEAR+1)).toString();
						disc.loc = (String) rs.getObject(COL_LOC + 1);
						disc.copy = (String) rs.getObject(COL_COPY + 1);
						disc.type = (String) rs.getObject(COL_TYPE + 1);
						disc.mark = (String) rs.getObject(COL_MARK + 1);
						disc.review = (String) rs.getObject(COL_REVIEW + 1);
						row = tabModel.searchDisc(disc.id);
						disc.present = (String) tabModel.getValueAt(row,
								COL_PRESENT);
						disc.path = (File) tabModel.getValueAt(row, COL_PATH);
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
	
	// INSERT DISC IN DATABASE AND TABLEMODEL
	public void insertNewDisc(Disc disc) {
		String myInsert = "insert into "+table+" (groupName,title,year,style,loc,copy,type,mark,review) values (\""
				+ disc.group + "\",\"" + disc.title + "\",\"" + disc.year+ "\",\"" + disc.style + "\",\"" + disc.loc
				+ "\",\"" + disc.copy + "\",\"" + disc.type + "\",\"" + disc.mark + "\",\"" + disc.review
				+ "\")";
		int row;
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					if ((row = insert(myInsert)) != -1) {
						disc.id = lastInsertID();
						row = tabModel.addDisc(disc);
						tabModel.setValueAt("?", row, COL_PRESENT);
						tabModel.setValueAt(disc.path, row, COL_PATH);
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

	// DELETE DISC FROM DATABASE AND TABLEMODEL
	public void deleteDisc(int row) {
		int id = (Integer) tabModel.getValueAt(row, COL_ID);
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					String myDel;
					myDel = "delete from "+table+" where id=\"" + id + "\"";
					if (delete(myDel) != -1) {
						// System.out.println("Deleting succesful!!!");
						tabModel.deleteDisc(row);
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
	public void updateDisc(Disc disc,int selectedRow) {
		String myUpd = "";
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {

					myUpd = "update "+table+" set groupName=\"" + disc.group
							+ "\",title=\"" + disc.title + "\",style=\""
							+ disc.style + "\",year=\"" + disc.year
							+ "\",loc=\"" + disc.loc + "\",type=\"" + disc.type
							+ "\",mark=\"" + disc.mark + "\",copy=\""
							+ disc.copy + "\",review=\"" + disc.review
							+ "\" where id=\"" + disc.id + "\"";
					if (update(myUpd) != -1) {
						tabModel.setDiscAtRow(disc,selectedRow);
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
	
	public void updateReviewOnly(Disc disc){
		try {
            if (cargaControlador()) {
                if (open("jdbc:mysql://localhost:"+port+"/"+database,user,pass)) {
                    String myUpd;
                    myUpd = "update "+table+" set review=\"" + disc.review + "\" where id=\"" + disc.id + "\"";
                    //System.out.println(myUpd);
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
	
	public boolean makeBackupCSV(String dirDest){
		String myExport = "";
		try {
			if (cargaControlador()) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)) {
					dirDest=dirDest.replace("\\","\\\\");
					myExport = "select * into outfile \""+dirDest+"\\\\"+nameCSV+
					"\" fields terminated by \",\" enclosed by \"\\\"\""+
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
			close();
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
             DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
             Date date = new Date();
             
             File filedst = new File(dirDest+"\\"+dateFormat.format(date)+table+".sql");
             FileOutputStream dest = new FileOutputStream(filedst);
             ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(dest));
             zip.setMethod(ZipOutputStream.DEFLATED);
             zip.setLevel(Deflater.BEST_COMPRESSION);
             zip.putNextEntry(new ZipEntry(dirDest+"\\"+dateFormat.format(date)+table+".sql"));
             zip.write(temp.toString().getBytes());
             zip.close();
             dest.close();
             return true;
         } catch (Exception ex) {
        	 ex.printStackTrace();
         	reviewView.append("Error while trying to make backup\n");
         	return false;
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
