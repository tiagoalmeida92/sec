import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.*;

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
				CommunicationParameters params = (CommunicationParameters) inputStream.readObject();
				
				switch(method)
				{
					case "put_k": Service.putK(params);break;
					default: break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			
		}
	}
}
