package algorithms.image;

import java.awt.image.BufferedImage;

import algorithms.Algorithm;
import archive.Archive;
import config.Constants;
import key.Key;
import util.ByteConversion;
import util.algorithms.HashRandom;
import util.algorithms.UniqueRandomRange;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The main class which handles reading and writing in the image format.
 */
public class Image implements Archive
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
	protected int[] f_rgbs;

	/**
	 * Creates an image archive interpreter instance
	 * @param p_algo The associated algorithm which contains required
	 * parameters among other things.
	 * @param p_key The key which will be used to read or write archives
	 */
	public Image(Algorithm p_algo, Key p_key)
	{
		f_algorithm = p_algo;
		f_key = p_key;
		f_width = Integer.parseInt(p_algo.getParameterValue(Definition.WIDTH_PARAM));
		f_height = Integer.parseInt(p_algo.getParameterValue(Definition.HEIGHT_PARAM));
		f_maxWriteSize = f_width * f_height * 3;
		f_rgbs = new int[f_width * f_height];
	}

	/**
	 * Resets the archive state
	 */
	protected void reset()
	{
		// use any constant seed to start
		f_random = new HashRandom(Constants.DEFAULT_SEED);
		f_randOrder = new UniqueRandomRange(f_random, f_maxWriteSize);
	}

	/* (non-Javadoc)
	 * @see archive.Archive#secureStream()
	 */
	@Override
	public void secureStream()
	{
		// since this is a secure archive, the uuid was already set and written
		f_randOrder.reseed(ByteConversion.concat(f_key.getKeyHash(), f_uuid));
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
	 * @see archive.Archive#getUUID()
	 */
	@Override
	public byte[] getUUID()
	{
		return this.f_uuid;
	}
}
