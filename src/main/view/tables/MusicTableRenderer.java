package main.view.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import music.db.Disc;
import music.db.DiscTableModel;

public class MusicTableRenderer extends JLabel implements TableCellRenderer {
	   
	private static final long serialVersionUID = 1L;

	public MusicTableRenderer() {
		super();
	}

	public MusicTableRenderer(String arg0) {
		super(arg0);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean isFocused, int row, int col) {
		
		Font font;		
		int modelCol=table.convertColumnIndexToModel(col);
		DiscTableModel model = (DiscTableModel) table.getModel();

		switch (modelCol){
			case Disc.COL_GROUP:
				this.setBackground(Color.CYAN);
				font= new Font("Arial",Font.BOLD,12);
				break;
			case Disc.COL_TITLE:
				this.setBackground(Color.YELLOW);
				font= new Font("Lucida Console",Font.BOLD,11);
				break;
			case Disc.COL_YEAR:
				this.setBackground(Color.getHSBColor(50,36,1215));
				font= new Font("Arial",Font.BOLD,11);
				break;	
			case Disc.COL_STYLE:
				this.setBackground(Color.GREEN);
				font= new Font("Century Gothic",Font.PLAIN,11);
				break;	
			case Disc.COL_LOC:
				this.setBackground(Color.PINK);
				font= new Font("Book Antiqua",Font.PLAIN,11);
				break;		
			case Disc.COL_COPY:
				this.setBackground(Color.ORANGE);
				font= new Font("Arial",Font.BOLD,11);
				break;		
			case Disc.COL_TYPE:
				this.setBackground(Color.LIGHT_GRAY);
				font= new Font("Arial",Font.PLAIN,11);
				break;	
			case Disc.COL_MARK:
				this.setBackground(Color.RED);
				font= new Font("Arial",Font.PLAIN,11);
				break;	
			case Disc.COL_PRESENT:
				this.setBackground(Color.MAGENTA);
				font= new Font("Arial",Font.BOLD,11);
				break;		
			default:
				this.setBackground(Color.getHSBColor(50,147,1635));
				font= new Font("Garamond",Font.BOLD,13);
				break;
		}
		if (model.rowInGreyedOut(row)){
			this.setBackground(Color.GRAY);
		}
		this.setFont(font);
		this.setOpaque(true);
		if (isSelected){
			Border loweredBevel = BorderFactory.createLoweredBevelBorder();
			this.setBorder(loweredBevel); 
			this.setBackground(Color.getHSBColor(50,147,1635));
		}
		else this.setBorder(BorderFactory.createEmptyBorder());
		if (value!=null) this.setText(value.toString());
		return this;
	} 
	   
   }