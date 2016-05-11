package pt.meic.sec;

import java.io.File;

public class Constants {

	public static final String PUT_PUBLIC_KEY_BLOCK = "put_k";
	public static final String PUT_FILE_CONTENT_BLOCK = "put_h";

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
	public static final int TIME_STAMP_SIZE = 4;
	public static final int CONTENT_BLOCK_ID_SIZE = 64;
	public static final int BLOCK_HASH_SIZE = 64;
	
	// Client Dependability exception messages
	public static final String SUCCESS = "Success";
	public static final String CERTIFOCATE_INVALID_OR_TAMPERED = "An attacker tampered the received certificate or is not valid or was revogated";
	public static final String CERTIFICATE_ALREADY_REGISTERED = "Certificate already registered.";
	public static final String CERTIFICATE_INVALID = "Certificate or is not valid or was revogated.";
	public static final String CERTIFICATE_TAMPERED = "An attacker tampered the received certificate.";
	public static final String AVAILABILITYWASCOMPROMISED = "Due to attacks or faults the availability of the system was compromised";
	public static final String TAMPERED_KEY_MESSAGE = "An attacker tampered the identifier of a block";
	public static final String TAMPERED_SIGNATURE_MESSAGE = "An attacker tampered the signature of a block";
	public static final String TAMPEREDWITHCONTENTBLOCKEXCEPTIONMESSAGE = "An attacker tampered the received content block";


	//BYZANTINE METHOD TYPE
	public static final String ACKTYPE = "put_k_ack";
	public static final String WRITETYPE = "put_k";
	public static final String READ_BLOCK = "get";
	public static final String READ_ACK = "get_ack";
	public static final String ADAPTED_ACKTYPE = "put_h_ack";
	public static final String ADAPTED_WRITETYPE = "put_h";
	public static final String DELIMITER = ",";
}
