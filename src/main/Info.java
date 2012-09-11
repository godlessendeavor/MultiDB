package main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Info {
	
	public static MultiDB f;
	private static JFrame infoFrame;
	private static JTextArea infoText;
	private static JScrollPane infoScroll; 
    public static Logger logger = Logger.getLogger("MultiDBlog");
	public static Map<Integer, String> map= new HashMap<Integer,String>();
	//Codes for definition of warnings
	
	public static final Integer INFO_MSG =-1;
      
	//Strings for definition of errors
    public static final String SEE_LOG = "See log for details";
    public static final String UNDEF_MSG="Undefined mesg";
        
    public Info(){
    	infoFrame = new JFrame("Info");
		infoFrame.setSize(500, 300);
		infoText = new JTextArea();
		infoText.setWrapStyleWord(true);
		infoText.setLineWrap(true);
		infoScroll = new JScrollPane(infoText);
		infoScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		infoScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		infoFrame.add(infoScroll);
    	
    }
    
    
    public static void setWarnings(){
    	map.put(INFO_MSG,"Info message");
    }
    
   
	
	//WARNINGS, doesn't log
	//directory warning
	public static void errorDir(String dir) {
	    // the following statement is used to log any messages   
        //logger.log(Level.WARNING,"El directorio especificado no es correcto: " + dir);
		JOptionPane.showMessageDialog(f,"Specified directory is wrong: " + dir);
	}

	//syntax warning
	public static void errorSint(String dir) {
        //logger.log(Level.WARNING,"El directorio no responde a las especificaciones habituales: "+ dir);
		f.reviewView.append("Syntax error, directory wrong: "+ dir + "\n");
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
