package algorithms.fullpng;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import logging.LogLevel;
import logging.Logger;

import util.ByteConversion;
import util.Constants;
import config.Configuration;
import algorithms.Algorithm;
import algorithms.Parameter;
import algorithms.Product;
import algorithms.ProductMode;

public class FullPNG implements Product{
	
	private static final String ALGORITHM_NAME = "FullPNG";
	private static final int ALGORITHM_VERSION_NUMBER = 1;
	private static final ProductMode[] SUPPORTED_PRODUCT_MODES = 
			new ProductMode[]{ProductMode.SECURE};
	private BufferedImage img;
	private UniqueRandomRange randOrder;
	private int maxWriteSize;
	private HeartRandom random;
	private byte[] keyHash;
	private boolean skippedAll = false;
	private byte[] uuid;
	private static ProductMode productMode = ProductMode.SECURE;
	
	public FullPNG(Algorithm algo, byte[] keyHash)
	{
		if (Arrays.asList(SUPPORTED_PRODUCT_MODES).contains(mode))
			productMode = mode;
		else
			System.err.println("FullPNG: Product mode not supported, running with default.");
		
		
		this.keyHash = keyHash;
		maxWriteSize = Configuration.getFullPNGMaxWidth() * Configuration.getFullPNGMaxHeight() * 3;
	}

	@Override
	public void newProduct() {
		//should really use the rgb configuration parameter somehow
		img = new BufferedImage(Configuration.getFullPNGMaxWidth(), 
				Configuration.getFullPNGMaxHeight(), 
				BufferedImage.TYPE_INT_RGB);
		
		reset();
	}
	
	private void reset()
	{
		random = new HeartRandom(1337l);//any constant seed
		randOrder = new UniqueRandomRange(random, maxWriteSize);
	}

	@Override
	public void write(byte b) {
		//15.7.4 Argument Lists are Evaluated Left-to-Right
		//"It is recommended that code not rely crucially on this specification."
		//... :(
		setImageByte(randOrder.next(), ByteConversion.intToByte(b ^ random.nextByte()));
	}

	@Override
	public void write(byte[] bytes) {
		System.out.println("Writing " + bytes.length + " bytes.");
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
			File imgFile = new File(Configuration.getProductStagingFolder().getAbsolutePath() + "\\" + fileName + ".png");
			Logger.log(LogLevel.k_info, "Saving product file: " + imgFile.getAbsolutePath());
			if (!imgFile.getParentFile().exists())
				imgFile.getParentFile().mkdirs();
			ImageIO.write(img, "PNG", imgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setKeyHash(byte[] bytes) {
		keyHash = bytes;
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
		System.out.println("Reading " + bytes.length + " bytes.");
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
		System.out.println("Skipping " + bytes + " bytes. (" + getRemainingBytes() + " are remaining before this.)");
		
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
		random = new HeartRandom(1337l);//any constant seed
		randOrder = new UniqueRandomRange(random, maxWriteSize);
		byte[] uuid = new byte[Constants.PRODUCT_UUID_SIZE];
		read(uuid);
		randOrder.reseed(ByteConversion.concat(keyHash, uuid));
		
		int productSequenceNumber = ByteConversion.bytesToInt(uuid[8], uuid[9], uuid[10], uuid[11]);
		
		System.out.println("Product sequence number: " + productSequenceNumber);
		
		return uuid;
	}

	/* (non-Javadoc)
	 * @see algorithms.SecureProduct#secureStream()
	 */
	@Override
	public void secureStream() {
		//since this is a secure product, the uuid was already set and written
		randOrder.reseed(ByteConversion.concat(keyHash, uuid));
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
		this.uuid = uuid;
	}

	@Override
	public byte[] getUUID() {
		return this.uuid;
	}

	@Override
	public ProductMode getProductMode() {
		return productMode;
	}

	@Override
	public List<Parameter> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParameter(String name, String value) {
		// TODO Auto-generated method stub
		
	}
}
