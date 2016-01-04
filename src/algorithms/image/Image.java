package algorithms.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductMode;
import stats.ProgressMonitor;
import stats.Stat;
import util.ByteConversion;
import util.Constants;
import util.algorithms.HashRandom;
import util.algorithms.ImageUtil;
import util.algorithms.UniqueRandomRange;
import config.Configuration;
import data.Key;
import algorithms.Algorithm;
import algorithms.Parameter;
import algorithms.ProductIOException;

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
		random = new HashRandom(1337l);// any constant seed
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

	@Override
	public ProductMode getProductMode()
	{
		return ProductMode.getMode(algorithm.getParameterValue("productMode"));
	}
}
