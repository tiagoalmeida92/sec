package Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Files {

	public static String WriteFile(String path, byte[] data){
		BufferedOutputStream writer = null;
		try {			
			writer = new BufferedOutputStream(new FileOutputStream(path));
			writer.write(data);
			writer.close();
			return "Success";
		} catch (IOException e) {
			return "Writing file failure";
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException ex) {
				return null;
			}
		}
	}
	
	public static void ListFiles(String directoryName, ArrayList<File> files) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	        	ListFiles(file.getAbsolutePath(), files);
	        }
	    }
	}
	
	public static boolean Exists(String filename)
	{
		File currentDirFile = new File("");
		String currentProjectDir = currentDirFile.getAbsolutePath();
		File file = new File(currentProjectDir+"\\"+filename);
		return file.exists();
	}
	
	public static void DeleteFile(String filename)
	{
		File currentDirFile = new File("");
		String currentProjectDir = currentDirFile.getAbsolutePath();
		File file = new File(currentProjectDir+"\\"+filename);
		file.delete();
	}
	
	public static void DeleteAllBlockServerFiles()
	{
		File currentDirFile = new File("");
		for (File file : currentDirFile.listFiles()) {
			String fName = file.getName();
		    if (fName.endsWith(Constants.CBLOCKEXTENSION) || fName.endsWith(Constants.PKBLOCKEXTENSION)) {
		        file.delete();
		    }
		}
	}
	
	public static boolean FindOnContent(File file, String id)
	{
		Scanner scanner = null;
		try {
			scanner = new Scanner(file).useDelimiter("\\Z");
			String contents = scanner.next();
			return contents.indexOf(id) != -1;
		} catch (FileNotFoundException e) {
			return false;
		}finally{
			if(scanner != null)
				scanner.close();
		}
	   
	}
}
