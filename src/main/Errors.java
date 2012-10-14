package main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;

public abstract class Errors {
	
	public static MultiDB f;
    private static Logger logger = Logger.getLogger("MultiDBlog");
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
    public static final Integer UPLOADING_BUP=-11;
    public static final Integer GROUP_NOT_FOUND=-12;
    public static final Integer MOVIE_NOT_FOUND=-13;
    
    //FILE ERRORS
    public static final Integer COPYING_FILE_EXISTS=-14;
    public static final Integer FILE_NOT_FOUND=-15;
    public static final Integer COPYING_IOERROR=-16;
    public static final Integer WRITING_IOERROR=-17;
    public static final Integer FILE_DELETE_ERROR=-18;
    
    //ERROR SAVING REVIEW
    public static final Integer SAVING_REVIEW=-19;
    
    
    //WEB ERRORS
    public static final Integer WEB_NOT_FOUND=-20;
    public static final Integer WEB_MALF_URL=-21;
    
    //IMAGE ERRORS
    public static final Integer IMAGE_NOT_SAVED=-22;
   
    //FORMAT ERRORS
    public static final Integer NEGATIVE_NUMBER=-23;

    //NULL ERRORS
    public static final Integer VAR_NULL=-24;
    
    //ERROR FOR PRINTING STACKTRACE
    public static final Integer GENERIC_STACK_TRACE = -25;
    
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
    	errorsMap.put(COPYING_IOERROR,"Error copying file, IOError");
    	errorsMap.put(WRITING_IOERROR,"Error writing to file, IOError");
    	errorsMap.put(FILE_DELETE_ERROR,"Error deleting file");
    	errorsMap.put(SAVING_REVIEW,"Error saving review");
    	errorsMap.put(NEGATIVE_NUMBER,"Cannot work with negative numbers"); 
    	errorsMap.put(WEB_NOT_FOUND,"Error retrieving web, web not found");
    	errorsMap.put(WEB_MALF_URL,"Error retrieving web, url malform error");
    	errorsMap.put(IMAGE_NOT_SAVED,"Couldn't save image");   
    	errorsMap.put(VAR_NULL,"Error with some var null");
    	errorsMap.put(GENERIC_STACK_TRACE,"Error, see log for details"); 	
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
	
	/////////////////////////////////////////////WARNINGS, doesn't log

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

///////////////////////////////////////////////////////////////////////////////////
//////////////////////SETUP LOGGING/////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////


	public static void setLogging(){
		String dirName="";
		FileHandler fh;
		String fileName = "multidb.log";
		
		String os = System.getProperty("os.name").toLowerCase();
		if((os.indexOf( "win" ) >= 0)){
			if (MultiDB.logPath!=null) dirName=MultiDB.logPath; 
			else{
				String appPath = System.getProperties().getProperty("user.dir"); 
				dirName=appPath+File.separator+fileName;
				}
		}else if(os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0){
			dirName="/var/log/multidb.log";
		}else { return;}
		
		try {
		
			// This block configure the logger with handler and formatter
			fh = new FileHandler(dirName, true);
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
	}
	
	public void log(Level level,String message){
		logger.log(level,message);
	}
	
	
}
