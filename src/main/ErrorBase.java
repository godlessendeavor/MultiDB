package main;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public abstract class ErrorBase {
	
	public static MultiDB f;
    protected static Logger logger = Logger.getLogger("MultiDBlog");
	private static Map<Integer, String> errorsMap= new HashMap<Integer,String>();
	private static Map<Integer, String> warningsMap= new HashMap<Integer,String>();
	//Codes for definition of errors
	//Database errors
	public static final Integer LOADING_DB_DRIVER =-1;
    public static final Integer OPENING_DB =-2;
    public static final Integer CLOSING_DB =-3;
    public static final Integer DB_SELECT =-4;
    public static final Integer DB_INSERT =-5;
    public static final Integer DB_DELETE =-6;
    public static final Integer DB_UPDATE =-7;
    public static final Integer DB_UNDEFINED = -8;
    public static final Integer DB_BUP = -9;
    public static final Integer DB_RESTORE = -10;
    
    //WEB READER ERRORS
    public static final Integer UPLOADING_BUP=-101;
    public static final Integer GROUP_NOT_FOUND=-102;
    public static final Integer MOVIE_NOT_FOUND=-103;
    
    //FILE ERRORS
    public static final Integer COPYING_FILE_EXISTS=-204;
    public static final Integer FILE_NOT_FOUND=-205;
    public static final Integer COPYING_IOERROR=-206;
    public static final Integer WRITING_IOERROR=-207;
    public static final Integer FILE_DELETE_ERROR=-208;
    public static final Integer FILE_NOT_POSIBLE_TO_CREATE=-209;
    public static final Integer FILE_IO_ERROR=-210;
    
    //ERROR SAVING REVIEW
    public static final Integer SAVING_REVIEW=-300;
    
    
    //WEB ERRORS
    public static final Integer WEB_NOT_FOUND=-401;
    public static final Integer WEB_MALF_URL=-402;
    public static final Integer WEB_ERROR_NOT_FOUND=-403;
    
    //IMAGE ERRORS
    public static final Integer IMAGE_NOT_SAVED=-501;
   
    //FORMAT ERRORS
    public static final Integer NEGATIVE_NUMBER=-601;

    //NULL ERRORS
    public static final Integer VAR_NULL=-701;
    
    //ERROR FOR PRINTING STACKTRACE
    public static final Integer GENERIC_ERROR = -801;
    
	//Strings for definition of errors
    public static final String SEE_LOG = "See log for details";
    public static final String ERROR_UNDEF="Undefined error";
    
    
    //////////////////////////////////WARNINGS/////////////////
    //FILE WARNINGS
    public static final Integer FILE_DELETE_WARNING=-1;
    
    public static final Integer IMAGE_NOT_FOUND=-2;
    
    public static final Integer WRONG_DIRECTORY=-3;
    
        
    public static void setErrors(){
    	errorsMap.put(LOADING_DB_DRIVER,"Error loading database driver");
    	errorsMap.put(OPENING_DB,"Error opening database");
    	errorsMap.put(CLOSING_DB,"Error closing database");
    	errorsMap.put(DB_SELECT,"Error on select sentence on database");
    	errorsMap.put(DB_INSERT,"Error on insert sentence on database");
    	errorsMap.put(DB_DELETE,"Error on delete sentence on database");
    	errorsMap.put(DB_UPDATE,"Error on update sentence on database");
    	errorsMap.put(DB_UNDEFINED,"Error on database. "+SEE_LOG);
    	errorsMap.put(DB_BUP,"Error while making backup of database");
    	errorsMap.put(DB_RESTORE,"Error while restoring backup of database");
    	errorsMap.put(UPLOADING_BUP,"Error while uploading backup to web");
    	errorsMap.put(GROUP_NOT_FOUND,"Error while looking for band name on web");
    	errorsMap.put(MOVIE_NOT_FOUND,"Film not found");
    	errorsMap.put(COPYING_FILE_EXISTS,"Error while copying file, file already exists");
    	errorsMap.put(FILE_NOT_FOUND,"Error, file not found");
    	errorsMap.put(FILE_NOT_POSIBLE_TO_CREATE, "Error trying to create folder or file");
    	errorsMap.put(FILE_IO_ERROR, "Error trying to read or write file, IOError");
    	errorsMap.put(COPYING_IOERROR,"Error copying file, IOError");
    	errorsMap.put(WRITING_IOERROR,"Error writing to file, IOError");
    	errorsMap.put(FILE_DELETE_ERROR,"Error deleting file");
    	errorsMap.put(SAVING_REVIEW,"Error saving review");
    	errorsMap.put(NEGATIVE_NUMBER,"Cannot work with negative numbers"); 
    	errorsMap.put(WEB_NOT_FOUND,"Error retrieving web, web not found");
    	errorsMap.put(WEB_MALF_URL,"Error retrieving web, url malform error");
    	errorsMap.put(WEB_ERROR_NOT_FOUND,"Error, web server returns no expected data");
    	errorsMap.put(IMAGE_NOT_SAVED,"Couldn't save image");   
    	errorsMap.put(VAR_NULL,"Error with some var null");
    	errorsMap.put(GENERIC_ERROR,"Error, see log for details"); 	
    }
    
    public static void setWarnings(){
    	warningsMap.put(FILE_DELETE_WARNING,"Could not delete file");
    	warningsMap.put(IMAGE_NOT_FOUND,"Could not find image on web");
    	warningsMap.put(WRONG_DIRECTORY,"Specified directory is wrong");
    }
    
    //without message
	public static void showError(int code){
		if (errorsMap.containsKey(code)) {
			JOptionPane.showMessageDialog(f, errorsMap.get(code));
			logger.log(Level.SEVERE,errorsMap.get(code));
		}
		else {
			JOptionPane.showMessageDialog(f, ERROR_UNDEF);
			logger.log(Level.SEVERE,ERROR_UNDEF);
		}
	}
	
	//with message
	public static void showError(int code,String message){
		if (errorsMap.containsKey(code)) {
			JOptionPane.showMessageDialog(f, errorsMap.get(code));
			logger.log(Level.SEVERE,errorsMap.get(code)+"\n"+message);
		}			
		else {
			JOptionPane.showMessageDialog(f, ERROR_UNDEF);
			logger.log(Level.SEVERE,message);
		}		
	}
	
	//with logging message but no alert window
	public static void writeError(int code,String message){
		if (errorsMap.containsKey(code)) {
			logger.log(Level.SEVERE,errorsMap.get(code)+"\n"+message);
		}			
		else {
			logger.log(Level.SEVERE,message);
		}		
	}
	

	//syntax warning
	public static void errorSint(String dir) {
        //logger.log(Level.WARNING,"El directorio no responde a las especificaciones habituales: "+ dir);
		f.reviewView.append("Syntax error, directory wrong: "+ dir + "\n");
	}
	
    //without message
	public static void showWarning(int code){
		if (warningsMap.containsKey(code)) {
			JOptionPane.showMessageDialog(f, warningsMap.get(code));
		}
		else {
			JOptionPane.showMessageDialog(f, ERROR_UNDEF);
			logger.log(Level.SEVERE,ERROR_UNDEF);
		}
	}

	//with message
	public static void showWarning(int code,String message){
		if (warningsMap.containsKey(code)) {
			JOptionPane.showMessageDialog(f, warningsMap.get(code)+": "+message);
		}			
		else {
			JOptionPane.showMessageDialog(f, ERROR_UNDEF);
			logger.log(Level.SEVERE,message);
		}		
	}

	public static int confirmDialog(String message){
		return JOptionPane.showConfirmDialog(f, message);
	}
	
	public void log(Level level,String message){
		logger.log(level,message);
	}
	
///////////////////////////////////////////////////////////////////////////////////
//////////////////////SETUP LOGGING/////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////


	
}
