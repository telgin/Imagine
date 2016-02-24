package util.algorithms;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import testing.CodeTimer;
import util.ByteConversion;


/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class HashRandom
{
	private byte[] f_curHash;
	private MessageDigest f_digest;
	private int f_index = 0;
	private final int STOP = ((512 / 8) / 2); // half way
	
	/**
	 * @update_comment
	 * @param p_seed
	 */
	public HashRandom(byte[] p_seed)
	{
		init(p_seed);
	}

	/**
	 * @update_comment
	 * @param p_seed
	 */
	public HashRandom(String p_seed)
	{
		init(p_seed.getBytes());
	}

	/**
	 * @update_comment
	 * @param p_seed
	 */
	public HashRandom(Long p_seed)
	{
		init(ByteConversion.longToBytes(p_seed));
	}

	/**
	 * @update_comment
	 * @param p_seed
	 */
	private void init(byte[] p_seed)
	{
		try
		{
			f_digest = MessageDigest.getInstance("SHA-512");
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		f_curHash = f_digest.digest(p_seed);
	}

	/**
	 * @update_comment
	 * @param p_to
	 * @return
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
	 * @update_comment
	 */
	private void refresh()
	{
		f_index = 0;
		f_curHash = f_digest.digest(f_curHash);
	}

	/**
	 * @update_comment
	 * @return
	 */
	public byte nextByte()
	{
		if (f_index >= STOP)
			refresh();

		return f_curHash[f_index++];
	}

	/**
	 * @update_comment
	 * @param p_to
	 * @return
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
	 * @update_comment
	 * @param args
	 */
	public static void main(String p_args[])
	{
		profileIntIterative();
		profileIntPreGeneration();
	}

	/**
	 * @update_comment
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
	 * @update_comment
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
