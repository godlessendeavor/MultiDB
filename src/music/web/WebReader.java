package music.web;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import main.MultiDB;
import music.db.Disc;

public class WebReader {
	private ArrayList<Disc> discList=new ArrayList<Disc>();
	private final String webEMSearch=MultiDB.webEMSearch;// new String("http://www.metal-archives.com/search.php?string=");
	private final String webEMBand=MultiDB.webEMBand; // new String("http://www.metal-archives.com/band.php?id=");
	private final String webEMRelease=MultiDB.webEMRelease; //new String("http://www.metal-archives.com/release.php?id=");
	private final String webEMLyrics=MultiDB.webEMLyrics; //new String("http://www.metal-archives.com/viewlyrics.php?id=");	
	private final String blogUser=MultiDB.blogUser;
	private final String blogPswd=MultiDB.blogPswd;
	private String bString= new String("band.php?id=");
	private String rString = new String("release.php?id=");
	
	private String HTMLText;
	private String groupId;
	private Disc groupInfo = new Disc();
	private JFrame selectFrame;
	private JTable groupsTable;
	private JButton button;
	private JScrollPane spTable;
	private static Object lock = new Object();
	private static boolean ended=false;
	private DefaultTableModel tableModel;
	private int selectedViewRow,selectedModelRow;
	
	
	public WebReader(){
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
		selectFrame = new JFrame("Select a group");
		selectFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		selectFrame.setLayout(new BoxLayout(selectFrame.getContentPane(),BoxLayout.Y_AXIS));
	    button = new JButton("Chose this group");
	    SelectGroupHandler selectGroupHandler = new SelectGroupHandler();
	    button.addActionListener(selectGroupHandler);
	    tableModel = new DefaultTableModel(); 
	    tableModel.addColumn("Group"); 
	    tableModel.addColumn("Id");
	    groupsTable=new JTable(tableModel){  
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row,int column){  
	    		     return false;  
	    		  }  
	    };  
	    groupsTable.removeColumn(groupsTable.getColumn("Id"));
	    spTable = new JScrollPane(groupsTable);
	    selectFrame.add(spTable);
	    selectFrame.add(button);
	}
	
	///main function, gets the discography from Encyclpaedia Metallum
	public ArrayList<Disc> getGroupInfo(String group){
		
		discList.clear();
		String id = getGroupId(group);
		HTMLText=getHTMLfromURL(webEMBand+id);
		groupInfo.reset();
		groupInfo.group=group;
		groupInfo=getGroupInfoFromHTML(HTMLText);
		if (groupInfo.group.compareTo("Group not found")==0){
			discList.clear();
		}else discList=getGroupDiscography(HTMLText,groupInfo);
		return discList;			
	}
	//get any HTML page from an URL
	public String getHTMLfromURL(String urltext){
		String ret = new String("");
		URL url = null;
		try {		
			url = new URL(urltext);
			BufferedReader in = null;	
			in = new BufferedReader(new InputStreamReader(url.openStream()));	
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				// Process each line.
				ret=ret+inputLine;
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return(ret);
	} 
		
	
	//get only groupInfo from HTML
	public Disc getGroupInfoFromHTML(String HTMLText){
		if (HTMLText.indexOf("Error 404")>0){
			groupInfo.group="Group not found";
			return groupInfo;
		}
		else{
			String st1=new String("<td colspan=\"4\">");
			String st2=new String("<td>");
			int pos1=HTMLText.indexOf("Genre(s)");
			pos1=HTMLText.indexOf(st1,pos1);
			int pos2=HTMLText.indexOf("</td>",pos1);
			groupInfo.style=HTMLText.substring(pos1+st1.length(),pos2);
			groupInfo.style=groupInfo.style.trim();
			pos1=HTMLText.indexOf("Status");
			pos1=HTMLText.indexOf("<td>",pos1);
			pos2=HTMLText.indexOf("</td>",pos1);
			groupInfo.loc=HTMLText.substring(pos1+st2.length(),pos2);
			groupInfo.loc=groupInfo.loc.replaceAll("\\<.*?\\>", "");
			groupInfo.loc=groupInfo.loc.trim();
		}
		return groupInfo;
	}

	///get discography from HTML page
	public ArrayList<Disc> getGroupDiscography(String HTMLText,Disc groupInfo){
		int posDisc=HTMLText.indexOf("Discography");
		int posPrev=posDisc;
		int posPost;
		Disc disc;
		while (posPrev>=posDisc){
			posPrev=HTMLText.indexOf("<a class='album'",posPrev);
			if (posPrev>posDisc){
				posPrev=HTMLText.indexOf(">",posPrev);
				posPost=HTMLText.indexOf("<",posPrev);			
				disc=new Disc();
				disc.group=groupInfo.group;
				disc.loc=groupInfo.loc;
				disc.style=groupInfo.style;
				disc.title=HTMLText.substring(posPrev+1,posPost);
				disc.title.trim();
				posPrev=HTMLText.indexOf("class='album'>",posPrev);
				posPrev=HTMLText.indexOf(",",posPrev);
				posPost=HTMLText.indexOf("<",posPrev);
				disc.year=HTMLText.substring(posPrev+1,posPost);
				disc.year.trim();
				discList.add(disc);
			}
		}
		return discList;
	}
	
	///get lyrics from group/disc
	public String getLyricsOfDisc(String group,String disc){
		String lyrics="";
		ArrayList<Disc> dList = new ArrayList<Disc>();
		String id = getGroupId(group);
		HTMLText=getHTMLfromURL(webEMBand+id);
		groupInfo.reset();
		groupInfo.group=group;
		groupInfo=getGroupInfoFromHTML(HTMLText);
		dList=getGroupDiscography(HTMLText,groupInfo);
		Disc discWeb = new Disc();
		boolean found=false;
		if (dList!=null){
			for (int indWeb=0;indWeb<dList.size();indWeb++){
				discWeb=dList.get(indWeb);
				if (String.CASE_INSENSITIVE_ORDER.compare(discWeb.title,disc)==0){
					found=true;
					break;												
				}
			}
		}
		if (found){
			id=getDiscIdFromHTML(discWeb.title,HTMLText);
			HTMLText=getHTMLfromURL(webEMRelease+id);
			int pos2=0;
			int pos3=0;
			int pos4=0;
			String st="javascript:openLyrics(";
			String st2="<center><h4>";
			String st3="</h4></center>";
			String st4="<center>[<a href=";
			int pos1=HTMLText.indexOf(st);
			int l1=st.length();
			String HTMLLyrpage="";
			String title="";
			while (pos1>-1){	
				pos2=HTMLText.indexOf(")", pos1);
				id=HTMLText.substring(pos1+l1, pos2);
				//once we have the lyrics page we parse the page to extract the lyrics
				HTMLLyrpage=getHTMLfromURL(webEMLyrics+id);
				pos3=HTMLLyrpage.indexOf(st2);
				pos4=HTMLLyrpage.indexOf(st3);
				title=HTMLLyrpage.substring(pos3+st2.length(),pos4); //extracting title
				lyrics=lyrics.concat(title.toUpperCase()+"\n\n");           //copying title and two linebreaks
				HTMLLyrpage=HTMLLyrpage.substring(pos4+st3.length());
				pos3=HTMLLyrpage.indexOf(st4);
				HTMLLyrpage=HTMLLyrpage.substring(0,pos3);
				HTMLLyrpage=HTMLLyrpage.replaceAll("<br>","\n");
				lyrics=lyrics.concat(HTMLLyrpage+"\n");
				HTMLText=HTMLText.substring(pos2);			
				pos1=HTMLText.indexOf(st);
			}
		}
		if (!found) lyrics="lyrics no found";
		return lyrics;
	}
	
	public String getDiscIdFromHTML(String disc,String HTMLText){
		String id="";
		int pos1=HTMLText.lastIndexOf(disc);
		if (pos1>-1){
			String pst=HTMLText.substring(0,pos1);
			pos1=pst.lastIndexOf(rString); 
			int pos2=pst.indexOf("'", pos1);
			id=HTMLText.substring(pos1+rString.length(), pos2);
		}
		return id;
	}
	
	public String getGroupId(String group){
		String id="";
		try {
			group=URLEncoder.encode(group,"UTF-8");		
			String webEMSearch2=webEMSearch+group+"&type=band";
			HTMLText=getHTMLfromURL(webEMSearch2);
			int pos1=HTMLText.indexOf(bString);
			int posOther=HTMLText.lastIndexOf(bString);
			int pos2;
			String[] currentGroup=new String[2];
			if ((pos1>-1)&(posOther==pos1)){
				pos2=HTMLText.indexOf("'", pos1);
				id=HTMLText.substring(pos1+bString.length(), pos2);
			}else if (pos1>-1){
				for (int itCol=0;itCol<tableModel.getRowCount();itCol++){
					tableModel.removeRow(itCol);
				}
				while (pos1!=posOther){
					pos2=HTMLText.indexOf("'>", pos1);
					id=HTMLText.substring(pos1+bString.length(), pos2);
					currentGroup[1]=id;
					pos1=HTMLText.indexOf("<", pos2);
					currentGroup[0]=HTMLText.substring(pos2+2, pos1);	
					tableModel.addRow(currentGroup);
					pos1=HTMLText.indexOf(bString, pos1);
					if (pos1<0) pos1=posOther;
				}
				ended=false;
				if (groupsTable.getRowCount()<40) selectFrame.setSize(500, groupsTable.getRowCount()*18+60);
				else selectFrame.setSize(500, 500);
				selectFrame.setVisible(true);
				synchronized(lock) {
                    if (!ended)
                    try {
                          lock.wait();
                    } catch (InterruptedException e) {
                                    e.printStackTrace();
                    }
				}
				id=groupId;
			}else return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	
	public boolean uploadBackup(String fileName,String webBUP){
		try {
			fileName=URLEncoder.encode(fileName,"UTF-8");		
			String webBUP2=webBUP+fileName+"&nick="+blogUser+"&pswd="+blogPswd;
			System.out.println(webBUP2);
			HTMLText=getHTMLfromURL(webBUP2);
			return HTMLText.contains("Data succesfully inserted");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			return false;
		}
	}
	
	///////////////////////INTERNAL CLASS FOR VIEWING GROUPS////////////////////////
	 private class SelectGroupHandler implements ActionListener {


	       public void actionPerformed(ActionEvent evento) {
	    	   ListSelectionModel lsm = (ListSelectionModel) groupsTable.getSelectionModel();
	           if (!lsm.isSelectionEmpty()){

	               selectedViewRow = lsm.getMinSelectionIndex();
	               selectedModelRow = groupsTable.convertRowIndexToModel(selectedViewRow);
		           groupId=(String)tableModel.getValueAt(selectedModelRow, 1);
		           selectFrame.dispose();
		           synchronized(lock) {
	                   ended=true;
	                   lock.notify();
					}
	           }
	       }
	   } 
	
}

