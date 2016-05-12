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

    public static int bytesToInt(byte[] array){
        int value = 0;
        value |= array[0] << 24;
        value |= array[1] << 16;
        value |= array[2] << 8;
        value |= array[3];


        return value;
    }

    public static byte[] intToBytes(int value){
        byte[] bytes = new byte[4];

        bytes[0] = (byte) (value >>> 24);
        bytes[1] = (byte) (value >>> 16);
        bytes[2] = (byte) (value >>> 8);
        bytes[3] = (byte) (value);

        return bytes;
    }
    
    public static boolean equals(byte[] b1, byte[] b2){
    	if (b1 == null && b2 == null)
    	{
    		return true;
    	}
    	if (b1 == null || b2 == null){
    		return false;
    	}
    	if (b1.length != b2.length){
    	    return false;
    	}
    	for (int i=0; i<b1.length; i++){
    	  	if (b1[i] != b2[i]){
    	    	return false;
    	  	}
    	}
    	return true;
    }
}
