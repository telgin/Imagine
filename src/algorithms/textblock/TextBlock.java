package algorithms.textblock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import logging.LogLevel;
import logging.Logger;

import config.Configuration;

import algorithms.Product;
import algorithms.ProductMode;

public class TextBlock implements Product{
	private static final String ALGORITHM_NAME = "TextBlock";
	private static final int ALGORITHM_VERSION_NUMBER = 1;
	private static final ProductMode[] SUPPORTED_PRODUCT_MODES = 
			new ProductMode[]{ProductMode.NORMAL};
	
	private int blockSize = 1024 * 100;
	
	//TODO: create temporary file to write buffer to so this doesn't stay here.
	private byte[] buffer;
	private int index;
	
	
	private static ProductMode productMode = ProductMode.NORMAL;

	public TextBlock(ProductMode mode) {
		if (Arrays.asList(SUPPORTED_PRODUCT_MODES).contains(mode))
			productMode = mode;
		else
			Logger.log(LogLevel.k_error, "TextBlock: Product mode " + mode + " not supported, running with default.");
		
		Integer configBlockSize = Configuration.getTextBlockBlockSize();
		
		if (configBlockSize != null && configBlockSize > 500)
			blockSize = configBlockSize;
		else
			Logger.log(LogLevel.k_error, "TextBlock: invalid block size, using default: " + blockSize);
		
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
		return ALGORITHM_NAME;
	}

	@Override
	public int getAlgorithmVersionNumber() {
		return ALGORITHM_VERSION_NUMBER;
	}

	@Override
	public void setUUID(byte[] uuid) {
		//not used currently
	}

	@Override
	public ProductMode getProductMode() {
		return productMode;
	}

	@Override
	public void setKeyHash(byte[] bytes) {
		//not used currently
	}

	@Override
	public void secureStream() {
		//only normal mode supported
	}

	@Override
	public void write(byte b) {
		buffer[index++] = b;
	}

	@Override
	public void write(byte[] bytes) {
		System.arraycopy(bytes, 0, buffer, index, bytes.length);
		index += bytes.length;
	}

	@Override
	public void saveFile(String filename) {
		try {
			File toSave = new File(Configuration.getProductStagingFolder().getAbsolutePath() + "\\" +
					filename + ".txt");
			Logger.log(LogLevel.k_info, "Saving product file: " + toSave.getAbsolutePath());
			Files.write(toSave.toPath(), buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte read() {
		return buffer[index++];
	}

	@Override
	public void read(byte[] bytes) {
		System.arraycopy(buffer, index, bytes, 0, bytes.length);
		index += bytes.length;
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
