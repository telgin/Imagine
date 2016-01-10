package algorithms.imageoverlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import algorithms.Algorithm;
import algorithms.imageoverlay.patterns.Pattern;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductIOException;
import product.ProductWriter;
import scratch.Scratch;
import stats.ProgressMonitor;
import stats.Stat;
import util.ByteConversion;
import util.myUtilities;
import util.algorithms.ImageUtil;

public class ImageOverlayWriter extends ImageOverlay implements ProductWriter
{
	private File inputImages;
	private InputImageManager manager;

	public ImageOverlayWriter(Algorithm algo, Key key)
	{
		super(algo, key);
		File imageFolder = new File(algo.getParameterValue("ImageFolder"));
		ConsumptionMode mode = ConsumptionMode.parseMode(
						algo.getParameterValue("ImageConsumptionMode"));
		manager = InputImageManager.getInstance(imageFolder, mode);
	}

	@Override
	public void newProduct() throws ProductIOException
	{
		// Scratch.x = 0;
		loadCleanFile();
		reset();
	}

	private void loadCleanFile() throws ProductIOException
	{
		File imgFile = manager.nextImageFile();
		
		//ran out of images
		if (imgFile == null)
		{
			throw new ProductIOException("No input images remain.");
		}
		
		boolean foundFile = false;
		while (imgFile != null && !foundFile)
		{
			try
			{
				img = ImageIO.read(imgFile);
				if (img.getColorModel().hasAlpha())
				{
					removeAlpha();
				}
				foundFile = true;
			}
			catch (IOException e)
			{
				Logger.log(LogLevel.k_warning, "Could not interpret input image file: " + imgFile.getName());
			}
		}
		
		//ran out of images after trying some unsuccessfully
		if (imgFile == null)
		{
			throw new ProductIOException("No input images remain.");
		}
		
		
	}
	
	/**
	 * @credit http://stackoverflow.com/questions/26918675/removing-transparency-in-png-bufferedimage
	 * @update_comment
	 */
	private void removeAlpha()
	{
		BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = copy.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();
		
		img = copy;
	}

	@Override
	public boolean write(byte b)
	{
		// if (byteCount > 534000)
		// {
		// System.out.print("Byte Count: " + byteCount);
		// System.out.println(", Remaining Bytes: " + getRemainingBytes());
		// }

		// Note: can get 18% using 4x4 or 16% using 16x2 (vs. current of 3%)
		// ~24% using 4x4
		// 22.6% using binary and zeros
		// 45.2% using '4 enforcement'
		// remains to be seen how this will affect png compression
		// 21MB may become larger? There's technically 47MB of rgb's
		// would then become 25%
		// though maybe not that bad because won't be as random as image
		// everything will still be close
		// could be even more confusing and switch modes from 1,4,16
		// based on space available
		// really complicated function, but could probably be efficient too

		// probably:
		// try viability of 4 enforcement first
		// otherwise use regular 4x4

//		if (pv[0] == 3044 && pv[1] == 1690)
//			System.out.println("Happened by write");
		
//		if (Scratch.x < 20)
//				System.out.println(Scratch.x + ": Writing: " + ByteConversion.bytesToHex(
//								new byte[]{ByteConversion.intToByte(b)}));
		

		try
		{
			int vLeft = ByteConversion
							.byteToInt(ByteConversion.intToByte(b ^ random.nextByte()));
			
			

			fourEnforcement(vLeft);

		}
		catch (ProductIOException e)
		{
			e.printStackTrace();
			return false;
		}
		// System.out.print(rotations + " ");

		++byteCount;
		return true;
	}

	// private void working()
	// {
	// nextPair();
	//
	//// System.out.println("-------------------");
	//
	// int c1 = ByteConversion.byteToInt(getColor(pv[2], pv[3]));
	// int c2 = ByteConversion.byteToInt(getColor(pv[4], pv[5]));
	//
	//// System.out.println("pv[0]: " + pv[0] + ", " + "pv[1]: " + pv[1] + ", "
	// + "pv[2]: " + pv[2]);
	//// System.out.println("c1: " + c1);
	//// System.out.println("c2: " + c2);
	//// System.out.println("vLeft: " + vLeft);
	//
	// int diff = Math.abs(c1 - c2);
	// int vsub = Math.min(vLeft, diff);
	// int val = Math.min(c1, c2) + vsub;
	//
	//// System.out.println("diff: " + diff);
	//// System.out.println("vsub: " + vsub);
	//// System.out.println("val: " + val);
	//// System.out.println(ByteConversion.byteToInt(getColor(pv[0])) + " --> "
	// + val);
	//
	// setColor(pv[0], pv[1], ByteConversion.intToByte(val));
	//
	// vLeft -= vsub;
	//
	// //System.out.println("vLeft: " + vLeft);
	//
	// //System.out.println("-------------------");
	//
	// //if (diff != 0)
	// //{
	// ++Scratch.x;
	//// Scratch.y += diff;
	//// if (Scratch.map.containsKey(diff))
	//// {
	//// int newVal = Scratch.map.get(diff) + 1;
	//// Scratch.map.put(diff, newVal);
	//// }
	//// else
	//// {
	//// Scratch.map.put(diff, 1);
	//// }
	// //}
	//
	// if (vsub < diff)
	// {
	//
	// //System.out.println("(break)");
	//
	// break;
	// }
	// }

	private final void fourEnforcement(int val) throws ProductIOException
	{
//		if (val > 255 || val < 0)
//			System.out.println("This is bad: " + val);
//
//		if (pv[0] == 3044 && pv[1] == 1690)
//			System.out.println("Happened by four enforcement1");
//		if (Scratch.x < 20)
//			System.out.println("4enforcement val: " + 
//							ByteConversion.bytesToHex(new byte[]{ByteConversion.intToByte(val)}));

		int div16 = val / 16;
		int mod16 = val % 16;

		int[] fourVals = new int[] { div16 / 4, div16 % 4, mod16 / 4, mod16 % 4 };
		// System.out.println(fourVals[0] + ", " + fourVals[1] + ", " +
		// fourVals[2] + ", " + fourVals[3]);

		for (int i = 0; i < 4; ++i)
		{
			nextPair();

//			if (pv[0] == 3044 && pv[1] == 1690)
//				System.out.println("affected by getColor?");

			int c1 = ByteConversion.byteToInt(getColor(pv[2], pv[3]));
			int c2 = ByteConversion.byteToInt(getColor(pv[4], pv[5]));

//			if (pv[0] == 3044 && pv[1] == 1690)
//				System.out.println("Happened by write loop");

			int min = Math.min(c1, c2);
			int toSet = -1;
//			if (Scratch.x < 20)
//				System.out.println("min: " + min);
			if (min > 2)
			{
				toSet = (min - 3) + fourVals[i];
				setColor(pv[0], pv[1], ByteConversion.intToByte(toSet));
			}
			else
			{
				toSet = (min + 3) - fourVals[i];
				setColor(pv[0], pv[1], ByteConversion.intToByte(toSet));
			}
			
//			if (Scratch.x < 20)
//				System.out.println("fourVals[i]: " + fourVals[i]);
//			
//			if (Scratch.x < 20)
//				System.out.println("toSet: " + ByteConversion.intToByte(toSet));
//			
//			if (Scratch.x < 20)
//				System.out.println("Set: " + formatColor(pv[0], pv[1]));

//			int c0 = ByteConversion.byteToInt(getColor(pv[0], pv[1]));
//
//			if (pv[4] == 3044 && pv[5] == 1690)
//			{
//				System.out.println("Magic point group: " + c0 + ", " + c1 + ", " + c2);
//				System.out.println("Current color index: " + colorIndex);
//				int r1 = ByteConversion
//								.byteToInt(ImageUtil.getRed(img.getRGB(3044, 1690)));
//				int g1 = ByteConversion
//								.byteToInt(ImageUtil.getGreen(img.getRGB(3044, 1690)));
//				int b1 = ByteConversion
//								.byteToInt(ImageUtil.getBlue(img.getRGB(3044, 1690)));
//				System.out.println(
//								"Independantly evaluated: " + r1 + ", " + g1 + ", " + b1);
//			}

//			int bytes = 3642821;
//			if ((byteCount > bytes - 5 && byteCount < bytes + 5) || c0 != toSet
//							|| fourVals[i] < 0 || fourVals[i] > 3)
//			{
//				System.out.println("val: " + val + ", i: " + i);
				//xSystem.out.println("toSet: " + toSet + ", c0: " + c0);
//				System.out.println(byteCount);
//				System.out.println(fourVals[0] + ", " + fourVals[1] + ", " + fourVals[2]
//								+ ", " + fourVals[3]);
//				System.out.println(c0 + ", " + c1 + ", " + c2);
//				System.out.println(pv[0] + ", " + pv[1] + ", " + pv[2] + ", " + pv[3]
//								+ ", " + pv[4] + ", " + pv[5]);
				// System.out.println(Pattern.validIndex(pattern, pv[0], pv[1],
				// img.getWidth(), img.getHeight()));
				// System.out.println(Pattern.validIndex(pattern, pv[2], pv[3],
				// img.getWidth(), img.getHeight()));
				// System.out.println(Pattern.validIndex(pattern, pv[4], pv[5],
				// img.getWidth(), img.getHeight()));

//			}
		}

//		boolean truth = val == (((fourVals[0] * 4) + fourVals[1]) * 16)
//						+ ((fourVals[2] * 4) + fourVals[3]);
//		if (!truth)
//			System.out.println("No truth..." + val + ", "
//							+ (((fourVals[0] * 4) + fourVals[1]) * 16)
//							+ ((fourVals[2] * 4) + fourVals[3]));

		// ++Scratch.x;
	}

	private void setColor(int x, int y, byte data)
	{

//		if (!Pattern.validIndex(pattern, x, y, img.getWidth(), img.getHeight()))
//			System.out.println("Something's wrong: " + x + ", " + y);
//		if (x == 3044 && y == 1690)
//		{
//			System.out.println("Setting color " + colorIndex + " to "
//							+ ByteConversion.byteToInt(data));
//			System.out.println();
//		}

		if (colorIndex == 0)
		{
			int redRGB = ImageUtil.setRed(img.getRGB(x, y), data);
			
			img.setRGB(x, y, redRGB);
//			if (Scratch.x < 20)
//				System.out.println(new Color(img.getRGB(x, y)).getRed());
		}
		else if (colorIndex == 1)
		{
			img.setRGB(x, y, ImageUtil.setGreen(img.getRGB(x, y), data));
		}
		else
		{
			img.setRGB(x, y, ImageUtil.setBlue(img.getRGB(x, y), data));
		}
	}

	@Override
	public int write(byte[] bytes, int offset, int length)
	{
//		if (bytes.length == 8)
//			System.out.println("Writing array: " + ByteConversion.bytesToLong(bytes));
//		Logger.log(LogLevel.k_debug, "Writing " + bytes.length + " bytes.");
//		System.out.println("Remaining numbers: " + randOrder.remainingNumbers());
		for (int x = offset; x < offset + length; ++x)
		{
//			if (pv[0] == 3044 && pv[1] == 1690)
//				System.out.println("Happened by write array: " + offset + ", " + length
//								+ ", " + x + ", " + bytes.length);

			if (!write(bytes[x]))
				return x - offset;
		}
		//// System.out.println();

		return length;
	}

	@Override
	public void saveFile(File productStagingFolder, String fileName)
	{
		// System.out.println("Scratch.x: " + Scratch.x);
		// System.out.println("Avg. " + Scratch.y / Scratch.x);
		// ArrayList<String> diffs = new ArrayList<String>();
		// ArrayList<Integer> sorted = new
		// ArrayList<Integer>(Scratch.map.keySet());
		// sorted.sort(null);
		// for (int key:sorted)
		// diffs.add(key + "," + Scratch.map.get(key));
		// myUtilities.writeListToFile(new File("/home/tom/diffs.csv"), diffs);
		try
		{
			File imgFile = new File(productStagingFolder.getAbsolutePath(), fileName + ".png");
			Logger.log(LogLevel.k_info,
							"Saving product file: " + imgFile.getAbsolutePath());
			if (!imgFile.getParentFile().exists())
				imgFile.getParentFile().mkdirs();
			ImageIO.write(img, "png", imgFile);

//			System.out.println("Testing image for correctness...");
//			BufferedImage img2 = ImageIO.read(imgFile);
//			for (int x = 0; x < img2.getWidth(); ++x)
//			{
//				for (int y = 0; y < img2.getHeight(); ++y)
//				{
//					if (img.getRGB(x, y) != img2.getRGB(x, y))
//					{
//						System.out.println("Not right: " + x + ", " + y);
//					}
//
//					int r1 = ByteConversion.byteToInt(ImageUtil.getRed(img.getRGB(x, y)));
//					int g1 = ByteConversion
//									.byteToInt(ImageUtil.getGreen(img.getRGB(x, y)));
//					int b1 = ByteConversion
//									.byteToInt(ImageUtil.getBlue(img.getRGB(x, y)));
//					int r2 = ByteConversion
//									.byteToInt(ImageUtil.getRed(img2.getRGB(x, y)));
//					int g2 = ByteConversion
//									.byteToInt(ImageUtil.getGreen(img2.getRGB(x, y)));
//					int b2 = ByteConversion
//									.byteToInt(ImageUtil.getBlue(img2.getRGB(x, y)));
//
//					if (r1 != r2)
//						System.out.println("Red not right: " + r1 + ", " + r2);
//
//					if (g1 != g2)
//						System.out.println("Red not right: " + g1 + ", " + g2);
//
//					if (b1 != b2)
//						System.out.println("Red not right: " + b1 + ", " + b2);
//
//					if (x == 3044 && y == 1690)
//					{
//						System.out.println("Magic point: " + r1 + ", " + g1 + ", " + b1);
//						System.out.println("Magic point: " + r2 + ", " + g2 + ", " + b2);
//					}
//				}
//			}

			// update progress
			Stat stat = ProgressMonitor.getStat("productsCreated");
			if (stat != null)
				stat.incrementNumericProgress(1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
