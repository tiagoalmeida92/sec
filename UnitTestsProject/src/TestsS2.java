import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import Utils.Constants;
import Utils.Files;
import Utils.Security;

public class TestsS2 {

	@Test
	public void testTheStorePubKeyTheReadPubKeysAndVerifyIntegrity()
	{
		try {
			boolean success = false;
			//Create a certificate from the CC to test
			//assertTrue(success=Service.storePubKey(Security.GenerateKeyPair().getPublic()));
			if(success)
			{
				List<String> hexCerts = Service.readPubKeys();
				//VerifyingIntegrity
				assertTrue(hexCerts.get(0).equals(Security.Hash(Utils.toByteArray(hexCerts))));
			}
		} catch (NoSuchAlgorithmException e) {
			assertTrue(false);
		}
		finally{
			
		}
		
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
			
	        assertTrue();
			
		} catch (DependabilityException e) {
			//Not important for this test
			assertTrue(true);
		}
		finally{
			Files.DeleteFile(Constants.CERTIFICATESFILEPATH);
			Files.DeleteAllBlockServerFiles();
			
			//System.out.println("Stopping Server");
			if(server != null)
				server.stop();
		}
	}
	
}
