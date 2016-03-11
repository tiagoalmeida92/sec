package Utils;

import java.io.File;

public class Constants {

	public static final int PORT = 64535;
	
	//File paths
	public static final String PKBLOCKEXTENSION = ".pkblock";
	public static final String CBLOCKEXTENSION = ".cblock";
	public static final String PKBLOCKPATH = new File("").getAbsolutePath()+"\\pkblocks\\";
	public static final String CBLOCKPATH = new File("").getAbsolutePath()+"\\cblocks\\";
	
	//Content blocks length
	public static final int CBLOCKLENGTH = 6000; //10KB
	
	public static final int SIGNATURE_SIZE = 128;
	public static final int PUBLIC_KEY_SIZE = 162;
	public static final int CONTENT_BLOCK_ID_SIZE = 64;
	public static final int BLOCK_HASH_SIZE = 64;
	
	//Keystore alias
	public static final String PUBLICKEYALIAS = "publicKeyAlias";
	public static final String PRIVATEKEYALIAS = "privateKeyAlias";
	public static final String KEYSTOREEXTENSION = ".jks";
	
}
