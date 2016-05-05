package Utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AuthPerfectPointToPointLinks {
	
	public static void Send(Socket con, byte[] m) throws IOException
	{
		byte[] secretKey = 
				Files.CreateAndOrGetFileContent(Constants.SECRETKEYFILE,
						Security.GenerateSecretKey());
		ObjectOutputStream outputStream = 
				new ObjectOutputStream(con.getOutputStream());
		byte[] auth = Security.generateHMac(secretKey, m, "HmacSHA256");
		outputStream.writeObject(m);
		outputStream.writeObject(auth);
	}
	
	public static byte[] Deliver(Socket con) throws IOException, ClassNotFoundException
	{
		byte[] secretKey = 
				Files.CreateAndOrGetFileContent(Constants.SECRETKEYFILE,
						Security.GenerateSecretKey());
		ObjectInputStream inputStream =
				new ObjectInputStream(con.getInputStream());
		byte[] m = (byte[]) inputStream.readObject();
		byte[] auth = (byte[]) inputStream.readObject();
		return Security.verifyHMac(secretKey, m, auth, "HmacSHA256")? m : null;
	}
	
}
