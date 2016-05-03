import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import Utils.OneWriteNReadByzantineRegularRegister;

public class Worker implements Runnable {

	private Socket _connection = null;
	private int _nReplicas;
	private int _nFaults;
	
	public Worker(Socket connection, int nReplicas, int nFaults)
	{
		_connection = connection;
		_nReplicas = nReplicas;
		_nFaults = nFaults;
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
			X509Certificate cert;
			inputStream = new ObjectInputStream(_connection.getInputStream());
			outputStream = new ObjectOutputStream(_connection.getOutputStream());
			method = (String) inputStream.readObject();
			
			switch(method)
			{
//				case "put_k": 	
//										
//					data = (byte[]) inputStream.readObject();
//					signature = (byte[]) inputStream.readObject();
//					publicK = (PublicKey) inputStream.readObject();
//					id = Service.putK(data,signature,publicK);
//					outputStream.writeObject(id);
//					Service.filesGarbageCollection();
//					break;
//					
//				case "put_h":
//					data = (byte[]) inputStream.readObject();
//					id = Service.putH(data);
//					outputStream.writeObject(id);
//					break;
//				
//				case "get":
//					id = (String) inputStream.readObject();
//					data = Service.get(id);
//					outputStream.writeObject(data);
//					break;
				case "storePubKey":
					cert = (X509Certificate) inputStream.readObject();
					String result = Service.storePubKey(cert);
					outputStream.writeObject(result);
					outputStream.flush();
					break;
				case "readPubKeys":
					outputStream.writeObject(Service.readPubKeys());
					break;
				default: 
					break;
			}
			
			data = (byte[]) inputStream.readObject();
			OneWriteNReadByzantineRegularRegister on = 
					new OneWriteNReadByzantineRegularRegister
						(_nReplicas, _nFaults);
			on.Deliver(_connection, data);
			
		} catch (IOException | ClassNotFoundException e) {
			
		}
	}

}
