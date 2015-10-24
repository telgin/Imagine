package product;

import java.io.File;
import java.io.IOException;

import algorithms.ProductIOException;

public interface Product {

	//generic methods
	public void newProduct();
	
	public long getRemainingBytes();
	
	public String getAlgorithmName();

	public int getAlgorithmVersionNumber();
	
	public void setUUID(byte[] uuid);
	
	public byte[] getUUID();
	
	public ProductMode getProductMode();
	
	
	//secure methods
	public void secureStream();
	
	
	//write methods
	public void write(byte b) throws ProductIOException;

	public void write(byte[] bytes) throws ProductIOException;

	public void saveFile(File productStagingFolder, String fileName);

	//should be able to take an input stream reader to make things more efficient. 
	
	

	//read methods
	public byte read() throws ProductIOException;

	public void read(byte[] bytes) throws ProductIOException;

	public void loadFile(File f) throws IOException;

	public void skip(long bytes) throws ProductIOException;

}