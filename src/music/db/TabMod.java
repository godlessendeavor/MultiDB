
package music.db;

import java.io.File;
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
	private static final int numCols=12;
	private String[] labels=new String[numCols];
    private ArrayList<Disc> data;
    private AbstractDDBB dataBase = new AbstractDDBB();
    private ResultSet rs = null;
    private Disc disc;
	private ArrayList<Integer> greyedOutRows = new ArrayList<Integer>();
    public static int port=MultiDB.port;
    public static String host=MultiDB.host;
    public static String user=MultiDB.user;
    public static String pass=MultiDB.pass;
    public static String database=MultiDB.musicDatabase;
    public static String table=MultiDB.musicTable;
    public static String mysqlPath=MultiDB.mysqlPath;

    public TabMod() {
    	data=new ArrayList<Disc>();
    	labels[Disc.COL_GROUP]="groupName";
    	labels[Disc.COL_TITLE]="title";
    	labels[Disc.COL_STYLE]="style";
    	labels[Disc.COL_YEAR]="year";
    	labels[Disc.COL_LOC]="loc";
    	labels[Disc.COL_COPY]="copy";
    	labels[Disc.COL_TYPE]="type";
    	labels[Disc.COL_MARK]="mark";
    	labels[Disc.COL_PRESENT]="present";
    	labels[Disc.COL_REVIEW]="review";
    	labels[Disc.COL_PATH]="path";
    	labels[Disc.COL_ID]="Id";
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
                            //data[i]= new Disco();
                            disc=new Disc();
                            disc.id=(Integer)rs.getObject(Disc.COL_ID+1);
                            disc.group=(String)rs.getObject(Disc.COL_GROUP+1);
                            disc.title=(String)rs.getObject(Disc.COL_TITLE+1);
                            disc.style=(String)rs.getObject(Disc.COL_STYLE+1);
                            if ((Long)rs.getObject(Disc.COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(Disc.COL_YEAR+1)).toString();
                            disc.loc=(String)rs.getObject(Disc.COL_LOC+1);
                            disc.copy=(String)rs.getObject(Disc.COL_COPY+1);
                            disc.type=(String)rs.getObject(Disc.COL_TYPE+1);
                            disc.mark=(String)rs.getObject(Disc.COL_MARK+1);
                            disc.review=(String)rs.getObject(Disc.COL_REVIEW+1);
                            disc.present=new String("NO");
                            disc.path=new File("");
                            data.add(disc);
                        }
                    }
                    dataBase.close();
                    //ORDERING DATA
                    Collections.sort(data);
                }
            }         
        }catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException comex)
	    {
        	Errors.showError(Errors.DB_ACCESS, "Exception during access to DB "+comex.getMessage());        	
        } catch (Exception ex) {
        	Errors.showError(Errors.DB_ACCESS, "Exception during access to DB "+ex.getMessage());  
        	ex.printStackTrace();
        }     
    }
    
    public void setAllData(ArrayList<Disc> data){
    	this.data=data;
    	this.fireTableDataChanged();
    }
    
    public void clearData(){
    	data=new ArrayList<Disc>();
    }
    
    public void setAllDataString(ArrayList<String[]> data) {
    	String[] currRow = new String[numCols];
    	int size = this.getRowCount();
		this.data.clear();
		if (size>1) this.fireTableRowsDeleted(0, size-1);
    	for (int currDisc=0;currDisc<data.size();currDisc++){
    		Disc disc = new Disc();
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
    
	public void greyOutRows(ArrayList<Integer> greyedOutRows){
		for (int i=0;i<this.greyedOutRows.size();i++){
			this.greyOutRow(this.greyedOutRows.get(i));
		} 
	}
	public void greyOutRow(int row){
		this.greyedOutRows.add(row);
		fireTableRowsUpdated(row, row);
	}
	
	public boolean rowInGreyedOut(int row){
		return this.greyedOutRows.contains(new Integer(row));
	}

    public Object getValueAt(int rowIndex, int columnIndex) {
    	Object ob = new Object();
        
        if (columnIndex==Disc.COL_ID) ob=data.get(rowIndex).id;
        if (columnIndex==Disc.COL_GROUP) ob=data.get(rowIndex).group;
        if (columnIndex==Disc.COL_TITLE) ob=data.get(rowIndex).title;
        if (columnIndex==Disc.COL_STYLE) ob=data.get(rowIndex).style;
        if (columnIndex==Disc.COL_YEAR) ob=data.get(rowIndex).year;
        if (columnIndex==Disc.COL_LOC) ob=data.get(rowIndex).loc;
        if (columnIndex==Disc.COL_COPY) ob=data.get(rowIndex).copy;
        if (columnIndex==Disc.COL_TYPE) ob=data.get(rowIndex).type;
        if (columnIndex==Disc.COL_MARK) ob=data.get(rowIndex).mark;
        if (columnIndex==Disc.COL_REVIEW) ob=data.get(rowIndex).review;
        if (columnIndex==Disc.COL_PRESENT) ob=data.get(rowIndex).present;
        if (columnIndex==Disc.COL_PATH) ob=data.get(rowIndex).path;

        return ob;
    }

    public Disc getDiscAtRow(int row){
        disc= new Disc();
        disc=data.get(row);
        return disc;
    }

     public void setDiscAtRow(Disc disc, int row) {
         this.setValueAt(disc.id,row,Disc.COL_ID);
         this.setValueAt(disc.group,row,Disc.COL_GROUP);
         this.setValueAt(disc.title,row,Disc.COL_TITLE);
         this.setValueAt(disc.style,row,Disc.COL_STYLE);
         this.setValueAt(disc.year,row,Disc.COL_YEAR);
         this.setValueAt(disc.loc,row,Disc.COL_LOC);
         this.setValueAt(disc.copy,row,Disc.COL_COPY);
         this.setValueAt(disc.type,row,Disc.COL_TYPE);
         this.setValueAt(disc.mark,row,Disc.COL_MARK);
         this.setValueAt(disc.review,row,Disc.COL_REVIEW);
         this.setValueAt(disc.present,row,Disc.COL_PRESENT);
         this.setValueAt(disc.path,row,Disc.COL_PATH);
         this.fireTableRowsUpdated(row, row);
    }
    public int addDisc(Disc disc){
        data.add(disc);
        this.fireTableDataChanged();
        return(data.size()-1);
    }
    public void deleteDisc(int row){
        data.remove(row);
        this.fireTableRowsDeleted(row, row);
    }

    public void sort(){
        Collections.sort(data);
        this.fireTableDataChanged();
    }
   
    public int searchDisc(String group,String disc){
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
            		if (data.get(i).title.compareToIgnoreCase(disc)==0){
            			sol =i;
            			break;
            		}
            }

        }
        return sol;
    }

    //OVERLOADED METHOD SEARCHELEMENT BY ID
    public int searchDisc(int id){
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

    /*    @Override
  public Class<? extends Object> getColumnClass(int c) {
    	int i=0;
    	Object obj=new Object();
    	if (data.size()>0){
    		System.out.println("i is "+i+" and c is "+c);
    		while(getValueAt(i,c)==null) {
    			if (i<data.size()) i++;
    			else return obj.getClass();
    		}
    	}else return obj.getClass();
    	return getValueAt(i, c).getClass();
   }*/


    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if ((col == Disc.COL_ID)||(col==Disc.COL_PRESENT)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
     
        if (columnIndex==Disc.COL_ID) data.get(rowIndex).id=(Integer)value;
        if (columnIndex==Disc.COL_GROUP) data.get(rowIndex).group=(String)value;
        if (columnIndex==Disc.COL_TITLE) data.get(rowIndex).title=(String)value;
        if (columnIndex==Disc.COL_STYLE) data.get(rowIndex).style=(String)value;
        if (columnIndex==Disc.COL_YEAR) data.get(rowIndex).year=value.toString();
        if (columnIndex==Disc.COL_LOC) data.get(rowIndex).loc=(String)value;
        if (columnIndex==Disc.COL_COPY) data.get(rowIndex).copy=(String)value;
        if (columnIndex==Disc.COL_TYPE) data.get(rowIndex).type=(String)value;
        if (columnIndex==Disc.COL_MARK) data.get(rowIndex).mark=(String)value;
        if (columnIndex==Disc.COL_REVIEW) data.get(rowIndex).review=(String)value;
        if (columnIndex==Disc.COL_PRESENT) data.get(rowIndex).present=(String)value;
        if (columnIndex==Disc.COL_PATH) data.get(rowIndex).path=(File)value;
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }



}

