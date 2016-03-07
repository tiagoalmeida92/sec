import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;

public class Main {

	public static void main(String[] args)
	{	
			while(true)
			{
				new Thread(new Runnable() {
					public void run(){
						try{
							Socket connection;
							ServerSocket serverSocket = new ServerSocket(6978);
							ObjectInputStream inputStream;
							ObjectOutputStream outputStream;
							byte[] data,signature;
							PublicKey publicK;
							String method,id;
							connection = serverSocket.accept();
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
						} catch (IOException | ClassNotFoundException e) {
							
						}
					}
				}).start();
				
				new Thread(new Runnable() {
					public void run(){
						Service.GarbageCollection();
					}
				}).start();
			}
	}
}
