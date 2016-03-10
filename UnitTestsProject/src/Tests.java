import static org.junit.Assert.*;

import java.io.Writer;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import Utils.Constants;
import Utils.Files;
import Utils.Security;

public class Tests {

	private final String USERNAME = "test12";
	private final String PASSWORD = "21tset";
	
	@Test
	public void keyStoreFileCreationAndVerification()
	{
		assertTrue(Security.CreateKeyStore(USERNAME, PASSWORD));
		assertTrue(Files.Exists(USERNAME+Constants.KEYSTOREEXTENSION));
		assertNotNull(Security.GetKeyPair(USERNAME, PASSWORD));	
		Files.DeleteFile(USERNAME+Constants.KEYSTOREEXTENSION);
	}
	
	private boolean readBeyondEndOfTheFileByteArrayTest;
	
	@Test
	public void readBeyondEndOfTheFile()
	{
		KeyPair kp = null;
		
		assertTrue(Security.CreateKeyStore(USERNAME, PASSWORD));
		assertTrue(Files.Exists(USERNAME+Constants.KEYSTOREEXTENSION));
		assertNotNull(kp = Security.GetKeyPair(USERNAME, PASSWORD));
		
		new Thread(new Runnable() {
			public void run(){
				ServerMain.main();
			}
		}).start();
		
		Thread tClientMain = new Thread(new Runnable() {
			public void run(){
				Client client = new Client(null, 1234);
		        client.init();
				byte[] data = new byte[Constants.CBLOCKLENGTH + 20];
				Random r = new Random();
				r.nextBytes(data);
		        client.write(0, Constants.CBLOCKLENGTH+20,data);
		        
		        byte[] finalbytes = 
		        		client.read(client.GetPublicKeyBlockId(), Constants.CBLOCKLENGTH, 50);
		        
		        ByteBuffer buf = ByteBuffer.wrap(data,Constants.CBLOCKLENGTH, 20);
		        readBeyondEndOfTheFileByteArrayTest
		        	= Arrays.equals(buf.array(), finalbytes);
		        
			}
		});
		tClientMain.start();
		try {
			tClientMain.wait();
		} catch (InterruptedException e) {
		}
		
		assertTrue(readBeyondEndOfTheFileByteArrayTest);
	}

}
