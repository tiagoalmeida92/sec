package Utils;

import java.io.File;

public class Constants {

	public static int PORT = 64535;
	
	//File paths
	public static final String PKBLOCKEXTENSION = ".pkblock";
	public static final String CBLOCKEXTENSION = ".cblock";
	public static final String PKBLOCKPATH = new File("").getAbsolutePath()+"\\"+PORT+"\\pkblocks\\";
	public static final String CBLOCKPATH = new File("").getAbsolutePath()+"\\cblocks\\";
	public static final String SECRETKEYFILE = new File("").getAbsolutePath()+"\\bin\\secret.key";
	public static final String CERTIFICATESFILEPATH = new File("").getAbsolutePath()+"\\certificates.clients";
	public static final String CERTIFICATESFILENAME = "certificates.clients";
	
	//Content blocks length
	public static final int CBLOCKLENGTH = 6000; //10KB
	
	//Security
	public static final int SIGNATURE_SIZE = 128;
	public static final int PUBLIC_KEY_SIZE = 162;
	public static final int CONTENT_BLOCK_ID_SIZE = 64;
	public static final int BLOCK_HASH_SIZE = 64;
	public static final int TIMESTAMP_SIZE = 4; //23 bytes -> yyyy-MM-dd HH:mm:ss.SSS
	public static final String KEYSTOREEXTENSION = ".jks";
	
	// Client Dependability exception messages
	public static final String SUCCESS = "Success";
	public static final String CERTIFICATENOTVALIDORTAMPERED = "An attacker tampered the received certificate or is not valid or was revogated";
	public static final String CERTIFICATENOTVALID = "Certificate or is not valid or was revogated.";
	public static final String CERTIFICATETAMPERED = "An attacker tampered the received certificate.";
	public static final String CERTIFICATEALREADYREGISTERED = "Certificate already registered.";
	public static final String TAMPEREDAKEYEXCEPTIONMESSAGE = "Block not found OR An attacker tampered the identifier of a block";
	public static final String TAMPEREDSIGNATUREEXCEPTIONMESSAGE = "An attacker tampered the signature of a block";
	public static final String TAMPEREDWITHCONTENTBLOCKEXCEPTIONMESSAGE = "An attacker tampered the received content block";

	//CA certificates path
	public static final String CCCA1 = new File("").getAbsolutePath() + "\\EC de Autenticacao do Cartao de Cidadao 0001.cer";
	public static final String CCCA2 = new File("").getAbsolutePath() + "\\EC de Autenticacao do Cartao de Cidadao 0002.cer";
	public static final String CCCA3 = new File("").getAbsolutePath() + "\\EC de Autenticacao do Cartao de Cidadao 0003.cer";
	public static final String CCCA4 = new File("").getAbsolutePath() + "\\EC de Autenticacao do Cartao de Cidadao 0004.cer";
	public static final String CCCA5 = new File("").getAbsolutePath() + "\\EC de Autenticacao do Cartao de Cidadao 0005.cer";
	public static final String CCCA6 = new File("").getAbsolutePath() + "\\EC de Autenticacao do Cartao de Cidadao 0006.cer";
	public static final String CCCA7 = new File("").getAbsolutePath() + "\\EC de Autenticacao do Cartao de Cidadao 0007.cer";
	public static final String CCCA8 = new File("").getAbsolutePath() + "\\EC de Autenticacao do Cartao de Cidadao 0008.cer";
	public static final String CCCA9 = new File("").getAbsolutePath() + "\\EC de Autenticacao do Cartao de Cidadao 0009.cer";
	public static final String CCCA10 = new File("").getAbsolutePath() + "\\EC de Autenticacao do Cartao de Cidadao 0010.cer";
	//CRLs paths
	public static final String CCCRL1 = "https://pki.cartaodecidadao.pt/publico/lrc/cc_ec_cidadao_crl001_crl.crl";
	public static final String CCCRL2 = "https://pki.cartaodecidadao.pt/publico/lrc/cc_ec_cidadao_crl002_crl.crl";
	public static final String CCCRL3 = "https://pki.cartaodecidadao.pt/publico/lrc/cc_ec_cidadao_crl003_crl.crl";

	//Freshness - Counter replay attacks
	public static final long FRESHNESSTIMESTAMP = 5000;

	//BYZANTINE METHOD TYPE
	public static final String ACKTYPE = "ACK";
	public static final String WRITETYPE = "WRITE";
	public static final String READTYPE = "READ";
	public static final String VALUETYPE = "VALUE";
	public static final String ADAPTED_ACKTYPE = "ADAPTED_ACK";
	public static final String ADAPTED_WRITETYPE = "ADAPTED_WRITE";
	public static final String DELIMITER = ",";
	
}
