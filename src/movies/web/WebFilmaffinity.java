package movies.web;

import java.net.URLEncoder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import movies.db.Movie;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import web.WebReader;

public class WebFilmaffinity {

	protected static final String WEFAURLSearchMovieInfo="http://www.filmaffinity.com/es/search.php?stype=title&stext=";
	public String movieId;
	public String web;
	
	
	public WebFilmaffinity(){

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
	
	public static ArrayList<Movie> getMovieList(String title){
	
		Movie movieInfo=null;
		ArrayList<Movie> movies = new ArrayList<Movie>();
		Collator comparador = Collator.getInstance();
	    comparador.setStrength(Collator.TERTIARY);
	    Parser parser;
	    Node nodea,nodeb;
	    String href,year,director;
	    int currentMovie=0;
		
		try {
			String HTMLText = WebReader.getHTMLfromURL(WEFAURLSearchMovieInfo+URLEncoder.encode(title,"UTF-8"),"ISO-8859-1");

				parser = new Parser(HTMLText);
				NodeList nl = parser.parse(null);
				NodeList ntd = WebReader.getTagNodesOfType(nl,"td",true);
				for (NodeIterator k = ntd.elements (); k.hasMoreNodes (); ){				
	 				  if ((nodea=k.nextNode()) instanceof TagNode){
	 					 if (nodea.getChildren()!=null){
		 					 NodeList anodes = WebReader.getTagNodesOfType(nodea.getChildren(),"a",false);
		 	        		 for (NodeIterator j = anodes.elements (); j.hasMoreNodes (); ){
		 		 				  if ((nodeb=j.nextNode()) instanceof TagNode){
		 		 					  	href=null;
		 			 			    	href = ((TagNode)nodeb).getAttribute("href");
		 			 			    	if (href!=null){
		 			 			    		if (href.contains("/es/film")){
		 			 			    			if (((TagNode)nodeb).toPlainTextString()!=null){
		 			 			    				if (nodea.getChildren()!=null){
			 			 			 	        		 for (NodeIterator i = nodea.getChildren().elements (); i.hasMoreNodes (); ){
			 			 			 		 				  if ((nodea=i.nextNode()) instanceof TextNode){
			 			 			 		 					    year=nodea.getText().trim();
			 			 			 		 					    if (year.length()>5){
				 			 			 		 					    year=year.substring(1,5);
				 			 			 		 					    try{
				 			 			 		 					    	Integer.getInteger(year);
				 			 			 		 					    	movieInfo=new Movie();
				 			 			 		 					    	movieInfo.setTitle(((TagNode)nodeb).toPlainTextString());
				 			 			 		 					    	movieInfo.setYear(year);
				 			 			 		 					    	movies.add(movieInfo);
				 			 			 		 					    	break;
				 			 			 		 					    }
				 			 			 		 					    catch(Exception ex){
				 			 			 		 					    	
				 			 			 		 					    }
			 			 			 		 					    }
			 			 			 		 					  }
			 			 			 		 				  }
			 			 			 	        		 }
		 			 			    			}
		 			 			    		}
		 			 			    	}
		 		 				  }
		 	        		  }
	 					 }
	 				  }
				}				
				NodeList spannodes = WebReader.getTagNodesOfType(nl,"span",false);
	        	for (NodeIterator j = spannodes.elements (); j.hasMoreNodes (); ){
		 			if ((nodea=j.nextNode()) instanceof TagNode){
		 				href=null;
		 			    href = ((TagNode)nodea).getAttribute("class");
		 			    if (href!=null){
		 			    	if (href.contains("director")){
				    			if (nodea.getChildren()!=null){
					    			for (NodeIterator i = nodea.getChildren().elements (); i.hasMoreNodes (); ){
					    				if ((nodeb=i.nextNode()) instanceof TagNode){
					    					href=null;
					    					href = ((TagNode)nodeb).getAttribute("href");
					    					if (href!=null){
						    					if (href.contains("/es/search.php?stype=director")){
								    				if ((director=((TagNode)nodeb).toPlainTextString())!=null){
								    					if (currentMovie<movies.size()){
									    					movieInfo=movies.get(currentMovie);
									    					if (movieInfo.getDirector()==null)
									    						movieInfo.setDirector(director);
									    					else movieInfo.setDirector(movieInfo.getDirector()+", "+director);
								    					}
								    				}
						    	 				}
					    					}
					    				}
				    				 }
				    			}
		    					currentMovie++;
		 			    	}
		 			    }
		 			}
		 			
		 		}
        		return movies;		
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Movie getMovieInfo(String title){
		
		Movie movieInfo=null;
		Collator comparador = Collator.getInstance();
	    comparador.setStrength(Collator.TERTIARY);
	    Parser parser;
	    Node nodea,nodeb,nodec;
	    String str,year;
		
		try {
			String HTMLText = WebReader.getHTMLfromURL(WEFAURLSearchMovieInfo+URLEncoder.encode(title,"UTF-8"),"ISO-8859-1");

				parser = new Parser(HTMLText);
				NodeList nl = parser.parse(null);
				NodeList ntr = WebReader.getTagNodesOfType(nl,"tr",true);
				movieInfo = new Movie();
				for (NodeIterator k = ntr.elements (); k.hasMoreNodes (); ){				
	 				  if ((nodea=k.nextNode()) instanceof TagNode){
	 					  if (nodea.getChildren()!=null){
		 					 NodeList thnodes = WebReader.getTagNodesOfType(nodea.getChildren(),"th",false);
		 	        		 for (NodeIterator j = thnodes.elements (); j.hasMoreNodes (); ){
		 		 				  if ((nodeb=j.nextNode()) instanceof TagNode){
		 		 					  if (((TagNode)nodeb).toPlainTextString()!=null){
		 		 						  str=((TagNode)nodeb).toPlainTextString();
		 		 						  if (str.contains("A&Ntilde;O")){
		 		 							 NodeList tdnodes = WebReader.getTagNodesOfType(nodea.getChildren(),"td",false);
		 		 		 	        		 for (NodeIterator i = tdnodes.elements (); i.hasMoreNodes (); ){
		 		 		 		 				 if ((nodec=i.nextNode()) instanceof TagNode){
		 		 		 		 					 if (((TagNode)nodec).toPlainTextString()!=null){
		 		 		 		 						year=((TagNode)nodec).toPlainTextString();
		 		 		 		 						Pattern p = Pattern.compile("\\d+");
		 		 		 		 						Matcher m = p.matcher(year);
		 		 		 		 						if (m.find()) movieInfo.setYear(m.group());
		 		 		 		 					 }
		 		 		 		 				 }
		 		 		 	        		 }
		 		 						  }
		 		 						  else if (str.contains("DIRECTOR")){
			 		 							 NodeList tdnodes = WebReader.getTagNodesOfType(nodea.getChildren(),"td",false);
			 		 		 	        		 for (NodeIterator i = tdnodes.elements (); i.hasMoreNodes (); ){
			 		 		 		 				 if ((nodec=i.nextNode()) instanceof TagNode){
			 		 		 		 					 if (((TagNode)nodec).toPlainTextString()!=null){
			 		 		 		 					    movieInfo.setDirector(((TagNode)nodec).toPlainTextString());
			 		 		 		 					 }
			 		 		 		 				 }
			 		 		 	        		 }
			 		 					  }
		 		 					  }	 		 					  
		 		 				  }
		 	        		 }
	 	        		  }
	 				  }
				}				
        		return movieInfo;		
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
}
