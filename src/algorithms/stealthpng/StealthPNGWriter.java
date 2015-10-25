package algorithms.stealthpng;

import java.io.File;
import java.io.IOException;

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
import util.ByteConversion;
import util.algorithms.HashRandom;
import util.algorithms.ImageUtil;
import util.algorithms.UniqueRandomRange;

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
		try
		{
			int[] pv = new int[3];
			int vLeft = ByteConversion.byteToInt(
					ByteConversion.intToByte(b ^ random.nextByte()));
			int tLeft = 255;
			
			while (tLeft > 0)
			{
				pv[0] = randOrder.next();
				//if (byteCount < 50)
				//{
				//	System.out.print("Byte Count: " + byteCount);
				//	System.out.println(", Random Index: " + pv[0]);
				//}
				while (!Pattern.validIndex(pattern, pv[0], img.getWidth(), img.getHeight()))
					pv[0] = randOrder.next();
				
				Pattern.eval(pattern, pv, img.getWidth(), img.getHeight());
				int c1 = getColor(pv[1]);
				int c2 = getColor(pv[2]);
				int tsub = Math.abs(c1 - c2);
				int vsub = Math.min(vLeft, tsub);
				int val = Math.min(c1, c2) + vsub;
				setColor(pv[0], ByteConversion.intToByte(val));
				vLeft = Math.max(0, vLeft-vsub);
				tLeft -= tsub;
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
	
	private void setColor(int index, byte data)
	{
		int color = index % 3;
		int pixel = index / 3;
		int y = pixel / img.getWidth();
		int x = pixel % img.getWidth();

		if (color == 0)
		{
			img.setRGB(x, y, ImageUtil.setRed(img.getRGB(x, y), data));
		}
		else if (color == 1)
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
