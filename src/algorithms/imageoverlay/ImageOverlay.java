package algorithms.imageoverlay;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.google.common.io.Files;

import algorithms.Algorithm;
import algorithms.imageoverlay.patterns.Pattern;
import algorithms.imageoverlay.patterns.Pattern2;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductIOException;
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
	protected InsertionDensity density;;
	protected int colorIndex = 0;
	protected int[] split;
	private int colorMod = 0;
	protected int[] curPixelCoord;
	private boolean incrementFailed = false;

	public ImageOverlay(Algorithm algo, Key key)
	{
		this.algorithm = algo;
		this.key = key;
		density = InsertionDensity.parseDensity(algo.getParameterValue("InsertionDensity"));
		curPixelCoord = new int[2];
		
		//the split array is just a stationary array
		//for efficiency in the byte splitting operations
		if (density.equals(InsertionDensity.k_25))
		{
			split = new int[4];
		}
		else //k_50
		{
			split = new int[2];
		}
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
		// use any constant seed to start
		random = new HashRandom(Constants.DEFAULT_SEED);

		// obtain a random order
		randOrder = new UniqueRandomRange(random, img.getWidth() * img.getHeight());

		colorIndex = 0;
		colorMod = 0;

		incrementFailed = false;
	}

	protected final void nextPair() throws ProductIOException
	{
		if (incrementFailed)
			throw new ProductIOException("previous increment failed");
		
		incrementColor();
		
		if (colorIndex == 0)
			incrementVector();
	}

	private final void incrementColor()
	{
		colorIndex = colorMod++ % 3;
	}

	private final void incrementVector() throws ProductIOException
	{
		incrementFailed = true;

		int pixel = randOrder.next();
		curPixelCoord[1] = pixel / img.getWidth();//y
		curPixelCoord[0] = pixel % img.getWidth();//x

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
		// uuid should be set prior to this
		randOrder.reseed(ByteConversion.concat(key.getKeyHash(), uuid));
	}

	protected byte getColor(int x, int y)
	{
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
	
	public String formatPoint(int x, int y)
	{
		return "(" + x + ", " + y + ")";
	}
	
	public String formatPV()
	{
		return "Fill point: " + formatPoint(curPixelCoord[0], curPixelCoord[1]) + " | Ref 1: " + 
						formatPoint(curPixelCoord[2], curPixelCoord[3]) + " | Ref 2: " + 
						formatPoint(curPixelCoord[4], curPixelCoord[5]);
	}
	
	public String formatPVColors()
	{
		return "Fill point: " + formatColor(curPixelCoord[0], curPixelCoord[1]) + " | Ref 1: " + 
						formatColor(curPixelCoord[2], curPixelCoord[3]) + " | Ref 2: " + 
						formatColor(curPixelCoord[4], curPixelCoord[5]);
	}
	
	public String formatColor(int x, int y)
	{
		Color c = new Color(img.getRGB(x, y));
		
		return "(r:" + c.getRed() + ", g:" + c.getGreen() + 
						", b:" + c.getBlue() + ", a: " + c.getAlpha() + ")";
	}
}
