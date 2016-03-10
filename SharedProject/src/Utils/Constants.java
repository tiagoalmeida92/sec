package Utils;

import java.io.File;

public class Constants {

	//File paths
	public static final String PKBLOCKEXTENSION = ".pkblock";
	public static final String CBLOCKEXTENSION = ".cblock";
	public static final String PKBLOCKPATH = new File("").getAbsolutePath()+"\\pkblocks";
	public static final String CBLOCKPATH = new File("").getAbsolutePath()+"\\cblocks";
	
	//Content blocks length
	public static final int CBLOCKLENGTH = 6000; //10KB
	
	//Keystore alias
	public static final String PUBLICKEYALIAS = "publicKeyAlias";
	public static final String PRIVATEKEYALIAS = "privateKeyAlias";
	public static final String KEYSTOREEXTENSION = ".jks";
	
}
