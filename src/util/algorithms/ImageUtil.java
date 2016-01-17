package util.algorithms;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	
	
	public static BufferedImage constructTestWebImage1()
	{
		BufferedImage img = new BufferedImage(1000,500,BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		
		//set white background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1000, 500);
		
		for (int s=1; s<31; ++s)
		{
			//System.out.println(s);
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
	
	private static int consecSum(int i)
	{
		if (i == 0)
			return 0;
		else
			return i + consecSum(i-1);
	}

}
