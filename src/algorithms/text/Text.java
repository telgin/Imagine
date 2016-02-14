package algorithms.text;

import algorithms.Algorithm;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import util.ByteConversion;
import util.algorithms.HashRandom;
import util.algorithms.UniqueRandomRange;
import config.Constants;
import key.Key;

public abstract class Text implements Product
{
	protected Algorithm algorithm;
	private Key key;
	protected byte[] buffer;
	protected int blockSize;
	protected byte[] uuid;
	protected HashRandom random;
	protected UniqueRandomRange order;

	public Text(Algorithm algo, Key key)
	{
		algorithm = algo;
		this.key = key;
		blockSize = Integer.parseInt(algorithm.getParameterValue(Definition.BLOCK_SIZE_PARAM));
		Logger.log(LogLevel.k_debug, "Text Product Created");
	}

	protected void reset()
	{
		// use any constant seed to start
		random = new HashRandom(Constants.DEFAULT_SEED);
		order = new UniqueRandomRange(random, blockSize);
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
	public void secureStream()
	{
		order.reseed(ByteConversion.concat(key.getKeyHash(), uuid));
	}

	@Override
	public byte[] getUUID()
	{
		return uuid;
	}
}
