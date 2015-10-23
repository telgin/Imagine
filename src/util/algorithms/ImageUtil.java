package util.algorithms;

public class ImageUtil {
	
	public static byte getRed(int rgb)
	{
		return (byte) ((rgb >> 16) & 0xFF);
	}
	
	public static int setRed(int rgb, byte red)
	{
		return 0xFF000000 | ((red << 16) & 0x00FF0000) | ((getGreen(rgb) << 8) & 0x0000FF00) | (getBlue(rgb) & 0x000000FF);
	}
	
	public static byte getGreen(int rgb)
	{
		return (byte) ((rgb >> 8) & 0xFF);
	}
	
	public static int setGreen(int rgb, byte green)
	{
		return 0xFF000000 | ((getRed(rgb) << 16) & 0x00FF0000) | ((green << 8) & 0x0000FF00) | (getBlue(rgb) & 0x000000FF);
	}
	
	public static byte getBlue(int rgb)
	{
		return (byte) (rgb & 0xFF);
	}
	
	public static int setBlue(int rgb, byte blue)
	{
		return 0xFF000000 | ((getRed(rgb) << 16) & 0x00FF0000) | ((getGreen(rgb) << 8) & 0x0000FF00) | (blue & 0x000000FF);
	}
	
}
