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
	
	public Worker(Socket connection)
	{
		_connection = connection;
	}
	
	@Override
	public void run() {
		try
		{
			byte[] data,signature;
			PublicKey publicK;
			String method,id;
			X509Certificate cert;
			ObjectInputStream inputStream;
			ObjectOutputStream outputStream;
			//verify
			byte[] m = AuthPerfectPointToPointLinks.Deliver(_connection);
			if( m != null)
			{
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
						id = Service.BizantinePutH(data);
						AuthPerfectPointToPointLinks.Send(_connection, id.getBytes());
						break;
					
					case Constants.READTYPE:
						id = mSplited[1];
						int rid = Integer.valueOf(mSplited[2]);
						String message = Service.BizantineGet(id, rid);
						AuthPerfectPointToPointLinks.Send(_connection, 
								message.getBytes());
						break;
					case "storePubKey":
						inputStream =
								new ObjectInputStream(_connection.getInputStream());
						outputStream = 
								new ObjectOutputStream(_connection.getOutputStream());
						cert = (X509Certificate) inputStream.readObject();
						String result = Service.storePubKey(cert);
						outputStream.writeObject(result);
						outputStream.flush();
						break;
					case "readPubKeys":
						outputStream = 
							new ObjectOutputStream(_connection.getOutputStream());
						outputStream.writeObject(Service.readPubKeys());
						outputStream.flush();
						break;
					default: 
						break;
				}
			}
			
		} catch (IOException | ClassNotFoundException e) {
			
		}
	}

}
