package image;

import java.io.File;
import java.net.URLEncoder;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import main.Errors;
import main.MultiDB;
import main.ProgressBarWindow;
import web.WebReader;

//THREAD FOR SEARCH IMAGE INTERNET////////////////////////////////////////////////////////////////////////  
public class SearchImageInternet extends Thread {
	   
	/**
	 * 
	 */
	private final ImageDealer imageDealer;
	private String HTMLText="";
	private MultiDBImage tempIm;
	private String name;
	String type;
	private ProgressBarWindow pw = new ProgressBarWindow();
	  
	public SearchImageInternet(ImageDealer imageDealer, String name) {
		super();
		this.imageDealer = imageDealer;
		this.name=name;
		this.type=ImageDealer.BING_SEARCH;
	}
	
	public SearchImageInternet(ImageDealer imageDealer, String name,String type) {
		super();
		this.imageDealer = imageDealer;
		this.name=name;
		this.type=type;
	}
	    
	@Override
	public void run() {    		
	    pw.setFrameSize(pw.dimWebImageReader);
	    pw.startProgBar(2);

	    this.imageDealer.imageList.clear();
	   
	    if (type.compareTo(ImageDealer.BING_SEARCH)==0) searchBing();
	    //TODO: add more search engines
	    // else 	
	    if (this.imageDealer.imageList.size()>0){
	    	this.imageDealer.spinnerCoversM.setList(this.imageDealer.imageList);
			tempIm=new MultiDBImage();
			if (this.imageDealer.imageList.get(0).image!=null) tempIm.putImage(this.imageDealer.selectCoversView,this.imageDealer.imageList.get(0));
			else if (this.imageDealer.imageList.get(0).thumbNail!=null) tempIm.putImage(this.imageDealer.selectCoversView,this.imageDealer.imageList.get(0).thumbNail);
			else Errors.showWarning(Errors.IMAGE_NOT_FOUND);
			this.imageDealer.nofPicLabel.setText("Number of pics: "+this.imageDealer.imageList.size());
			this.imageDealer.selectCoverFrame.getContentPane().add(this.imageDealer.selectCoversView);
			this.imageDealer.selectCoverFrame.setVisible(true);
	    }else {
	    	pw.setPer(2,"");
	    	Errors.showWarning(Errors.IMAGE_NOT_FOUND);
	    }
		pw.setPer(2,"");			 				
	}
	
	private void searchBing(){
		 try{
			JSONObject job;
    		String search=URLEncoder.encode("'"+name+"'","UTF-8");
			String searchString = this.imageDealer.bingUrl+search;
			//System.out.println(searchString);
			pw.setPer(0, "Searching...");
			HTMLText=WebReader.getHTMLfromURLHTTPS(searchString,MultiDB.webBingAccountKey);
			pw.setPer(1, "Downloading results...");
			if (HTMLText.compareTo("Error")==0)  
			{
				Errors.showError(Errors.WEB_MALF_URL);
			}
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
    						tempIm.path=new File(this.imageDealer.currentDisc.path+File.separator+i);
    						tempIm.width=job.getInt("Width");
    						tempIm.height=job.getInt("Height");
    						tempIm.fileSize=job.getInt("FileSize");
    						this.imageDealer.imageList.add(tempIm);
    					}
					}
					
				} catch (JSONException e) {
					Errors.writeError(Errors.WEB_IMAGE_ERROR, e.getMessage());
					e.printStackTrace();
			}
		}
		} catch (Exception e) {
			Errors.writeError(Errors.WEB_IMAGE_ERROR, e.getMessage());
			e.printStackTrace();
		}
	}
	
	
}