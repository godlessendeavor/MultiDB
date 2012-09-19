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

	private final String bingUrl1=MultiDB.webBing1;
	private final String bingUrl2=MultiDB.webBing2;

	public static boolean frontCover=true;
    private static Disc currentDisc;
    
    private JLabel selectCoversView;
    private JFrame selectCoverFrame;
    private JSpinner spinnerCovers;
    private JButton spinnerCoversButton;
    private JRadioButton backRButton,frontRButton;
    private SpinnerListModel spinnerCoversM;
    private MultiDBImage multiIm;
	private ArrayList<MultiDBImage> imageListWeb = new ArrayList<MultiDBImage>();
	protected  String bingUrl;
	   
	
	
    
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
        
	    selectCoverFrame = new JFrame("Select a picture");
	    selectCoverFrame.setSize(500, 520);
	    selectCoversView = new JLabel();
	    selectCoversView.setMinimumSize(COVERS_DIM);
	    selectCoverFrame.getContentPane().setLayout(new BoxLayout(selectCoverFrame.getContentPane(),BoxLayout.Y_AXIS));
	    spinnerCoversM = new SpinnerListModel();
	    spinnerCovers = new JSpinner(spinnerCoversM);
	    spinnerCoversButton = new JButton("Save current cover");
	    //handler to view covers on selectFrameCover
        ViewCoverHandler viewHandler = new ViewCoverHandler();
        spinnerCovers.addChangeListener(viewHandler);
	    SaveCurrentCoverHandler saveCurrentCoverHandler = new SaveCurrentCoverHandler();
	    spinnerCoversButton.addActionListener(saveCurrentCoverHandler);

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
	    selectCoverFrame.getContentPane().add(spinnerCoversButton);
	    
	    
	  /*  coversView = new JLabel();
        //dimensions for covers
        coversView.setMinimumSize(COVERS_DIM);
	    */
    }
    
    
    public void searchImage(String name){
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
		imageListWeb.clear();
		
		for (int i = 0; i < numArchivos; i++) {
			listaArchivos[i] = listaArchivos[i].toLowerCase();
			if (((listaArchivos[i].indexOf(".jpg") > -1) || (listaArchivos[i].indexOf(".gif") > -1)|| (listaArchivos[i].indexOf(".png")) > -1)) {
				tempIm=new MultiDBImage();
				System.out.println(pathDisc.getAbsolutePath() + File.separator + listaArchivos[i]);
				tempIm.setImageFromFile(pathDisc.getAbsolutePath() + File.separator + listaArchivos[i]);
				tempIm.width=tempIm.image.getWidth(null);
				tempIm.height=tempIm.image.getHeight(null);
				System.out.println(tempIm.width);
				imageListWeb.add(tempIm);
				if (listaArchivos[i].indexOf(type) > -1) {
					found = 1;
					indexCover = i;
					break;
				} else
					found = 2;
			}
		}
		
		if (found == 1) {
			imageListWeb.clear();
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

			if (imageListWeb.size()>0){
				spinnerCoversM.setList(imageListWeb);
				System.out.println(imageListWeb.get(0).width);
				multiIm.putImage(selectCoversView, imageListWeb.get(0).image);
			
				selectCoverFrame.getContentPane().add(selectCoversView);
				selectCoverFrame.setVisible(true);
			}
			
		} 
		if (found==0) return false; else return true;
    }
    
    
    
    
  //THREAD FOR SEARCH IMAGE INTERNET////////////////////////////////////////////////////////////////////////  
    public class SearchImageInternet extends Thread {
    	   
    	private String HTMLText="";
    	private JSONObject job;
    	private MultiDBImage tempIm;
    	private String name;
    	  
    	public SearchImageInternet(String name) {
    		super();
    		this.name=name;
    	}
    	    
    		@Override
    		public void run() {
    			ProgressBarWindow pw = new ProgressBarWindow();
    	        pw.setFrameSize(pw.dimWebImageReader);
    	        pw.startProgBar(2);

    	    	imageListWeb.clear();
    	    	try{
    	    		
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
    							//System.out.println(tempIm.url);
    							tempIm.width=job.getInt("Width");
    							tempIm.height=job.getInt("Height");
    							tempIm.fileSize=job.getInt("FileSize");
    							imageListWeb.add(tempIm);
    						}
    						
    					
    						/*tempIm=new MultiDBImage();
    						tempIm.url="http://i118.photobucket.com/albums/o99/JouniK86/absml/frontbig.jpg";				
    						tempIm.image=MultiDBImage.getImageFromUrl(tempIm.url);
    						if (tempIm.image==null) System.out.println("merdaa!");
    						imageListWeb.add(tempIm);
    						tempIm=new MultiDBImage();
    						tempIm.url="http://i118.photobucket.com/albums/o99/JouniK86/absml/rawfront.jpg";				
    						tempIm.image=MultiDBImage.getImageFromUrl(tempIm.url);
    						if (tempIm.image==null) System.out.println("merdaa!");
    						imageListWeb.add(tempIm);
    						tempIm=new MultiDBImage();
    						tempIm.url="http://www.metalkingdom.net/album/img/d45/23694.jpg";				
    						tempIm.image=MultiDBImage.getImageFromUrl(tempIm.url);
    						if (tempIm.image==null) System.out.println("merdaa!");
    						imageListWeb.add(tempIm);*/
    						
    						spinnerCoversM.setList(imageListWeb);
    						tempIm=new MultiDBImage();
    						tempIm.putImage(selectCoversView,imageListWeb.get(0).image);
    						selectCoverFrame.getContentPane().add(selectCoversView);
    						selectCoverFrame.setVisible(true);
    						pw.setPer(2,"");
    					} catch (JSONException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    				}
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    }
    

    
///////////////////////////////////////////COVER MENU HANDLERS///////////////////////////////
///////////////////////////////////////////COVER MENU HANDLERS///////////////////////////////
///////////////////////////////////////////COVER MENU HANDLERS///////////////////////////////

    
private class SaveCurrentCoverHandler implements ActionListener {

    String archivo,rutaArch,type;
    File file;
    	
    public void actionPerformed(ActionEvent evento) {
    	archivo = ((MultiDBImage) spinnerCovers.getValue()).name;
    	rutaArch = currentDisc.path.getAbsolutePath();
    	file = new File(rutaArch + File.separator + archivo);
    	if (file.canWrite()) {
    		if (frontCover) type="front"; else type="back";
    		if (file.renameTo(new File(rutaArch + File.separator + currentDisc.group + " - " + currentDisc.title + " - " + type+".jpg"))) {
    			JOptionPane.showMessageDialog(selectCoverFrame, "File renamed succesfully");
    		} else 	JOptionPane.showMessageDialog(selectCoverFrame, "Could not rename file");
    	} else JOptionPane.showMessageDialog(selectCoverFrame, "Could not rename file");
    	selectCoverFrame.dispose();
    }
} //FIN HANDLER CHANGE NAME
    


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
        multiIm.putImage(selectCoversView, ((MultiDBImage) spinner.getValue()).image);
    }
} //FIN HANDLER VIEW COVERS

}
