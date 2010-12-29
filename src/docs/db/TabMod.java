/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docs.db;

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
    private ArrayList<Doc> data;
    private AbstractDDBB dataBase = new AbstractDDBB();
    private ResultSet rs = null;
    private Doc doc;
    public static int port=MultiDB.port;
    public static String host=MultiDB.host;
    public static String user=MultiDB.user;
    public static String pass=MultiDB.pass;
    public static String database=MultiDB.moviesDatabase;
    public static String table=MultiDB.docsTable;
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
                        labels = new String[numCol];// Se obtiene cada una de las etiquetas para cada columna
                        int i=0;
                        for (i = 0; i < numCol; i++) {
                            // para ResultSetMetaData la primera columna es la 1.
                            labels[i] = metaDatos.getColumnLabel(i + 1);
                        }
                        int countData = 0;
                         //Contamos los datos que hay
                        while (rs.next()) {
                            countData++;
                        }
  
                        //data=new Disco[countData];
                        data=new ArrayList<Doc>();
                        dataBase.select("Select * from "+table);
                        rs = dataBase.getRs();
                        
                        for (i = 0; i < countData; i++) {
                            rs.next();            
                            //data[i]= new Disco();
                            doc=new Doc();
                            doc.id=(Integer)rs.getObject(COL_ID+1);
                            doc.title=(String)rs.getObject(COL_TITLE+1);
    						doc.loc = (String) rs.getObject(COL_LOC + 1);
    						doc.setThemeByString((String) rs.getObject(COL_THEME + 1));
    						doc.comments = (String) rs.getObject(COL_COMMENTS + 1);
                            data.add(doc);
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
        if (columnIndex==COL_LOC) ob=data.get(rowIndex).loc;
        if (columnIndex==COL_COMMENTS) ob=data.get(rowIndex).comments;
        if (columnIndex==COL_THEME) ob=data.get(rowIndex).theme;
        return ob;
    }

    public Doc getDocAtRow(int row){
        doc= new Doc();
        doc=data.get(row);
        return doc;
    }

     public void setDocAtRow(Doc doc, int row) {
         this.setValueAt(doc.id,row,COL_ID);
         this.setValueAt(doc.title,row,COL_TITLE);
         this.setValueAt(doc.loc,row,COL_LOC);
         this.setValueAt(doc.comments,row,COL_COMMENTS);
         this.setValueAt(doc.theme,row,COL_THEME);
         this.fireTableRowsUpdated(row, row);
    }
    public int addDoc(Doc doc){
        data.add(doc);
        this.fireTableDataChanged();
        return(data.size()-1);
    }
    public void deleteDoc(int row){
        data.remove(row);
        this.fireTableRowsDeleted(row, row);
    }

    public void sort(){
        Collections.sort(data);
        this.fireTableDataChanged();
    }
   
    public int searchDoc(String title,String doc){
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
    public int searchDoc(int id){
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
    	if (c==COL_THEME) return DocTheme.class; else return String.class;
   }


    @Override
    public boolean isCellEditable(int row, int col) {
    	return true;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
     
        if (columnIndex==COL_ID) data.get(rowIndex).id=(Integer)value;
        if (columnIndex==COL_TITLE) data.get(rowIndex).title=(String)value;
        if (columnIndex==COL_LOC) data.get(rowIndex).loc=(String)value;
        if (columnIndex==COL_COMMENTS) data.get(rowIndex).comments=(String)value;
        if (columnIndex==COL_THEME) {
        	if (value instanceof String) data.get(rowIndex).theme = DocTheme.getDocTheme((String)value);
        	else if (value instanceof DocTheme) data.get(rowIndex).theme = (DocTheme)value;
        }
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }



}

