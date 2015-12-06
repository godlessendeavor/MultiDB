package image;

import image.SaveImageThread;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SaveCurrentCoverHandler implements ActionListener {

	final ImageDealer imageDealer;

	/**
	 * @param imageDealer
	 */
	SaveCurrentCoverHandler(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
	}

	private MultiDBImage tempIm;
    	
    public void actionPerformed(ActionEvent evento) {
    	tempIm=(MultiDBImage) this.imageDealer.spinnerCovers.getValue();
    	SaveImageThread saveImageThread = new SaveImageThread(this, tempIm);
    	saveImageThread.setDaemon(true);
    	saveImageThread.start(); 	 
    }
}