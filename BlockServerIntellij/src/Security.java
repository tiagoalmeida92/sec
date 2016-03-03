import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Formatter;

public class Security 
{
	
	public static KeyPair GenerateKeyPair() throws NoSuchAlgorithmException{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	    kpg.initialize(1024);
	    return kpg.genKeyPair();
	}
	
	public static boolean Verify(byte[] data, byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initVerify(publicKey);
		sig.update(data);
		return sig.verify(signature);
	}
	
	public static byte[] Sign(byte[] data, KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		Signature sig = Signature.getInstance("SHA256withRSA");
	    sig.initSign(keyPair.getPrivate());
	    sig.update(data);
	    return sig.sign();
	}
	
	public static String GetPublicKeyHash(PublicKey publicK) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(publicK.getEncoded());
		return byteToHex(md.digest());
	}
	
	public static String Hash(byte[] data) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data);
		return byteToHex(md.digest());
	}

	private static String byteToHex(final byte[] hash)
	{
		Formatter formatter = new Formatter();
		for (byte b : hash)
		{
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

}
