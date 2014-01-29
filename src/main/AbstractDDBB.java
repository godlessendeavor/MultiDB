package main;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.Errors;

public class AbstractDDBB {

	public static final int port=MultiDB.port;
    public static final String host=MultiDB.host;
    public static final String user=MultiDB.user;
    public static final String pass=MultiDB.pass;
    private Connection con;
    private Statement stmt;
    private String error;
    private ResultSet rs;

    public AbstractDDBB() {
        con = null;
        stmt = null;
        error = "";
        rs = null;
    }
    ////////// METODOS BASE DE DATOS

    public int cargaControlador() {
        int flag = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
        	ex.printStackTrace();
            return Errors.LOADING_DB_DRIVER;
        }
        return (flag);
    }

    public int open(String url, String user, String password) {
        int flag = 0;
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = (Statement) con.createStatement();
            //System.out.println("Conexion establecida");
        } catch (Exception ex) {
        	ex.printStackTrace();
            return Errors.OPENING_DB;
        }
        return (flag);
    }

    public int close() {
        int flag = 0;
        try {
            if (this.rs != null) {
                this.rs.close();
                this.rs = null;
            }
            if (this.con != null) {
                this.con.close();
                this.con = null;
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            return Errors.CLOSING_DB;
        }
        return flag;
    }
    
//    public void getForeignTableData(String tableName){
//    	if (con!=null){
//	    	DatabaseMetaData metaData;
//			try {
//				metaData = con.getMetaData();
//			    ResultSet foreignKeys = metaData.getImportedKeys(con.getCatalog(), null, tableName);
//		        while (foreignKeys.next()) {
//		            String fkTableName = foreignKeys.getString("FKTABLE_NAME");
//		            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
//		            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
//		            String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
//		            System.out.println(fkTableName + "." + fkColumnName + " -> " + pkTableName + "." + pkColumnName);
//		        }
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	       
//    	}
//    }

    /////////////////////////   CONSULTAS BD
    public int select(String query) {
        int flag = 0;
        //query a la base de datos y devuelve resultset
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Errors.DB_SELECT;
        }
        return flag;
    }


    public int insert(String query) {
        int row = -1;
        //query a la base de datos y devuelve cantidad de columnas afectadas
        try {
            stmt = con.createStatement();
            row = stmt.executeUpdate(query);
        } catch (Exception ex) {
        	//ex.printStackTrace();
        	return Errors.DB_INSERT;
        }
        return row;
    }
    public int lastInsertID() {
        int id = -1;
        //query a la base de datos y devuelve cantidad de columnas afectadas
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select LAST_INSERT_ID()");
            rs.next();
            id=((Long)rs.getObject(1)).intValue();
        } catch (Exception ex) {
        	//ex.printStackTrace();
        	return Errors.DB_INSERT;            
        }
        return id;
    }
    
    public int delete(String query) {
        int row = -1;
        //query a la base de datos y cantidad de columnas afectadas
        try {
            stmt = con.createStatement();
            row = stmt.executeUpdate(query);
        } catch (Exception ex) {
        	//ex.printStackTrace();
        	return Errors.DB_DELETE;
        }
        return row;
    }

    public int update(String query) {
        int row = -1;
        //query a la base de datos y devuelve cantidad de columnas afectadas
        try {  
            stmt = con.createStatement();
            row=stmt.executeUpdate(query);
        } catch (Exception ex) {
        	ex.printStackTrace();
            return Errors.DB_UPDATE;
        }
        return row;
    }

    
    public int makeBackupCSV(String dirDest,String database,String table){
		String myExport = "";
		String set = "";
		int ret;
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					set = "set names utf8";
					if (select(set)>-1) {
						dirDest=dirDest.replace("\\","\\\\");
						myExport = "select * into outfile \""+dirDest+
						"\" fields terminated by \",\" enclosed by \"\\\"\""+
						" escaped by \"\\\\\" lines terminated by \"\\r\\n\" from "+table+" where 1";
						if ((ret=select(myExport))>-1) {
							return 1;
						} else {
							return ret;
						}					
					} else {
						return Errors.DB_SELECT;		
					}				
				}else return Errors.OPENING_DB;
			}else return Errors.LOADING_DB_DRIVER;
		} catch (Exception ex) {
			ex.printStackTrace();
			return Errors.DB_UNDEFINED;
		}
		finally{
			close();
		}
	}
	
	public int restoreBackupCSV(String nameFile,String database,String table){
		String myExport = "";
		String truncate = "";
		String set = "";
		try {
			if (cargaControlador()>-1) {
				if (open("jdbc:mysql://" + host + ":" + port+ "/" + database, user, pass)>-1) {
					truncate= "truncate table "+table;
					set = "set names utf8";
					if (select(set)>-1) {
						if (delete(truncate)!=-1) {
							nameFile=nameFile.replace("\\","\\\\");
							myExport = "load data infile \""+nameFile+"\" into table "+table+
							" character set 'utf8' fields terminated by \",\" enclosed by \"\\\"\""+
							" escaped by \"\\\\\" lines terminated by \"\\n\"";
							if (select(myExport)>-1) {
								return 1;
							} else {
								return Errors.DB_SELECT;
							}						
						} else {
							return Errors.DB_DELETE;
						}					
								
					} else {
						return Errors.DB_SELECT;		
					}				
					
				}else return Errors.OPENING_DB;
			}else return Errors.LOADING_DB_DRIVER;

		} catch (Exception ex) {
			ex.printStackTrace();
			return Errors.DB_UNDEFINED;
		}
		finally{
			close();
		}
	}
    
    
    public ResultSet getRs() {
        return rs;
    }
}
