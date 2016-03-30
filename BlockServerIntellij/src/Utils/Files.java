package Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class Files {

	public static String WriteFile(String path, byte[] data){
		BufferedOutputStream writer = null;
		try {	
			File theDir = new File(path.substring(0, path.lastIndexOf("\\")));
			if(!theDir.exists())
				theDir.mkdir();
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
	    if(fList!= null)
	    {
		    for (File file : fList) {
		        if (file.isFile()) {
		            files.add(file);
		        } else if (file.isDirectory()) {
		        	ListFiles(file.getAbsolutePath(), files);
		        }
		    }
	    }
	}
	
	public static boolean Exists(String fileNameWithExtension)
	{
		return GetPath(fileNameWithExtension) != null;
	}
	
	public static String GetPath(String fileNameWithExtension)
	{
		File currentDirFile = new File("");
		String absPath = currentDirFile.getAbsolutePath();
		String currentProjectDir = absPath.substring(0,absPath.lastIndexOf(File.separator));
		ArrayList<File> files = new ArrayList<File>();
		ListFilesWithFileName(currentProjectDir, fileNameWithExtension, files);
		if(files.size()!= 0)
			return files.get(0).getAbsolutePath();
		return null;
	}
	
	private static void ListFilesWithFileName(String directoryName, String fileNameWithExtension, ArrayList<File> files) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    if(fList!= null)
	    {
		    for (File file : fList) {
		        if (file.isFile() && (file.getName()).equals(fileNameWithExtension)) {
		            files.add(file);
		        } else if (file.isDirectory()) {
		        	ListFilesWithFileName(file.getAbsolutePath(), fileNameWithExtension,files);
		        }
		    }
	    }
	}
	
	public static void DeleteFile(String filename)
	{
		File currentDirFile = new File("");
		String currentProjectDir = currentDirFile.getAbsolutePath();
		File file = new File(currentProjectDir+"\\"+filename);
		file.delete();
	}
	
	private static void RecursiveDelete(final File dir) {
		final File[] files = dir.listFiles();
		if (files != null) {
			for (final File file : files) {
				String fName = file.getName();
				if (file.isDirectory()) {
					RecursiveDelete(file);
				} else if (fName.endsWith(Constants.CBLOCKEXTENSION) 
						|| fName.endsWith(Constants.PKBLOCKEXTENSION)) {
					file.delete();
				}
			}
		}
	}
	
	public static void DeleteAllBlockServerFiles()
	{
		File currentDirFile = new File("");
		String absPath = currentDirFile.getAbsolutePath();
		String currentProjectDir = absPath.substring(0,absPath.lastIndexOf(File.separator));
		currentDirFile = new File(currentProjectDir);
		RecursiveDelete(currentDirFile);
	}
	
	public static boolean FindOnContent(File file, String id)
	{
		byte[] myByteArray = new byte[(int) file.length()];
		
		BufferedInputStream reader = null;
		try {
			reader = new BufferedInputStream(new FileInputStream(file));
			reader.read(myByteArray, 0, myByteArray.length);
			reader.close();
			String source = new String(myByteArray);			
			return source.indexOf(id) != -1;
		} catch (IOException e) {
			return false;
		}
	}
	
}
