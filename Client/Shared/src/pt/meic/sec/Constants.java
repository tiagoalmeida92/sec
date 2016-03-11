import java.io.File;

public class Constants {

	//File paths
	public static final String PKBLOCKEXTENSION = ".pkblock";
	public static final String CBLOCKEXTENSION = ".cblock";
	public static final String PKBLOCKPATH = new File("").getAbsolutePath()+"\\pkblocks\\";
	public static final String CBLOCKPATH = new File("").getAbsolutePath()+"\\cblocks\\";

	//Hash Length

	//Content blocks length
	public static final int CBLOCKLENGTH = 6000; //6KB

	public static final int SIGNATURE_SIZE = 128;
	public static final int PUBLIC_KEY_SIZE = 162;
	public static final int CONTENT_BLOCK_ID_SIZE = 64;
	public static final int BLOCK_HASH_SIZE = 64;
	
	// Client Dependability exception messages
	public static final String AVAILABILITYWASCOMPROMISED = "Due to attacks or faults the availability of the system was compromised";
	public static final String TAMPEREDAKEYEXCEPTIONMESSAGE = "An attacker tampered the identifier of a block";
	public static final String TAMPEREDSIGNATUREEXCEPTIONMESSAGE = "An attacker tampered the signature of a block";
	public static final String TAMPEREDWITHCONTENTBLOCKEXCEPTIONMESSAGE = "An attacker tampered the received content block";


}
