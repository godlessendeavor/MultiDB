/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movies.db;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;

import main.AbstractDDBB;
import main.MultiDB;


/**
 *
 * @author thrasher
 */
public class TabMod extends AbstractTableModel implements DataBaseLabels{

    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] labels;
    private ArrayList<Movie> data;
    private AbstractDDBB dataBase = new AbstractDDBB();
    private ResultSet rs = null;
    private Movie movie;
    public static int port=MultiDB.port;
    public static String host=MultiDB.host;
    public static String user=MultiDB.user;
    public static String pass=MultiDB.pass;
    public static String database=MultiDB.moviesDatabase;
    public static String table=MultiDB.moviesTable;
    public static String mysqlPath=DataBaseTable.mysqlPath;

    public TabMod() {

        try {
            if (dataBase.cargaControlador()) {
                if (dataBase.open("jdbc:mysql://"+host+":"+port+"/"+database, user, pass)) {
                    if (dataBase.select("Select * from "+table)) {

                        rs = dataBase.getRs();
                        ResultSetMetaData metaDatos = rs.getMetaData();
                        // Se obtiene el numero de columnas.
                        int numCol = metaDatos.getColumnCount();
                        labels = new String[numCol+2];// Se obtiene cada una de las etiquetas para cada columna
                        int i=0;
                        for (i = 0; i < numCol; i++) {
                            // para ResultSetMetaData la primera columna es la 1.
                            labels[i] = metaDatos.getColumnLabel(i + 1);
                        }
                        labels[i]="present"; //añadir columnas de presente en backup y directorio
                        labels[i+1]="path";
                        int countData = 0;
                         //Contamos los datos que hay
                        while (rs.next()) {
                            countData++;
                        }
  
                        //data=new Disco[countData];
                        data=new ArrayList<Movie>();
                        dataBase.select("Select * from "+table);
                        rs = dataBase.getRs();
                        
                        for (i = 0; i < countData; i++) {
                            rs.next();            
                            //data[i]= new Disco();
                            movie=new Movie();
                            movie.id=(Integer)rs.getObject(COL_ID+1);
                            movie.title=(String)rs.getObject(COL_TITLE+1);
                            movie.other=(String)rs.getObject(COL_OTHER+1);
                            if (rs.getObject(COL_YEAR + 1)!=null) movie.year = rs.getObject(COL_YEAR + 1).toString();
                            movie.loc=(String)rs.getObject(COL_LOC+1);                     
                            movie.director=(String)rs.getObject(COL_DIR+1);
                            //movie.review=(String)rs.getObject(COL_REVIEW+1);
                            movie.present=new String("NO");
                            movie.path=new File("");
                            data.add(movie);
                        }
                    }
                    dataBase.close();
                    //ORDERING DATA
                    Collections.sort(data);
                }
            }         
        } catch (Exception ex) {
            System.out.println("Excepcion durante el acceso a la base de datos");
            ex.printStackTrace();
        }

    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return labels.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
    	Object ob = new Object();
        
        if (columnIndex==COL_ID) ob=data.get(rowIndex).id;
        if (columnIndex==COL_TITLE) ob=data.get(rowIndex).title;
        if (columnIndex==COL_DIR) ob=data.get(rowIndex).director;
        if (columnIndex==COL_YEAR) ob=data.get(rowIndex).year;
        if (columnIndex==COL_LOC) ob=data.get(rowIndex).loc;
        if (columnIndex==COL_OTHER) ob=data.get(rowIndex).other;
        if (columnIndex==COL_REVIEW) ob=data.get(rowIndex).review;
        if (columnIndex==COL_PRESENT) ob=data.get(rowIndex).present;
        if (columnIndex==COL_PATH) ob=data.get(rowIndex).path;

        return ob;
    }

    public Movie getMovieAtRow(int row){
        movie= new Movie();
        movie=data.get(row);
        return movie;
    }

     public void setMovieAtRow(Movie movie, int row) {
         this.setValueAt(movie.id,row,COL_ID);
         this.setValueAt(movie.title,row,COL_TITLE);
         this.setValueAt(movie.director,row,COL_DIR);
         this.setValueAt(movie.year,row,COL_YEAR);
         this.setValueAt(movie.loc,row,COL_LOC);
         this.setValueAt(movie.other,row,COL_OTHER);
         this.setValueAt(movie.review,row,COL_REVIEW);
         this.setValueAt(movie.present,row,COL_PRESENT);
         this.setValueAt(movie.path,row,COL_PATH);
         this.fireTableRowsUpdated(row, row);
    }
    public int addMovie(Movie movie){
        data.add(movie);
        this.fireTableDataChanged();
        return(data.size()-1);
    }
    public void deleteMovie(int row){
        data.remove(row);
        this.fireTableRowsDeleted(row, row);
    }

    public void sort(){
        Collections.sort(data);
        this.fireTableDataChanged();
    }
   
    public int searchMovie(String title,String disc){
        int i,sol=-1;
        int tam=this.getRowCount();
        //int groupComp,discComp;
        for(i=0;i<tam;i++){
            //Collator comparador = Collator.getInstance();
            //comparador.setStrength(Collator.PRIMARY);
            //System.out.println("comparing "+data[i].group+" with "+group);
            //groupComp = comparador.compare(data[i].group, group);
            //discComp = comparador.compare(data[i].title, disc);
            //if (groupComp==0&&discComp==0) {
          /*  if ((data[i].group.compareToIgnoreCase(group)==0)&&(data[i].title.compareToIgnoreCase(disc)==0)){
                    sol =i;
                    break;
            }*/
             if ((data.get(i).title.compareToIgnoreCase(title)==0)&&(data.get(i).title.compareToIgnoreCase(title)==0)){
                    sol =i;
                    break;
            }
        }
        return sol;
    }

    //OVERLOADED METHOD SEARCHELMENT BY ID
    public int searchMovie(int id){
        int i,sol=-1;
        int tam=this.getRowCount();
        for(i=0;i<tam;i++){
             if (data.get(i).id==id){
                    sol =i;
                    break;
            }
        }
        return sol;
    }

    public int searchFirstElementWithLetter(char car, int col, int charPos,int currentRow) {
        int row, sol = -1;
        int tam = this.getRowCount();
        String el;
        for (row = currentRow; row < tam; row++) {
            Object ob = this.getValueAt(row, col);
            if (ob instanceof String) {
                el = (String) this.getValueAt(row, col);
                if (el.length() > charPos + 1) {
					el = el.substring(charPos, charPos + 1);
					if (el.compareToIgnoreCase(car + "") == 0) {
						sol = row;
						break;
					}
				}
			}
        }
        return sol;
    }

    ///OVERRIDEN METHODS
    @Override
    public String getColumnName(int col) {
        return labels[col];
    }

    @Override
    public Class<? extends Object> getColumnClass(int c) {
    	if (c==7) return File.class;
       else return String.class;
   }


    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if ((col == COL_ID)||(col==COL_PRESENT)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
     
        if (columnIndex==COL_ID) data.get(rowIndex).id=(Integer)value;
        if (columnIndex==COL_TITLE) data.get(rowIndex).title=(String)value;
        if (columnIndex==COL_DIR) data.get(rowIndex).director=(String)value;
        if (columnIndex==COL_YEAR) data.get(rowIndex).year=(String)value;
        if (columnIndex==COL_OTHER) data.get(rowIndex).other=(String)value;
        if (columnIndex==COL_LOC) data.get(rowIndex).loc=(String)value;
        if (columnIndex==COL_REVIEW) data.get(rowIndex).review=(String)value;
        if (columnIndex==COL_PRESENT) data.get(rowIndex).present=(String)value;
        if (columnIndex==COL_PATH) data.get(rowIndex).path=(File)value;
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }



}

