package util.algorithms;

import archive.ArchiveIOException;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class UniqueRandomRange
{
	private int f_index;
	private int[] f_array;
	private HashRandom f_random;

	/**
	 * @update_comment
	 * @param p_random
	 * @param p_range
	 */
	public UniqueRandomRange(HashRandom p_random, int p_range)
	{
		f_random = p_random;

		f_index = p_range;
		f_array = new int[p_range];

		for (int i = 0; i < p_range; ++i)
			f_array[i] = i;
	}

	/**
	 * @update_comment
	 * @param p_seed
	 */
	public void reseed(byte[] p_seed)
	{
		f_random = new HashRandom(p_seed);
	}

	/**
	 * @update_comment
	 * @return
	 */
	public int remainingNumbers()
	{
		return f_index;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public boolean hasRemainingNumbers()
	{
		return f_index > 0;
	}

	/**
	 * @update_comment
	 * @return
	 * @throws ArchiveIOException
	 */
	public int next() throws ArchiveIOException
	{
		if (!hasRemainingNumbers())
			throw new ArchiveIOException("URR ran out of numbers.");

		try
		{
			int swapIndex = f_random.nextInt(f_index);
			int temp = f_array[swapIndex];
			f_array[swapIndex] = f_array[f_index - 1];
			f_array[f_index - 1] = temp;

			--f_index;

			return temp;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new ArchiveIOException("URR ran out of numbers.");
		}
	}
}
