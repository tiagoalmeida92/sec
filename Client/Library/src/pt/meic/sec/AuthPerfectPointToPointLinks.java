package pt.meic.sec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.Security;

public class AuthPerfectPointToPointLinks {

	private final Socket socket;

	public AuthPerfectPointToPointLinks(int port) throws IOException {
		socket = new Socket("localhost", port);
	}

	public void Send(byte[] m) throws IOException
	{
		byte[] secretKey = {};
		ObjectOutputStream outputStream =
				new ObjectOutputStream(socket.getOutputStream());
		byte[] auth = {};
		outputStream.writeObject(m);
		outputStream.writeObject(auth);
	}
	
	public byte[] Deliver() throws IOException, ClassNotFoundException
	{
		byte[] secretKey = {};
		ObjectInputStream inputStream =
				new ObjectInputStream(socket.getInputStream());
		byte[] m = (byte[]) inputStream.readObject();
		byte[] auth = (byte[]) inputStream.readObject();
		return m;
	}
	
}