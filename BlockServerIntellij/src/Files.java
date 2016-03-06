import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Files {

	public static String WriteFile(String path, byte[] data){
		BufferedOutputStream writer = null;
		try {
			File file = new File(path);
			File parentFile = file.getParentFile();
			if(!parentFile.exists()){
				parentFile.mkdirs();
			}
			boolean created = file.createNewFile();
			writer = new BufferedOutputStream(new FileOutputStream(file));
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
