import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;

public class Worker implements Runnable {

	private Socket connection = null;
	
	public Worker(Socket connection)
	{
		this.connection = connection;
	}
	
	@Override
	public void run() {
		try
		{
			ObjectInputStream inputStream;
			ObjectOutputStream outputStream;
			byte[] data,signature;
			PublicKey publicK;
			String method,id;
			inputStream = new ObjectInputStream(connection.getInputStream());
			outputStream = new ObjectOutputStream(connection.getOutputStream());
			method = (String) inputStream.readObject();
			
			switch(method)
			{
				case "put_k": 
					data = (byte[]) inputStream.readObject();
					signature = (byte[]) inputStream.readObject();
					publicK = (PublicKey) inputStream.readObject();
					id = Service.putK(data,signature,publicK);
					outputStream.writeObject(id);
					Service.filesGarbageCollection();
					break;
					
				case "put_h":
					data = (byte[]) inputStream.readObject();
					id = Service.putH(data);
					outputStream.writeObject(id);
					break;
				
				case "get":
					id = (String) inputStream.readObject();
					data = Service.get(id);
					outputStream.writeObject(data);
					break;
				default: 
					break;
			}
			
		} catch (IOException | ClassNotFoundException e) {
			
		}
	}

}