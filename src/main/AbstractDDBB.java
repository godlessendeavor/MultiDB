package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AbstractDDBB {

    public Connection con;
    public Statement stmt;
    public String error;
    public ResultSet rs;

    public AbstractDDBB() {
        con = null;
        stmt = null;
        error = "";
        rs = null;
    }
    ////////// METODOS BASE DE DATOS

    public boolean cargaControlador() {
        boolean flag = true;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            System.out.println("No se pudo cargar el controlador. " + ex.toString());
            flag = false;
        }
        return (flag);
    }

    public boolean open(String url, String user, String password) {
        boolean flag = true;
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = (Statement) con.createStatement();
            //System.out.println("Conexion establecida");
        } catch (Exception ex) {
            System.out.println("No se pudo establecer la conexion con " + ex.toString());
            flag = false;
        }
        return (flag);
    }

    public boolean close() {
        boolean flag = true;
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
            flag = false;
            System.out.println("Error al cerrar la conexion" + ex.toString());
        }
        return flag;
    }

    /////////////////////////   CONSULTAS BD
    public boolean select(String query) {
        boolean flag = true;
        //query a la base de datos y devuelve resultset
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
        } catch (Exception ex) {
            flag = false;
            System.out.println("Error al ejecutar el statement select");
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
            System.out.println("Error al ejecutar el statement insert");
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
            System.out.println("Error al consultar la base de datos para resolver el último ID ");
            ex.printStackTrace();
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
            System.out.println("Error al ejecutar el statement delete");
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
            System.out.println("Error al ejecutar el statement update");
        }
        return row;
    }

    public ResultSet getRs() {
        return rs;
    }
}
