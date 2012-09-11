/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movies.db;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;

import main.AbstractDDBB;
import main.MultiDB;
import db.CSV.CSV;


public class TabMod extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	private static final int numCols=9;
	private String[] labels = new String[numCols];
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

    public TabMod() {
        data=new ArrayList<Movie>();
    	labels[Movie.COL_TITLE]="title";
    	labels[Movie.COL_DIR]="director";
    	labels[Movie.COL_YEAR]="year";
    	labels[Movie.COL_LOC]="loc";
    	labels[Movie.COL_OTHER]="other";
    	labels[Movie.COL_PRESENT]="present";
    	labels[Movie.COL_REVIEW]="review";
    	labels[Movie.COL_PATH]="path";
    	labels[Movie.COL_ID]="Id";        
        try {
            if (dataBase.cargaControlador()>-1) {
                if (dataBase.open("jdbc:mysql://"+host+":"+port+"/"+database, user, pass)>-1) {
                    if (dataBase.select("Select * from "+table)>-1) {

                        rs = dataBase.getRs();
                        /*ResultSetMetaData metaDatos = rs.getMetaData();
                        // Se obtiene el numero de columnas.
                        int numCol = metaDatos.getColumnCount();
                        labels = new String[numCol+2];// Se obtiene cada una de las etiquetas para cada columna
                        int i=0;
                        for (i = 0; i < numCol; i++) {
                            // para ResultSetMetaData la primera columna es la 1.
                            labels[i] = metaDatos.getColumnLabel(i + 1);
                        }
                        labels[i]="present"; //añadir columnas de presente en backup y directorio
                        labels[i+1]="path";*/
                        int countData = 0;
                         //Contamos los datos que hay
                        while (rs.next()) {
                            countData++;
                        }
  
                        //data=new Disco[countData];
                        dataBase.select("Select * from "+table);
                        rs = dataBase.getRs();
                        
                        for (int i = 0; i < countData; i++) {
                            rs.next();            
                            //data[i]= new Disco();
                            movie=new Movie();
                            movie.id=(Integer)rs.getObject(Movie.COL_ID+1);
                            movie.title=(String)rs.getObject(Movie.COL_TITLE+1);
                            movie.other=(String)rs.getObject(Movie.COL_OTHER+1);
                            if (rs.getObject(Movie.COL_YEAR + 1)!=null) movie.year = rs.getObject(Movie.COL_YEAR + 1).toString();
                            movie.loc=(String)rs.getObject(Movie.COL_LOC+1);                     
                            movie.director=(String)rs.getObject(Movie.COL_DIR+1);
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
    
    public void setAllData(ArrayList<Movie> data){
    	this.data=data;
    	this.fireTableDataChanged();
    }
    
    public void setAllDataString(ArrayList<String[]> data) {
    	String[] currRow = new String[numCols];
    	int size = this.getRowCount();
		this.data.clear();
		if (size>1) this.fireTableRowsDeleted(0, size-1);
    	for (int currDisc=0;currDisc<data.size();currDisc++){
    		Movie movie = new Movie();
    		currRow = ((String[])data.get(currDisc));
    		movie.setFromStringArray(currRow);
            this.data.add(movie);
    	}
   	   Collections.sort(this.data);
       size = this.getRowCount();
       if (size>1) this.fireTableRowsInserted(0,size-1);
   
    }
    
    public int saveToCSV(String filename){
    	ArrayList<String[]> arrayString=new ArrayList<String[]>();
    	for (int currRow=0;currRow<data.size();currRow++){
    		arrayString.add(data.get(currRow).toStringArrayRel());
    	}
    	return CSV.storeCSV(filename,arrayString);
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return labels.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
    	Object ob = new Object();
        
        if (columnIndex==Movie.COL_ID) ob=data.get(rowIndex).id;
        if (columnIndex==Movie.COL_TITLE) ob=data.get(rowIndex).title;
        if (columnIndex==Movie.COL_DIR) ob=data.get(rowIndex).director;
        if (columnIndex==Movie.COL_YEAR) ob=data.get(rowIndex).year;
        if (columnIndex==Movie.COL_LOC) ob=data.get(rowIndex).loc;
        if (columnIndex==Movie.COL_OTHER) ob=data.get(rowIndex).other;
        if (columnIndex==Movie.COL_REVIEW) ob=data.get(rowIndex).review;
        if (columnIndex==Movie.COL_PRESENT) ob=data.get(rowIndex).present;
        if (columnIndex==Movie.COL_PATH) ob=data.get(rowIndex).path;

        return ob;
    }

    public Movie getMovieAtRow(int row){
        movie= new Movie();
        movie=data.get(row);
        return movie;
    }

     public void setMovieAtRow(Movie movie, int row) {
         this.setValueAt(movie.id,row,Movie.COL_ID);
         this.setValueAt(movie.title,row,Movie.COL_TITLE);
         this.setValueAt(movie.director,row,Movie.COL_DIR);
         this.setValueAt(movie.year,row,Movie.COL_YEAR);
         this.setValueAt(movie.loc,row,Movie.COL_LOC);
         this.setValueAt(movie.other,row,Movie.COL_OTHER);
         this.setValueAt(movie.review,row,Movie.COL_REVIEW);
         this.setValueAt(movie.present,row,Movie.COL_PRESENT);
         this.setValueAt(movie.path,row,Movie.COL_PATH);
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
        if ((col == Movie.COL_ID)||(col==Movie.COL_PRESENT)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
     
        if (columnIndex==Movie.COL_ID) data.get(rowIndex).id=(Integer)value;
        if (columnIndex==Movie.COL_TITLE) data.get(rowIndex).title=(String)value;
        if (columnIndex==Movie.COL_DIR) data.get(rowIndex).director=(String)value;
        if (columnIndex==Movie.COL_YEAR) data.get(rowIndex).year=(String)value;
        if (columnIndex==Movie.COL_OTHER) data.get(rowIndex).other=(String)value;
        if (columnIndex==Movie.COL_LOC) data.get(rowIndex).loc=(String)value;
        if (columnIndex==Movie.COL_REVIEW) data.get(rowIndex).review=(String)value;
        if (columnIndex==Movie.COL_PRESENT) data.get(rowIndex).present=(String)value;
        if (columnIndex==Movie.COL_PATH) data.get(rowIndex).path=(File)value;
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }



}

