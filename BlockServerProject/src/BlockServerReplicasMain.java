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
		int port = Integer.valueOf(args[0]);
		String nFaults = args[1];
		int nReplicas = 3 * Integer.valueOf(args[1]) + 1;
		ArrayList<Process> processes = new ArrayList<Process>();
		 
		try
		{
			for(int i = 0; i < nReplicas; ++i)
			{
				processes.add(executeProcess(ServerMain.class,
						String.valueOf(port+i), 
						String.valueOf(nReplicas), nFaults));	
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
	
	private static Process executeProcess(Class klass, 
			String port, String nReplicas, String nFaults) throws IOException{
		String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = klass.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className,
            			port, nReplicas, nFaults);
        Process process = builder.start();
        pipeOutput(process);
        return process;
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
