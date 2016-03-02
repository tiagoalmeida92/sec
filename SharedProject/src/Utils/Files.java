package Utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Files {

	public static String WriteFile(String path, byte[] data){
		BufferedOutputStream writer = null;
		try {			
			writer = new BufferedOutputStream(new FileOutputStream(path));
			writer.write(data);
			writer.close();
			return "Success";
		} catch (IOException e) {
			return "Writing file failure";
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException ex) {
				return null;
			}
		}
	}
}
