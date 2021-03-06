package util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import config.Constants;
import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Standard hashing functions
 */
public class Hashing
{
	/**
	 * Hashes a byte array
	 * @param bytes The array of bytes
	 * @return The hash as a byte array
	 */
	public static byte[] hash(byte[] bytes)
	{
		return getMessageDigest().digest(bytes);
	}

	/**
	 * Hashes a file
	 * @param p_file The file
	 * @return The hash as a byte array
	 */
	public static byte[] hash(File p_file)
	{
		byte[] digest = null;
		byte[] buffer = new byte[Constants.MAX_READ_BUFFER_SIZE];
		try
		{
			InputStream is = Files.newInputStream(p_file.toPath());
			MessageDigest md = getMessageDigest();

			DigestInputStream dis = new DigestInputStream(is, md);

			// just hashing
			while (dis.read(buffer) != -1){} 

			digest = md.digest();
			dis.close();
		}
		catch (Exception e)
		{
			//not going to distinguish reasons why this didn't work
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_error, "Failed to hash file: " + p_file.getAbsolutePath());
		}

		return digest;
	}

	/**
	 * Gets the message digest in a standard way for all hashing operations. Hashing
	 * algorithm is SHA-512.
	 * @return The message digest.
	 */
	private static MessageDigest getMessageDigest()
	{
		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance("SHA-512");
		}
		catch (NoSuchAlgorithmException e)
		{
			Logger.log(LogLevel.k_debug, e, false);
		}

		return md;
	}

}
