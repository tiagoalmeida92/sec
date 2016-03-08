package Utils;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;

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
		return Base64.getEncoder().encodeToString(md.digest());
	}
	
	public static String Hash(byte[] data) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data);
		return Base64.getEncoder().encodeToString(md.digest());
	}
	
	public static KeyPair GetKeyPair(String username, String password)
	{
		KeyStore ks;
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			FileInputStream fis = new FileInputStream(username+ Constants.KEYSTOREEXTENSION);
		    ks.load(fis, password.toCharArray());
		    fis.close();

		    //Get private key
		    ProtectionParameter paramPassword = 
		    		new KeyStore.PasswordProtection(password.toCharArray());
		    KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
		            ks.getEntry("certSelfSignedAlias", paramPassword);
	        PrivateKey privateKey = pkEntry.getPrivateKey();
	        //Get public key
	        PublicKey publicKey = ks.getCertificate("certSelfSignedAlias").getPublicKey();
	        return new KeyPair(publicKey, privateKey);
		} catch (KeyStoreException 
				| NoSuchAlgorithmException 
				| CertificateException 
				| IOException 
				| UnrecoverableEntryException e) {
			return null;
		}
	}
	
	public static boolean CreateKeyStore(String username, String password)
	{
        Process p = null;
		char[] charArrayPassword = password.toCharArray();
			
		//Generate self-signed certificate
		ArrayList<String> command = new ArrayList<String>();
		command.add("cmd");
		ProcessBuilder pb = new ProcessBuilder(command);
	    try {
			p = pb.start();
		
			BufferedWriter stdOutput = new BufferedWriter(new
	                OutputStreamWriter(p.getOutputStream()));
			stdOutput.write("keytool -genkey -alias certSelfSignedAlias -keyalg RSA -validity 365 -keystore " + username +Constants.KEYSTOREEXTENSION);
			stdOutput.newLine();
			stdOutput.write(password);
	        stdOutput.newLine();
	        stdOutput.write(password);
	        stdOutput.newLine();
	        stdOutput.write(username);
	        stdOutput.newLine();
	        stdOutput.newLine();	        
	        stdOutput.newLine();	        
	        stdOutput.newLine();	        
	        stdOutput.newLine();	        
	        stdOutput.newLine();	        
	        stdOutput.write("yes");
	        stdOutput.newLine();        
	        stdOutput.newLine();
	        stdOutput.close();
	        return Files.Exists(username+Constants.KEYSTOREEXTENSION);
	    } catch (IOException e) {
	    	return false;
		}
	}
}
