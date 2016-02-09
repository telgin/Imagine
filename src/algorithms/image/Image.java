package algorithms.image;

import java.awt.image.BufferedImage;
import product.Product;
import util.ByteConversion;
import util.algorithms.HashRandom;
import util.algorithms.UniqueRandomRange;
import algorithms.Algorithm;
import config.Constants;
import key.Key;

public class Image implements Product
{
	protected Algorithm algorithm;
	protected BufferedImage img;
	protected UniqueRandomRange randOrder;
	protected int maxWriteSize;
	protected HashRandom random;
	protected Key key;
	protected boolean skippedAll = false;
	protected byte[] uuid;
	protected int width;
	protected int height;

	public Image(Algorithm algo, Key key)
	{
		this.algorithm = algo;
		this.key = key;
		width = Integer.parseInt(algo.getParameterValue("width"));
		height = Integer.parseInt(algo.getParameterValue("height"));
		maxWriteSize = width * height * 3;
	}

	protected void reset()
	{
		// use any constant seed to start
		random = new HashRandom(Constants.DEFAULT_SEED);
		randOrder = new UniqueRandomRange(random, maxWriteSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algorithms.SecureProduct#secureStream()
	 */
	@Override
	public void secureStream()
	{
		// since this is a secure product, the uuid was already set and written
		randOrder.reseed(ByteConversion.concat(key.getKeyHash(), uuid));
	}

	@Override
	public String getAlgorithmName()
	{
		return algorithm.getName();
	}

	@Override
	public int getAlgorithmVersionNumber()
	{
		return algorithm.getVersion();
	}

	@Override
	public void setUUID(byte[] uuid)
	{
		this.uuid = uuid;
	}

	@Override
	public byte[] getUUID()
	{
		return this.uuid;
	}
}
