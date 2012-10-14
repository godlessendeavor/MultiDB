package image;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import main.Errors;
import main.MultiDB;
import main.ProgressBarWindow;
import music.db.Disc;
import web.WebReader;



public class ImageDealer {

    public final Dimension COVERS_DIM = new Dimension(400,400);
    public final Dimension MAX_COVERS_DIM = new Dimension(1200,800);
    public final int SEEK_NUMBER_LOW = 4;
    public final int SEEK_NUMBER_MAX = 8;
    public final int BING_SEARCH = 1;
    public final int COVER_PARADIES_SEARCH = 2;
    
	private final String bingUrl1=MultiDB.webBing1;
	private final String bingUrl2=MultiDB.webBing2;
	private final String coverParadiesURL="http://ecover.to/";
	private final String coverParadiesURLSearch=coverParadiesURL+"?Module=SimpleSearch";
	

	public static boolean frontCover=true;
    private static Disc currentDisc;
    
    protected JLabel selectCoversView;
    protected JFrame selectCoverFrame;
    protected JSpinner spinnerCovers;
    protected JButton saveCoverButton,deleteCoverButton;
    protected JRadioButton backRButton,frontRButton;
    protected SpinnerListModel spinnerCoversM;
    protected MultiDBImage multiIm;
    protected ArrayList<MultiDBImage> imageList = new ArrayList<MultiDBImage>();
	protected String bingUrl;
	protected ViewCoverHandler viewHandler;   
	
	
    
    public ImageDealer() {
    	bingUrl = bingUrl1+SEEK_NUMBER_LOW+bingUrl2;
    	selectFrameInit();
	}
    
    public ImageDealer(int seekNumber) {
    	if (seekNumber>SEEK_NUMBER_MAX) seekNumber=SEEK_NUMBER_MAX;
    	if (seekNumber<1) seekNumber=SEEK_NUMBER_LOW;
    	bingUrl = bingUrl1+seekNumber+bingUrl2;
    	selectFrameInit();
	}
    
    public ImageDealer(Disc disc) {
    	currentDisc=disc;
    	selectFrameInit();
	}
    
    public static void setDisc(Disc disc){
    	currentDisc=disc;
    }


	public void selectFrameInit(){
        ///////////setting icons for pictures
        multiIm = new MultiDBImage();
        if (selectCoverFrame!=null) selectCoverFrame.dispose();
	    selectCoverFrame = new JFrame("Select a picture");
	    selectCoverFrame.setSize(600, 550);
	    selectCoversView = new JLabel();
	    selectCoversView.setMinimumSize(COVERS_DIM);
	    selectCoverFrame.getContentPane().setLayout(new BoxLayout(selectCoverFrame.getContentPane(),BoxLayout.Y_AXIS));
	    spinnerCoversM = new SpinnerListModel();	
	    try{
	    	spinnerCoversM.setList(imageList);
	    }catch(IllegalArgumentException ilex){
	    	//do nothing
	    	//this exception breaks when the list has 0 members, like the first time this function is invoked
	    }
	    spinnerCovers = new JSpinner(spinnerCoversM);
	    saveCoverButton = new JButton("Save current cover");
	    deleteCoverButton = new JButton("Delete current cover");
	    //handler to view covers on selectFrameCover
        viewHandler = new ViewCoverHandler();
        spinnerCovers.addChangeListener(viewHandler);
	    SaveCurrentCoverHandler saveCurrentCoverHandler = new SaveCurrentCoverHandler();
	    saveCoverButton.addActionListener(saveCurrentCoverHandler);
	    DeleteCurrentCoverHandler deleteCurrentCoverHandler = new DeleteCurrentCoverHandler();
	    deleteCoverButton.addActionListener(deleteCurrentCoverHandler);

	    //Create the radio buttons.
	    frontRButton = new JRadioButton("Front");
	    frontRButton.setSelected(true);

	    backRButton = new JRadioButton("Back");

	    //Group the radio buttons.
	    ButtonGroup frontBackSelect = new ButtonGroup();
	    frontBackSelect.add(frontRButton);
	    frontBackSelect.add(backRButton);
	    
	    SelectTypeCoverHandler selectTypeCoverHandler = new SelectTypeCoverHandler();
	    frontRButton.addActionListener(selectTypeCoverHandler);
	    backRButton.addActionListener(selectTypeCoverHandler);
	    
	    selectCoverFrame.getContentPane().add(spinnerCovers);
	    selectCoverFrame.getContentPane().add(frontRButton);
	    selectCoverFrame.getContentPane().add(backRButton);
	    selectCoverFrame.getContentPane().add(saveCoverButton);
	    selectCoverFrame.getContentPane().add(deleteCoverButton);
	    
    }
    
    
    public void searchImage(String name){
    	selectFrameInit();
    	SearchImageInternet searchImageInternetThread = new SearchImageInternet(name);
    	searchImageInternetThread.start();
	}    
    
    
    public boolean showImage(File pathDisc,JLabel labelIn,String type){
    	int numArchivos,found,indexCover=0;
    	String[] listaArchivos;
    	MultiDBImage tempIm;
    	
		listaArchivos = pathDisc.list();
		numArchivos = listaArchivos.length;
		found = 0;
		imageList.clear();
		
		for (int i = 0; i < numArchivos; i++) {
			listaArchivos[i] = listaArchivos[i].toLowerCase();
			if (((listaArchivos[i].indexOf(".jpg") > -1) || (listaArchivos[i].indexOf(".gif") > -1)|| (listaArchivos[i].indexOf(".png")) > -1)) {
				tempIm=new MultiDBImage();
				//System.out.println(pathDisc.getAbsolutePath() + File.separator + listaArchivos[i]);
				tempIm.path = new File(pathDisc.getAbsolutePath() + File.separator + listaArchivos[i]);
				
				//System.out.println(tempIm.width);
				imageList.add(tempIm);
				if (listaArchivos[i].indexOf(type) > -1) {
					found = 1;
					indexCover = i;
					break;
				} else
					found = 2;
			}
		}
		
		if (found == 1) {
			imageList.clear();
			if (type.compareTo("front") == 0) frontCover = true; else frontCover = false;
			multiIm.putImage(labelIn, MultiDBImage.FILE_TYPE, pathDisc + File.separator + listaArchivos[indexCover]);
			
			//splitRight.setTopComponent(coversView);
		} else if (found == 2) {
			//splitRight.setTopComponent(COVERS_NOT_NAMED_PROP_MSG);

			/*List<String> imageFiles = new LinkedList<String>();
			//int currentImage = 0;
			for (int i = 0; i < numArchivos; i++) {
				if (((listaArchivos[i].indexOf(".jpg") > -1) || (listaArchivos[i].indexOf(".gif") > -1) || (listaArchivos[i].indexOf(".png")) > -1)){
					imageFiles.add(listaArchivos[i]);
					//currentImage++;
				}
			}*/

			if (imageList.size()>0){
				for (int i=0;i<imageList.size();i++){
					imageList.get(i).setImageFromFile();
				}
				spinnerCoversM.setList(imageList);
				//System.out.println(imageListWeb.get(0).width);
				multiIm.putImage(selectCoversView, imageList.get(0));
			
				selectCoverFrame.getContentPane().add(selectCoversView);
				selectCoverFrame.setVisible(true);
			}
			
		} 
		if (found==0) return false; else return true;
    }
    
    
    
    
  //THREAD FOR SEARCH IMAGE INTERNET////////////////////////////////////////////////////////////////////////  
    public class SearchImageInternet extends Thread {
    	   
    	private String HTMLText="";
    	private MultiDBImage tempIm;
    	private String name;
    	private Integer type;
    	private ProgressBarWindow pw = new ProgressBarWindow();
    	  
    	public SearchImageInternet(String name) {
    		super();
    		this.name=name;
    		this.type=COVER_PARADIES_SEARCH;
    	}
    	
    	public SearchImageInternet(String name,Integer type) {
    		super();
    		this.name=name;
    		this.type=type;
    	}
    	    
    	@Override
    	public void run() {    		
    	    pw.setFrameSize(pw.dimWebImageReader);
    	    pw.startProgBar(2);

    	    imageList.clear();
    	   
    	    if (type==BING_SEARCH) searchBing();
    	    else searchCoverParadies(0);	
    	    if (imageList.size()>0){
    	    	spinnerCoversM.setList(imageList);
				tempIm=new MultiDBImage();
				if (imageList.get(0).image!=null) tempIm.putImage(selectCoversView,imageList.get(0));
				else if (imageList.get(0).thumbNail!=null) tempIm.putImage(selectCoversView,imageList.get(0).thumbNail);
				else Errors.showWarning(Errors.IMAGE_NOT_FOUND);
				selectCoverFrame.getContentPane().add(selectCoversView);
				selectCoverFrame.setVisible(true);
    	    }else Errors.showWarning(Errors.IMAGE_NOT_FOUND);
			pw.setPer(2,"");			 				
    	}
    	
    	private void searchBing(){
    		 try{
    			JSONObject job;
	    		String search=URLEncoder.encode("'"+name+"'","UTF-8");
				String searchString = bingUrl+search;
				//System.out.println(searchString);
				pw.setPer(0, "Searching...");
				HTMLText=WebReader.getHTMLfromURLHTTPS(searchString,MultiDB.webBingAccountKey);
				pw.setPer(1, "Downloading results...");
				if (HTMLText.compareTo("Error")==0)  Errors.showError(Errors.WEB_MALF_URL);
				else{
					try {
						job = new JSONObject(HTMLText);
						job=job.getJSONObject("d");
							//System.out.println(job.toString());
						JSONArray list = job.getJSONArray("results");				
						for (int i=0;i<list.length();i++){
							job=list.getJSONObject(i);
							tempIm=new MultiDBImage();
							tempIm.url=job.getString("MediaUrl");
							tempIm.setImageFromUrl();
							if (tempIm.image!=null){
	    						tempIm.path=new File(ImageDealer.currentDisc.path+File.separator+i);
	    						tempIm.width=job.getInt("Width");
	    						tempIm.height=job.getInt("Height");
	    						tempIm.fileSize=job.getInt("FileSize");
	    						imageList.add(tempIm);
	    					}
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			} catch (Exception e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	
    	private void searchCoverParadies(Integer page){
    		Disc disc;
    		ArrayList<Disc> discList = new ArrayList<Disc>();
    		try{
 				pw.setPer(0, "Searching...");
 			    String data = URLEncoder.encode("Page", "UTF-8") + "=" + URLEncoder.encode(page.toString(), "UTF-8");
 			    data += "&" + URLEncoder.encode("SearchString", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
 			    //data += "&" + URLEncoder.encode("Sektion", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
 				HTMLText=WebReader.postHTMLfromURL(coverParadiesURLSearch,data);
 				pw.setPer(1, "Downloading results...");
 				if (HTMLText.compareTo("Error")==0)  Errors.showError(Errors.WEB_MALF_URL);
 				else{
 					Parser parser;
 					parser = new Parser(HTMLText);		 				
		 			NodeList nl = parser.parse(null); 
 					//System.out.println(HTMLText);
 					NodeList tableNodes=WebReader.getTagNodesOfType(nl,"td",false);
		 			Node node;
		 			NodeList links;
		 			String classTd="";
		 			boolean filter=false;
		 			for (NodeIterator i = tableNodes.elements (); i.hasMoreNodes ();){
		 				node=i.nextNode();
		 				if ((node) instanceof TagNode){
			 				classTd=((TagNode)node).getAttribute("class");
			 				filter=false;
			 				if (classTd!=null){
				 				if (classTd.contains("Cell_Menu")) filter=false;
				 				else filter=true;
			 				}else {
			 					filter=true;
			 				}
			 				if (filter){ 					
			 					if (((TagNode)node).getChildren()!=null){
			 						links=WebReader.getTagNodesOfType(((TagNode)node).getChildren(),"a",false);
			 						if (links.size()>0){  		 			    		
			 							for (NodeIterator itlinks = links.elements (); itlinks.hasMoreNodes (); ){
			 								String href = ((TagNode)itlinks.nextNode()).getAttribute("href");
			 								if (href!=null){
			 									if (href.contains("?Module=ViewEntry&amp;")){
			 										//more than  1 result
			 										href=href.substring(0, 18)+href.substring(22);
			 										//get rid of "amp;"
			 										filter=false;
			 										for (int it=0;it<discList.size();it++){
			 											disc = discList.get(it);
			 											if (disc.getLink().contains(href)){
			 												filter=true;//they can repeat, this is the only way to void repetitions
			 												break;
			 											}
			 										}
			 										if (!filter){
			 											disc = new Disc();
			 											disc.setLink(coverParadiesURL+href);
			 											discList.add(disc);
			 										}
			 									}	
			 								}	
			 							}
			 						}	
			 					}	
			 				}
		 				}
		 			}

		 			if (discList.size()>0){
		 				for (int it=0;it<discList.size();it++){
							disc = discList.get(it);
							imageList.addAll(getImageFromLinkCoverParadies(WebReader.getHTMLfromURL(disc.getLink())));
						}
		 			}else {
		 				imageList.addAll(getImageFromLinkCoverParadies(HTMLText));
		 			}
 			}
 			} catch (Exception e) {
 					// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
    	}
    	
    	private ArrayList<MultiDBImage> getImageFromLinkCoverParadies(String HTMLText){
    		ArrayList<MultiDBImage> listTi = new ArrayList<MultiDBImage>();
    		MultiDBImage ti=null;
    		try{
 				if (HTMLText.compareTo("Error")==0)  Errors.showError(Errors.WEB_MALF_URL);
 				else{
 					Parser parser;
 					parser = new Parser(HTMLText);		 				
		 			NodeList nl = parser.parse(null); 
 					//System.out.println(HTMLText);
 					NodeList tableNodes=WebReader.getTagNodesOfType(nl,"div",true);
		 			Node node,nodeImg;
		 			NodeList links,divChildren,itImg;
		 			String classTd="";
		 			boolean filter=false;
		 			for (NodeIterator i = tableNodes.elements (); i.hasMoreNodes ();){
		 				node=i.nextNode();
		 				if (node instanceof TagNode){
			 				classTd=((TagNode)node).getAttribute("class");
			 				filter=false;
			 				if (classTd!=null){
				 				if (classTd.contains("ThumbDetails")) filter=true;
				 				else filter=false;
			 				}
			 				if (filter){ 
			 					divChildren=((TagNode)node).getChildren();
			 					if (divChildren!=null){
			 						links=WebReader.getTagNodesOfType(((TagNode)node).getChildren(),"a",false);
			 						if (links.size()>0){  		 			    		
			 							for (NodeIterator itlinks = links.elements (); itlinks.hasMoreNodes (); ){
			 								String href = ((TagNode)itlinks.nextNode()).getAttribute("href");
			 								if (href!=null){
			 									ti=new MultiDBImage();
			 									if (!href.contains("Type=Test.JPG")){
				 									itImg=WebReader.getTagNodesOfType(((TagNode)node).getChildren(),"img",false);
				 									for (NodeIterator iterImg = itImg.elements (); iterImg.hasMoreNodes (); ){
				 										nodeImg=iterImg.nextNode();
				 										if (nodeImg instanceof TagNode){
				 											String img=((TagNode)nodeImg).getAttribute("src");
				 											if (img!=null){
				 												ti.setThumbNailFromUrl(img);
				 												break;
				 											}
				 										}
				 									}
								 					//System.out.println(href);
				 									href=href.substring(2);
				 									//get rid of "./;"
				 									href=coverParadiesURL+href;
				 									ti.url=href;
				 									if (ti.thumbNail!=null){
				 										ti.path=new File(ImageDealer.currentDisc.path+File.separator+i);
				 										listTi.add(ti);
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

 			} catch (Exception e) {
 					// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
    		return listTi;
    	}
    }
    
    
///////////////////////////////////////////COVER HANDLERS///////////////////////////////
///////////////////////////////////////////COVER HANDLERS///////////////////////////////
///////////////////////////////////////////COVER HANDLERS///////////////////////////////

    
private class SaveCurrentCoverHandler implements ActionListener {

    private String archivo,rutaArch,type,ext;
    private File file;
    private boolean success=false;
    private MultiDBImage tempIm;
    	
    public void actionPerformed(ActionEvent evento) {
    	tempIm=(MultiDBImage) spinnerCovers.getValue();
    	file = tempIm.path;
    	rutaArch = currentDisc.path.getAbsolutePath();
    	archivo=file.getName();
    	int pos = archivo.lastIndexOf('.');
    	if (pos>0) ext = "."+archivo.substring(pos+1);
    	else ext=".jpg";
    	tempIm.type=ext.substring(1);
    	try{
	    	    	
	    	if (!file.canWrite()) {
	    		if (!file.createNewFile()) JOptionPane.showMessageDialog(selectCoverFrame, "Could not rename file");
	    		else  {
	    			tempIm.writeImageToFile();
	    			success=true;
	    		}
	    	} else success=true;
	    
	    	if (success){
		    	if (frontCover) type="front"; else type="back";
    			File nfile=new File(rutaArch + File.separator + currentDisc.group + " - " + currentDisc.title + " - " + type+ext);
	    		if (file.renameTo(nfile)) JOptionPane.showMessageDialog(selectCoverFrame, "File renamed succesfully");
	    	    else JOptionPane.showMessageDialog(selectCoverFrame, "Could not rename file");	    	
	    	}
    	}catch(IOException e){
    		//TODO
			e.printStackTrace();
		}
    	selectCoverFrame.dispose();
    }
} //FIN HANDLER CHANGE NAME
    


private class DeleteCurrentCoverHandler implements ActionListener {
    private MultiDBImage tempIm,newIm;
    private File file;
    
	public void actionPerformed(ActionEvent e) {
		tempIm=(MultiDBImage) spinnerCovers.getValue();		
		if (imageList.size()>1){
			imageList.remove(tempIm);
			selectFrameInit();
			newIm=new MultiDBImage();
			newIm.putImage(selectCoversView,imageList.get(0));
			selectCoverFrame.getContentPane().add(selectCoversView);
			selectCoverFrame.setVisible(true);
			selectCoverFrame.setVisible(true);
		} else{
			imageList.clear();
			selectCoverFrame.dispose();
		}
		
    	file = tempIm.path;
    	
		if(!file.delete()) {
		    // Deletion failed
			Errors.showWarning(Errors.FILE_DELETE_ERROR);
		}
	}
} //FIN SELECT TYPE COVER


private class SelectTypeCoverHandler implements ActionListener {

	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().compareTo("Back")==0) frontCover=false;
		else frontCover=true;
	}
} //FIN SELECT TYPE COVER


   
private class ViewCoverHandler implements ChangeListener {

    // manejar evento de cambio en lista
    public void stateChanged(ChangeEvent e) {
        JSpinner spinner = (JSpinner) e.getSource();
        try{
        	multiIm.putImage(selectCoversView, ((MultiDBImage) spinner.getValue()));
        }catch(IndexOutOfBoundsException ex){
        	System.out.println("problema 3");
        }
    }
} //FIN HANDLER VIEW COVERS

}
