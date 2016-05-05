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
		int port = Integer.valueOf(args[0]);
		int nReplicas = Integer.valueOf(args[1]);
		int nFaults = Integer.valueOf(args[2]);

		System.out.println("Starting Block Server... Port: "+ (port));
		MultiThread server = new MultiThread(port, nReplicas , nFaults);
		Thread t = new Thread(server);
		t.start();			
		
		try {
			t.join();
			Thread.sleep(20 * 1000);
		} catch (InterruptedException e1) {
			
		}
		finally{
			System.out.println("Stopping Block Server... Port: "+ (port));
			server.stop();
		}

	}
	

}
