package algorithms.imageoverlay;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.google.common.io.Files;

import algorithms.Algorithm;
import algorithms.ProductIOException;
import algorithms.imageoverlay.patterns.Pattern;
import algorithms.imageoverlay.patterns.Pattern2;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductMode;
import scratch.Scratch;
import stats.ProgressMonitor;
import stats.Stat;
import util.ByteConversion;
import util.Constants;
import util.algorithms.HashRandom;
import util.algorithms.ImageUtil;
import util.algorithms.UniqueRandomRange;

public class ImageOverlay implements Product
{
	protected Algorithm algorithm;
	protected BufferedImage img;
	protected UniqueRandomRange randOrder;
	protected HashRandom random;
	protected Key key;
	protected boolean skippedAll = false;
	protected byte[] uuid;
	protected int pattern = 0;
	protected int byteCount = 0;
	protected int colorIndex = 0;
	private int colorMod = 0;
	protected int[] pv;
	private boolean incrementFailed = false;

	public ImageOverlay(Algorithm algo, Key key)
	{
		this.algorithm = algo;
		this.key = key;
		pattern = Integer.parseInt(algo.getParameterValue("pattern"));
		pv = new int[6];
	}

	/**
	 * @credit http://stackoverflow.com/questions/3514158/how-do-you-clone-a-
	 *         bufferedimage user 'Klark'
	 * @param toCopy
	 * @return
	 */
	protected static BufferedImage clone(BufferedImage toCopy)
	{
		ColorModel cm = toCopy.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = toCopy.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public void reset()
	{
		// any constant seed
		random = new HashRandom(1337l);

		// obtain a random order
		randOrder = new UniqueRandomRange(random, img.getWidth() * img.getHeight());

		byteCount = 0;

		colorIndex = 0;
		colorMod = 0;

		System.out.println("Scratch.x: " + Scratch.x);

		Scratch.x = 0;

		incrementFailed = false;
	}

	protected final void nextPair() throws ProductIOException
	{
		if (incrementFailed)
			throw new ProductIOException("previous increment failed");

		if (pv[0] == 3044 && pv[1] == 1690)
			System.out.println("Happened by increment color1");
		++Scratch.x;

		if (pv[0] == 3044 && pv[1] == 1690)
			System.out.println("Happened by increment color2");
		incrementColor();

		if (pv[0] == 3044 && pv[1] == 1690)
			System.out.println("Happened by increment color3");
		if (colorIndex == 0)
			incrementVector();

		if (pv[0] == 3044 && pv[1] == 1690)
			System.out.println("Happened by increment color4");
	}

	private final void incrementColor()
	{
		colorIndex = colorMod++ % 3;
	}

	private final void incrementVector() throws ProductIOException
	{
		incrementFailed = true;
		if (pv[0] == 3044 && pv[1] == 1690)
			System.out.println("Happened by increment vector1");

		int pixel = randOrder.next();
		pv[1] = pixel / img.getWidth();
		pv[0] = pixel % img.getWidth();

		while (!Pattern.validIndex(pattern, pv[0], pv[1], img.getWidth(),
						img.getHeight()))
		{
			pixel = randOrder.next();
			pv[1] = pixel / img.getWidth();
			pv[0] = pixel % img.getWidth();
		}

		if (pv[0] == 3044 && pv[1] == 1690)
			System.out.println("Happening in increment");

		Pattern.eval(pattern, pv, img.getWidth(), img.getHeight());

		if (pv[0] == 3044 && pv[1] == 1690)
			System.out.println("Happening after eval");

		incrementFailed = false;
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
		this.uuid = uuid;// TODO remove this
		// this.uuid = new byte []{0,0,0,0,0,0,0,0,0,0,0,0};
	}

	@Override
	public ProductMode getProductMode()
	{
		return algorithm.getProductSecurityLevel();
	}

	@Override
	public void secureStream()
	{
		// uuid should be set prior to this
		randOrder.reseed(ByteConversion.concat(key.getKeyHash(), uuid));
	}

	protected byte getColor(int x, int y)
	{
		if (x < 0 || y < 0 || x >= img.getWidth() || y >= img.getHeight())
			System.out.println(x + ", " + y + ", " + img.getWidth() + ", "
							+ img.getHeight());

		if (colorIndex == 0)
		{
			return ImageUtil.getRed(img.getRGB(x, y));
		}
		else if (colorIndex == 1)
		{
			return ImageUtil.getGreen(img.getRGB(x, y));
		}
		else
		{
			return ImageUtil.getBlue(img.getRGB(x, y));
		}
	}

	@Override
	public byte[] getUUID()
	{
		return uuid;
	}
}