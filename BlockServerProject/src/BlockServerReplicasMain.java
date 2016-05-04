import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class BlockServerReplicasMain {

	public static void main(String[] args)
	{
		System.out.println("Starting Block Servers...");
		String port = args[0];
		String nFaults = args[1];
		int nReplicas = 3*Integer.valueOf(args[1]);
		ArrayList<Process> processes = new ArrayList<Process>();
		
		ArrayList<File> allProjectFiles = new ArrayList<File>();
		ArrayList<File> sharedProjectFiles = new ArrayList<File>();
		ListFiles(new File("").getAbsolutePath(),allProjectFiles);
		ListFiles(new File("").getAbsolutePath()+"\\..\\SharedProject",sharedProjectFiles);
		allProjectFiles.addAll(sharedProjectFiles);
		String compileServerPath = null;
		String constantsSharedPath = null;
		String runServerPath = null;	
		for(File f : allProjectFiles)
		{
			if(f.getName().contains("ServerMain.java"))
				compileServerPath = f.getAbsolutePath();
			if(f.getName().contains("Constants.java"))
				constantsSharedPath = f.getAbsolutePath();
			if(f.getName().contains("ServerMain.class"))
				runServerPath = f.getAbsolutePath();
		}
		if(compileServerPath != null && runServerPath != null)
		{
			try
			{
				pipeOutput(new ProcessBuilder("javac -cp \""+ constantsSharedPath.substring(0, 
						constantsSharedPath.indexOf("Constants.java"))+"*.jar" +"\" "+
						constantsSharedPath.substring(0, 
								constantsSharedPath.indexOf("Constants.java"))+"*.java").start());
				pipeOutput(new ProcessBuilder("javac ",
						compileServerPath.substring(0, 
								compileServerPath.indexOf("ServerMain.java"))+"*.java").start());
				for(int i = 0; i < nReplicas; ++i)
				{
					//serverProcessPath.substring(0,serverProcessPath.indexOf(".java")
					ProcessBuilder pb = new ProcessBuilder("java", "-cp",
							runServerPath.substring(0,runServerPath.indexOf(".class"))
							+" "+(port+i)+" "+String.valueOf(nReplicas)+" "+nFaults);
					Process process = null;
					process = pb.start();
					pipeOutput(process);
				}
			} catch (IOException e) {
				System.out.println("Error running BS: " + e.getMessage());
				return;
			}
			
			boolean b;
			do{
				System.out.println("Exit all subprocesses? <True or False>");
				b = new Scanner(System.in).nextBoolean();
			}while(!b);
			for(Process process : processes)
			{
				process.destroy();
			}
		}
	}
	
	private static void pipeOutput(Process process) {
	    pipe(process.getErrorStream(), System.err);
	    pipe(process.getInputStream(), System.out);
	}

	private static void pipe(final InputStream src, final PrintStream dest) {
	    new Thread(new Runnable() {
	        public void run() {
	            try {
	                byte[] buffer = new byte[1024];
	                for (int n = 0; n != -1; n = src.read(buffer)) {
	                    dest.write(buffer, 0, n);
	                }
	            } catch (IOException e) { // just exit
	            }
	        }
	    }).start();
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
	
}
