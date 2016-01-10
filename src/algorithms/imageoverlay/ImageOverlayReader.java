package algorithms.imageoverlay;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import algorithms.Algorithm;
import algorithms.imageoverlay.patterns.Pattern;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductIOException;
import product.ProductMode;
import product.ProductReader;
import scratch.Scratch;
import util.ByteConversion;

public class ImageOverlayReader extends ImageOverlay implements ProductReader
{

	public ImageOverlayReader(Algorithm algo, Key key)
	{
		super(algo, key);
	}

	private byte read() throws ProductIOException
	{
		// Logger.log(LogLevel.k_debug, "Reading " + 1 + " byte.");
		byte xor = random.nextByte();

		int[] fourVals = new int[] { 0, 0, 0, 0 };

		for (int i = 0; i < 4; ++i)
		{
			nextPair();

			int c0 = ByteConversion.byteToInt(getColor(pv[0], pv[1]));
			int c1 = ByteConversion.byteToInt(getColor(pv[2], pv[3]));
			int c2 = ByteConversion.byteToInt(getColor(pv[4], pv[5]));

//			if (Scratch.x < 20)
//			{
//				System.out.println("Magic point group: " + c0 + ", " + c1 + ", " + c2);
//			}

			int min = Math.min(c1, c2);
//			if (Scratch.x < 20)
//				System.out.println("min: " + min);
			if (min > 2)
			{
				fourVals[i] = c0 - (min - 3);
			}
			else
			{
				fourVals[i] = (min + 3) - c0;
			}
			
			
//			if (Scratch.x < 20)
//				System.out.println("fourVals[i]: " + fourVals[i]);
			
			//int bytes = 3642821;
			//if ((byteCount > bytes - 5 && byteCount < bytes + 5) || fourVals[i] < 0
			//				|| fourVals[i] > 3)
			//{
			//	System.out.println("i: " + i);
			//	System.out.println(byteCount);
			//	System.out.println(fourVals[0] + ", " + fourVals[1] + ", " + fourVals[2]
			//					+ ", " + fourVals[3]);
			//	System.out.println(c0 + ", " + c1 + ", " + c2);
			//	System.out.println(pv[0] + ", " + pv[1] + ", " + pv[2] + ", " + pv[3]
			//					+ ", " + pv[4] + ", " + pv[5]);
			//}
		}

		int val = (((fourVals[0] * 4) + fourVals[1]) * 16)
						+ ((fourVals[2] * 4) + fourVals[3]);
		// if (byteCount < 500)
		// System.out.println(val);

		++byteCount;
		// ++Scratch.x;

		// System.out.print(ByteConversion.bytesToHex(new byte[]{secured}));
		byte toReturn =  ByteConversion.intToByte(ByteConversion.intToByte(val) ^ xor);
		
//		if (Scratch.x < 20)
//			System.out.println(Scratch.x + ": Reading: " + ByteConversion.bytesToHex(
//							new byte[]{ByteConversion.intToByte(toReturn)}));
		
		return toReturn;
	}

	// private void old()
	// {
	// while (true)
	// {
	// nextPair();
	// int val = 0;
	// int c0 = ByteConversion.byteToInt(getColor(pv[0], pv[1]));
	// int c1 = ByteConversion.byteToInt(getColor(pv[2], pv[3]));
	// int c2 = ByteConversion.byteToInt(getColor(pv[4], pv[5]));
	//
	// int diff = Math.abs(c1 - c2);
	// int vadd = c0 - Math.min(c1, c2);
	// val += vadd;
	//
	// if (vadd < diff)
	// break;
	// }
	// }

	@Override
	public int read(byte[] bytes, int offset, int length)
	{
		// Logger.log(LogLevel.k_debug, "Reading " + bytes.length + " bytes.");
		for (int x = offset; x < offset + length; ++x)
		{
			try
			{
				bytes[x] = read();
			}
			catch (ProductIOException e)
			{
				return x;
			}
		}
		//// System.out.println();

		return offset + length;
	}

	@Override
	public void loadFile(File f) throws IOException
	{
		img = ImageIO.read(f);
		reset();
	}

	@Override
	public long skip(long bytes)
	{
		Logger.log(LogLevel.k_debug, "Skipping " + bytes + " bytes.");

		long skipped = 0;
		try
		{
			for (long l = 0; l < bytes; ++l)
			{
				random.nextByte();
				nextPair();

				++skipped;
			}
		}
		catch (ProductIOException e)
		{
			// couldn't skip as many as requested,
			// nothing to do
		}

		Logger.log(LogLevel.k_debug, "Skipping " + bytes + " bytes was requested and "
						+ skipped + " were skipped.");
		byteCount += bytes;

		return skipped;

	}
}
