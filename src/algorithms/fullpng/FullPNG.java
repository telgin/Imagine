package algorithms.fullpng;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductMode;
import stats.ProgressMonitor;
import util.ByteConversion;
import util.Constants;
import config.Configuration;
import data.Key;
import algorithms.Algorithm;
import algorithms.Parameter;

public class FullPNG implements Product{
	
	private Algorithm algorithm;
	private BufferedImage img;
	private UniqueRandomRange randOrder;
	private int maxWriteSize;
	private HashRandom random;
	private Key key;
	private boolean skippedAll = false;
	private byte[] uuid;
	private int width;
	private int height;
	
	public FullPNG(Algorithm algo, Key key)
	{
		this.algorithm = algo;
		this.key = key;
		width = Integer.parseInt(algo.getParameterValue("width"));
		height = Integer.parseInt(algo.getParameterValue("height"));
		maxWriteSize = width * height * 3;
	}

	@Override
	public void newProduct() {
		//should really use the rgb configuration parameter somehow
		img = new BufferedImage(width, height, 
				BufferedImage.TYPE_INT_RGB);
		
		reset();
	}
	
	private void reset()
	{
		random = new HashRandom(1337l);//any constant seed
		randOrder = new UniqueRandomRange(random, maxWriteSize);
	}

	@Override
	public void write(byte b) {
		int index = randOrder.next();
		byte toSet = ByteConversion.intToByte(b ^ random.nextByte());
		setImageByte(index, toSet);
	}

	@Override
	public void write(byte[] bytes) {
		//Logger.log(LogLevel.k_debug, "Writing " + bytes.length + " bytes.");
		for (int x=0; x<bytes.length; ++x)
		{
			write(bytes[x]);
		}
	}

	@Override
	public long getRemainingBytes() {
		return skippedAll ? 0 : randOrder.remainingNumbers();
	}

	@Override
	public void saveFile(String fileName) {
		try {
			File imgFile = new File(Configuration.getProductStagingFolder().getAbsolutePath() + "/" + fileName + ".png");
			Logger.log(LogLevel.k_info, "Saving product file: " + imgFile.getAbsolutePath());
			if (!imgFile.getParentFile().exists())
				imgFile.getParentFile().mkdirs();
			ImageIO.write(img, "PNG", imgFile);
			
			//update progress
			ProgressMonitor.getStat("productsCreated").incrementNumericProgress(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setImageByte(int index, byte data)
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
	public byte read() {
		return ByteConversion.intToByte(getImageByte(randOrder.next()) ^ random.nextByte());
	}

	private byte getImageByte(int index) {
		int color = index % 3;
		int pixel = index / 3;
		int y = pixel / img.getWidth();
		int x = pixel % img.getWidth();
		
		if (color == 0)
		{
			return ImageUtil.getRed(img.getRGB(x, y));
		}
		else if (color == 1)
		{
			return ImageUtil.getGreen(img.getRGB(x, y));
		}
		else
		{
			return ImageUtil.getBlue(img.getRGB(x, y));
		}
	}

	@Override
	public void read(byte[] bytes) {
		Logger.log(LogLevel.k_debug, "Reading " + bytes.length + " bytes.");
		for (int x=0; x<bytes.length; ++x)
		{
			bytes[x] = read();
		}
	}

	@Override
	public void loadFile(File f) throws IOException {
		img = ImageIO.read(f);
		reset();
	}

	@Override
	public void skip(long bytes) {
		Logger.log(LogLevel.k_debug, "Skipping " + bytes + " bytes. (" + getRemainingBytes() + " are remaining before this.)");
		
		if (bytes == getRemainingBytes())
			skippedAll = true;
		else
		{
			for (long l=0; l<bytes; ++l)
			{
				randOrder.next();
				random.nextByte();
			}
		}
		
	}

	@Override
	public byte[] readUUID() {		
		random = new HashRandom(1337l);//any constant seed
		randOrder = new UniqueRandomRange(random, maxWriteSize);
		byte[] uuid = new byte[Constants.PRODUCT_UUID_SIZE];
		read(uuid);
		randOrder.reseed(ByteConversion.concat(key.getKeyHash(), uuid));
		
		int productSequenceNumber = ByteConversion.bytesToInt(uuid[8], uuid[9], uuid[10], uuid[11]);
		
		Logger.log(LogLevel.k_debug, "Product sequence number: " + productSequenceNumber);
		
		return uuid;
	}

	/* (non-Javadoc)
	 * @see algorithms.SecureProduct#secureStream()
	 */
	@Override
	public void secureStream() {
		//since this is a secure product, the uuid was already set and written
		randOrder.reseed(ByteConversion.concat(key.getKeyHash(), uuid));
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
	public byte[] getUUID() {
		return this.uuid;
	}

	@Override
	public ProductMode getProductMode() {
		return ProductMode.getMode(algorithm.getParameterValue("productMode"));
	}
}
