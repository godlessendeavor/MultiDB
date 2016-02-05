package db.CSV;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;

import main.Errors;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CSV {
	String[] outline;
	private ArrayList<String[]> data=new ArrayList<String[]>();

	public ArrayList<String[]> readFile(String fileName,int rowSize) {
		try {
			data.clear();
			Reader fin = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
			CsvReader csv = new CsvReader(fin);
			csv.setRecordDelimiter('\n');
			csv.setDelimiter(',');
			csv.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
			while (csv.readRecord())
			{
				outline = new String[rowSize];
				for (int i=0;i<rowSize;i++){
					outline[i]=csv.get(i);
					//System.out.println(outline[i]);
				}
				
				data.add(outline);
			}
			fin.close();
			csv.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Errors.showError(Errors.FILE_NOT_FOUND, "File name: " + fileName);
		} catch (IOException e) {
			e.printStackTrace();
			Errors.showError(Errors.FILE_IO_ERROR, "File name: " + fileName);
		}
		return data;
	}
	
	public static int storeCSV(String filename,ArrayList<String[]> datain){
		// before we open the file check to see if it already exists
		boolean alreadyExists = new File(filename).exists();
		String[] currRow;	
		try {
			// use FileWriter constructor that specifies open for appending
			OutputStreamWriter outstream = new OutputStreamWriter(new FileOutputStream(new File(filename)),"UTF-8");
			CsvWriter csvOutput = new CsvWriter(outstream, ',');
			csvOutput.setEscapeMode(CsvWriter.ESCAPE_MODE_BACKSLASH);
			csvOutput.setRecordDelimiter('\n');
			// if the file didn't already exist then we need to write out the header line
			if (!alreadyExists)
			{
				for (int ind=0; ind<datain.size();ind++){
					currRow=datain.get(ind);
					for (int currField=0;currField<currRow.length;currField++){
						csvOutput.write(currRow[currField]);
					}
					csvOutput.endRecord();
				}
				csvOutput.close();
			}
			else {
				outstream.close();
				return Errors.COPYING_FILE_EXISTS;			
			}
			outstream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return Errors.FILE_IO_ERROR;
		}
		return 0;
	}
}
