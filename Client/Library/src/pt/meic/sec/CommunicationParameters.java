package pt.meic.sec;

import java.io.Serializable;
import java.security.PublicKey;

public class CommunicationParameters implements Serializable {
	public CommunicationParameters(byte[] data, byte[] signature, PublicKey publicK) {
		Data = data;
		Signature = signature;
		PublicK = publicK;
	}

	public byte[] Data;
	public byte[] Signature;
	public PublicKey PublicK;
	
}
