import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;

public class Main {

	public static void main(String[] args)
	{
		try {
			ServerSocket serverSocket = new ServerSocket(6978);
			while(true)
			{
				Socket connection = serverSocket.accept();
				ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream());
				ObjectOutputStream outputStream = new ObjectOutputStream(connection.getOutputStream());
				String method = (String) inputStream.readObject();
				
				byte[] data,signature;
				PublicKey publicK;
				String id;
				switch(method)
				{
					case "put_k": 
						data = (byte[]) inputStream.readObject();
						signature = (byte[]) inputStream.readObject();
						publicK = (PublicKey) inputStream.readObject();
						id = Service.putK(data,signature,publicK);
						outputStream.writeObject(id);
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
					default: break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			
		}
	}
}
