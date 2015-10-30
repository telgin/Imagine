package util.algorithms;

import algorithms.ProductIOException;
import util.ByteConversion;

public class UniqueRandomRange
{
	private int index;
	private int[] array;
	private HashRandom random;

	public UniqueRandomRange(HashRandom random, int range)
	{
		this.random = random;

		index = range;
		array = new int[range];

		for (int i = 0; i < range; ++i)
			array[i] = i;
	}

	public void reseed(byte[] seed)
	{
		System.out.println("Reseed: " + ByteConversion.bytesToHex(seed));
		random = new HashRandom(seed);
	}

	public int remainingNumbers()
	{
		return index;
	}

	public boolean hasRemainingNumbers()
	{
		return index > 0;
	}

	public int next() throws ProductIOException
	{
		if (!hasRemainingNumbers())
			throw new ProductIOException("URR ran out of numbers.");

		try
		{
			int swapIndex = random.nextInt(index);
			int temp = array[swapIndex];
			array[swapIndex] = array[index - 1];
			array[index - 1] = temp;

			--index;

			// System.out.println(temp);
			return temp;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new ProductIOException("URR ran out of numbers.");
		}
	}
}
