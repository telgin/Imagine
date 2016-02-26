package algorithms.text;

import algorithms.Algorithm;
import archive.Archive;
import config.Constants;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import util.ByteConversion;
import util.algorithms.HashRandom;
import util.algorithms.UniqueRandomRange;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The main class which handles reading and writing in the text format.
 */
public abstract class Text implements Archive
{
	protected Algorithm f_algorithm;
	private Key f_key;
	protected byte[] f_buffer;
	protected int f_blockSize;
	protected byte[] f_uuid;
	protected HashRandom f_random;
	protected UniqueRandomRange f_order;

	/**
	 * Creates a text archive interpreter instance
	 * @param p_algo The associated algorithm which contains required
	 * parameters among other things.
	 * @param p_key The key which will be used to read or write archives
	 */
	public Text(Algorithm p_algo, Key p_key)
	{
		f_algorithm = p_algo;
		f_key = p_key;
		f_blockSize = Integer.parseInt(f_algorithm.getParameterValue(Definition.BLOCK_SIZE_PARAM));
		Logger.log(LogLevel.k_debug, "Text Archive Created");
	}

	/**
	 * Resets the archive state
	 */
	protected void reset()
	{
		// use any constant seed to start
		f_random = new HashRandom(Constants.DEFAULT_SEED);
		f_order = new UniqueRandomRange(f_random, f_blockSize);
	}

	/* (non-Javadoc)
	 * @see archive.Archive#getAlgorithmName()
	 */
	@Override
	public String getAlgorithmName()
	{
		return f_algorithm.getName();
	}

	/* (non-Javadoc)
	 * @see archive.Archive#getAlgorithmVersionNumber()
	 */
	@Override
	public int getAlgorithmVersionNumber()
	{
		return f_algorithm.getVersion();
	}

	/* (non-Javadoc)
	 * @see archive.Archive#setUUID(byte[])
	 */
	@Override
	public void setUUID(byte[] p_uuid)
	{
		f_uuid = p_uuid;
	}

	/* (non-Javadoc)
	 * @see archive.Archive#secureStream()
	 */
	@Override
	public void secureStream()
	{
		f_order.reseed(ByteConversion.concat(f_key.getKeyHash(), f_uuid));
	}

	/* (non-Javadoc)
	 * @see archive.Archive#getUUID()
	 */
	@Override
	public byte[] getUUID()
	{
		return f_uuid;
	}
}
