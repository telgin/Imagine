package algorithms.imageoverlay;

import java.awt.Color;
import java.awt.image.BufferedImage;
import algorithms.Algorithm;
import archive.Archive;
import archive.ArchiveIOException;
import config.Constants;
import key.Key;
import util.ByteConversion;
import util.algorithms.HashRandom;
import util.algorithms.ImageUtil;
import util.algorithms.UniqueRandomRange;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The main class which handles reading and writing in the image overlay format.
 */
public class ImageOverlay implements Archive
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
	protected int[] f_curPixelCoord;
	private int f_colorMod;
	private boolean f_incrementFailed;

	/**
	 * Creates an image overlay archive interpreter instance
	 * @param p_algo The associated algorithm which contains required
	 * parameters among other things.
	 * @param p_key The key which will be used to read or write archives
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
	 * Resets the archive state
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
	 * Moves the archive interpreter to the next color location. (Possibly by incrementing
	 * the pixel location as well.)
	 * @throws ArchiveIOException If the
	 */
	protected final void nextColor() throws ArchiveIOException
	{
		if (f_incrementFailed)
			throw new ArchiveIOException("The previous increment failed, so this one will too.");
		
		f_colorIndex = f_colorMod++ % 3;
		
		if (f_colorIndex == 0)
			nextPixel();
	}

	/**
	 * Moves the archive interpreter to the next pixel location.
	 * @throws ArchiveIOException If the archive runs out of colors
	 */
	private final void nextPixel() throws ArchiveIOException
	{
		f_incrementFailed = true;

		int pixel = f_randOrder.next();
		f_curPixelCoord[1] = pixel / f_img.getWidth();//y
		f_curPixelCoord[0] = pixel % f_img.getWidth();//x

		f_incrementFailed = false;
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
	public void setUUID(byte[] uuid)
	{
		this.f_uuid = uuid;
	}

	/* (non-Javadoc)
	 * @see archive.Archive#secureStream()
	 */
	@Override
	public void secureStream()
	{
		// uuid should be set prior to this
		f_randOrder.reseed(ByteConversion.concat(f_key.getKeyHash(), f_uuid));
	}

	/**
	 * Gets the current color value
	 * @return The current color byte
	 */
	protected byte getColor()
	{
		if (f_colorIndex == 0)
		{
			return ImageUtil.getRed(f_img.getRGB(f_curPixelCoord[0], f_curPixelCoord[1]));
		}
		else if (f_colorIndex == 1)
		{
			return ImageUtil.getGreen(f_img.getRGB(f_curPixelCoord[0], f_curPixelCoord[1]));
		}
		else
		{
			return ImageUtil.getBlue(f_img.getRGB(f_curPixelCoord[0], f_curPixelCoord[1]));
		}
	}

	/* (non-Javadoc)
	 * @see archive.Archive#getUUID()
	 */
	@Override
	public byte[] getUUID()
	{
		return f_uuid;
	}
	
	/**
	 * Creates a formatted string of the given coordinate. Used for testing purposes.
	 * @param p_x The x coord
	 * @param p_y The y coord
	 * @return The formatted string
	 */
	public String formatPoint(int p_x, int p_y)
	{
		return "(" + p_x + ", " + p_y + ")";
	}
	
	/**
	 * Creates a formatted string of the color at position x,y. Used for testing purposes.
	 * @param p_x The x coord
	 * @param p_y The y coord
	 * @return The formatted string
	 */
	public String formatColor(int p_x, int p_y)
	{
		Color c = new Color(f_img.getRGB(p_x, p_y));
		
		return "(r:" + c.getRed() + ", g:" + c.getGreen() + 
			", b:" + c.getBlue() + ", a: " + c.getAlpha() + ")";
	}
}
