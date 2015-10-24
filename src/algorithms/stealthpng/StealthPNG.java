package algorithms.stealthpng;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.common.io.Files;

import algorithms.Algorithm;
import algorithms.ProductIOException;
import algorithms.stealthpng.patterns.Pattern;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductMode;
import stats.ProgressMonitor;
import stats.Stat;
import util.ByteConversion;
import util.Constants;
import util.algorithms.HashRandom;
import util.algorithms.ImageUtil;
import util.algorithms.UniqueRandomRange;

public class StealthPNG implements Product{
	private Algorithm algorithm;
	private BufferedImage img;
	private UniqueRandomRange randOrder;
	private int maxWriteSize;
	private HashRandom random;
	private Key key;
	private boolean skippedAll = false;
	private byte[] uuid;
	private File inputImages;
	private int pattern = 0;
	private int byteCount = 0;
	
	public StealthPNG(Algorithm algo, Key key)
	{
		this.algorithm = algo;
		this.key = key;
		inputImages = new File(algo.getParameterValue("imageFolder"));
		InputImageManager.setInputFolder(inputImages);
		pattern = Integer.parseInt(algo.getParameterValue("pattern"));
	}
	
	/**
	 * @credit http://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
	 * user 'Klark'
	 * @param toCopy
	 * @return
	 */
	private static BufferedImage clone(BufferedImage toCopy)
	{
		ColorModel cm = toCopy.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = toCopy.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	@Override
	public void newProduct() {
		loadFile();
		testSize();
	}
	
	private void loadFile()
	{
		File imgFile = InputImageManager.nextImageFile();
		try {
			img = ImageIO.read(imgFile);
		} catch (IOException e) {
			 //TODO how to handle no images left
			e.printStackTrace();
		}
	}
	
	private void reset()
	{
		//any constant seed
		random = new HashRandom(1337l);
		
		//obtain a random order
		randOrder = new UniqueRandomRange(random, img.getWidth()*img.getHeight()*3);
		
		byteCount = 0;
	}

	private int testSize() {
		
		//TODO ~75% speedup if you figure out the math for chi squared
		//would need to handle overwrite failure in that case though
		//might mean reloading your product and modifying the end code
		//for the last fragment?
		
		BufferedImage tmp = clone(img);
		reset();
		
		maxWriteSize = 0;
		
		while (true)
		{
			try
			{
				write(ByteConversion.intToByte(100));
				++maxWriteSize;
				if (maxWriteSize % 10000 == 0)
					System.out.println(maxWriteSize);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				break;
			}
		}
		
		System.out.println("Max Write Size: " + maxWriteSize);
		
		img = tmp;
		reset();
		
		return maxWriteSize;
	}

	@Override
	public long getRemainingBytes() {
		return maxWriteSize - byteCount;
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

	@Override
	public void write(byte b) throws ProductIOException {
		//if (byteCount > 534000)
		//{
		//	System.out.print("Byte Count: " + byteCount);
		//	System.out.println(", Remaining Bytes: " + getRemainingBytes());
		//}
		try{
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
		
		++byteCount;
		} catch(ProductIOException e)
		{
			e.printStackTrace();
			throw e;
		}
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
	
	private byte getColor(int index) {
		int color = index % 3;
		int pixel = index / 3;
		int y = pixel / img.getWidth();
		int x = pixel % img.getWidth();
		
		if (color == 0)
		{
			if (x < 0 || y < 0 || x >= img.getWidth() || y >= img.getHeight())
				System.out.println(x + ", " + y + ", " + index + ", " + img.getWidth() + ", " + img.getHeight());
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
	public void write(byte[] bytes) throws ProductIOException {
		for (int i=0; i<bytes.length; ++i)
			write(bytes[i]);
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

	@Override
	public byte read() throws ProductIOException {
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
	public void read(byte[] bytes) throws ProductIOException {
		//Logger.log(LogLevel.k_debug, "Reading " + bytes.length + " bytes.");
		for (int x=0; x<bytes.length; ++x)
		{
			bytes[x] = read();
		}
		////System.out.println();
	}

	@Override
	public void loadFile(File f) throws IOException {
		img = ImageIO.read(f);
		testSize();
	}

	@Override
	public void skip(long bytes) throws ProductIOException {
		Logger.log(LogLevel.k_debug, "Skipping " + bytes + " bytes. (" + getRemainingBytes() + " are remaining before this.)");
		
		if (bytes == getRemainingBytes())
			skippedAll = true;
		else
		{
			for (long l=0; l<bytes; ++l)
			{
				random.nextByte();
				int[] tempPv = new int[3];
				tempPv[0] = randOrder.next();
				while (!Pattern.validIndex(pattern, tempPv[0], img.getWidth(), img.getHeight()))
					tempPv[0] = randOrder.next();
			}
		}
		
		byteCount += bytes;
	}

	@Override
	public byte[] readUUID() throws ProductIOException {
		random = new HashRandom(1337l);//any constant seed
		randOrder = new UniqueRandomRange(random, maxWriteSize);
		byte[] uuid = new byte[Constants.PRODUCT_UUID_SIZE];
		read(uuid);
		randOrder.reseed(ByteConversion.concat(key.getKeyHash(), uuid));
		
		int productSequenceNumber = ByteConversion.bytesToInt(uuid[8], uuid[9], uuid[10], uuid[11]);
		
		Logger.log(LogLevel.k_debug, "Product sequence number: " + productSequenceNumber);
		
		return uuid;
	}

	@Override
	public byte[] getUUID() {
		return uuid;
	}

}
