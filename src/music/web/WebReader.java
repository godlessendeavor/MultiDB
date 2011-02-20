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

import org.apache.commons.lang.StringEscapeUtils;

import main.MultiDB;
import music.db.Disc;

public class WebReader {
	private ArrayList<Disc> discList=new ArrayList<Disc>();
	private final String webEMSearch=MultiDB.webEMSearch;// new String("http://www.metal-archives.com/search.php?string=");
	private final String webEMSearchAdvanced="http://www.metal-archives.com/advanced.php?band_name=";
	private final String webEMBand=MultiDB.webEMBand; // new String("http://www.metal-archives.com/band.php?id=");
	private final String webEMRelease=MultiDB.webEMRelease; //new String("http://www.metal-archives.com/release.php?id=");
	private final String webEMLyrics=MultiDB.webEMLyrics; //new String("http://www.metal-archives.com/viewlyrics.php?id=");
	private final String webMBSearch=MultiDB.webMBSearch;
	private final String webMBBand=MultiDB.webMBBand;
	private final String blogUser=MultiDB.blogUser;
	private final String blogPswd=MultiDB.blogPswd;
	private String bString= new String("band.php?id=");
	private String rString = new String("release.php?id=");
	private String mbString = new String("<a href=\"/artist/");
	
	private String HTMLText;
	private String groupId;
	private Disc groupInfo = new Disc();
	private JFrame selectFrame;
	private JTable groupsTable;
	private JButton chbutton;
	private JButton nbutton;
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
		selectFrame = new JFrame("Select a group for");
		selectFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		selectFrame.setLayout(new BoxLayout(selectFrame.getContentPane(),BoxLayout.Y_AXIS));
	    chbutton = new JButton("Chose this group");	    
	    SelectGroupHandler selectGroupHandler = new SelectGroupHandler();
	    chbutton.addActionListener(selectGroupHandler);
	    nbutton = new JButton("None of them!");
	    NoneGroupHandler noneGroupHandler = new NoneGroupHandler();
	    nbutton.addActionListener(noneGroupHandler);
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
	    selectFrame.add(chbutton); 
	    selectFrame.add(nbutton);
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
			return("400");
		}
		return(ret);
	} 	
	
	
	
	///gets the discography from Encyclpaedia Metallum or MusicBrainz
	public ArrayList<Disc> getGroupInfo(String group,String Web){
		while(tableModel.getRowCount()>0) {
			tableModel.removeRow(0);
		}
		discList.clear();
		String id = getGroupId(group,Web);
		if (id!=null){
			if (Web.compareTo("webEM")==0) HTMLText=getHTMLfromURL(webEMBand+id);
			else HTMLText=getHTMLfromURL(webMBBand+id); //web MuiscBrainz
		}
		
		groupInfo.reset();
		groupInfo.group=group;
		if (Web.compareTo("webEM")==0) groupInfo=getGroupInfoFromHTML(HTMLText);
		if (groupInfo.group.compareTo("Group not found")==0){
			discList.clear();
		}else discList=getGroupDiscography(HTMLText,groupInfo,Web);
		return discList;			
	}
			
	
	//get only groupInfo from HTML
	public Disc getGroupInfoFromHTML(String HTMLText){
		if ((HTMLText.indexOf("Error 404")>0)||(HTMLText.indexOf("No results found")>0)){
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
			groupInfo.style=groupInfo.loc.replaceAll("\\<.*?\\>", "");			
			groupInfo.style=StringEscapeUtils.unescapeHtml(groupInfo.style);
			pos1=HTMLText.indexOf("Status");
			pos1=HTMLText.indexOf("<td>",pos1);
			pos2=HTMLText.indexOf("</td>",pos1);
			groupInfo.loc=HTMLText.substring(pos1+st2.length(),pos2);
			groupInfo.loc=groupInfo.loc.trim();
			groupInfo.loc=groupInfo.loc.replaceAll("\\<.*?\\>", "");			
			groupInfo.loc=StringEscapeUtils.unescapeHtml(groupInfo.loc);
		}
		return groupInfo;
	}
	///get discography from HTML page
	public ArrayList<Disc> getGroupDiscography(String HTMLText,Disc groupInfo,String web){
			
		int posPost=-1, posPrev=-1, posDisc=-1, posOther=-1;
		
		Disc disc;
		selectFrame.setTitle("Select a group for "+groupInfo.group);
		if (web.compareTo("webEM")==0){			
			posDisc=HTMLText.indexOf("Discography");
			posPrev=posDisc;
			
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
					disc.title=disc.title.trim();
					disc.title=disc.title.replaceAll("\\<.*?\\>", "");			
					disc.title=StringEscapeUtils.unescapeHtml(disc.title);
					posPrev=HTMLText.indexOf("class='album'>",posPrev);
					posPrev=HTMLText.indexOf(",",posPrev);
					posPost=HTMLText.indexOf("<",posPrev);
					disc.year=HTMLText.substring(posPrev+1,posPost);
					disc.year.trim();
					discList.add(disc);
				}
			}	
		}else{   //si entramos en MUSICBRAINZ
			String bseek="<td class=\"rdate\">";   
			posPrev=HTMLText.indexOf(bseek,posPrev);               //buscamos ya el primero
			
			while(posPrev>-1){  //mientras encontremos LP's
				posPost=HTMLText.indexOf("</td>",posPrev);			
				disc=new Disc();
				disc.group=groupInfo.group;
				disc.year=HTMLText.substring(posPrev+bseek.length(),posPost);
				disc.year.trim();
				posPrev=HTMLText.indexOf("<a href=\"/release-group/",posPost);
				posPrev=HTMLText.indexOf(">",posPrev);
				posPost=HTMLText.indexOf("</a>",posPrev);
				disc.title=HTMLText.substring(posPrev+1,posPost);
				disc.title.trim();
				disc.title=disc.title.replaceAll("\\<.*?\\>", "");			
				disc.title=StringEscapeUtils.unescapeHtml(disc.title);
				discList.add(disc);
				posPrev=HTMLText.indexOf(bseek,posPost)+1; //desplazo para no encontrarme con este innecesario tag
				posOther=HTMLText.indexOf("<th",posPrev);  //observo si es el ultimo antes de llegar a EP's o singles
				if (HTMLText.indexOf(bseek,posPrev)<posPrev) break;
			    else posPrev=HTMLText.indexOf(bseek,posPrev);			    
			    if ((posOther>-1)&&(posOther<posPrev)) {
			    	break;			    	
			    }
			    HTMLText=HTMLText.substring(posPrev);	
			    posPrev=0;	  
			    
			}
		}
		return discList;
	}
	
	///get lyrics from group/disc
	public String getLyricsOfDisc(String group,String disc){
		String lyrics="";
		ArrayList<Disc> dList = new ArrayList<Disc>();
		String id = getGroupId(group,"webEM");
		HTMLText=getHTMLfromURL(webEMBand+id);
		groupInfo.reset();
		groupInfo.group=group;
		groupInfo=getGroupInfoFromHTML(HTMLText);
		dList=getGroupDiscography(HTMLText,groupInfo,"webEM");
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
	
	public String getGroupId(String group,String web){
		String id="";
		String[] currentGroup=new String[2];
		if (web.compareTo("webEM")==0){
			try {
				System.out.println(group);
				group=URLEncoder.encode(group,"UTF-8");		
				String webEMSearch2=webEMSearch+group+"&type=band";
				System.out.println(webEMSearch2);
				HTMLText=getHTMLfromURL(webEMSearch2);
				if (HTMLText.compareTo("400")!=0){
					int pos1=HTMLText.indexOf(bString);
					int posOther=HTMLText.lastIndexOf(bString);
					int pos2;
					System.out.println(HTMLText);
					System.out.println("pos1 is: "+pos1+" and we search  for "+bString);
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
						if (groupsTable.getRowCount()<40) selectFrame.setSize(500, groupsTable.getRowCount()*20+60);
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
				}else{
					System.out.println("Entering here but nothing done");
						webEMSearch2=webEMSearchAdvanced+group;
						System.out.println(webEMSearch2);
						HTMLText=getHTMLfromURL(webEMSearch2);
						int pos1=HTMLText.indexOf(bString);
						int posOther=HTMLText.lastIndexOf(bString);
						int pos2;
						System.out.println(HTMLText);
						System.out.println("pos1 is: "+pos1+" and we search  for "+bString);
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
							if (groupsTable.getRowCount()<40) selectFrame.setSize(500, groupsTable.getRowCount()*20+60);
							else selectFrame.setSize(500, 500);
							selectFrame.setVisible(true);
							synchronized(lock) {
			                    if (!ended)
			                    try {
			                          lock.wait();
			                    } catch (InterruptedException ex) {
			                                  ex.printStackTrace();
			                    }
							}
							id=groupId;
						}else return null;			
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{ ///////////////////search on MUSICBRAINZ
			try {
				group=URLEncoder.encode(group,"UTF-8");		
				String webMBSearch2=webMBSearch+group+"&handlearguments=1";
				HTMLText=getHTMLfromURL(webMBSearch2);
				int pos1=HTMLText.indexOf(mbString);
				int pos2=HTMLText.indexOf("<tr class=\"searchresult");
				int count=0;
				do{                   // contamos cuantos grupos coinciden al 100% con el nombre
					pos1=HTMLText.indexOf("<td>100</td>",pos2);
					if (pos1<pos2) break;
					count++;
					pos2=pos1+1;
				}while(true);
				pos1=HTMLText.indexOf(mbString);
				for (int cont=1;cont<=count;cont++){          //si hay algun grupo conicidente 100% con el nombre					
					int posName=HTMLText.indexOf("\">",pos1);     
					pos2=HTMLText.indexOf("<",posName);
					id=HTMLText.substring(pos1+mbString.length(), posName);
					if (count==1)  return id;    //si solo hay uno entonces ya solo retornamos el id						
					currentGroup[1]=id;
					pos2=HTMLText.indexOf("</td>",posName);
					currentGroup[0]=HTMLText.substring(posName+2, pos2);	
					currentGroup[0].trim();
					currentGroup[0]=currentGroup[0].replaceAll("\\<.*?\\>", "");			
					currentGroup[0]=StringEscapeUtils.unescapeHtml(currentGroup[0]);
					tableModel.addRow(currentGroup);
					pos1=HTMLText.indexOf(mbString,pos2);
				}
				if (groupsTable.getRowCount()>0){
					ended=false;
					if (groupsTable.getRowCount()<40) selectFrame.setSize(500, groupsTable.getRowCount()*18+120);
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
	
	///////////////////////INTERNAL CLASS FOR BUTTON SELECT GROUPS////////////////////////
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
	 ///class for button if there's no group
	 private class NoneGroupHandler implements ActionListener {


	       public void actionPerformed(ActionEvent evento) {
	    	   groupId=null;
		       selectFrame.dispose();
		       synchronized(lock) {
	                ended=true;
	                lock.notify();
		       }
	       }
	   } 
	
}

