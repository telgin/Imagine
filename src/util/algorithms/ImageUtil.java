package util.algorithms;

import java.awt.image.BufferedImage;

import util.ByteConversion;

public class ImageUtil
{

	public static byte getRed(int rgb)
	{
		return (byte) ((rgb >> 16) & 0xFF);
	}

	public static int setRed(int rgb, byte red)
	{
		return 0xFF000000 | ((red << 16) & 0x00FF0000)
						| ((getGreen(rgb) << 8) & 0x0000FF00)
						| (getBlue(rgb) & 0x000000FF);
	}

	public static byte getGreen(int rgb)
	{
		return (byte) ((rgb >> 8) & 0xFF);
	}

	public static int setGreen(int rgb, byte green)
	{
		return 0xFF000000 | ((getRed(rgb) << 16) & 0x00FF0000)
						| ((green << 8) & 0x0000FF00) | (getBlue(rgb) & 0x000000FF);
	}

	public static byte getBlue(int rgb)
	{
		return (byte) (rgb & 0xFF);
	}

	public static int setBlue(int rgb, byte blue)
	{
		return 0xFF000000 | ((getRed(rgb) << 16) & 0x00FF0000)
						| ((getGreen(rgb) << 8) & 0x0000FF00) | (blue & 0x000000FF);
	}
	
	public static void compareImages(BufferedImage img1, BufferedImage img2)
	{
		for (int x = 0; x < img2.getWidth(); ++x)
		{
			for (int y = 0; y < img2.getHeight(); ++y)
			{
				if (img2.getRGB(x, y) != img2.getRGB(x, y))
				{
					System.out.println("Not right: " + x + ", " + y);
				}

				int r1 = ByteConversion.byteToInt(ImageUtil.getRed(img1.getRGB(x, y)));
				int g1 = ByteConversion
								.byteToInt(ImageUtil.getGreen(img1.getRGB(x, y)));
				int b1 = ByteConversion
								.byteToInt(ImageUtil.getBlue(img1.getRGB(x, y)));
				int r2 = ByteConversion
								.byteToInt(ImageUtil.getRed(img2.getRGB(x, y)));
				int g2 = ByteConversion
								.byteToInt(ImageUtil.getGreen(img2.getRGB(x, y)));
				int b2 = ByteConversion
								.byteToInt(ImageUtil.getBlue(img2.getRGB(x, y)));

				if (r1 != r2)
					System.out.println("Red not right: " + r1 + ", " + r2);

				if (g1 != g2)
					System.out.println("Green not right: " + g1 + ", " + g2);

				if (b1 != b2)
					System.out.println("Blue not right: " + b1 + ", " + b2);
			}
		}
	}

}
