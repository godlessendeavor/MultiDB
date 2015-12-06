package image;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import main.Errors;
import music.db.Disc;

class SaveImageThread extends Thread {
	private final SaveCurrentCoverHandler saveCurrentCoverHandler;
	private MultiDBImage im;
    private String archivo,rutaArch,type,ext;
    private File file;
    private boolean success=false;
    private Disc currDisc;
    
    public SaveImageThread(SaveCurrentCoverHandler saveCurrentCoverHandler, MultiDBImage im) {
		super();
		this.saveCurrentCoverHandler = saveCurrentCoverHandler;
		this.im=im;
	}
	@Override
	public void run() {
		file = im.path;
		currDisc= new Disc(this.saveCurrentCoverHandler.imageDealer.currentDisc);
    	rutaArch = currDisc.path.getAbsolutePath();
    	archivo=file.getName();
    	int pos = archivo.lastIndexOf('.');
    	if (pos>0) ext = "."+archivo.substring(pos+1);
    	else ext=".jpg";
    	im.type=ext.substring(1);
    	try{	    	    	
	    	if (!file.canWrite()) {
	    		if (!file.createNewFile()) JOptionPane.showMessageDialog(this.saveCurrentCoverHandler.imageDealer.selectCoverFrame, "Could not rename file");
	    		else  {
	    			im.writeImageToFile();
	    			success=true;
	    		}
	    	} else success=true;
	    
	    	if (success){
	    		File nfile;
		    	if (ImageDealer.frontCover) type="front"; 
		    	else if (!ImageDealer.otherCover) type="back";
		    	else type="other";
		    	String name;
		    	if (type.compareTo("other")==0){
		    		name=rutaArch + File.separator + im +ext;
		    		if (this.saveCurrentCoverHandler.imageDealer.newNameField.getText().compareTo("")!=0) name=rutaArch + File.separator + this.saveCurrentCoverHandler.imageDealer.newNameField.getText() +ext;
		    	}else name=rutaArch + File.separator + currDisc.group + " - " + currDisc.title + " - " + type+ext;
		    	nfile= new File(name);
	    		if (file.renameTo(nfile)) JOptionPane.showMessageDialog(this.saveCurrentCoverHandler.imageDealer.selectCoverFrame, "File renamed succesfully to "+name);
	    	    else JOptionPane.showMessageDialog(this.saveCurrentCoverHandler.imageDealer.selectCoverFrame, "Could not rename file");	    	
	    	}
    	}catch(IOException e){
    		Errors.showWarning(Errors.IMAGE_NOT_SAVED, e.getMessage());
			//e.printStackTrace();
		}
    }
}