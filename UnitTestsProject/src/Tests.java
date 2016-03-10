import static org.junit.Assert.*;

import java.io.File;
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
		assertTrue(Security.CreateKeyStore(USERNAME, PASSWORD));
		assertTrue(Files.Exists(USERNAME+Constants.KEYSTOREEXTENSION));
		assertNotNull(Security.GetKeyPair(USERNAME, PASSWORD));
		
		//Init Block server
//		MultiThread server = new MultiThread();
//		new Thread(server).start();
//
//		try {
//		    Thread.sleep(5 * 1000);
//		} catch (InterruptedException e) {
//		    e.printStackTrace();
//		}
		
		//Init Client library
		Client client = new Client("localhost", Constants.PORT);
        String publicKeyBlockId = client.init();
		byte[] data = new byte[Constants.CBLOCKLENGTH];
		Random r = new Random();
		r.nextBytes(data);
        client.write(0, Constants.CBLOCKLENGTH,data);
        
        byte[] finalbytes = 
        		client.read(publicKeyBlockId, Constants.CBLOCKLENGTH, 50);
        
        ByteBuffer buf = ByteBuffer.wrap(data,Constants.CBLOCKLENGTH, 20);
        readBeyondEndOfTheFileByteArrayTest
        	= Arrays.equals(buf.array(), finalbytes);

		Files.DeleteFile(USERNAME+Constants.KEYSTOREEXTENSION);
		Files.DeleteAllBlockServerFiles();
		
		System.out.println("Stopping Server");
		//server.stop();
		
		assertTrue(readBeyondEndOfTheFileByteArrayTest);	
	}

}
