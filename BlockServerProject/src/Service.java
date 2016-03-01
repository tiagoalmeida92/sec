import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Base64;

public class Service {

	private static final String EXTENSION = ".block";
	private static final String PATH = "c:\\";
	
	public static String putK(CommunicationParameters params)
	{
		try {
			if(!Security.Verify(params.Data, params.Signature, params.PublicK))
				return "Integrity failure or bad public key.";
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			return "Invalid key or signature exception.";
		}
		
		//criar ficheiro hashPublicKey.block
		String fileName;
		try {
			fileName = Security.GetPublicKeyHash(params.PublicK) + EXTENSION;
		} catch (NoSuchAlgorithmException e1) {
			return "Invalid digest algorithm";
		}
		try {			
			BufferedWriter writer = new BufferedWriter(new FileWriter(PATH+fileName));
			writer.write(Base64.getEncoder().encodeToString(params.Data));
			writer.close();
		} catch (IOException e) {
			return "Write file failure";
		}
		return null;
	}
	
	public static String putH(byte[] data)
	{
		return null;
	}
	
	public static byte[] get(String id)
	{
		return null;
	}
}
