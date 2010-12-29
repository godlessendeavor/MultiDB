package main.exceptions;

/**
 *
 * @author thrasher
 */
public class MP3FilesNotFound extends Exception{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MP3FilesNotFound(String message) {
        super(message);
    }

    public MP3FilesNotFound() {
        super();
    }
    
}
