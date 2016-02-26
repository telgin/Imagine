package util.algorithms;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import util.ByteConversion;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Standard image operations.
 */
public class ImageUtil
{

	/**
	 * Gets the red byte from an rgb int
	 * @param p_rgb The rgb int
	 * @return The red value
	 */
	public static byte getRed(int p_rgb)
	{
		return (byte) ((p_rgb >> 16) & 0xFF);
	}

	/**
	 * Creates an rgb int which is the given rgb int with the red set to the given value.
	 * @param p_rgb The rgb int
	 * @param p_red The red value to set
	 * @return The new rgb
	 */
	public static int setRed(int p_rgb, byte p_red)
	{
		return 0xFF000000 | ((p_red << 16) & 0x00FF0000)
						| ((getGreen(p_rgb) << 8) & 0x0000FF00)
						| (getBlue(p_rgb) & 0x000000FF);
	}

	/**
	 * Gets the green byte from an rgb int
	 * @param p_rgb The rgb int
	 * @return The green value
	 */
	public static byte getGreen(int p_rgb)
	{
		return (byte) ((p_rgb >> 8) & 0xFF);
	}

	/**
	 * Creates an rgb int which is the given rgb int with the green set to the given value.
	 * @param p_rgb The rgb int
	 * @param p_green The green value to set
	 * @return The new rgb
	 */
	public static int setGreen(int p_rgb, byte p_green)
	{
		return 0xFF000000 | ((getRed(p_rgb) << 16) & 0x00FF0000)
						| ((p_green << 8) & 0x0000FF00) | (getBlue(p_rgb) & 0x000000FF);
	}

	/**
	 * Gets the blue byte from an rgb int
	 * @param p_rgb The rgb int
	 * @return The blue value
	 */
	public static byte getBlue(int p_rgb)
	{
		return (byte) (p_rgb & 0xFF);
	}

	/**
	 * Creates an rgb int which is the given rgb int with the blue set to the given value.
	 * @param p_rgb The rgb int
	 * @param p_blue The blue value to set
	 * @return The new rgb
	 */
	public static int setBlue(int p_rgb, byte p_blue)
	{
		return 0xFF000000 | ((getRed(p_rgb) << 16) & 0x00FF0000)
						| ((getGreen(p_rgb) << 8) & 0x0000FF00) | (p_blue & 0x000000FF);
	}
	
	/**
	 * Compares images to see if each pixel has the same colors. Used for testing purposes only.
	 * @param p_img1 The first image
	 * @param p_img2 The second image
	 */
	public static void compareImages(BufferedImage p_img1, BufferedImage p_img2)
	{
		for (int x = 0; x < p_img2.getWidth(); ++x)
		{
			for (int y = 0; y < p_img2.getHeight(); ++y)
			{
				if (p_img2.getRGB(x, y) != p_img2.getRGB(x, y))
				{
					System.out.println("Not right: " + x + ", " + y);
				}

				int r1 = ByteConversion.byteToInt(ImageUtil.getRed(p_img1.getRGB(x, y)));
				int g1 = ByteConversion
								.byteToInt(ImageUtil.getGreen(p_img1.getRGB(x, y)));
				int b1 = ByteConversion
								.byteToInt(ImageUtil.getBlue(p_img1.getRGB(x, y)));
				int r2 = ByteConversion
								.byteToInt(ImageUtil.getRed(p_img2.getRGB(x, y)));
				int g2 = ByteConversion
								.byteToInt(ImageUtil.getGreen(p_img2.getRGB(x, y)));
				int b2 = ByteConversion
								.byteToInt(ImageUtil.getBlue(p_img2.getRGB(x, y)));

				if (r1 != r2)
					System.out.println("Red not right: " + r1 + ", " + r2);

				if (g1 != g2)
					System.out.println("Green not right: " + g1 + ", " + g2);

				if (b1 != b2)
					System.out.println("Blue not right: " + b1 + ", " + b2);
			}
		}
	}
	
	
	/**
	 * Construct a image of ever increasing sizes of green squares. 
	 * Color increases from black to green from right to left. Square 
	 * size increases from top to bottom. This image is intended to show
	 * how compression works with similar but different color values.
	 * @return The image
	 */
	public static BufferedImage constructTestWebImage1()
	{
		BufferedImage img = new BufferedImage(1000,500,BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		
		//set white background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1000, 500);
		
		for (int s=1; s<31; ++s)
		{
			for (int x=0; x<256; ++x)
			{
				int x0 = s*x;
				int y0 = consecSum(s)-1;
				int w = s;
				int h = s;
				int x1 = x0 + w;
				int y1 = y0 + h;
				
				if (x1 > 1000 || y1 > 500)
				{
					break;
				}
				
				g.setColor(new Color(0,x,0));
				g.fillRect(x0, y0, s, s);
			}
		}

		return img;
	}
	
	/**
	 * Construct a black and white image where pixels of white are surrounded
	 * by pixels of black. This is used to show how compression works when colors
	 * are very different but close together.
	 * @return The image
	 */
	public static BufferedImage constructTestWebImage2()
	{
		BufferedImage img = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		
		//set white background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 100, 100);
		
		for (int y=0; y<100; ++y)
		{
			for (int x=0; x<100; ++x)
			{
				if (x % 2 == 0 || y % 2 == 0)
					img.setRGB(x, y, Color.BLACK.getRGB());
			}
		}

		return img;
	}
	
	/**
	 * Helper function which gets consecutive sums. This is defined as
	 * the sum of the input number and every integer less than it down to zero.
	 * Bad recursive function. Will explode stack if input is too large.
	 * @param p_num The input number
	 * @return The sum
	 */
	private static int consecSum(int p_num)
	{
		if (p_num == 0)
			return 0;
		else
			return p_num + consecSum(p_num-1);
	}

	/**
	 * @credit http://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
	 * Clones a buffered image
	 * @param p_toCopy the buffered image to clone
	 * @return The cloned buffered image
	 */
	public static BufferedImage clone(BufferedImage p_toCopy)
	{
		ColorModel cm = p_toCopy.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = p_toCopy.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
}
