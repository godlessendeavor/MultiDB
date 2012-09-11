package main.view.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import movies.db.Movie;


public class MoviesTableRenderer extends JLabel implements TableCellRenderer {

		   
		private static final long serialVersionUID = 1L;

		public MoviesTableRenderer() {
			super();
		}

		public MoviesTableRenderer(String arg0) {
			super(arg0);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean isFocused, int row, int col) {
			
			Font font;			
			int modelCol=table.convertColumnIndexToModel(col);

			switch (modelCol){
				case Movie.COL_TITLE:
					this.setBackground(Color.YELLOW);
					font= new Font("Lucida Console",Font.BOLD,11);
					break;
				case Movie.COL_DIR:
					this.setBackground(Color.GREEN);
					font= new Font("Century Gothic",Font.PLAIN,11);
					break;	
				case Movie.COL_YEAR:
					this.setBackground(Color.ORANGE);
					font= new Font("Tahoma",Font.BOLD,11);
					break;	
				case Movie.COL_LOC:
					this.setBackground(Color.PINK);
					font= new Font("Book Antiqua",Font.PLAIN,11);
					break;		
				case Movie.COL_OTHER:
					this.setBackground(Color.RED);
					font= new Font("Arial",Font.PLAIN,11);
					break;	
				case Movie.COL_PRESENT:
					this.setBackground(Color.MAGENTA);
					font= new Font("Arial",Font.BOLD,11);
					break;		
				default:
					font= new Font("Garamond",Font.BOLD,13);
					break;
			}
			this.setFont(font);
			this.setOpaque(true);
			if (isSelected){
				Border loweredBevel = BorderFactory.createLoweredBevelBorder();
				this.setBorder(loweredBevel); 
				this.setBackground(Color.getHSBColor(50,147,1635));
			}
			else this.setBorder(BorderFactory.createEmptyBorder());
			this.setText((String)value);
			return this;
		} 
}

