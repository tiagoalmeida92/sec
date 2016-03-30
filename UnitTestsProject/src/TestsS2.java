import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

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
			Files.DeleteFile(Constants.CERTIFICATESFILEPATH);
		}
	}
	
}
