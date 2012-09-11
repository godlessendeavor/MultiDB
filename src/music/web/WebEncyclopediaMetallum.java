package music.web;

import java.net.URLEncoder;
import java.text.Collator;
import java.util.ArrayList;

import json.JSONArray;
import json.JSONObject;
import main.Errors;
import music.db.Disc;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import web.WebReader;

public class WebEncyclopediaMetallum {

	protected static final String WEBEMURLSearchGroupInfo="http://www.metal-archives.com/search/ajax-band-search/?field=name&iDisplayStart=&sEcho&query=";
	protected static final String WEBEMURLSearchGroupDiscography1="http://www.metal-archives.com/band/discography/id/";
	protected static final String WEBEMURLSearchGroupDiscography2="/tab/main";
	protected static final String WEBEMURLSearchLyrics="http://www.metal-archives.com/release/ajax-view-lyrics/id/";

	public String groupId;
	public String web;
	
	
	public WebEncyclopediaMetallum(){

	}
		
	public static ArrayList<Disc> getGroupDiscography(String id,String group,String location,String style){
	
		Parser parser;
		//Double year = -1.0;
		Disc disc;
		ArrayList<Disc> discList = new ArrayList<Disc>(); 
		
		try {
			String webURL=WEBEMURLSearchGroupDiscography1+id;
			String HTMLText = WebReader.getHTMLfromURL(webURL);
			if (HTMLText.compareTo("400")!=0){
				  
		 			  parser = new Parser(HTMLText);
		 				
		 			  NodeList nl = parser.parse(null); 
		 			  NodeList trnodes=WebReader.getTagNodesOfType(nl,"tr",false);
		 			  //System.out.println(trnodes.size());
		 			  Node node;
		 			  NodeList links;
		 			  NodeList tdnodes;
		 			  for (NodeIterator i = trnodes.elements (); i.hasMoreNodes (); ){
		 				  disc = null;
		 				  if ((node=i.nextNode()) instanceof TagNode){
			 			    	links=WebReader.getTagNodesOfType(((TagNode)node).getChildren(),"a",false);
			 			    	if (links.size()>0){    //node tr has a link for album
			 			    		disc = new Disc();
			 			    		for (NodeIterator itlinks = links.elements (); itlinks.hasMoreNodes (); ){
			 			    			String href = ((TagNode)itlinks.nextNode()).getAttribute("href");
			 			    			if (href!=null){
				 			    			if (href.contains("albums")){
				 			    				//add link for album to class Disco
				 			    				disc.setLink(href);
				 			    			}	
				 			    			if (href.contains("review")){
				 			    				//add link for album to class Disco
				 			    				disc.setReview(href);
				 			    			}
			 			    			}	
			 			    		}

		 			    			tdnodes=WebReader.getTagNodesOfType(((TagNode)node).getChildren(),"td",false);
		 			    			if (disc!=null){
			 			    			disc.setGroup(group);
			 			    			disc.setStyle(style);
			 			    			disc.setLoc(location);
			 			    			disc.setTitle(tdnodes.elementAt(0).toPlainTextString());
			 			    			disc.setType(tdnodes.elementAt(1).toPlainTextString());
				    					disc.setYear(tdnodes.elementAt(2).toPlainTextString());
				    					String mark=tdnodes.elementAt(3).toPlainTextString();
				    					int perc = mark.indexOf("%");
				    					if (perc>-1){
				    						int paren = mark.indexOf("(");
				    						if (paren>-1){
				    							try{
				    			            		Double dmark=Double.valueOf(mark.substring(paren+1,perc)).doubleValue();
				    			            		dmark=dmark/10.0;
				    			            		disc.setMark(dmark.toString());
				    			            	}catch(NumberFormatException ex){
				    			            	}
				    						}
				    					}
				    					 
				    					discList.add(disc);
				    					
		 			    			}
		 			    		}		 			    	
			 			    	//System.out.println(node.toHtml());
		 				    }
		 			  }
		 			 
		 		 return discList;
			}
			
			
//			if (trnodes.size()>0)
//				
//	             for (NodeIterator i = trnodes.elements (); i.hasMoreNodes (); )
//	                 if ((node=i.nextNode()) instanceof TagNode){
//	                	tdnodes = trnodes.extractAllNodesThatMatch(new TagNameFilter ("td"));
//	                	//System.out.println("Got here");
//	                	if (tdnodes.size()>0)
//	       	             for (NodeIterator ittd = tdnodes.elements (); ittd.hasMoreNodes (); )
//	       	                 if ((node=ittd.nextNode()) instanceof TagNode){
//	       	                	NodeList anodes = nl.extractAllNodesThatMatch(new TagNameFilter ("a"));
//	       	                	//System.out.println("Got here");
//	       	                	if (anodes.size()>0){
//		       	                	 for (NodeIterator ita = anodes.elements (); ita.hasMoreNodes (); )
//		       	                		 if ((node=ita.nextNode()) instanceof TagNode){
//		       	                			 tag=(TagNode)node;
//		       	                			 String href = tag.getAttribute("href");
//		       	                			 System.out.println(href);
//		       	                		 }
//		       	                			 
//	       	                	}
//	       	                 }
//	                 }
			
			//in.close();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (e instanceof ParserException) System.out.println("Error parsing");
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static TagNode parseLinkGroup(String line){
		Parser parser;
		Node node;
		TagNode tag=null;
		
		try {
			parser = new Parser(line);
		
			NodeList nl = parser.parse (null); 
			NodeList nodes = nl.extractAllNodesThatMatch(new TagNameFilter ("a"));
			if (nodes.size()>0)
	             for (NodeIterator i = nl.elements (); i.hasMoreNodes (); )
	                 if ((node=i.nextNode()) instanceof TagNode){
	                	tag  = (TagNode) node;	
	                	break;
	                 }
			return tag;  //return
       	     // output the modified HTML
		} catch (ParserException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static ArrayList<GroupInfo> getGroupList(String groupName){
	
		String link,id,group,groupNameEnc;
		TagNode linkTag;
		GroupInfo groupInfo=null;
		ArrayList<GroupInfo> groups = new ArrayList<GroupInfo>();
		Collator comparador = Collator.getInstance();
	    comparador.setStrength(Collator.PRIMARY);
		
		try {
			groupNameEnc=URLEncoder.encode(groupName,"UTF-8");
			String HTMLText = WebReader.getHTMLfromURL(WEBEMURLSearchGroupInfo+groupNameEnc);
			
			JSONObject job = new JSONObject(HTMLText);
			int nofGroups = job.getInt("iTotalRecords");
			JSONArray list = job.getJSONArray("aaData");
			for (int i=0;i<nofGroups;i++){
				JSONArray data = list.getJSONArray(i);
				linkTag = parseLinkGroup(data.getString(0));
				if (linkTag!=null){
					link = linkTag.getAttribute("href");
					id = link.substring(link.lastIndexOf("/")+1);
					group=linkTag.getChildren().elementAt(0).getText();
					if(comparador.compare(group, groupName)==0){
						groupInfo = new GroupInfo(group, link, data.getString(1), data.getString(2),id.trim());
						groups.add(groupInfo);
						}					
				}			
			}
		
			return groups;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static String getLyricsOfDisc(String group,String disc){
		ArrayList<GroupInfo> groups = new ArrayList<GroupInfo>();
		ArrayList<Disc> discs = new ArrayList<Disc>();
		Collator comparador = Collator.getInstance();
	    comparador.setStrength(Collator.PRIMARY);
	    Parser parser;
	    Node node;
	    String attr,href,lyr="";   
		
		groups=getGroupList(group);
		if (groups.size()==0){
			Errors.showError(Errors.GROUP_NOT_FOUND);
		}
		for (int i=0;i<groups.size();i++){
			discs.clear();
	        discs=getGroupDiscography(groups.get(i).id,"","","");
	        for (int j=0;j<discs.size();j++){
	        	//System.out.println(discs.get(j).link);
	        	if (comparador.compare(discs.get(j).title,disc)==0){
	        		String HTMLText = WebReader.getHTMLfromURL(discs.get(j).link);
	        		//System.out.println(discs.get(j).link);
	        		try {
						parser = new Parser(HTMLText);
						NodeList nl = parser.parse(null); 
		        		NodeList anodes = WebReader.getTagNodesOfType(nl,"a",false);
		        		for (NodeIterator k = anodes.elements (); k.hasMoreNodes (); ){
			 				  if ((node=k.nextNode()) instanceof TagNode){
			 					  	attr=null;
				 			    	attr = ((TagNode)node).getAttribute("onclick");
				 			    	if (attr!=null){
				 			    		if (attr.contains("toggleLyrics")){
				 			    			href = ((TagNode)node).getAttribute("href");
				 			    			int posg = href.lastIndexOf("=");
				 			    			int posa = href.lastIndexOf("#");
				 			    			String id = href.substring(posg+1,posa);
				 			    			String title = getTitle4id(id,nl);
				 			    			HTMLText = WebReader.getHTMLfromURL(WEBEMURLSearchLyrics+id.trim());
				 			    			HTMLText = WebReader.HTML2PlainText(HTMLText);
				 			    			lyr=lyr+title+"\n\n";
				 			    			lyr=lyr+HTMLText;
				 			    			lyr=lyr+("\n\n ----------------------------------------------\n\n"); 
				 			    			//href is like this
				 			    			//href="http://www.metal-archives.com/release/view/id/3954/songId/35900#35900"
				 			    			//get the id (35900) and ad to WEBEMURLSearchLyrics
				 			    		}
				 			    	}
			 				  }
		        		}
		        		///getting lyrics
					} catch (ParserException e) {
						e.printStackTrace();
					}	    				      		  	
	        		break;
	        	}
		        
			}
		}
		return lyr;
	}
	
	private static String getTitle4id(String id, NodeList nl){ //gets the title of song in disc page
		String attr;
		Node node;
		NodeList aux;
		
		NodeList tdnodes = WebReader.getTagNodesOfType(nl,"td",true);
		try {
			for (NodeIterator td = tdnodes.elements (); td.hasMoreNodes (); ){
					  if ((node=td.nextNode()) instanceof TagNode){

							
						aux=WebReader.getTagNodesOfType(node.getChildren(),"a",false);
						for (NodeIterator a = aux.elements (); a.hasMoreNodes (); ){
							  if ((node=a.nextNode()) instanceof TagNode){
								attr=null;
								//attr = ((TagNode)node).getAttribute("title");
								//System.out.println(((TagNode)node).getText());
								//if (attr==null) 
								attr = ((TagNode)node).getAttribute("name");
								//System.out.println(((TagNode)node).getText());
						    	if (attr!=null){
		 			    			//System.out.println(attr+" "+id);
						    		if (attr.contains(id)){
						    			  node = td.nextNode(); //follow to next node which has the title
						    			  return node.toPlainTextString().trim();
						    			}
						    		}
							  }
						}
				    	
					  }
			}
			return "No title";
		} catch (ParserException e) {
			e.printStackTrace();
			return "No title";
		}
	}
	
}
