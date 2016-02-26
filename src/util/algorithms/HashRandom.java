package util.algorithms;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import config.Constants;
import logging.LogLevel;
import logging.Logger;
import testing.CodeTimer;
import util.ByteConversion;


/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A form of secure random number generation based on SHA-512
 */
public class HashRandom
{
	private byte[] f_curHash;
	private MessageDigest f_digest;
	private int f_index = 0;
	private final int STOP = ((512 / 8) / 2); // half way
	
	/**
	 * Constructs a new hash random with a given seed
	 * @param p_seed The seed as an array of bytes. Can be of any length.
	 */
	public HashRandom(byte[] p_seed)
	{
		try
		{
			f_digest = MessageDigest.getInstance("SHA-512");
		}
		catch (NoSuchAlgorithmException e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_fatal, "The random number generator failed to initialize.");
		}

		f_curHash = f_digest.digest(p_seed);
	}

	/**
	 * Constructs a new hash random with a given seed.
	 * @param p_seed The seed as a string. Can be of any length.
	 */
	public HashRandom(String p_seed)
	{
		this(p_seed.getBytes(Constants.CHARSET));
	}

	/**
	 * Constructs a new hash random with a given seed
	 * @param p_seed The seed as a long
	 */
	public HashRandom(long p_seed)
	{
		this(ByteConversion.longToBytes(p_seed));
	}

	/**
	 * Gets a random short
	 * @param p_to The max value of the short (non inclusive)
	 * @return A random short value
	 */
	public short nextShort(short p_to)
	{
		if (f_index >= STOP - 1)
			refresh();

		short next = (short) ((f_curHash[f_index] << 8) | (f_curHash[f_index + 1]));
		f_index += 2;
		return (short) Math.abs(next % p_to);
	}

	/**
	 * Refreshes the buffer of random bytes
	 */
	private void refresh()
	{
		//because of the half way stop, the last half of the hash was 
		//maintained as unknown entropy for the refresh
		f_index = 0;
		f_curHash = f_digest.digest(f_curHash);
	}

	/**
	 * Gets a random byte
	 * @return a random byte
	 */
	public byte nextByte()
	{
		if (f_index >= STOP)
			refresh();

		return f_curHash[f_index++];
	}

	/**
	 * Gets a random int
	 * @param p_to The max value of the int (non inclusive)
	 * @return A random int value
	 */
	public int nextInt(int p_to)
	{
		if (p_to == 0)
			return 0;

		if (f_index >= STOP - 3)
			refresh();

		f_index += 4;

		return Math.abs((f_curHash[f_index] << 24 | (f_curHash[f_index + 1] & 0xFF) << 16
			| (f_curHash[f_index + 2] & 0xFF) << 8 | (f_curHash[f_index + 3] & 0xFF)) % p_to);
	}

	/**
	 * Bonus profiling entry point
	 */
	public static void main(String p_args[])
	{
		profileIntIterative();
		profileIntPreGeneration();
	}

	/**
	 * Bonus profiling function
	 */
	private static void profileIntIterative()
	{
		int averageImageSize = 6022800;
		int images = 16;
		int rotations = averageImageSize * images;

		CodeTimer ct = new CodeTimer();
		ct.start();

		HashRandom random = new HashRandom("seed");
		long dump = 0;
		for (int i = 0; i < rotations; ++i)
		{
			// this number is not important for profiling
			dump += random.nextInt(averageImageSize);
			
			dump += random.nextByte();
		}

		ct.end();
		System.out.println("Dump: " + dump);
		System.out.println(rotations + " rotations in " + ct.getElapsedTime()
			+ " milliseconds.");
		System.out.println(
			"(~" + (ct.getElapsedTime() / images) + " milliseconds/image)");
	}

	/**
	 * Bonus profiling function
	 */
	private static void profileIntPreGeneration()
	{
		int averageImageSize = 6022800;
		int maxApplicationSize = 8000000;
		
		// the number of images close to iterative profiling
		int images = 100000000 / averageImageSize; 

		CodeTimer ct = new CodeTimer();
		ct.start();

		HashRandom random = new HashRandom("seed");
		int[] ibuffer = new int[maxApplicationSize];
		byte[] bbuffer = new byte[maxApplicationSize];
		for (int i = 0; i < maxApplicationSize; ++i)
		{
			ibuffer[i] = random.nextInt(averageImageSize);
			bbuffer[i] = random.nextByte();
		}

		long dump = 0;
		for (int x = 0; x < images; ++x)
		{
			for (int y = 0; y < averageImageSize; ++y)
			{
				dump += ibuffer[y];
				dump += bbuffer[y];
			}
		}

		ct.end();
		System.out.println("Dump: " + dump);
		System.out.println("Generated " + images + " images in " + ct.getElapsedTime()
			+ " milliseconds.");
		System.out.println(
			"(~" + (ct.getElapsedTime() / images) + " milliseconds/image)");
	}
}
