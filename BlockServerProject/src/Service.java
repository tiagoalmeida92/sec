import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import Utils.Constants;
import Utils.Files;
import Utils.Security;
import Utils.Utils;

public class Service {
	
	public static String BizantinePutK(byte[] data, byte[] signature, PublicKey publicK)
	{
		final int fromIndex = Constants.SIGNATURE_SIZE + Constants.PUBLIC_KEY_SIZE;
		int writerTimestamp, currentTimestamp=0;
		try {
			writerTimestamp = Integer.valueOf(new String(
					Arrays.copyOfRange(data, fromIndex, fromIndex + Constants.TIMESTAMP_SIZE),"UTF-8"));
			String pkFileName = Security.GetPublicKeyHash(publicK);
			byte[] storedData = Service.get(pkFileName);
			if(storedData != null)
				currentTimestamp = Integer.valueOf(new String(
						Arrays.copyOfRange(storedData, fromIndex, fromIndex + Constants.TIMESTAMP_SIZE),"UTF-8"));
			if(writerTimestamp > currentTimestamp)
			{
				String response = Service.putK(data, signature, publicK);
				if(pkFileName.equals(response))
					return Constants.ACKTYPE + Constants.DELIMITER +
							writerTimestamp + Constants.DELIMITER + 
							Service.putK(data, signature, publicK);
			}	
		} catch (UnsupportedEncodingException e) {
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}
	
	public static String BizantinePutH(byte[] data)
	{
		String response = Service.putH(data);
		return Constants.ACKTYPE + Constants.DELIMITER + response;
	}
	
	public static String BizantineGet(String id, int rid)
	{
		BufferedInputStream reader = null;
		try {

			//Get ContentBlock data
			byte[] data = new byte[Constants.CBLOCKLENGTH];
			reader = new BufferedInputStream(new FileInputStream(Constants.CBLOCKPATH+id+Constants.CBLOCKEXTENSION));
			reader.read(data, 0, data.length);
			reader.close();		
			return Constants.VALUETYPE + Constants.DELIMITER + rid + Constants.DELIMITER + Utils.byteToHex(data);

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
				return Constants.VALUETYPE + Constants.DELIMITER + rid + Constants.DELIMITER + Utils.byteToHex(data);
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
	
	/*
	 * S1
	 */
	public static String putK(byte[] data, byte[] signature, PublicKey publicK)
	{
		try {
			if(!Security.Verify(data, signature, publicK))
				return "[Integrity] Integrity failure or bad public key.";
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			return "[Integrity] Invalid key, encoding or signature exception.";
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

	/*
	 * S1
	 */
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

	/*
	 * S1
	 */
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

	/*
	 * S1
	 */
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

	/*
	 * S2
	 */
	public static String storePubKey(X509Certificate cert)
	{
		try {

			Path path = Paths.get(Constants.CERTIFICATESFILEPATH);
			boolean fileNotExists = new File(Constants.CERTIFICATESFILEPATH)
					.createNewFile();
			List<String> certs = java.nio.file.Files.readAllLines(path);
			String hexCert = Security.ByteToHex(cert.getEncoded());
			if(certs.contains(hexCert))
				return Constants.CERTIFICATEALREADYREGISTERED;
			if(fileNotExists)
			{
				certs.add(0,"none");
				certs.add(1,hexCert);
			}
			else
				certs.add(hexCert);
			certs.remove(0);
			certs.add(0, Security.Hash(Utils.toByteArray(certs)));
			java.nio.file.Files.write(path, certs);
			return Constants.SUCCESS;

		} catch (CertificateException | NoSuchAlgorithmException | IOException e) {
			return Constants.CERTIFICATENOTVALIDORTAMPERED;
		}
	}

	/*
	 * S2
	 */
	public static List<String> readPubKeys()
	{
		Path path = Paths.get(Constants.CERTIFICATESFILEPATH);
		try {
			return java.nio.file.Files.readAllLines(path);
		} catch (IOException e) {
			return null;
		}
	}
}
