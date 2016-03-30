
public class ServerMain {
	
	public static void main(String[] args)
	{
		System.out.println("Starting Block Server");
		MultiThread server = new MultiThread();
		Thread t =new Thread(server);
		t.start();
		try {
			t.join();
			Thread.sleep(20 * 1000);
		} catch (InterruptedException e1) {
			
		}
		finally{
			System.out.println("Stopping Block Server");
			server.stop();
		}
	}
}
