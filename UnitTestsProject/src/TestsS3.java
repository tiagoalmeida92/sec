import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import Utils.Constants;
import Utils.Files;

public class TestsS3 {

	private boolean _end = false;
	
	private void RunBlockServer(String port, String nReplicas, String nFaults)
	{
		int portInt = Integer.valueOf(port);
		int nReplicasInt = 3 * Integer.valueOf(nReplicas) + 1;
		ArrayList<Process> processes = new ArrayList<Process>();
		try
		{
			for(int i = 0; i < nReplicasInt; ++i)
			{
				processes.add(
						BlockServerReplicasMain.executeProcess(ServerMain.class,
						String.valueOf(port+i), 
						String.valueOf(nReplicas), nFaults));	
			}
		} catch (IOException e) {
			System.out.println("Error running BS: " + e.getMessage());
			return;
		}
		
		while(!_end);
		for(Process process : processes)
		{
			process.destroy();
		}
	}
	
	@Test
	public void test_putk_bad_flag_wts_from_bizantine_block_server()
	{
		String port = "64535";
		String nReplicas = "4";
		String nFaults = "1";
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				RunBlockServer( port, nReplicas, nFaults);	
			}
		});
		
		try{
	
			try {
			    Thread.sleep(1000);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			String[] ports = {
					Constants.PORT + "",
					(Constants.PORT+1) + "",
					(Constants.PORT+2) + "",
					(Constants.PORT+3) + "",
			};

			Client client = new Client(ports, nFaults);
			
			client.init();
			
			//The server send a bad flag (wts = 0)
			Service.receivedTimestampForTestsS3 = 0;
			
			byte[] data = new byte[Constants.CBLOCKLENGTH];
			Random r = new Random();
			r.nextBytes(data);
	        client.write(0, Constants.CBLOCKLENGTH,data);
	        
	        String publicKeyBlockId = client.publicKeyBlockId;
	        byte[] finalbytes = 
	        		client.read(publicKeyBlockId, 0, Constants.CBLOCKLENGTH);
	        
	        ByteBuffer buf = ByteBuffer.wrap(data,0, Constants.CBLOCKLENGTH);
	        boolean byteArrayTest = Arrays.equals(buf.array(), finalbytes);
	        
	        assertTrue(!byteArrayTest);
		} catch (DependabilityException | CertificateException | FileNotFoundException e1) {
			//Not Important for this test
		}
		finally{
			_end = true;
		}
	}
	
	@Test
	public void test_get_pkfile_bad_flag_rid_from_bizantine_block_server()
	{
		String port = "64535";
		String nReplicas = "4";
		String nFaults = "1";
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				RunBlockServer( port, nReplicas, nFaults);	
			}
		});
		
		try{
	
			try {
			    Thread.sleep(1000);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			String[] ports = {
					Constants.PORT + "",
					(Constants.PORT+1) + "",
					(Constants.PORT+2) + "",
					(Constants.PORT+3) + "",
			};

			Client client = new Client(ports, nFaults);
			
			client.init();
			
			byte[] data = new byte[Constants.CBLOCKLENGTH];
			Random r = new Random();
			r.nextBytes(data);
	        client.write(0, Constants.CBLOCKLENGTH,data);
	        
	        String publicKeyBlockId = client.publicKeyBlockId;
	        
			//The server send a bad flag (rid = 0)
			Service.receivedTimestampForTestsS3 = 0;
	        byte[] finalbytes = 
	        		client.read(publicKeyBlockId, 0, Constants.CBLOCKLENGTH);
	        
	        ByteBuffer buf = ByteBuffer.wrap(data,0, Constants.CBLOCKLENGTH);
	        boolean byteArrayTest = Arrays.equals(buf.array(), finalbytes);
	        
	        assertTrue(!byteArrayTest);
		} catch (DependabilityException | CertificateException | FileNotFoundException e1) {
			//Not Important for this test
		}
		finally{
			_end = true;
		}
	}
	
	//TODO
	@Test
	public void test_get_pkfile_bad_signature_from_bizantine_block_server()
	{
		String port = "64535";
		String nReplicas = "4";
		String nFaults = "1";
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				RunBlockServer( port, nReplicas, nFaults);	
			}
		});
		
		try{
	
			try {
			    Thread.sleep(1000);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			String[] ports = {
					Constants.PORT + "",
					(Constants.PORT+1) + "",
					(Constants.PORT+2) + "",
					(Constants.PORT+3) + "",
			};

			Client client = new Client(ports, nFaults);
			
			client.init();
			
			byte[] data = new byte[Constants.CBLOCKLENGTH];
			Random r = new Random();
			r.nextBytes(data);
	        client.write(0, Constants.CBLOCKLENGTH,data);
	        
	        String publicKeyBlockId = client.publicKeyBlockId;
	        
			//The server send a bad signature (teta)
			Service.isBizantineSignature = true;
	        byte[] finalbytes = 
	        		client.read(publicKeyBlockId, 0, Constants.CBLOCKLENGTH);
	        
	        ByteBuffer buf = ByteBuffer.wrap(data,0, Constants.CBLOCKLENGTH);
	        
	        assertNotNull(buf);
	        assertNull(finalbytes);
		} catch (DependabilityException | CertificateException | FileNotFoundException e1) {
			//Not Important for this test
		}
		finally{
			_end = true;
		}
	}
	
}
