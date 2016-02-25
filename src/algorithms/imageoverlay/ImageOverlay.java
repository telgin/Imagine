package algorithms.imageoverlay;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import algorithms.Algorithm;
import config.Constants;
import key.Key;
import product.Product;
import product.ProductIOException;
import util.ByteConversion;
import util.algorithms.HashRandom;
import util.algorithms.ImageUtil;
import util.algorithms.UniqueRandomRange;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ImageOverlay implements Product
{
	protected Algorithm f_algorithm;
	protected BufferedImage f_img;
	protected UniqueRandomRange f_randOrder;
	protected HashRandom f_random;
	protected Key f_key;
	protected boolean f_skippedAll;
	protected byte[] f_uuid;
	protected InsertionDensity f_density;
	protected int f_colorIndex;
	protected int[] f_split;
	private int f_colorMod;
	protected int[] f_curPixelCoord;
	private boolean f_incrementFailed;

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 */
	public ImageOverlay(Algorithm p_algo, Key p_key)
	{
		f_algorithm = p_algo;
		f_key = p_key;
		
		f_skippedAll = false;
		f_incrementFailed = false;
		f_colorIndex = 0;
		f_colorMod = 0;
		
		f_density = InsertionDensity.parseDensity(p_algo.getParameterValue("InsertionDensity"));
		f_curPixelCoord = new int[2];
		
		//the split array is just a stationary array
		//for efficiency in the byte splitting operations
		if (f_density.equals(InsertionDensity.k_25))
		{
			f_split = new int[4];
		}
		else //k_50
		{
			f_split = new int[2];
		}
	}

	/**
	 * @credit http://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
	 * @param p_toCopy
	 * @return
	 */
	protected static BufferedImage clone(BufferedImage p_toCopy)
	{
		ColorModel cm = p_toCopy.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = p_toCopy.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	/**
	 * @update_comment
	 */
	public void reset()
	{
		// use any constant seed to start
		f_random = new HashRandom(Constants.DEFAULT_SEED);

		// obtain a random order
		f_randOrder = new UniqueRandomRange(f_random, f_img.getWidth() * f_img.getHeight());

		f_colorIndex = 0;
		f_colorMod = 0;

		f_incrementFailed = false;
	}

	/**
	 * @update_comment
	 * @throws ProductIOException
	 */
	protected final void nextPair() throws ProductIOException
	{
		if (f_incrementFailed)
			throw new ProductIOException("previous increment failed");
		
		incrementColor();
		
		if (f_colorIndex == 0)
			incrementVector();
	}

	/**
	 * @update_comment
	 */
	private final void incrementColor()
	{
		f_colorIndex = f_colorMod++ % 3;
	}

	/**
	 * @update_comment
	 * @throws ProductIOException
	 */
	private final void incrementVector() throws ProductIOException
	{
		f_incrementFailed = true;

		int pixel = f_randOrder.next();
		f_curPixelCoord[1] = pixel / f_img.getWidth();//y
		f_curPixelCoord[0] = pixel % f_img.getWidth();//x

		f_incrementFailed = false;
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
	 * @see product.Product#secureStream()
	 */
	@Override
	public void secureStream()
	{
		// uuid should be set prior to this
		f_randOrder.reseed(ByteConversion.concat(f_key.getKeyHash(), f_uuid));
	}

	/**
	 * @update_comment
	 * @param p_x
	 * @param p_y
	 * @return
	 */
	protected byte getColor(int p_x, int p_y)
	{
		if (f_colorIndex == 0)
		{
			return ImageUtil.getRed(f_img.getRGB(p_x, p_y));
		}
		else if (f_colorIndex == 1)
		{
			return ImageUtil.getGreen(f_img.getRGB(p_x, p_y));
		}
		else
		{
			return ImageUtil.getBlue(f_img.getRGB(p_x, p_y));
		}
	}

	/* (non-Javadoc)
	 * @see product.Product#getUUID()
	 */
	@Override
	public byte[] getUUID()
	{
		return f_uuid;
	}
	
	/**
	 * @update_comment
	 * @param p_x
	 * @param p_y
	 * @return
	 */
	public String formatPoint(int p_x, int p_y)
	{
		return "(" + p_x + ", " + p_y + ")";
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public String formatPV()
	{
		return "Fill point: " + formatPoint(f_curPixelCoord[0], f_curPixelCoord[1]) + " | Ref 1: " + 
			formatPoint(f_curPixelCoord[2], f_curPixelCoord[3]) + " | Ref 2: " + 
			formatPoint(f_curPixelCoord[4], f_curPixelCoord[5]);
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public String formatPVColors()
	{
		return "Fill point: " + formatColor(f_curPixelCoord[0], f_curPixelCoord[1]) + " | Ref 1: " + 
			formatColor(f_curPixelCoord[2], f_curPixelCoord[3]) + " | Ref 2: " + 
			formatColor(f_curPixelCoord[4], f_curPixelCoord[5]);
	}
	
	/**
	 * @update_comment
	 * @param p_x
	 * @param p_y
	 * @return
	 */
	public String formatColor(int p_x, int p_y)
	{
		Color c = new Color(f_img.getRGB(p_x, p_y));
		
		return "(r:" + c.getRed() + ", g:" + c.getGreen() + 
			", b:" + c.getBlue() + ", a: " + c.getAlpha() + ")";
	}
}
