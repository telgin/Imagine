package product;

import java.io.File;
import java.io.IOException;

public interface Product {

	//generic methods
	public void newProduct();
	
	public long getRemainingBytes();
	
	public String getAlgorithmName();

	public int getAlgorithmVersionNumber();
	
	public void setUUID(byte[] uuid);
	
	public ProductMode getProductMode();
	
	
	//secure methods
	public void setKeyHash(byte[] bytes);
	
	public void secureStream();
	
	
	//write methods
	public void write(byte b);

	public void write(byte[] bytes);

	public void saveFile(String fileName);

	//should be able to take an input stream reader to make things more efficient. 
	
	

	//read methods
	public byte read();

	public void read(byte[] bytes);

	public void loadFile(File f) throws IOException;

	public void skip(long bytes);

	public byte[] readUUID();

	public byte[] getUUID();



}