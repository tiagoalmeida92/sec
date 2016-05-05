import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import Utils.AuthPerfectPointToPointLinks;
import Utils.Constants;
import Utils.OneWriteNReadByzantineRegularRegister;
import Utils.Security;

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
			String dataStr;
			PublicKey publicK;
			String method,id;
			X509Certificate cert;
			inputStream = new ObjectInputStream(_connection.getInputStream());
			outputStream = new ObjectOutputStream(_connection.getOutputStream());
			method = (String) inputStream.readObject();
			
			switch(method)
			{
				case "put_k": 	
					//verify			
					byte[] m = 
							AuthPerfectPointToPointLinks.Deliver(_connection);
					id = Service.BizantinePutK(m);
					//authenticate
					AuthPerfectPointToPointLinks.Send(_connection, id.getBytes());
					Service.filesGarbageCollection();
					break;
					
				case "put_h":
					data = (byte[]) inputStream.readObject();
					id = Service.putH(data);
					String[] paramsPutH = id.split(Constants.DELIMITER);
					outputStream.writeObject(paramsPutH[0]);
					outputStream.writeObject(paramsPutH[1]);
					break;
				
				case "get":
					id = (String) inputStream.readObject();
					int rid = inputStream.readInt();
					String[] paramsGet = Service.BizantineGet(id, rid)
											.split(Constants.DELIMITER);
					outputStream.writeObject(paramsGet[0]);
					outputStream.writeObject(paramsGet[1]);
					outputStream.writeObject(Security.HexStringToByteArray(paramsGet[2]));
					break;
//				case "storePubKey":
//					cert = (X509Certificate) inputStream.readObject();
//					String result = Service.storePubKey(cert);
//					outputStream.writeObject(result);
//					outputStream.flush();
//					break;
//				case "readPubKeys":
//					outputStream.writeObject(Service.readPubKeys());
//					break;
				default: 
					break;
			}
			
		} catch (IOException | ClassNotFoundException e) {
			
		}
	}

}
