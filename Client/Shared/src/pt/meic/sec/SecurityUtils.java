
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Formatter;

public class SecurityUtils
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

    public static boolean Verify(byte[] data, byte[] signature, String publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
    {
        return Verify(data, signature, SecurityUtils.getKey(publicKey));
    }



    public static byte[] Sign(byte[] data, KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
    {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(keyPair.getPrivate());
        sig.update(data);
        return sig.sign();
    }

//    public static String GetPublicKeyHash(PublicKey publicK) throws NoSuchAlgorithmException
//    {
//        MessageDigest md = MessageDigest.getInstance("SHA-256");
//        md.update(publicK.getEncoded());
//        return Base64.getEncoder().encodeToString(md.digest());
//    }
//
    public static String Hash(byte[] data) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data);
        return byteToHex(md.digest());
    }

    public static String byteToHex(final byte[] hash)
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
    
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

    public static boolean verifyHash(byte[] bytes, String expectedHash) throws NoSuchAlgorithmException {
        String hash = Hash(bytes);
        return hash.equals(expectedHash);
    }

    public static PublicKey getKey(String key){
        try{
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(key.getBytes());
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
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
