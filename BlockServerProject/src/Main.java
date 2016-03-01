import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Main {

	public static void main(String[] args)
	{
		try {
			ServerSocket serverSocket = new ServerSocket(6978);
			while(true)
			{
				Socket connection = serverSocket.accept();
				BufferedReader buffClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
			}
		} catch (IOException e) {
			
		}
	}
}
