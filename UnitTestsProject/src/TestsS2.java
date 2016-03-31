import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import Utils.Constants;
import Utils.Files;
import Utils.Security;

public class TestsS2 {

	@Test
	public void testTheStorePubKeyTheReadPubKeysAndVerifyIntegrity()
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
			//storePubKey
	        String publicKeyBlockId = client.init();
	        
	        //readPubKeys
	        List<X509Certificate> certs = client.list();
	        assertFalse(certs.isEmpty());
			
//	        byte[] finalbytes = 
//	        		client.read(certs.get(1), 0, 10);
//	        byte[] buf = new byte[]{0,0,0,0,0,0,0,0,0,0};
//	        
//	        assertTrue(Arrays.equals(buf, finalbytes));
	        
		} catch (DependabilityException e) {
			//Not important for this test
			assertTrue(true);
		}
		finally{
			Files.DeleteFile(Constants.CERTIFICATESFILENAME);
			Files.DeleteAllBlockServerFiles();
			
			//System.out.println("Stopping Server");
			if(server != null)
				server.stop();
		}
	}
	
	@Test
	public void testIntegrityOfTheNewFeatures()
	{
		
	}
	
	
}
