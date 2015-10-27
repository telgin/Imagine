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
import product.ProductMode;
import product.ProductReader;
import util.ByteConversion;

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
		int val = 0;
		
		while (true)
		{
			nextPair();
			
			int c0 = ByteConversion.byteToInt(getColor(pv[0], pv[1]));
			int c1 = ByteConversion.byteToInt(getColor(pv[2], pv[3]));
			int c2 = ByteConversion.byteToInt(getColor(pv[4], pv[5]));
			
			int diff = Math.abs(c1 - c2);
			int vadd = c0 - Math.min(c1, c2);
			val += vadd;
			
			if (vadd < diff)
				break;
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
				nextPair();
				
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
