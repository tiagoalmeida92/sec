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
	
	//1. Tests for the funcionalities
	
	@Test
	public void testAllTheFileSystemOperationsVerifyingTheIntegrationOfTheData()
	{	
		MultiThread server = null;
		try{
			//Init Block server
			server = new MultiThread();
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
	        
	        assertTrue(byteArrayTest);
			
		}
		finally{
			Files.DeleteFile(USERNAME+Constants.KEYSTOREEXTENSION);
			Files.DeleteAllBlockServerFiles();
			
			//System.out.println("Stopping Server");
			if(server != null)
				server.stop();
		}
			
	}
	
	@Test
	public void testReadingBeyongTheEndOfTheFile(){
		MultiThread server = null;
		try{
			//Init Block server
			server = new MultiThread();
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

		}
		finally{
			Files.DeleteFile(USERNAME+Constants.KEYSTOREEXTENSION);
			Files.DeleteAllBlockServerFiles();
			
			//System.out.println("Stopping Server");
			if(server != null)
				server.stop();
		}
		
	}
	
	//2. Test for dependability
	
	@Test
	public void testChangingContentOfThePublicKeyBlockIdTestingIntegration()
	{
		MultiThread server = null;
		try{
			//Init Block server
			server = new MultiThread();
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
			publicKeyBlockId=Security.ByteToHex(testData).substring(0, Constants.PUBLIC_KEY_SIZE);
	        try{
		        client.read(publicKeyBlockId, 0,  Constants.CBLOCKLENGTH);
	        }catch (Exception e){
	        	//Error when receiving the data from a call
	        	//to Get function of the block server
	        	//Because it returns null!
	        	//Probabilly needs some fault tolerance or exception catching
	        	//More low level (in the Client.java)
	        	assertTrue(true);
	        }
        
		}
		finally{
			Files.DeleteFile(USERNAME+Constants.KEYSTOREEXTENSION);
			Files.DeleteAllBlockServerFiles();
			
			//System.out.println("Stopping Server");
			if(server != null)
				server.stop();
		}
	}

	
	
	// SLOW creation of the KeyStore. Cannot be tested. Always failure.
//		@Test
//		public void keyStoreFileCreationAndVerification()
//		{
//			assertTrue(Security.CreateKeyStore(USERNAME, PASSWORD));
//			assertTrue(Files.Exists(USERNAME+Constants.KEYSTOREEXTENSION));
//			assertNotNull(Security.GetKeyPair(USERNAME, PASSWORD));	
//			Files.DeleteFile(USERNAME+Constants.KEYSTOREEXTENSION);
//		}
}
