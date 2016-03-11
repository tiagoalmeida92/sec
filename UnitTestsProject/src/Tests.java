import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import Utils.Constants;
import Utils.Files;
import Utils.Security;

public class Tests {

	private final String USERNAME = "test12";
	private final String PASSWORD = "21tset";
	
// SLOW creation of the KeyStore. Cannot be tested
//	@Test
//	public void keyStoreFileCreationAndVerification()
//	{
//		assertTrue(Security.CreateKeyStore(USERNAME, PASSWORD));
//		assertTrue(Files.Exists(USERNAME+Constants.KEYSTOREEXTENSION));
//		assertNotNull(Security.GetKeyPair(USERNAME, PASSWORD));	
//		Files.DeleteFile(USERNAME+Constants.KEYSTOREEXTENSION);
//	}
	
	@Test
	public void testAllTheFileSystemOperationsVerifyingTheIntegrationOfTheData()
	{		
		//Init Block server
		MultiThread server = new MultiThread();
		new Thread(server).start();

		try {
		    Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//Init Client library
		Client client = new Client("localhost", Constants.PORT);
        String publicKeyBlockId = client.init();
		byte[] data = new byte[Constants.CBLOCKLENGTH];
		Random r = new Random();
		r.nextBytes(data);
        client.write(0, Constants.CBLOCKLENGTH,data);
        
        byte[] finalbytes = 
        		client.read(publicKeyBlockId, 0, Constants.CBLOCKLENGTH);
        
        ByteBuffer buf = ByteBuffer.wrap(data,0, Constants.CBLOCKLENGTH);
        boolean byteArrayTest = Arrays.equals(buf.array(), finalbytes);

		Files.DeleteFile(USERNAME+Constants.KEYSTOREEXTENSION);
		Files.DeleteAllBlockServerFiles();
		
		//System.out.println("Stopping Server");
		server.stop();
		
		assertTrue(byteArrayTest);	
	}
	
	@Test
	public void testReadingBeyongTheEndOfTheFile(){
		//Init Block server
		MultiThread server = new MultiThread();
		new Thread(server).start();

		try {
		    Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//Init Client library
		Client client = new Client("localhost", Constants.PORT);
        String publicKeyBlockId = client.init();
		byte[] data = new byte[Constants.CBLOCKLENGTH];
		Random r = new Random();
		r.nextBytes(data);
        client.write(0, Constants.CBLOCKLENGTH,data);
        
        //reading beyong 10 more bytes
        byte[] finalbytes = 
        		client.read(publicKeyBlockId, Constants.CBLOCKLENGTH, 10);
        assertNull(finalbytes);
		Files.DeleteFile(USERNAME+Constants.KEYSTOREEXTENSION);
		Files.DeleteAllBlockServerFiles();
		
		//System.out.println("Stopping Server");
		server.stop();
		
	}
	
	@Test
	public void testChangingContentOfTheDataTestingIntegration()
	{
		//Init Block server
		MultiThread server = new MultiThread();
		new Thread(server).start();

		try {
		    Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//Init Client library
		Client client = new Client("localhost", Constants.PORT);
        String publicKeyBlockId = client.init();
		byte[] data = new byte[Constants.CBLOCKLENGTH];
		Random r = new Random();
		r.nextBytes(data);
        client.write(0, Constants.CBLOCKLENGTH,data);

        //Dependability test
        //Someone in the middle of the communication
        //tampers with the public key hash (id)
        byte[] testData = new byte[Constants.PUBLIC_KEY_SIZE];
		Random auxR = new Random();
		auxR.nextBytes(testData);
		publicKeyBlockId= "dependability testing!";
        
        byte[] finalbytes = 
        		client.read(publicKeyBlockId, 0,  Constants.CBLOCKLENGTH);
        assertNull(finalbytes);
		Files.DeleteFile(USERNAME+Constants.KEYSTOREEXTENSION);
		Files.DeleteAllBlockServerFiles();
		
		//System.out.println("Stopping Server");
		server.stop();
	}

}
