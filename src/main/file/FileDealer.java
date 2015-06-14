package main.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import main.Errors;
import main.MultiDB;
import sun.awt.shell.ShellFolder;

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
                        if (!newDir.mkdir()) Errors.showError(Errors.FILE_NOT_POSIBLE_TO_CREATE, "Tried to create directory: " + newDir.getName());
                           else copyFiles(currentFile,newDir);
                    }
                }else{
                    newDir=new File(toFile.getAbsolutePath()+sep+files[i]);
                    //System.out.println("copying file "+currentFile.getAbsolutePath()+"\n into "+newDir.getAbsolutePath());
                    if (!fileCopy(currentFile,newDir)){
                    	if (Errors.confirmDialog("Could not copy file, continue with the rest?")==JOptionPane.NO_OPTION){
                    		break;
                    	}
                    }
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
        	Errors.showError(Errors.FILE_NOT_POSIBLE_TO_CREATE,"File not possible to create: "+ newDir.getName());
        	return true;
        }
        else return copyFiles(fromFile,newDir);
    }

    //copy file
    //return true if successful
    public static boolean fileCopy(File fromFile, File toFile){
        FileInputStream fromStream = null;
        FileOutputStream toStream = null;
        boolean result=true;
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
        	result = false;
        }
        finally {
            if (fromStream != null) {
                try {
                    fromStream.close();
                } catch (IOException e) {
                	Errors.showError(Errors.GENERIC_ERROR,e.toString());
                	result = false;
                }
            }
            if (toStream != null) {
                try {
                    toStream.close();
                } catch (IOException e) {
                	Errors.showError(Errors.COPYING_IOERROR,"Could not copy file "+fromFile+ " into "+toFile+"\nError: "+e.toString());
                	result = false;
                }
            }
        }
        return result;
    }
    
    public static File selectFile(MultiDB f,String message){
    	JFileChooser fc = new JFileChooser(new NewFileSystemView());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogTitle(message);
        int status = fc.showOpenDialog(f);
        if (status == JFileChooser.APPROVE_OPTION){  
	           File file = fc.getSelectedFile();
	           if (file.isDirectory()) return null;
	           return file;
        }else return null;    	
    }
    
    public static File selectPath(MultiDB f,String message){
    	JFileChooser fc = new JFileChooser(new NewFileSystemView());
        fc.setDialogTitle(message);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int status = fc.showOpenDialog(f);
        if (status == JFileChooser.APPROVE_OPTION){  
	        File file = fc.getSelectedFile();
	        if (!file.isDirectory()) return null;
	        return file;
        }else return null;    	
    }
    
    //////////////////////////////////////WORKARAOUND FOR BUGS IN XP AND VISTA FOR JFILECHOOSER///////////////
    public static class NewFileSystemView extends FileSystemView {
        public File createNewFolder(File containingDir) throws IOException {
            return FileSystemView.getFileSystemView().createNewFolder(containingDir);
        }

        public File[] getRoots() {
            List<File> result = new ArrayList<File>();

            for (File file : (File[]) ShellFolder.get("fileChooserComboBoxFolders")) {
                if (!(file instanceof ShellFolder) || !((ShellFolder) file).isLink()) {
                    result.add(file);
                }
            }

            return result.toArray(new File[result.size()]);
        }
    }
    
    
    
}
