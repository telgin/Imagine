package algorithms.stealthpng;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.google.common.io.Files;

import algorithms.Algorithm;
import algorithms.ProductIOException;
import algorithms.fullpng.FullPNG;
import algorithms.stealthpng.patterns.Pattern;
import algorithms.stealthpng.patterns.Pattern2;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductMode;
import product.ProductReader;
import stats.ProgressMonitor;
import stats.Stat;
import util.ByteConversion;
import util.Constants;
import util.algorithms.HashRandom;
import util.algorithms.ImageUtil;
import util.algorithms.UniqueRandomRange;

public class StealthPNGReader extends StealthPNG implements ProductReader{
	
	public StealthPNGReader(Algorithm algo, Key key)
	{
		super(algo, key);
	}

	@Override
	public String getAlgorithmName() {
		return algorithm.getName();
	}

	@Override
	public int getAlgorithmVersionNumber() {
		return algorithm.getVersion();
	}

	@Override
	public void setUUID(byte[] uuid) {
		this.uuid = uuid;
	}

	@Override
	public ProductMode getProductMode() {
		return algorithm.getProductSecurityLevel();
	}

	@Override
	public void secureStream() {
		//uuid should be set prior to this
		randOrder.reseed(ByteConversion.concat(key.getKeyHash(), uuid));
	}

	private byte read() throws ProductIOException {
		//Logger.log(LogLevel.k_debug, "Reading " + 1 + " byte.");
		byte xor = random.nextByte();
		int[] pv = new int[3];
		int val = 0;
		int tLeft = 255;
		
		while (tLeft > 0)
		{
			pv[0] = randOrder.next();
			while (!Pattern.validIndex(pattern, pv[0], img.getWidth(), img.getHeight()))
				pv[0] = randOrder.next();
			
			Pattern.eval(pattern, pv, img.getWidth(), img.getHeight());
			int c0 = getColor(pv[0]);
			int c1 = getColor(pv[1]);
			int c2 = getColor(pv[2]);
			int tsub = Math.abs(c1 - c2);
			val += c0 - Math.min(c1, c2);
			tLeft -= tsub;
		}
		
		++byteCount;

		//System.out.print(ByteConversion.bytesToHex(new byte[]{secured}));
		return ByteConversion.intToByte(ByteConversion.intToByte(val) ^ xor);
	}

	@Override
	public int read(byte[] bytes, int offset, int length)
	{
		//Logger.log(LogLevel.k_debug, "Reading " + bytes.length + " bytes.");
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
		////System.out.println();
		
		return offset + length;
	}

	@Override
	public void loadFile(File f) throws IOException {
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
			for (long l=0; l<bytes; ++l)
			{
				random.nextByte();
				int[] tempPv = new int[3];
				tempPv[0] = randOrder.next();
				while (!Pattern.validIndex(pattern, tempPv[0], img.getWidth(), img.getHeight()))
					tempPv[0] = randOrder.next();
				
				++skipped;
			}
		}
		catch (ProductIOException e)
		{
			//couldn't skip as many as requested,
			//nothing to do
		}
		
		Logger.log(LogLevel.k_debug, "Skipping " + bytes + " bytes was requested and " + skipped + " were skipped.");
		byteCount += bytes;
		
		return skipped;
		
	}
}
