package image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import main.Errors;

class DeleteCurrentCoverHandler implements ActionListener {
    /**
	 * 
	 */
	private final ImageDealer imageDealer;

	/**
	 * @param imageDealer
	 */
	DeleteCurrentCoverHandler(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
	}

	private MultiDBImage tempIm,newIm;
    private File file;
    
	public void actionPerformed(ActionEvent e) {
		tempIm=(MultiDBImage) this.imageDealer.spinnerCovers.getValue();		
		if (this.imageDealer.imageList.size()>1){
			this.imageDealer.imageList.remove(tempIm);
			this.imageDealer.selectFrameInit();
			newIm=new MultiDBImage();
			newIm.putImage(this.imageDealer.selectCoversView,this.imageDealer.imageList.get(0));
			this.imageDealer.nofPicLabel.setText("Number of pics: "+this.imageDealer.imageList.size());
			this.imageDealer.selectCoverFrame.getContentPane().add(this.imageDealer.selectCoversView);
			this.imageDealer.selectCoverFrame.setVisible(true);
		} else{
			this.imageDealer.imageList.clear();
			this.imageDealer.selectCoverFrame.dispose();
		}
		
    	file = tempIm.path;
    	
		if(!file.delete()) {
		    // Deletion failed
			Errors.showWarning(Errors.FILE_DELETE_ERROR);
		}
	}
}