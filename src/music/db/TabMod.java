
package music.db;

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
    private ArrayList<Disc> data;
    private AbstractDDBB dataBase = new AbstractDDBB();
    private ResultSet rs = null;
    private Disc disc;
    public static int port=MultiDB.port;
    public static String host=MultiDB.host;
    public static String user=MultiDB.user;
    public static String pass=MultiDB.pass;
    public static String database=MultiDB.musicDatabase;
    public static String mysqlPath=MultiDB.mysqlPath;

    public TabMod() {

        try {
            if (dataBase.cargaControlador()) {
                if (dataBase.open("jdbc:mysql://"+host+":"+port+"/"+database, user, pass)) {
                    if (dataBase.select("Select * from music")) {

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
                        data=new ArrayList<Disc>();
                        dataBase.select("Select * from music");
                        rs = dataBase.getRs();
                        
                        for (i = 0; i < countData; i++) {
                            rs.next();            
                            //data[i]= new Disco();
                            disc=new Disc();
                            disc.id=(Integer)rs.getObject(COL_ID+1);
                            disc.group=(String)rs.getObject(COL_GROUP+1);
                            disc.title=(String)rs.getObject(COL_TITLE+1);
                            disc.style=(String)rs.getObject(COL_STYLE+1);
                            if ((Long)rs.getObject(COL_YEAR+1)!=null) disc.year=((Long)rs.getObject(COL_YEAR+1)).toString();
                            disc.loc=(String)rs.getObject(COL_LOC+1);
                            disc.copy=(String)rs.getObject(COL_COPY+1);
                            disc.type=(String)rs.getObject(COL_TYPE+1);
                            disc.mark=(String)rs.getObject(COL_MARK+1);
                            disc.review=(String)rs.getObject(COL_REVIEW+1);
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
        if (columnIndex==COL_GROUP) ob=data.get(rowIndex).group;
        if (columnIndex==COL_TITLE) ob=data.get(rowIndex).title;
        if (columnIndex==COL_STYLE) ob=data.get(rowIndex).style;
        if (columnIndex==COL_YEAR) ob=data.get(rowIndex).year;
        if (columnIndex==COL_LOC) ob=data.get(rowIndex).loc;
        if (columnIndex==COL_COPY) ob=data.get(rowIndex).copy;
        if (columnIndex==COL_TYPE) ob=data.get(rowIndex).type;
        if (columnIndex==COL_MARK) ob=data.get(rowIndex).mark;
        if (columnIndex==COL_REVIEW) ob=data.get(rowIndex).review;
        if (columnIndex==COL_PRESENT) ob=data.get(rowIndex).present;
        if (columnIndex==COL_PATH) ob=data.get(rowIndex).path;

        return ob;
    }

    public Disc getDiscAtRow(int row){
        disc= new Disc();
        disc=data.get(row);
        return disc;
    }

     public void setDiscAtRow(Disc disc, int row) {
         this.setValueAt(disc.id,row,COL_ID);
         this.setValueAt(disc.group,row,COL_GROUP);
         this.setValueAt(disc.title,row,COL_TITLE);
         this.setValueAt(disc.style,row,COL_STYLE);
         this.setValueAt(disc.year,row,COL_YEAR);
         this.setValueAt(disc.loc,row,COL_LOC);
         this.setValueAt(disc.copy,row,COL_COPY);
         this.setValueAt(disc.type,row,COL_TYPE);
         this.setValueAt(disc.mark,row,COL_MARK);
         this.setValueAt(disc.review,row,COL_REVIEW);
         this.setValueAt(disc.present,row,COL_PRESENT);
         this.setValueAt(disc.path,row,COL_PATH);
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
            //System.out.println("comparing "+data[i].group+" with "+group);
            //groupComp = comparador.compare(data[i].group, group);
            //discComp = comparador.compare(data[i].title, disc);
            //if (groupComp==0&&discComp==0) {
          /*  if ((data[i].group.compareToIgnoreCase(group)==0)&&(data[i].title.compareToIgnoreCase(disc)==0)){
                    sol =i;
                    break;
            }*/
             if ((data.get(i).group.compareToIgnoreCase(group)==0)&&(data.get(i).title.compareToIgnoreCase(disc)==0)){
                    sol =i;
                    break;
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
    	int i=1;
    	while(getValueAt(i,c)==null) i++;
    	return getValueAt(i, c).getClass();
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
        if (columnIndex==COL_GROUP) data.get(rowIndex).group=(String)value;
        if (columnIndex==COL_TITLE) data.get(rowIndex).title=(String)value;
        if (columnIndex==COL_STYLE) data.get(rowIndex).style=(String)value;
        if (columnIndex==COL_YEAR) data.get(rowIndex).year=value.toString();
        if (columnIndex==COL_LOC) data.get(rowIndex).loc=(String)value;
        if (columnIndex==COL_COPY) data.get(rowIndex).copy=(String)value;
        if (columnIndex==COL_TYPE) data.get(rowIndex).type=(String)value;
        if (columnIndex==COL_MARK) data.get(rowIndex).mark=(String)value;
        if (columnIndex==COL_REVIEW) data.get(rowIndex).review=(String)value;
        if (columnIndex==COL_PRESENT) data.get(rowIndex).present=(String)value;
        if (columnIndex==COL_PATH) data.get(rowIndex).path=(File)value;
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }



}

