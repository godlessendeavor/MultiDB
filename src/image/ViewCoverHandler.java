package image;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Errors;

class ViewCoverHandler implements ChangeListener {

    /**
	 * 
	 */
	private final ImageDealer imageDealer;

	/**
	 * @param imageDealer
	 */
	ViewCoverHandler(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
	}

	// manejar evento de cambio en lista
    public void stateChanged(ChangeEvent e) {
        JSpinner spinner = (JSpinner) e.getSource();
        //int lenght=0;
        try{
        	
        	this.imageDealer.multiIm.putImage(this.imageDealer.selectCoversView, ((MultiDBImage) spinner.getValue()));
        }catch(IndexOutOfBoundsException ex){
        	Errors.writeError(Errors.GENERIC_ERROR, ex.toString());
        }
    }
} //FIN HANDLER VIEW COVERS