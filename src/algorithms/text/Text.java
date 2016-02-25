package algorithms.text;

import algorithms.Algorithm;
import config.Constants;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import util.ByteConversion;
import util.algorithms.HashRandom;
import util.algorithms.UniqueRandomRange;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class Text implements Product
{
	protected Algorithm f_algorithm;
	private Key f_key;
	protected byte[] f_buffer;
	protected int f_blockSize;
	protected byte[] f_uuid;
	protected HashRandom f_random;
	protected UniqueRandomRange f_order;

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 */
	public Text(Algorithm p_algo, Key p_key)
	{
		f_algorithm = p_algo;
		f_key = p_key;
		f_blockSize = Integer.parseInt(f_algorithm.getParameterValue(Definition.BLOCK_SIZE_PARAM));
		Logger.log(LogLevel.k_debug, "Text Product Created");
	}

	/**
	 * @update_comment
	 */
	protected void reset()
	{
		// use any constant seed to start
		f_random = new HashRandom(Constants.DEFAULT_SEED);
		f_order = new UniqueRandomRange(f_random, f_blockSize);
	}

	/* (non-Javadoc)
	 * @see product.Product#getAlgorithmName()
	 */
	@Override
	public String getAlgorithmName()
	{
		return f_algorithm.getName();
	}

	/* (non-Javadoc)
	 * @see product.Product#getAlgorithmVersionNumber()
	 */
	@Override
	public int getAlgorithmVersionNumber()
	{
		return f_algorithm.getVersion();
	}

	/* (non-Javadoc)
	 * @see product.Product#setUUID(byte[])
	 */
	@Override
	public void setUUID(byte[] p_uuid)
	{
		f_uuid = p_uuid;
	}

	/* (non-Javadoc)
	 * @see product.Product#secureStream()
	 */
	@Override
	public void secureStream()
	{
		f_order.reseed(ByteConversion.concat(f_key.getKeyHash(), f_uuid));
	}

	/* (non-Javadoc)
	 * @see product.Product#getUUID()
	 */
	@Override
	public byte[] getUUID()
	{
		return f_uuid;
	}
}
