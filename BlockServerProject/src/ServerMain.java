import java.awt.List;
import java.util.ArrayList;
import java.util.Scanner;

import Utils.Constants;

public class ServerMain {
	
	public static class BlockServer{
		public MultiThread _server;
		public Thread _t;
		
		public BlockServer(MultiThread server, Thread t)
		{
			_server = server;
			_t = t;
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println("Starting Block Servers...");
		System.out.println("How many Block Server replicas?");
		int nReplicas = new Scanner(System.in).nextInt();
		ArrayList<BlockServer> replicas = new ArrayList<BlockServer>();
		for( int i = 0; i<nReplicas; ++i){
			System.out.println("Starting Block Server... Port: "+ (Constants.PORT+i));
			MultiThread server = new MultiThread(Constants.PORT+i);
			Thread t = new Thread(server);
			BlockServer bServer = new BlockServer(server, t);
			replicas.add(bServer);
			t.start();			
		}
		for(int i = 0; i< nReplicas; ++i)
		{
			BlockServer bServer = replicas.get(i);
			Thread t = bServer._t;
			try {
				t.join();
				Thread.sleep(20 * 1000);
			} catch (InterruptedException e1) {
				
			}
			finally{
				System.out.println("Stopping Block Server");
				bServer._server.stop();
			}
		}
	}
	

}
