package image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SelectTypeCoverHandler implements ActionListener {

	/**
	 * 
	 */
	private final ImageDealer imageDealer;

	/**
	 * @param imageDealer
	 */
	SelectTypeCoverHandler(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
	}

	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().compareTo("Back")==0) {
			ImageDealer.frontCover=false;
			ImageDealer.otherCover=false;
			this.imageDealer.newNameField.setEnabled(false);
		}
		else if (e.getActionCommand().compareTo("Front")==0) {
			ImageDealer.frontCover=true;
			ImageDealer.otherCover=false;
			this.imageDealer.newNameField.setEnabled(false);
		}else{
			ImageDealer.frontCover=false;
			ImageDealer.otherCover=true;
			this.imageDealer.newNameField.setEnabled(true);
		}
	}
}