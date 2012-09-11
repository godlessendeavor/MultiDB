package music.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import main.Errors;
import main.MultiDB;

import org.htmlparser.Node;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebReader {

	private static final String blogUser=MultiDB.blogUser;
	private static final String blogPswd=MultiDB.blogPswd;
	
	
	public WebReader(){
		
	}
	
	//get any HTML page from an URL
	public static String getHTMLfromURL(String urltext){
		String ret = new String("");
		URL url = null;
		try {		
			url = new URL(urltext);
			BufferedReader in = null;	
			in = new BufferedReader(new InputStreamReader(url.openStream(),"UTF8"));	
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				// Process each line.
				ret=ret+inputLine;
			}
			in.close();
			//System.out.println(url.getResponseCode());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return("Error");
		} catch (IOException e) {
			e.printStackTrace();
			return("400");
		}
		return(ret);
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
				node=i.nextNode();
				if (node.toHtml().contains("name")) System.out.println(node.toHtml());
			    if (node instanceof TagNode){
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
	
	
	public static String HTML2PlainText(String HTMLText){
		Document document = Jsoup.parse(HTMLText);
		document.select("br").append("\\n");
	    document.select("p").prepend("\\n\\n");
	    return document.text().replaceAll("\\\\n", "\n");
	}
//	//get any HTML page from an URL
//	public String getHTMLfromURL2(String urltext){
//		String ret = new String("");
//		BufferedReader reader = null;
//		URL url = null;
//		try {		
//			url = new URL(urltext);
//			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            //connection.setDoOutput(true);
//            connection.setRequestMethod("GET");
//            HttpURLConnection.setFollowRedirects(false);
//            connection.setReadTimeout(15*1000);
//            connection.connect();
//
//            // read the output from the server
//            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//            String line = null;
//            while ((line = reader.readLine()) != null){
//            	ret=ret+line;
//            }
//            System.out.println(connection.getResponseCode());
//            
//         }
//          catch (Exception e){
//            e.printStackTrace();
//          }
//          finally{
//            // close the reader; this can throw an exception too, so
//            // wrap it in another try/catch block.
//            if (reader != null){
//              try{
//                reader.close();
//              }
//              catch (IOException ioe){
//                ioe.printStackTrace();
//              }
//            }
//          }
//          return ret;
//           
//	} 	
	

	public static int uploadBackup(String fileName,String webBUP){
		try {
			fileName=URLEncoder.encode(fileName,"UTF-8");	
			String webBUP2=webBUP+fileName+"&nick="+blogUser+"&pswd="+blogPswd;
			
			String HTMLText=getHTMLfromURL(webBUP2);
			System.out.println(HTMLText);
			if (HTMLText.contains("Data succesfully inserted")) return 0;
			else return Errors.UPLOADING_BUP;
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			return Errors.UPLOADING_BUP;
		}
	}
	

	
}

