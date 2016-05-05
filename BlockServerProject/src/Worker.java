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
			byte[] data,signature;
			PublicKey publicK;
			String method,id;

			//verify
			byte[] m = 
					AuthPerfectPointToPointLinks.Deliver(_connection);
			String[] mSplited = new String(m).split(Constants.DELIMITER);
			
			switch(mSplited[0])
			{
				case Constants.WRITETYPE: 								
					data = Security.HexStringToByteArray(mSplited[1]);
					signature = Security.HexStringToByteArray(mSplited[2]);
					publicK = Security.getKey(
							Security.HexStringToByteArray(mSplited[3]));
					id = Service.BizantinePutK(data,signature,publicK);
					//authenticate
					AuthPerfectPointToPointLinks.Send(_connection, id.getBytes());
					Service.filesGarbageCollection();
					break;
					
				case Constants.ADAPTED_WRITETYPE:
					data = Security.HexStringToByteArray(mSplited[1]);
					id = Service.putH(data);
					AuthPerfectPointToPointLinks.Send(_connection, id.getBytes());
					break;
				
				case Constants.READTYPE:
					id = mSplited[1];
					int rid = Integer.valueOf(mSplited[2]);
					String message = Service.BizantineGet(id, rid);
					AuthPerfectPointToPointLinks.Send(_connection, 
							message.getBytes());
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
