import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Base64;

public class Service {

	public static String putK(byte[] data, byte[] signature, PublicKey publicK)
	{
		try {
			if(!Security.Verify(data, signature, publicK))
				return "Integrity failure or bad public key.";
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			return "Invalid key or signature exception.";
		}
		
		String fileName;
		try {
			fileName = Security.GetPublicKeyHash(publicK);
		} catch (NoSuchAlgorithmException e1) {
			return "Invalid digest algorithm";
		}
		BufferedWriter writer = null;
		try {			
			writer = new BufferedWriter(new FileWriter(Constants.PKBLOCKPATH+fileName+Constants.PKBLOCKEXTENSION));
			writer.write(Base64.getEncoder().encodeToString(data));
			writer.close();
			
		} catch (IOException e) {
			return "Writing file failure";
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	public static String putH(byte[] data)
	{
		return null;
	}
	
	public static byte[] get(String id)
	{
		BufferedInputStream reader = null;
		try {
			//Get ContentBlock data
			byte[] data = new byte[Constants.CBLOCKLENGTH];
			reader = new BufferedInputStream(new FileInputStream(Constants.CBLOCKPATH+id+Constants.CBLOCKEXTENSION));
			reader.read(data, 0, data.length);
			reader.close();
			return data;
		} catch (FileNotFoundException e) {
			//Get PublicKeyBlock data
			
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
