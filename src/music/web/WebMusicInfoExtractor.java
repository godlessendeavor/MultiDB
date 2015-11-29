package music.web;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import music.db.Disc;

public class WebMusicInfoExtractor {
	private final int POSID=0;
	private final int POSGROUP=1;
	private final int POSLOC=2;
	private final int POSSTYLE=3;
	private JFrame selectFrame;
	private JTable groupsTable;
	private JButton chbutton;
	private JButton nbutton;
	private JLabel infoGroup;
	private JScrollPane spTable;
	private static Object lock = new Object();
	private static boolean ended=false;
	private DefaultTableModel tableModel;
	private int selectedViewRow,selectedModelRow;
	public String groupId;
	public String web;
	
	public WebMusicInfoExtractor(){
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
		selectFrame = new JFrame("Select a group for");
		selectFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		selectFrame.setLayout(new BoxLayout(selectFrame.getContentPane(),BoxLayout.Y_AXIS));
		selectFrame.setMaximumSize(new Dimension(500,700));
	    chbutton = new JButton("Choose this group");
	    infoGroup = new JLabel();
	    SelectGroupHandler selectGroupHandler = new SelectGroupHandler();
	    chbutton.addActionListener(selectGroupHandler);
	    nbutton = new JButton("None of them!");
	    NoneGroupHandler noneGroupHandler = new NoneGroupHandler();
	    nbutton.addActionListener(noneGroupHandler);
	    tableModel = new DefaultTableModel(); 
	    tableModel.addColumn("Id");
	    tableModel.addColumn("Group"); 
	    tableModel.addColumn("Location");
	    tableModel.addColumn("Style");
	    groupsTable=new JTable(tableModel){  
			private static final long serialVersionUID = 1L;
	
			public boolean isCellEditable(int row,int column){  
	    		     return false;  
	    		  }  
	    };  
	    
	    groupsTable.removeColumn(groupsTable.getColumn("Id"));
	    spTable = new JScrollPane(groupsTable);
	    selectFrame.add(spTable);
	    selectFrame.add(infoGroup); 
	    selectFrame.add(chbutton); 
	    selectFrame.add(nbutton);
	}
	
	///gets the discography from Encyclpaedia Metallum or MusicBrainz
	public ArrayList<Disc> getGroupInfo(String group, String web, String country, String styleft){
		ArrayList<Disc> discInfo = new ArrayList<Disc>();
		while(tableModel.getRowCount()>0) {
			tableModel.removeRow(0);
		}
		selectFrame.setTitle("Select a group for: "+group);
		if (web.compareTo("webEM")==0) {
			ArrayList<GroupInfo> groupList = WebEncyclopediaMetallum.getGroupList(group);
			
			for (int itCol=0;itCol<tableModel.getRowCount();itCol++){
				tableModel.removeRow(itCol);
			}
			if (groupList==null) return null;
			if (groupList.size()>1){//if there's more than 1 group it has to be selected from the list
				infoGroup.setText("Location: "+country+" Style: "+styleft);
				for(int i=0;i<groupList.size();i++){
					tableModel.addRow(new Vector<String>(4));
					tableModel.setValueAt(groupList.get(i).group, i, POSGROUP);
					tableModel.setValueAt(groupList.get(i).loc, i, POSLOC);
					tableModel.setValueAt(groupList.get(i).style, i, POSSTYLE);
					tableModel.setValueAt(groupList.get(i).id, i, POSID);
				}
				ended=false;
				if (groupsTable.getRowCount()<40) selectFrame.setSize(500, groupsTable.getRowCount()*18+130);
				else selectFrame.setSize(500, 500);
				selectFrame.setVisible(true);
				selectedModelRow=-1;
				synchronized(lock) { //wait until a group is selected
	                if (!ended)
	                try {
	                      lock.wait();
	                } catch (InterruptedException e) {
	                      e.printStackTrace();
	                }
				}
				if (selectedModelRow>=0){
			        String groupId=(String)tableModel.getValueAt(selectedModelRow, POSID);
			        String cgroup=(String)tableModel.getValueAt(selectedModelRow, POSGROUP);
			        String loc=(String)tableModel.getValueAt(selectedModelRow, POSLOC);
			        String style=(String)tableModel.getValueAt(selectedModelRow, POSSTYLE);
					discInfo = WebEncyclopediaMetallum.getGroupDiscography(groupId,cgroup,loc,style);
				}
			}else if (groupList.size()==1){ //if it's only one group just search for discography
				String groupId=groupList.get(0).id;
			    String cgroup=groupList.get(0).group;
			    String loc=groupList.get(0).loc;
			    String style=groupList.get(0).style;
			    discInfo = WebEncyclopediaMetallum.getGroupDiscography(groupId,cgroup,loc,style);
			}			
		}
		return discInfo;
//		else if (web.compareTo("webMB")==0) id=WebMusicBrainz.getGroupId(group,web);
//		if (id!=null){
//			HTMLText=WebReader.getHTMLfromURL(WebMusicBrainz.webMBBand+id); //web MuiscBrainz
//		}
//		
//		groupInfo.reset();
//		groupInfo.group=group;
//		if (web.compareTo("webEM")==0) groupInfo=WebEncyclopediaMetallum.getGroupInfoFromHTML(HTMLText);
//		if (groupInfo.group.compareTo("Group not found")==0){
//			discList.clear();
//		}else {
//			String url=webEMDiscography+id+"/tabs/all";
//			HTMLText=getHTMLfromURL(url);
//			discList=getGroupDiscography(HTMLText,groupInfo,Web,id);
//		}
					
	}
	
	
	public static String getLyricsOfDisc(String lyricsGroup,String lyricsAlbum,String web){
		String lyrics="";
		if (web.compareTo("webEM")==0) {
			lyrics=WebEncyclopediaMetallum.getLyricsOfDisc(lyricsGroup,lyricsAlbum);
		}
		return lyrics;
	}
	
	///////////////////////INTERNAL CLASS FOR BUTTON SELECT GROUPS////////////////////////
	 private class SelectGroupHandler implements ActionListener {
	       public void actionPerformed(ActionEvent evento) {
	    	   ListSelectionModel lsm = (ListSelectionModel) groupsTable.getSelectionModel();
	           if (!lsm.isSelectionEmpty()){

	               selectedViewRow = lsm.getMinSelectionIndex();
	               selectedModelRow = groupsTable.convertRowIndexToModel(selectedViewRow);
		           selectFrame.dispose();
		           synchronized(lock) {
	                   ended=true;
	                   lock.notify();
					}
	           }
	       }
	   } 
	 ///class for button if there's no group that matches the search
	 private class NoneGroupHandler implements ActionListener {

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
