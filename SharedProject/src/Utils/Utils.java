package Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.List;

public class Utils {

    public static byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c= new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public static String[] concat(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] c= new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
    
    public static byte[] toByteArray(List<String> strings)
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	DataOutputStream out = new DataOutputStream(baos);
    	for (String element : strings) {
    	    try {
				out.writeUTF(element);
			} catch (IOException e) {
				return null;
			}
    	}
    	return baos.toByteArray();
    }
    
    public static String byteToHex(final byte[] byteArr) {
        Formatter formatter = new Formatter();
        for (byte b : byteArr) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
	
}
