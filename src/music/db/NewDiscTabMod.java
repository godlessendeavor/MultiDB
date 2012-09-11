package music.db;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;


public class NewDiscTabMod extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private String[] labels={"Id","Group","Title","Type","Style","Year","Location"};
    private ArrayList<Disc> data = new ArrayList<Disc>();
    private Disc disc;

    public NewDiscTabMod(ArrayList<Disc> inList) {
    	this.data=inList;
    }
    public NewDiscTabMod() {
    }
    
    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return labels.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
    	Object ob = new Object();
        if (columnIndex==Disc.COL_NID) ob=data.get(rowIndex).id;
        if (columnIndex==Disc.COL_NGROUP) ob=data.get(rowIndex).group;
        if (columnIndex==Disc.COL_NTITLE) ob=data.get(rowIndex).title;
        if (columnIndex==Disc.COL_NTYPE) ob=data.get(rowIndex).type;
        if (columnIndex==Disc.COL_NSTYLE) ob=data.get(rowIndex).style;
        if (columnIndex==Disc.COL_NYEAR) ob=data.get(rowIndex).year;
        if (columnIndex==Disc.COL_NLOC) ob=data.get(rowIndex).loc;
        return ob;
    }

    public Disc getDiscAtRow(int row){
        disc= new Disc();
        disc=data.get(row);
        return disc;
    }

     public void setDiscAtRow(Disc disc, int row) {
         this.setValueAt(disc.id,row,Disc.COL_NID);
         this.setValueAt(disc.group,row,Disc.COL_NGROUP);
         this.setValueAt(disc.title,row,Disc.COL_NTITLE);
         this.setValueAt(disc.type,row,Disc.COL_NTYPE);
         this.setValueAt(disc.style,row,Disc.COL_NSTYLE);
         this.setValueAt(disc.year,row,Disc.COL_NYEAR);
         this.setValueAt(disc.loc,row,Disc.COL_NLOC);
         this.fireTableRowsUpdated(row, row);
    }
     
     public void setData(ArrayList<Disc> data){
    	 this.data.clear();
    	 this.data=data;
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

    ///OVERRIDEN METHODS
    @Override
    public String getColumnName(int col) {
        return labels[col];
    }

    @Override
    public Class<? extends Object> getColumnClass(int c){ 
    	return String.class;
   }


    @Override
    public boolean isCellEditable(int row, int col) {
         return false;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
     
        if (columnIndex==Disc.COL_NID) data.get(rowIndex).id=(Integer)value;
        if (columnIndex==Disc.COL_NGROUP) data.get(rowIndex).group=(String)value;
        if (columnIndex==Disc.COL_NTITLE) data.get(rowIndex).title=(String)value;
        if (columnIndex==Disc.COL_NTYPE) data.get(rowIndex).type=(String)value;
        if (columnIndex==Disc.COL_NSTYLE) data.get(rowIndex).style=(String)value;
        if (columnIndex==Disc.COL_NYEAR) data.get(rowIndex).year=value.toString();
        if (columnIndex==Disc.COL_NLOC) data.get(rowIndex).loc=(String)value;
        this.fireTableRowsUpdated(rowIndex, rowIndex);
    }



}

