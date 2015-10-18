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
import config.Configuration;

public class TextBlock implements Product{
	private Algorithm algorithm;
	private byte[] buffer;
	private int index;
	private int blockSize;

	public TextBlock(Algorithm algo) {
		algorithm = algo;
		blockSize = Integer.parseInt(algorithm.getParameterValue("blockSize"));
		Logger.log(LogLevel.k_debug, "TextBlock Created");
	}

	@Override
	public void newProduct() {
		buffer = new byte[blockSize];
		index = 0;
	}

	@Override
	public long getRemainingBytes() {
		return buffer.length - index;
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
		//not used currently
	}

	@Override
	public ProductMode getProductMode() {
		return algorithm.getProductSecurityLevel();
	}

	@Override
	public void secureStream() {
		//only normal mode supported
	}

	@Override
	public void write(byte b) {
		//System.out.println("Wrote: " + 1);
		buffer[index++] = b;
	}

	@Override
	public void write(byte[] bytes) {
		System.arraycopy(bytes, 0, buffer, index, bytes.length);
		index += bytes.length;
		//System.out.println("Wrote: " + bytes.length);
	}

	@Override
	public void saveFile(File productStagingFolder, String filename) {
		try {
			File toSave = new File(productStagingFolder.getAbsolutePath() + "/" +
					filename + ".txt");
			Logger.log(LogLevel.k_info, "Saving product file: " + toSave.getAbsolutePath());
			Files.write(toSave.toPath(), buffer);
			
			//update progress
			Stat stat = ProgressMonitor.getStat("productsCreated");
			if (stat != null)
				stat.incrementNumericProgress(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte read() {
		//System.out.println("Read: " + 1);
		return buffer[index++];
	}

	@Override
	public void read(byte[] bytes) {
		System.arraycopy(buffer, index, bytes, 0, bytes.length);
		index += bytes.length;
		//System.out.println("Read: " + bytes.length);
	}

	@Override
	public void loadFile(File f) throws IOException {
		index = 0;
		buffer = Files.readAllBytes(f.toPath());
	}

	@Override
	public void skip(long bytes) {
		index += bytes;
	}

	@Override
	public byte[] readUUID() {
		//not currently used
		return null;
	}

	@Override
	public byte[] getUUID() {
		//not currently used
		return null;
	}

}
