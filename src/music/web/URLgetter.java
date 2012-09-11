package music.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import json.JSONArray;
import json.JSONObject;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class URLgetter {

	protected static final String WEBEMURLSearchGroupInfo="http://www.metal-archives.com/search/ajax-band-search/?field=name&iDisplayStart=&sEcho&query=";
	protected static final String WEBEMURLSearchGroupDiscography1="http://www.metal-archives.com/band/discography/id/";
	protected static final String WEBEMURLSearchGroupDiscography2="/tab/main";
	

	
	public static GroupInfo getGroupInfo(String name){
		URL web;
		String inputLine;
		String output="";
		String link,id;
		GroupInfo groupInfo=null;
		ArrayList<GroupInfo> groups = new ArrayList<GroupInfo>();

		
		try {
			web = new URL(WEBEMURLSearchGroupInfo+name);
		
			BufferedReader in;
			InputStream inputStream= web.openStream();
			
			in = new BufferedReader(new InputStreamReader(inputStream));
					
			while ((inputLine = in.readLine()) != null){
			    //System.out.println(inputLine);
			    output=output+inputLine+"\n";
			}
			JSONObject job = new JSONObject(output);
			int nofGroups = job.getInt("iTotalRecords");
			JSONArray list = job.getJSONArray("aaData");
			for (int i=0;i<nofGroups;i++){
				JSONArray data = list.getJSONArray(i);
				link = parseLinkGroup(data.getString(0));
				id = link.substring(link.lastIndexOf("/")+1);
				groupInfo = new GroupInfo(name, link, data.getString(1), data.getString(2),id.trim());
				groups.add(groupInfo);
			}
			
			//select one of the groups and return value
			/////////////////////////////////////////////////////////////////////////////////////////
			//SELECT GROUP!!!!!!!!!!!!!!!!!!!!!
			
			in.close();
			return groupInfo;
		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static String parseLinkGroup(String line){
		Parser parser;
		Node node;
		String href=null;
		TagNode tag;
		
		try {
			parser = new Parser(line);
		
			NodeList nl = parser.parse (null); 
			NodeList nodes = nl.extractAllNodesThatMatch(new TagNameFilter ("a"));
			if (nodes.size()>0)
	             for (NodeIterator i = nl.elements (); i.hasMoreNodes (); )
	                 if ((node=i.nextNode()) instanceof TagNode){
	                	tag  = (TagNode) node;	
	                	href = tag.getAttribute("href");
	                	break;
	                 }
			return href;  //return
       	     // output the modified HTML
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	//gets all tag nodes of list entryList who matches type
	//if inside is marked then also nodes inside nodes are searched (tables into tables for example)
	public static NodeList getTagNodesOfType (NodeList entryList,String type,boolean inside){
		NodeList list=new NodeList();
		NodeList currentList = null;
		Node node;
		NodeList aux;
		
		try {		
			for (NodeIterator i = entryList.elements (); i.hasMoreNodes (); ){
			    if ((node=i.nextNode()) instanceof TagNode){
			    	 currentList=new NodeList();
			         TagNode tag = (TagNode)node;
			         // process recursively (nodes within nodes) via getChildren()
			         //System.out.println(tag.toHtml());
			         //System.out.println("...............               ...................");
			         //boolean found=false;
			         if (tag.getTagName().toLowerCase().compareTo(type)==0) {
			        	 //System.out.println("Adding tag "+tag.toHtml());
			        	 list.add(tag);
			        	 if (inside){
			        		 aux = tag.getChildren();
					         if (aux!=null){ 			        	 
					        	 currentList = aux.extractAllNodesThatMatch(new TagNameFilter(type));
					             if (currentList.size()==0) currentList.add(getTagNodesOfType(aux,type,inside));
					             list.add(currentList);
					         }
			        	 }
			         }
			         else{
			        	 
				         aux = tag.getChildren();
				         if (aux!=null){ 			        	 
				        	 currentList = aux.extractAllNodesThatMatch(new TagNameFilter(type));
				             if (currentList.size()==0) currentList.add(getTagNodesOfType(aux,type,inside));
				             list.add(currentList);
				         }
			         }
			     }	
			    }
			
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(list);
	 }

	
	
	public static ArrayList<WebDiscInfo> getDiscography(String webURL){
		URL web;
		String output="";
		Parser parser;
		//Double year = -1.0;
		WebDiscInfo disc;
		ArrayList<WebDiscInfo> discList = new ArrayList<WebDiscInfo>(); 
		
		try {
			web = new URL(webURL);
		
			InputStream inputStream= web.openStream();
			//File file= new File("C:\\Users\\thrasher\\workspace\\TestURL\\src\\url\\main.txt");
			//in = new BufferedReader(new InputStreamReader(inputStream));
			if (inputStream!=null){
				  
		 			  parser = new Parser(output);
		 				
		 			  NodeList nl = parser.parse(null); 
		 			  NodeList trnodes=getTagNodesOfType(nl,"tr",false);
		 			  //System.out.println(trnodes.size());
		 			  Node node;
		 			  NodeList links;
		 			  NodeList tdnodes;
		 			  for (NodeIterator i = trnodes.elements (); i.hasMoreNodes (); ){
		 				  disc = null;
		 				  if ((node=i.nextNode()) instanceof TagNode){
			 			    	links=getTagNodesOfType(((TagNode)node).getChildren(),"a",false);
			 			    	if (links.size()>0){    //node tr has a link for album
			 			    		for (NodeIterator itlinks = links.elements (); itlinks.hasMoreNodes (); ){
			 			    			String href = ((TagNode)itlinks.nextNode()).getAttribute("href");
			 			    			if (href!=null){
				 			    			if (href.contains("albums")){
				 			    				//add link for album to class Disco
				 			    				disc = new WebDiscInfo();
				 			    				disc.setLink(href);
				 			    			}	
				 			    			if (href.contains("review")){
				 			    				//add link for album to class Disco
				 			    				disc = new WebDiscInfo();
				 			    				disc.setReview(href);
				 			    			}
			 			    			}	
			 			    		}
		 			    			tdnodes=getTagNodesOfType(((TagNode)node).getChildren(),"td",false);
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
		 			    			if (disc!=null) discList.add(disc);
		 			    		}		 			    	
			 			    	//System.out.println(node.toHtml());
			 			    		
			 			    		
			 			        
			 			    	
			 			    	
		 				    }
		 			    	
		 			  }
		 			  //System.out.println("Size is "+trnodes.size());
		 		 
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

}
