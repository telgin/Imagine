package algorithms.stealthpng;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import algorithms.Algorithm;
import algorithms.ProductIOException;
import algorithms.stealthpng.patterns.Pattern;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductWriter;
import stats.ProgressMonitor;
import stats.Stat;
import testing.Scratch;
import util.ByteConversion;
import util.myUtilities;
import util.algorithms.ImageUtil;

public class StealthPNGWriter extends StealthPNG implements ProductWriter{
	private File inputImages;
	
	public StealthPNGWriter(Algorithm algo, Key key)
	{
		super(algo, key);
		inputImages = new File(algo.getParameterValue("imageFolder"));
		InputImageManager.setInputFolder(inputImages);
	}
	
	@Override
	public void newProduct() {
		Scratch.x = 0;
		loadCleanFile();
		reset();
	}
	
	private void loadCleanFile()
	{
		File imgFile = InputImageManager.nextImageFile();
		try {
			img = ImageIO.read(imgFile);
		} catch (IOException e) {
			 //TODO how to handle no images left
			e.printStackTrace();
		}
	}

	@Override
	public boolean write(byte b) {
		//if (byteCount > 534000)
		//{
		//	System.out.print("Byte Count: " + byteCount);
		//	System.out.println(", Remaining Bytes: " + getRemainingBytes());
		//}
		
		//Note: can get 18% using 4x4 or 16% using 16x2 (vs. current of 3%)
		//~24% using 4x4
		//22.6% using binary and zeros
		//45.2% using '4 enforcement'
		//	remains to be seen how this will affect png compression
		//	21MB may become larger? There's technically 47MB of rgb's
		//	would then become 25%
		//	though maybe not that bad because won't be as random as fullpng
		//	everything will still be close
		//could be even more confusing and switch modes from 1,4,16
		//based on space available
		//really complicated function, but could probably be efficient too
		
		//probably:
		//try viability of 4 enforcement first
		//	otherwise use regular 4x4
		
		
		try
		{
			int vLeft = ByteConversion.byteToInt(
					ByteConversion.intToByte(b ^ random.nextByte()));
			//nt tLeft = 255;
			//vLeft = Scratch.x % 16;
			
			//enforcemnt4();
			
			while (true)
			{
				nextPair();
				
//				System.out.println("-------------------");
				
				int c1 = ByteConversion.byteToInt(getColor(pv[2], pv[3]));
				int c2 = ByteConversion.byteToInt(getColor(pv[4], pv[5]));
				
//				System.out.println("pv[0]: " + pv[0] + ", " + "pv[1]: " + pv[1] + ", " + "pv[2]: " + pv[2]);
//				System.out.println("c1: " + c1);
//				System.out.println("c2: " + c2);
//				System.out.println("vLeft: " + vLeft);

				int diff = Math.abs(c1 - c2);
				int vsub = Math.min(vLeft, diff);
				int val = Math.min(c1, c2) + vsub;
				
//				System.out.println("diff: " + diff);
//				System.out.println("vsub: " + vsub);
//				System.out.println("val: " + val);
//				System.out.println(ByteConversion.byteToInt(getColor(pv[0])) + " --> " + val);
				
				setColor(pv[0], pv[1], ByteConversion.intToByte(val));
				
				vLeft -= vsub;
				
				//System.out.println("vLeft: " + vLeft);
				
				//System.out.println("-------------------");
				
				//if (diff != 0)
				//{
					++Scratch.x;
//					Scratch.y += diff;
//					if (Scratch.map.containsKey(diff))
//					{
//						int newVal = Scratch.map.get(diff) + 1;
//						Scratch.map.put(diff, newVal);
//					}
//					else
//					{
//						Scratch.map.put(diff, 1);
//					}
				//}
				
				if (vsub < diff)
				{
					
					//System.out.println("(break)");
				
					break;
				}
			}
		}
		catch (ProductIOException e)
		{
			return false;
		}
		//System.out.print(rotations + " ");
		
		++byteCount;
		return true;
	}
	
	private final void fourEnforcement(byte b) throws ProductIOException
	{
		//always exactly 4 pairs:
		int one = 0;
		int two = 0;
		int three = 0;
		int four = 0;
		int[] fourVals = new int[]{one, two, three, four};
		
		for (int i=0; i<4; ++i)
		{
			nextPair();
			
			int c1 = ByteConversion.byteToInt(getColor(pv[2], pv[3]));
			int c2 = ByteConversion.byteToInt(getColor(pv[4], pv[5]));

			int min = Math.min(c1, c2);
			if (min > 1)
			{
				setColor(pv[0], pv[1], ByteConversion.intToByte(min - 2 + fourVals[i]));
			}
			else
			{
				setColor(pv[0], pv[1], ByteConversion.intToByte(min + 3 - fourVals[i]));
			}
		}
	}
	
	private void setColor(int x, int y, byte data)
	{
		if (colorIndex == 0)
		{
			img.setRGB(x, y, ImageUtil.setRed(img.getRGB(x, y), data));
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
		//Logger.log(LogLevel.k_debug, "Writing " + bytes.length + " bytes.");
		for (int x = offset; x < offset + length; ++x)
		{
			if (!write(bytes[x]))
				return x - offset;
		}
		////System.out.println();
		
		return length;
	}

	@Override
	public void saveFile(File productStagingFolder, String fileName) {
		System.out.println("Scratch.x: " + Scratch.x);
		System.out.println("Avg. " + Scratch.y / Scratch.x);
		//ArrayList<String> diffs = new ArrayList<String>();
		//ArrayList<Integer> sorted = new ArrayList<Integer>(Scratch.map.keySet());
		//sorted.sort(null);
		//for (int key:sorted)
		//	diffs.add(key + "," + Scratch.map.get(key));
		//myUtilities.writeListToFile(new File("/home/tom/diffs.csv"), diffs);
		try {
			File imgFile = new File(productStagingFolder.getAbsolutePath() + "/" + fileName + ".png");
			Logger.log(LogLevel.k_info, "Saving product file: " + imgFile.getAbsolutePath());
			if (!imgFile.getParentFile().exists())
				imgFile.getParentFile().mkdirs();
			ImageIO.write(img, "PNG", imgFile);
			
			//update progress
			Stat stat = ProgressMonitor.getStat("productsCreated");
			if (stat != null)
				stat.incrementNumericProgress(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
