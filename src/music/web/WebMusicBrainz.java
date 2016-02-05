package music.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import main.MultiDB;
import music.db.Disc;

import org.apache.commons.lang.StringEscapeUtils;

import web.WebReader;

public class WebMusicBrainz {
	private JFrame selectFrame;
	private JTable groupsTable;
	private static final String webMBSearch=MultiDB.webMBSearch;
	public static final String webMBBand=MultiDB.webMBBand;
	private static Object lock = new Object();
	private static boolean ended=false;
	private DefaultTableModel tableModel;
	private String mbString = new String("<a href=\"/artist/");
	public String groupId;
	
	public WebMusicBrainz(){
		
	}
	
	public String getGroupId(String group,String web){
		String id="";
		String[] currentGroup=new String[2];
		
			try {
				group=URLEncoder.encode(group,"UTF-8");		
				String webMBSearch2=webMBSearch+group+"&handlearguments=1";
				String HTMLText=WebReader.getHTMLfromURL(webMBSearch2);
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
			
		
		return id;
	}
	
	public ArrayList<Disc> getGroupDiscography(String HTMLText,Disc groupInfo,String web,String id){
		ArrayList<Disc> discList = new ArrayList<Disc>();
		int posPost=-1, posPrev=-1, posOther=-1;
		
		Disc disc;
		selectFrame.setTitle("Select a group for "+groupInfo.group);
		
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
		
		return discList;
	}
}
