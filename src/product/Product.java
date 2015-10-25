package product;

import java.io.File;
import java.io.IOException;

import algorithms.ProductIOException;

public interface Product {

	//generic methods
	public void newProduct();
	
	//public long getRemainingBytes();
	
	public String getAlgorithmName();

	public int getAlgorithmVersionNumber();
	
	public void setUUID(byte[] uuid);
	
	public byte[] getUUID();
	
	public ProductMode getProductMode();
	
	
	//secure methods
	public void secureStream();
	
	
	//write methods
	/**
	 * @param b
	 * @return True if written
	 */
	public boolean write(byte b);

	/**
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return The length of bytes written
	 */
	public int write(byte[] bytes, int offset, int length);

	public void saveFile(File productStagingFolder, String fileName);

	//should be able to take an input stream reader to make things more efficient. 
	
	

	//read methods
	public int read(byte[] bytes, int offset, int length);

	public void loadFile(File f) throws IOException;

	public long skip(long bytes);

}