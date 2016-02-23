package util.algorithms;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import testing.CodeTimer;
import util.ByteConversion;

/**
 * @author Tom The random of my heart.
 */
public class HashRandom
{
	private byte[] curHash;
	private MessageDigest md;
	private final int STOP = ((512 / 8) / 2); // half way
	private int index = 0;

	public HashRandom(byte[] seed)
	{
		init(seed);
	}

	public HashRandom(String seed)
	{
		init(seed.getBytes());
	}

	public HashRandom(Long seed)
	{
		init(ByteConversion.longToBytes(seed));
	}

	private void init(byte[] seed)
	{
		try
		{
			md = MessageDigest.getInstance("SHA-512");
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		curHash = md.digest(seed);
	}

	public short nextShort(short to)
	{
		if (index >= STOP - 1)
			refresh();

		short next = (short) ((curHash[index] << 8) | (curHash[index + 1]));
		index += 2;
		return (short) Math.abs(next % to);
	}

	private void refresh()
	{
		index = 0;
		curHash = md.digest(curHash);
	}

	public byte nextByte()
	{
		if (index >= STOP)
			refresh();

		return curHash[index++];
	}

	public int nextInt(int to)
	{
		if (to == 0)
			return 0;

		if (index >= STOP - 3)
			refresh();

		index += 4;

		return Math.abs((curHash[index] << 24 | (curHash[index + 1] & 0xFF) << 16
						| (curHash[index + 2] & 0xFF) << 8 | (curHash[index + 3] & 0xFF))
						% to);
	}

	public static void main(String args[])
	{

		profileIntIterative();
		profileIntPreGeneration();

	}

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
