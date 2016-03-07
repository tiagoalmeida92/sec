package Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
}
