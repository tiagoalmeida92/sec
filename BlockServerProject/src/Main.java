import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.*;
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
						Service.putK(data,signature,publicK);
						break;
						
					case "put_h":
						data = (byte[]) inputStream.readObject();
						Service.putH(data);
						break;
					
					case "get"
						id = (String) inputStream.readObject();
						data = Service.get(id);
						
					default: break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			
		}
	}
}
