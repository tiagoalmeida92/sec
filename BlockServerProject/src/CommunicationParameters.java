import java.io.Serializable;
import java.security.PublicKey;

public class CommunicationParameters implements Serializable {
	
	public byte[] Data;
	public byte[] Signature;
	public PublicKey PublicK;
	
}
