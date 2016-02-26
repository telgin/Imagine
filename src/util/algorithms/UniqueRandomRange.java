package util.algorithms;

import archive.ArchiveIOException;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Creates a random range of numbers from zero to some length and
 * returns these numbers in a random order. Uses the Fisher-Yates O(n) shuffle
 * algorithm for efficiency.
 */
public class UniqueRandomRange
{
	private int f_index;
	private int[] f_array;
	private HashRandom f_random;

	/**
	 * Creates a unique random range object for the range: [0, p_range)
	 * @param p_random The hash random to use to shuffle the range.
	 * @param p_range The max value in the range (exclusive)
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
	 * Reseeds the random number generator which is picking the random numbers
	 * as they are requested.
	 * @param p_seed The new seed as an array of bytes
	 */
	public void reseed(byte[] p_seed)
	{
		f_random = new HashRandom(p_seed);
	}

	/**
	 * Gets the quantity of numbers remaining.
	 * @return The quantity of numbers remaining
	 */
	public int remainingNumbers()
	{
		return f_index;
	}

	/**
	 * Tells if the range has remaining numbers
	 * @return If the range has remaining numbers
	 */
	public boolean hasRemainingNumbers()
	{
		return f_index > 0;
	}

	/**
	 * Gets the next int from the range.
	 * @return The next int randomly chosen in the range.
	 * @throws ArchiveIOException When there are no remaining numbers in the range
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
