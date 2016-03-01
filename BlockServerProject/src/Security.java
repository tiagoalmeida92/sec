import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

public class Security {
	
	public static boolean Verify(byte[] data, byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initVerify(publicKey);
		sig.update(data);
		return sig.verify(signature);
	}
	
	public static String GetPublicKeyHash(PublicKey publicK) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(publicK.getEncoded());
		return Base64.getEncoder().encodeToString(md.digest());
	}
	
	public static String Hash(byte[] data) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data);
		return Base64.getEncoder().encodeToString(md.digest());
	}
}
