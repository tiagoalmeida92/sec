package pt.meic.sec;

import java.io.*;
import java.net.Socket;

public class AuthPerfectPointToPointLinks {

	private static final String MAC_ALGORITHM = "HmacSHA256";

	private final Socket socket;
	private final byte[] secret;

	public AuthPerfectPointToPointLinks(int port) throws IOException {
		socket = new Socket("localhost", port);
		File file = new File("secret.key");
		FileInputStream fileInputStream = new FileInputStream(file);
		secret = new byte[(int) file.length()];
		fileInputStream.read(secret);
	}

	public void Send(byte[] m) throws IOException
	{
		byte[] mac = SecurityUtils.generateHMac(secret, m, MAC_ALGORITHM);

		ObjectOutputStream outputStream =
				new ObjectOutputStream(socket.getOutputStream());

		outputStream.writeObject(m);
		outputStream.writeObject(mac);
	}
	
	public byte[] Deliver() throws IOException, ClassNotFoundException {
		ObjectInputStream inputStream =
				new ObjectInputStream(socket.getInputStream());
		byte[] m = (byte[]) inputStream.readObject();
		byte[] auth = (byte[]) inputStream.readObject();

		boolean verify = SecurityUtils.verifyHMac(secret, m, auth, MAC_ALGORITHM);
		if(!verify){
			return null;
		}
		return m;
	}
	
}