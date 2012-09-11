package musicmovies.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;

import main.AbstractDDBB;
import main.Errors;
import main.MultiDB;
import db.CSV.CSV;


public class TabMod extends AbstractTableModel{

  
	private static final long serialVersionUID = 1L;
	private static final int numCols=10;
	private String[] labels=new String[numCols];
    private ArrayList<Video> data;
    private AbstractDDBB dataBase = new AbstractDDBB();
    private ResultSet rs = null;
    private Video vid;
    public static int port=MultiDB.port;
    public static String host=MultiDB.host;
    public static String user=MultiDB.user;
    public static String pass=MultiDB.pass;
    public static String database=MultiDB.musicDatabase;
    public static String table=MultiDB.musicMoviesTable;
    public static String mysqlPath=MultiDB.mysqlPath;

    public TabMod() {
    	data=new ArrayList<Video>();
    	labels[Video.COL_GROUP]="groupName";
    	labels[Video.COL_TITLE]="title";
    	labels[Video.COL_STYLE]="style";
    	labels[Video.COL_YEAR]="year";
    	labels[Video.COL_LOC]="loc";
    	labels[Video.COL_COPY]="copy";
    	labels[Video.COL_TYPE]="type";
    	labels[Video.COL_MARK]="mark";
    	labels[Video.COL_REVIEW]="review";
    	labels[Video.COL_ID]="Id";
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
                        labels[i]="present"; //anhadir columnas de presente en backup y directorio
                        labels[i+1]="path";*/
                        int countData = 0;
                         //Contamos los datos que hay
                        while (rs.next()) {
                            countData++;
                        }
  
                        //data=new Disco[countData];
                        
                        dataBase.select("Select * from "+table);
                        rs = dataBase.getRs();
                        int i=0;
                        for (i = 0; i < countData; i++) {
                            rs.next();            
                            //data[i]= new Video();
                            vid=new Video();
                            vid.id=(Long)rs.getObject(Video.COL_ID+1);
                            vid.group=(String)rs.getObject(Video.COL_GROUP+1);
                            vid.title=(String)rs.getObject(Video.COL_TITLE+1);
                            vid.style=(String)rs.getObject(Video.COL_STYLE+1);
                            if ((Long)rs.getObject(Video.COL_YEAR+1)!=null) vid.year=((Long)rs.getObject(Video.COL_YEAR+1)).toString();
                            vid.loc=(String)rs.getObject(Video.COL_LOC+1);
                            vid.copy=(String)rs.getObject(Video.COL_COPY+1);
                            vid.type=(String)rs.getObject(Video.COL_TYPE+1);
                            vid.mark=(String)rs.getObject(Video.COL_MARK+1);
                            vid.review=(String)rs.getObject(Video.COL_REVIEW+1);
                            data.add(vid);
                        }
                    }
                    dataBase.close();
                    //ORDERING DATA
                    Collections.sort(data);
                }
            }         
        }catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException comex)
	    {
        	Errors.showError(Errors.DB_UNDEFINED, comex.getMessage());        	
        } catch (Exception ex) {
        	Errors.showError(Errors.DB_UNDEFINED, ex.getMessage());
        }
        

    }
    
    public void setAllData(ArrayList<Video> data){
    	this.data=data;
    	this.fireTableDataChanged();
    }
    
    public void setAllDataString(ArrayList<String[]> data) {
    	String[] currRow = new String[numCols];
    	int size = this.getRowCount();
		this.data.clear();
		if (size>1) this.fireTableRowsDeleted(0, size-1);
    	for (int currDisc=0;currDisc<data.size();currDisc++){
    		Video disc = new Video();
    		currRow = ((String[])data.get(currDisc));
    		disc.setFromStringArray(currRow);
            this.data.add(disc);
    	}
        Collections.sort(this.data);
        size = this.getRowCount();
        if (size>1) this.fireTableRowsInserted(0,size-1);
        //this.fireTableDataChanged();
    }
    
    public int saveToCSV(String filename){
    	ArrayList<String[]> arrayString=new ArrayList<String[]>();
    	for (int currRow=0;currRow<data.size();currRow++){
    		arrayString.add(data.get(currRow).toStringArray());
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
        
        if (columnIndex==Video.COL_ID) ob=data.get(rowIndex).id;
        if (columnIndex==Video.COL_GROUP) ob=data.get(rowIndex).group;
        if (columnIndex==Video.COL_TITLE) ob=data.get(rowIndex).title;
        if (columnIndex==Video.COL_STYLE) ob=data.get(rowIndex).style;
        if (columnIndex==Video.COL_YEAR) ob=data.get(rowIndex).year;
        if (columnIndex==Video.COL_LOC) ob=data.get(rowIndex).loc;
        if (columnIndex==Video.COL_COPY) ob=data.get(rowIndex).copy;
        if (columnIndex==Video.COL_TYPE) ob=data.get(rowIndex).type;
        if (columnIndex==Video.COL_MARK) ob=data.get(rowIndex).mark;
        if (columnIndex==Video.COL_REVIEW) ob=data.get(rowIndex).review;

        return ob;
    }

    public Video getVideoAtRow(int row){
        vid= new Video();
        vid=data.get(row);
        return vid;
    }

     public void setVideoAtRow(Video vid, int row) {
         this.setValueAt(vid.id,row,Video.COL_ID);
         this.setValueAt(vid.group,row,Video.COL_GROUP);
         this.setValueAt(vid.title,row,Video.COL_TITLE);
         this.setValueAt(vid.style,row,Video.COL_STYLE);
         this.setValueAt(vid.year,row,Video.COL_YEAR);
         this.setValueAt(vid.loc,row,Video.COL_LOC);
         this.setValueAt(vid.copy,row,Video.COL_COPY);
         this.setValueAt(vid.type,row,Video.COL_TYPE);
         this.setValueAt(vid.mark,row,Video.COL_MARK);
         this.setValueAt(vid.review,row,Video.COL_REVIEW);
         this.fireTableRowsUpdated(row, row);
    }
    public int addVideo(Video vid){
        data.add(vid);
        this.fireTableDataChanged();
        return(data.size()-1);
    }
    public void deleteVideo(int row){
        data.remove(row);
        this.fireTableRowsDeleted(row, row);
    }

    public void sort(){
        Collections.sort(data);
        this.fireTableDataChanged();
    }
   
    public int searchVideo(String group,String vid){
        int i,sol=-1;
        int tam=this.getRowCount();
        //int groupComp,discComp;
        for(i=0;i<tam;i++){
            //Collator comparador = Collator.getInstance();
            //comparador.setStrength(Collator.PRIMARY);
            //System.out.println("comparing "+data.get(i).group+" with "+group);
            //groupComp = comparador.compare(data.get(i).group, group);
            //discComp = comparador.compare(data.get(i).title, disc);
            //if (groupComp==0&&discComp==0) 
            if ((data.get(i).group.compareToIgnoreCase(group)==0)){
            		if (data.get(i).title.compareToIgnoreCase(vid)==0){
            			sol =i;
            			break;
            		}
            }

        }
        return sol;
    }

    //OVERLOADED METHOD SEARCHELEMENT BY ID
    public int searchVideo(int id){
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
        String el,el2;
        for (row = currentRow; row < tam; row++) {
            Object ob = this.getValueAt(row, col);
            if (ob instanceof String) {
                el = (String) this.getValueAt(row, col);
                if (el.length() > charPos) {
					el2 = el.substring(charPos, charPos + 1);
					if (el2.compareToIgnoreCase(car + "") == 0) {
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
    	int i=0;
    	while(getValueAt(i,c)==null) i++;
    	return getValueAt(i, c).getClass();
   }


    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col == Video.COL_ID) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
     
        if (columnIndex==Video.COL_ID) data.get(rowIndex).id=(Long)value;
        if (columnIndex==Video.COL_GROUP) data.get(rowIndex).group=(String)value;
        if (columnIndex==Video.COL_TITLE) data.get(rowIndex).title=(String)value;
        if (columnIndex==Video.COL_STYLE) data.get(rowIndex).style=(String)value;
        if (columnIndex==Video.COL_YEAR) data.get(rowIndex).year=value.toString();
        if (columnIndex==Video.COL_LOC) data.get(rowIndex).loc=(String)value;
        if (columnIndex==Video.COL_COPY) data.get(rowIndex).copy=(String)value;
        if (columnIndex==Video.COL_TYPE) data.get(rowIndex).type=(String)value;
        if (columnIndex==Video.COL_MARK) data.get(rowIndex).mark=(String)value;
        if (columnIndex==Video.COL_REVIEW) data.get(rowIndex).review=(String)value;
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }



}

