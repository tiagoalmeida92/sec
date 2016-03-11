
public class ServerMain {
	
	public static void main(String[] args)
	{
		MultiThread server = new MultiThread();
		Thread t =new Thread(server);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e1) {
			
		}
		try {
		    Thread.sleep(20 * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}
}
