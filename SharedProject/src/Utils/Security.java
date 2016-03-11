package Utils;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
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
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
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
		return ByteToHex(md.digest());
	}
	
	public static String Hash(byte[] data) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data);
		return ByteToHex(md.digest());
	}
	
    public static PublicKey GetKeyByBytes(byte[] publicKeyBytes) {
        try{
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
    
    public static String ByteToHex(final byte[] hash)
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
    
	public static byte[] HexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static KeyPair GetKeyPair(String username, String password)
	{
		KeyStore ks;
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			String path = Files.GetPath(username+ Constants.KEYSTOREEXTENSION);
			if(path != null){
				FileInputStream fis = new FileInputStream(path);
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
			}
			else
				return null;
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
	
    public static PublicKey getKey(byte[] publicKeyBytes) {
        try{
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
