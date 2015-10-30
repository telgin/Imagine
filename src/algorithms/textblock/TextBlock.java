package algorithms.textblock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import algorithms.Algorithm;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductMode;
import stats.ProgressMonitor;
import stats.Stat;
import util.ByteConversion;
import util.algorithms.HashRandom;
import util.algorithms.UniqueRandomRange;
import config.Configuration;
import data.Key;

public abstract class TextBlock implements Product{
	protected Algorithm algorithm;
	private Key key;
	protected byte[] buffer;
	protected int index;
	protected int blockSize;
	protected byte[] uuid;
	protected HashRandom random;
	protected UniqueRandomRange order;

	public TextBlock(Algorithm algo, Key key) {
		algorithm = algo;
		this.key = key;
		blockSize = Integer.parseInt(algorithm.getParameterValue("blockSize"));
		Logger.log(LogLevel.k_debug, "TextBlock Created");
	}
	
	protected void reset()
	{
		random = new HashRandom(1337l);
		order = new UniqueRandomRange(random, blockSize);
		index = 0;
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
		order.reseed(ByteConversion.concat(key.getKeyHash(), uuid));
	}
//	
//	@Override
//	public boolean write(byte b) {
//		///System.out.println("Wrote: " + 1);
//		try
//		{
//			buffer[index++] = b;
//			return true;
//		}
//		catch (ArrayIndexOutOfBoundsException e)
//		{
//			return false;
//		}
//	}
//
//	@Override
//	public int write(byte[] bytes, int offset, int length)
//	{
//		int toWrite = Math.min((buffer.length - 1) - index, length);
//		System.arraycopy(bytes, offset, buffer, index, toWrite);
//		index += toWrite;
//		return toWrite;
//		//System.out.println("Wrote: " + bytes.length);
//	}
//
//	@Override
//	public void saveFile(File productStagingFolder, String filename) {
//		try {
//			File toSave = new File(productStagingFolder.getAbsolutePath() + "/" +
//					filename + ".txt");
//			Logger.log(LogLevel.k_info, "Saving product file: " + toSave.getAbsolutePath());
//			Files.write(toSave.toPath(), buffer);
//			
//			//update progress
//			Stat stat = ProgressMonitor.getStat("productsCreated");
//			if (stat != null)
//				stat.incrementNumericProgress(1);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public int read(byte[] bytes, int offset, int length)
//	{
//		int toRead = Math.min((buffer.length - 1) - index, length);
//		System.arraycopy(buffer, index, bytes, offset, toRead);
//		index += toRead;
//		return toRead;
//		//System.out.println("Read: " + bytes.length);
//	}
//
//	@Override
//	public void loadFile(File f) throws IOException
//	{
//		index = 0;
//		buffer = Files.readAllBytes(f.toPath());
//	}
//
//	@Override
//	public long skip(long bytes)
//	{
//		long toSkip = Math.min((buffer.length - 1) - index, bytes);
//		index += toSkip;
//		return toSkip;
//	}

	@Override
	public byte[] getUUID() {
		//not currently used
		return uuid;
	}
}
