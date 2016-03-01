import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Base64;

public class Service {

	private static final String PKBLOCKEXTENSION = ".pkblock";
	private static final String CBLOCKEXTENSION = ".cblock";
	private static final String PKBLOCKPATH = "c:\\secProject\\pkblocks";
	private static final String CBLOCKPATH = "c:\\secProject\\cblocks";
	
	public static String putK(CommunicationParameters params)
	{
		try {
			if(!Security.Verify(params.Data, params.Signature, params.PublicK))
				return "Integrity failure or bad public key.";
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			return "Invalid key or signature exception.";
		}
		
		String fileName;
		try {
			fileName = Security.GetPublicKeyHash(params.PublicK);
		} catch (NoSuchAlgorithmException e1) {
			return "Invalid digest algorithm";
		}
		BufferedWriter writer = null;
		try {			
			writer = new BufferedWriter(new FileWriter(PKBLOCKPATH+fileName+PKBLOCKEXTENSION));
			writer.write(Base64.getEncoder().encodeToString(params.Data));
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
		
	}
	
	public static String get(String id)
	{
		BufferedReader reader = null;
		try {
			String currLine = null,base64Content = null;
			reader = new BufferedReader(new FileReader(PKBLOCKPATH+id+PKBLOCKEXTENSION));
			while ((currLine = reader.readLine()) != null) {
				base64Content += currLine;
			}
			return base64Content;
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
