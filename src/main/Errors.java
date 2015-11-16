package main;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;



public class Errors extends ErrorBase{
	
	
///////////////////////////////////////////////////////////////////////////////////
//////////////////////SETUP LOGGING/////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////


	public static void setLogging(){
		setErrors();
		setWarnings();
		String dirName = "";
		String dirNameInfo = "";
		FileHandler fh; //Error log
		FileHandler fhi; //multipurpose info log
		String fileName = "multidb.log";
		String fileNameInfoLog = "multidbInfo.log";
		
		
		if (MultiDB.logPath!=null) dirName=MultiDB.logPath; 
		else{
			String appPath = System.getProperties().getProperty("user.dir"); 
			dirName= appPath + File.separator + fileName;
			dirNameInfo = appPath + File.separator + fileNameInfoLog;
			}
		
		try {
		
			//configure the logger with handler and formatter
			fh = new FileHandler(dirName, true);
			fhi = new FileHandler(dirNameInfo, true);
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
			infoLogger.addHandler(fhi);
			infoLogger.setLevel(Level.INFO);
			fh.setFormatter(new Errors().new LogFormatter());
			fhi.setFormatter(new Errors().new LogFormatter());
			
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
	}
		
	

public class LogFormatter extends Formatter {
    private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.SSS");
    //TODO: find correct level for ultimate function caller level
    private final int stackLenght = 8;
    
 
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append(df.format(new Date(record.getMillis()))).append(" - ");
        //builder.append("[").append(record.getSourceClassName()).append(".");
        //builder.append(record.getSourceMethodName()).append("] - ");
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace(); 
        for (int i = stackLenght - 1; i < stackTraceElements.length; i++){
        	StackTraceElement stTrace = stackTraceElements[i];
        	builder.append("[").append(stTrace.getClassName()).append(".").append(stTrace.getMethodName()).append(".").append(stTrace.getLineNumber()).append("]");        
        }        
        builder.append("[").append(record.getLevel()).append("] - ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }
 
    public String getHead(Handler h) {
        return super.getHead(h);
    }
 
    public String getTail(Handler h) {
        return super.getTail(h);
    }
}
	
}
