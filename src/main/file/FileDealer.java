package main.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import main.Errors;

public class FileDealer {
	 //copy files from one folder to another 
    public static boolean copyFiles(File fromFile, File toFile){
        //System.out.println("recibimos los paths"+fromFile.getAbsolutePath()+" y "+toFile.getAbsolutePath());
        File currentFile;
        File newDir;
        String sep = File.separator;
        
        if (fromFile.isDirectory()) {
            String [] files = fromFile.list();
            for (int i = 0; i < files.length; i++) {
                currentFile=new File(fromFile.getAbsolutePath()+sep+files[i]);
                if (currentFile.isDirectory()){
                    newDir = new File(toFile+sep+currentFile.getName());
                    if (newDir.exists()) 
                    	{
                    	    Errors.showError(Errors.COPYING_FILE_EXISTS,"File already exists while trying to copy: "+newDir.getAbsolutePath());
                    		//reviewView.append("File already exists: "+newDir.getAbsolutePath()+"\n");
                    		return true;
                    	}
                    else {
                        if (!newDir.mkdir()) Errors.showError(Errors.FILE_NOT_FOUND,"File not found: "+ newDir.getName());
                           else copyFiles(currentFile,newDir);
                    }
                }else{
                    newDir=new File(toFile.getAbsolutePath()+sep+files[i]);
                    //System.out.println("copying file "+currentFile.getAbsolutePath()+"\n into "+newDir.getAbsolutePath());
                    fileCopy(currentFile,newDir);
                }
            }
        } else {
            newDir= new File(toFile.getAbsolutePath()+sep+fromFile.getName());
            //System.out.println("(2) copying file "+fromFile.getAbsolutePath()+"\n into "+newDir.getAbsolutePath());
            fileCopy(fromFile,newDir);
        }
        return false;
    }

    //copy folder and files inside folder to another folder already created
    public static boolean copyFolder(File fromFile, File toFile){

        File newDir = new File(toFile+File.separator+fromFile.getName());
        if (!newDir.mkdir()){
        	Errors.showError(Errors.FILE_NOT_FOUND,"File not found: "+ newDir.getName());
        	return true;
        }
        else return copyFiles(fromFile,newDir);
    }

    //copy file
    public static void fileCopy(File fromFile, File toFile){
        FileInputStream fromStream = null;
        FileOutputStream toStream = null;
        try {
            fromStream = new FileInputStream(fromFile);
            toStream = new FileOutputStream(toFile);
            byte[] buffer = new byte[32768];
            int bytesRead;

            while ((bytesRead = fromStream.read(buffer)) != -1) {
                toStream.write(buffer, 0, bytesRead); // write
            }
        } catch (IOException ex) {
        	Errors.showError(Errors.COPYING_IOERROR,"Could not copy file "+fromFile+ " into "+toFile+"\nError: "+ex.toString());
        	//reviewView.append("Could not copy file "+fromFile+ " into "+toFile+"\n");
        }
        finally {
            if (fromStream != null) {
                try {
                    fromStream.close();
                } catch (IOException e) {
                	Errors.showError(Errors.GENERIC_STACK_TRACE,e.toString());
                }
            }
            if (toStream != null) {
                try {
                    toStream.close();
                } catch (IOException e) {
                	Errors.showError(Errors.COPYING_IOERROR,"Could not copy file "+fromFile+ " into "+toFile+"\nError: "+e.toString());
                }
            }
        }
    }
}
