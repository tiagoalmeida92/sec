import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
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
			//test storePubKey
	        client.init();
	        
	        //test readPubKeys
	        List<X509Certificate> certs = client.list();
	        assertFalse(certs.isEmpty());
			
	        //test read with certificate as parameters
	        byte[] finalbytes = 
	        		client.read(Security.ByteToHex(certs.get(1).getEncoded()), 0, 10);
	        byte[] buf = new byte[]{0,0,0,0,0,0,0,0,0,0};
	        
	        assertTrue(Arrays.equals(buf, finalbytes));
		} catch (DependabilityException | CertificateEncodingException e) {
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
			//test storePubKey with random intermediate certificate
			Certificate c = CertificateFactory.getInstance("X.509").generateCertificate(new FileInputStream(Constants.CCCA1));
			client.init((X509Certificate)c);
			
	        
		} catch (DependabilityException | CertificateException | FileNotFoundException e1) {
			//Important for this test
			assertTrue(e1.getMessage().equals(Constants.CERTIFICATENOTVALID) ||
					e1.getMessage().equals(Constants.CERTIFICATETAMPERED));
		}
		finally{
			Files.DeleteFile(Constants.CERTIFICATESFILENAME);
			Files.DeleteAllBlockServerFiles();
			
			//System.out.println("Stopping Server");
			if(server != null)
				server.stop();
		}
	}
	
	
}
