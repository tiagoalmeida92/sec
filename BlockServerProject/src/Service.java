import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Iterator;

import Utils.Constants;
import Utils.Files;
import Utils.Security;

public class Service {

	public static String putK(byte[] data, byte[] signature, PublicKey publicK)
	{
		try {
			if(!Security.Verify(data, signature, publicK))
				return "[Integrity] Integrity failure or bad public key.";
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			return "[Integrity] Invalid key or signature exception.";
		}
		
		String fileName,fileStatus;
		try {
			fileName = Security.GetPublicKeyHash(publicK);
		} catch (NoSuchAlgorithmException e1) {
			return "[Integrity] Invalid digest algorithm.";
		}
		
		if((fileStatus = Files.WriteFile(Constants.PKBLOCKPATH+fileName+Constants.PKBLOCKEXTENSION,data)).equals("Success"))
		{
			return fileName;
		}
		return "[Fault] "+fileStatus;
	}
	
	public static String putH(byte[] data)
	{
		String fileStatus;
		if (data.length <= Constants.CBLOCKLENGTH)
		{
			try {
				String contentHash = Security.Hash(data);
				if((fileStatus = Files.WriteFile(Constants.CBLOCKPATH+contentHash+Constants.CBLOCKEXTENSION, data)).equals("Success"))
				{
					return contentHash;
				}
			} catch (NoSuchAlgorithmException e) {
				return "[1] Algorithm deprecated.";
			}
			return "[2] "+fileStatus;
		}
		return "[3] data received its bigger than " + Constants.CBLOCKLENGTH + "bytes";
	}
	
	public static byte[] get(String id)
	{
		BufferedInputStream reader = null;
		try {
			
			//Get ContentBlock data
			byte[] data = new byte[Constants.CBLOCKLENGTH];
			reader = new BufferedInputStream(new FileInputStream(Constants.CBLOCKPATH+id+Constants.CBLOCKEXTENSION));
			reader.read(data, 0, data.length);
			reader.close();
			return data;
			
		} catch (FileNotFoundException e) {
			
			//Get PublicKeyBlock data
			String pkFilePath = Constants.PKBLOCKPATH+id+Constants.PKBLOCKEXTENSION;
			File file = new File(pkFilePath);
			if (file.exists())
			{
				byte[] data = new byte[(int) file.length()];
				try {
					reader = new BufferedInputStream(new FileInputStream(pkFilePath));
					reader.read(data, 0, data.length);
					reader.close();
				} catch (FileNotFoundException e2) {
					return null;
				} catch (IOException e1) {
					return null;
				}
				return data;
			}
			else 
				return null;
			
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException ex) {
				return null;
			}
		}
	}

	public static void filesGarbageCollection() 
	{
		ArrayList<File> contentBlock = new ArrayList<File>();
		ArrayList<File> publicBlock = new ArrayList<File>();
		Files.ListFiles(Constants.CBLOCKPATH, contentBlock);
		Files.ListFiles(Constants.PKBLOCKPATH, publicBlock);
		Iterator<File> iterator = contentBlock.iterator();
		while(iterator.hasNext())
		{
			File contentFile = iterator.next();
			String id =contentFile.getName().replace(Constants.CBLOCKEXTENSION, "");
			for(File publicKFile : publicBlock)
			{
				if(Files.FindOnContent(publicKFile,id)){
					iterator.remove();
					break;
				}
			}
		}
		for(File f : contentBlock)
		{
			f.delete();
		}
	}
}
