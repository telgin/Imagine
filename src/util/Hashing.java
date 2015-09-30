package util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

	
	public static byte[] hash(byte[] bytes)
	{
		return getMessageDigest().digest(bytes);
	}
	
	public static byte[] hash(File file)
	{
		byte[] digest = null;
		byte[] buffer = new byte[Constants.MAX_READ_BUFFER_SIZE];
		try{
			InputStream is = Files.newInputStream(file.toPath());
			MessageDigest md = getMessageDigest();
		
			DigestInputStream dis = new DigestInputStream(is, md);

		    while(dis.read(buffer) != -1) {} //just hashing

		    digest = md.digest();
		    dis.close();
		}catch(Exception e){
			//didn't work...
			e.printStackTrace();
		}
		
		return digest;
	}

	private static MessageDigest getMessageDigest()
	{
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	
		return md;
	}
	
}
