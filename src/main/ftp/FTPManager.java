package main.ftp;

import java.io.File;
import java.io.IOException;

import cz.dhl.ftp.Ftp;
import cz.dhl.ftp.FtpFile;
import cz.dhl.io.CoFile;
import cz.dhl.io.CoLoad;
import cz.dhl.io.LocalFile;

import main.MultiDB;


public class FTPManager {
	
	private final String ftpUser=MultiDB.ftpUser;
	private final String ftpPswd=MultiDB.ftpPassword;
	private final String ftpHost=MultiDB.ftpHost;
	private final String ftpDirectory=MultiDB.ftpDirectory;
	
	public FTPManager() {
	}
	
	public void upload(File file) throws IOException{
		Ftp ftpCl = new Ftp();
		/* connect & login to host */
		ftpCl.connect(ftpHost,Ftp.PORT);
		ftpCl.login(ftpUser,ftpPswd);
	  
		/* destination FtpFile remote file */
		CoFile to = new FtpFile("/"+ftpDirectory+"/"+file.getName(),ftpCl);
		System.out.println(to.getAbsolutePath());
		/* source LocalFile file*/
		CoFile from = new LocalFile(file.getAbsolutePath());
		System.out.println(from.getAbsolutePath());
		/* uploading */
		CoLoad.copy(to,from);
		/* disconnect from server
	       * this must be always run */
		ftpCl.disconnect(); 
		System.out.println("Exit upload");
	}
}
