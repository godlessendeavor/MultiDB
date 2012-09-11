package movies.web;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import movies.db.Movie;

public class WebMoviesInfoExtractor {
	private final int POSID=0;
	private final int POSTITLE=1;
	private final int POSYEAR=2;
	private final int POSDIRECTOR=3;
	private JFrame selectFrame;
	private JTable moviesTable;
	private JButton chbutton;
	private JButton nbutton;
	private JScrollPane spTable;
	private static Object lock = new Object();
	private static boolean ended=false;
	private DefaultTableModel tableModel;
	private int selectedViewRow,selectedModelRow;
	public String groupId;
	public String web;
	
	public WebMoviesInfoExtractor(){
		try {
			UIManager.setLookAndFeel("com.easynth.lookandfeel.EaSynthLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		selectFrame = new JFrame("Select a movie for");
		selectFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		selectFrame.setLayout(new BoxLayout(selectFrame.getContentPane(),BoxLayout.Y_AXIS));
		selectFrame.setMaximumSize(new Dimension(500,700));
	    chbutton = new JButton("Choose this movie");	    
	    SelectTitleHandler selectTitleHandler = new SelectTitleHandler();
	    chbutton.addActionListener(selectTitleHandler);
	    nbutton = new JButton("None of them!");
	    NoneTitleHandler noneTitleHandler = new NoneTitleHandler();
	    nbutton.addActionListener(noneTitleHandler);
	    tableModel = new DefaultTableModel(); 
	    tableModel.addColumn("Id");
	    tableModel.addColumn("Title"); 
	    tableModel.addColumn("Year");
	    tableModel.addColumn("Director");
	    moviesTable=new JTable(tableModel){  
			private static final long serialVersionUID = 1L;
	
			public boolean isCellEditable(int row,int column){  
	    		     return false;  
	    		  }  
	    };  
	    
	    moviesTable.removeColumn(moviesTable.getColumn("Id"));
	    spTable = new JScrollPane(moviesTable);
	    selectFrame.add(spTable);
	    selectFrame.add(chbutton); 
	    selectFrame.add(nbutton);
	}
	
	///gets the film data relative to a title from filmaffinity
	public Movie getMovieInfo(String movieTitle){
		Movie movie = null;
		while(tableModel.getRowCount()>0) {
			tableModel.removeRow(0);
			tableModel.fireTableRowsDeleted(0,0);
		}
		
		selectFrame.setTitle("Select a movie for: "+movieTitle);
		ArrayList<Movie> movieList = WebFilmaffinity.getMovieList(movieTitle);		
		
		if (movieList.size()>0){//if the list is not empty then we have to choose some movie
			for(int i=0;i<movieList.size();i++){
				tableModel.addRow(new Vector<String>(4));
				tableModel.setValueAt(movieList.get(i).title, i, POSTITLE);
				tableModel.setValueAt(movieList.get(i).year, i, POSYEAR);
				tableModel.setValueAt(movieList.get(i).director, i, POSDIRECTOR);
				tableModel.setValueAt(movieList.get(i).id, i, POSID);
			}
			//moviesTable.repaint();
			ended=false;
			if (moviesTable.getRowCount()<40) selectFrame.setSize(500, moviesTable.getRowCount()*18+120);
			else selectFrame.setSize(500, 500);
			selectFrame.setVisible(true);
			selectedModelRow=-1;
			synchronized(lock) { //wait until a movie is selected
	               if (!ended)
	               try {
	                   lock.wait();
	               } catch (InterruptedException e) {
	                     e.printStackTrace();
	               }
			}
			if (selectedModelRow>=0){
				movie = new Movie();
		        movie.setTitle((String)tableModel.getValueAt(selectedModelRow, POSTITLE));
		        movie.setYear((String)tableModel.getValueAt(selectedModelRow, POSYEAR));
		        movie.setDirector((String)tableModel.getValueAt(selectedModelRow, POSDIRECTOR));
			}
		}else if (movieList.size()==0){ //if it's no movie then we have to look over the page again with other tags
			movie = WebFilmaffinity.getMovieInfo(movieTitle);
		}	
		return movie;
	
	}
	
	
	///////////////////////INTERNAL CLASS FOR BUTTON SELECT GROUPS////////////////////////
	 private class SelectTitleHandler implements ActionListener {
	       public void actionPerformed(ActionEvent evento) {
	    	   ListSelectionModel lsm = (ListSelectionModel) moviesTable.getSelectionModel();
	           if (!lsm.isSelectionEmpty()){

	               selectedViewRow = lsm.getMinSelectionIndex();
	               selectedModelRow = moviesTable.convertRowIndexToModel(selectedViewRow);
		           selectFrame.dispose();
		           synchronized(lock) {
	                   ended=true;
	                   lock.notify();
					}
	           }
	       }
	   } 
	 ///class for button if there's no group that matches the search
	 private class NoneTitleHandler implements ActionListener {

	       public void actionPerformed(ActionEvent evento) {
	    	   selectedModelRow=-1;
		       selectFrame.dispose();
		       synchronized(lock) {
	                ended=true;
	                lock.notify();
		       }
	       }
	   } 
}
