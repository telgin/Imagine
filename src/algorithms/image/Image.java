package algorithms.image;

import java.awt.image.BufferedImage;

import algorithms.Algorithm;
import config.Constants;
import key.Key;
import product.Product;
import util.ByteConversion;
import util.algorithms.HashRandom;
import util.algorithms.UniqueRandomRange;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class Image implements Product
{
	protected Algorithm f_algorithm;
	protected BufferedImage f_img;
	protected UniqueRandomRange f_randOrder;
	protected int f_maxWriteSize;
	protected HashRandom f_random;
	protected Key f_key;
	protected boolean f_skippedAll = false;
	protected byte[] f_uuid;
	protected int f_width;
	protected int f_height;

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 */
	public Image(Algorithm p_algo, Key p_key)
	{
		f_algorithm = p_algo;
		f_key = p_key;
		f_width = Integer.parseInt(p_algo.getParameterValue(Definition.WIDTH_PARAM));
		f_height = Integer.parseInt(p_algo.getParameterValue(Definition.HEIGHT_PARAM));
		f_maxWriteSize = f_width * f_height * 3;
	}

	/**
	 * @update_comment
	 */
	protected void reset()
	{
		// use any constant seed to start
		f_random = new HashRandom(Constants.DEFAULT_SEED);
		f_randOrder = new UniqueRandomRange(f_random, f_maxWriteSize);
	}

	/* (non-Javadoc)
	 * @see product.Product#secureStream()
	 */
	@Override
	public void secureStream()
	{
		// since this is a secure product, the uuid was already set and written
		f_randOrder.reseed(ByteConversion.concat(f_key.getKeyHash(), f_uuid));
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
	public void setUUID(byte[] uuid)
	{
		this.f_uuid = uuid;
	}

	/* (non-Javadoc)
	 * @see product.Product#getUUID()
	 */
	@Override
	public byte[] getUUID()
	{
		return this.f_uuid;
	}
}
