package algorithms.text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import algorithms.Algorithm;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductMode;
import stats.ProgressMonitor;
import stats.Stat;
import util.ByteConversion;
import util.Constants;
import util.algorithms.HashRandom;
import util.algorithms.UniqueRandomRange;
import config.Configuration;
import data.Key;

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
		blockSize = Integer.parseInt(algorithm.getParameterValue("blockSize"));
		Logger.log(LogLevel.k_debug, "TextBlock Created");
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
	public ProductMode getProductMode()
	{
		return algorithm.getProductSecurityLevel();
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
