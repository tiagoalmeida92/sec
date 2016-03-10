package pt.meic.sec;

public class Constants {

	//File paths
	public static final String PKBLOCKEXTENSION = ".pkblock";
	public static final String CBLOCKEXTENSION = ".cblock";
	public static final String PKBLOCKPATH = "c:\\secProject\\pkblocks\\";
	public static final String CBLOCKPATH = "c:\\secProject\\cblocks\\";

	//Hash Length

	//Content blocks length
	public static final int CBLOCKLENGTH = 6000; //6KB

	public static final int SIGNATURE_SIZE = 128;
	public static final int PUBLIC_KEY_SIZE = 162;
	public static final int CONTENT_BLOCK_ID_SIZE = 64;
	public static final int BLOCK_HASH_SIZE = 64;
}
