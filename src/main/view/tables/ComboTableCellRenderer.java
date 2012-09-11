package main.view.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import docs.db.DocTheme;

public class ComboTableCellRenderer implements TableCellRenderer {
	   DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
	   DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();

	 
	   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column) {
	     
		 tableRenderer = (DefaultTableCellRenderer) tableRenderer.getTableCellRendererComponent(table,value, isSelected, hasFocus, row, column);
	     Font font= new Font("Tahoma",Font.BOLD,11);
	     tableRenderer.setBackground(Color.ORANGE);
	     tableRenderer.setFont(font);
	     tableRenderer.setOpaque(true);	     
		 if (isSelected){
			Border loweredBevel = BorderFactory.createLoweredBevelBorder();
			tableRenderer.setBorder(loweredBevel); 
			tableRenderer.setBackground(Color.getHSBColor(50,147,1635));
		 }else tableRenderer.setBorder(BorderFactory.createEmptyBorder());
		 tableRenderer.setText((DocTheme.getStringTheme((DocTheme)value)));
	     return tableRenderer;
	   }
	 }
