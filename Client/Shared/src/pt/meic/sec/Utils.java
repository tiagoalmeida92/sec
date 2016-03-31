package pt.meic.sec;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Utils {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

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

    public static String getTimestamp(){
        DateTimeZone zone = DateTimeZone.forID("Europe/Lisbon");
        DateTime timestamp = new DateTime(zone);
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(TIMESTAMP_FORMAT);
        return dateTimeFormatter.print(timestamp);
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

}
