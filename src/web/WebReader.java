package web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import main.Errors;
import main.MultiDB;

import org.apache.commons.codec.binary.Base64;
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
	
	//get any HTML page from an URL, specifying encoding
	public static String getHTMLfromURL(String urltext,String encoding){
		String ret = new String("");
		URL url = null;
		try {		
			url = new URL(urltext);
			URLConnection conn = url.openConnection();  
			conn.connect();  
			String contentEncoding = conn.getContentEncoding();
			if (contentEncoding==null) contentEncoding=encoding;
			BufferedReader in = null;	
			in = new BufferedReader(new InputStreamReader(url.openStream(),contentEncoding));	
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
	
	//get any HTML page from an URL, UTF8 encoding default
	public static String getHTMLfromURL(String urltext){
		return(getHTMLfromURL(urltext,"UTF8"));
	}
	
	
	//get any HTML page from an URL, specifying encoding
	public static String getHTMLfromURLHTTPS(String urltext,String accountKey){
			String ret = new String("");
			try {		
				byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
			    String accountKeyEnc = new String(accountKeyBytes);
			    URL urlb = new URL(urltext);
			    URLConnection urlConnection =urlb.openConnection();
			    urlConnection.setRequestProperty("Authorization","Basic " + accountKeyEnc);
			            
			    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) 
					ret=ret+inputLine;
				//System.out.println(HTMLText);
				in.close();				
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return("Error");
			} catch (IOException e) {
				e.printStackTrace();
				return("400");
			}
			return(ret);
		} 	
	
	
	//get any HTML page from an URL, specifying encoding
	public static String postHTMLfromURL(String urltext,String urlParameters){
		String ret="";
		try {
			URL url = new URL(urltext);		
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false); 
			connection.setRequestMethod("POST"); 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches (false);
	
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
			wr.writeBytes(urlParameters);
			 // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    String inputLine;
		    while ((inputLine = rd.readLine()) != null) {
				ret=ret+inputLine;
		    }			
			wr.flush();
			wr.close();
			connection.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return("Error");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return("400");
		} 
		return ret;
	} 	
	
	public static InputStream decompressStream(InputStream is){
		try {

			GZIPInputStream gs = new GZIPInputStream(is);
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
			byte[] buffer = new byte[1024]; 
			while (gs.available()!=0) { 
				int count = gs.read(buffer); 
				if (count > 0) outputStream.write(buffer, 0, count); 
			} 
			outputStream.close(); 
			InputStream decodedInput=new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
			return decodedInput;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Errors.writeError(Errors.GENERIC_STACK_TRACE,"Error while decompressing stream");
			//e.printStackTrace();
		}
		return null;
	}
	
	
	//gets all tag nodes of list entryList who matches type
	//if inside is true then also nodes inside nodes are searched (tables into tables for example)
	public static NodeList getTagNodesOfType (NodeList entryList,String type,boolean inside){
		NodeList list=new NodeList();
		NodeList currentList = null;
		Node node;
		NodeList aux;
		
		try {		
			for (NodeIterator i = entryList.elements (); i.hasMoreNodes (); ){
				node=i.nextNode();
				//if (node.toHtml().contains("name")) System.out.println(node.toHtml());
			    if (node instanceof TagNode){
			    	 currentList=new NodeList();
			         TagNode tag = (TagNode)node;
			         // process recursively (nodes within nodes) via getChildren()
			         //System.out.println(tag.toHtml());
			         //System.out.println("...............               ...................");
			         if (tag.getTagName().toLowerCase().compareTo(type)==0) {
			        	 //System.out.println("Adding tag "+tag.toHtml());
			        	 list.add(tag);
			        	 if (inside){
			        		 aux = tag.getChildren();
					         if (aux!=null){ 
					             currentList=getTagNodesOfType(aux,type,inside);
					             list.add(currentList);
					         }
			        	 }
			         }
			         else{			        	 
				         aux = tag.getChildren();
				         if (aux!=null){ 			        	 
				        	 currentList = aux.extractAllNodesThatMatch(new TagNameFilter(type));
				             if (inside) currentList=getTagNodesOfType(aux,type,inside);
				             else if (currentList.size()==0) currentList.add(getTagNodesOfType(aux,type,inside));
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

