package data;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import algorithms.Algorithm;
import product.ProductMode;

public class TrackingGroup {
	private String name;
	private HashSet<File> fileSet;
	private ProductMode productSecurityLevel;
	private String keyName;
	private File keyLocation;
	private String algorithmName;
	private boolean usingDatabase;
	private boolean secure;
	private byte[] keyHash;
	private Algorithm algorithm;
	
	public TrackingGroup(String name)
	{
		this.setName(name);
		fileSet = new HashSet<File>();
	}
	
	public void addPath(String path)
	{
		File file = new File(path);
		if (!file.exists())
		{
			System.err.println("Warning: The path in tracking group '" + name + "' does not exist:");
			System.err.println(file.getPath());
		}
		else
		{
			fileSet.add(file);
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the fileSet
	 */
	public HashSet<File> getFileSet() {
		return fileSet;
	}

	/**
	 * @param fileSet the fileSet to set
	 */
	public void setFileSet(HashSet<File> fileSet) {
		this.fileSet = fileSet;
	}

	public String toString()
	{
		String text = "Tracking Group: " + name;
		for(File f:fileSet)
			try {
				text += "\n\t" + f.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return text;
	}

	/**
	 * @return the productSecurityLevel
	 */
	public ProductMode getProductSecurityLevel() {
		return productSecurityLevel;
	}

	/**
	 * @param productSecurityLevel the productSecurityLevel to set
	 */
	public void setProductSecurityLevel(ProductMode productSecurityLevel) {
		this.productSecurityLevel = productSecurityLevel;
	}

	/**
	 * @return the keyName
	 */
	public String getKeyName() {
		return keyName;
	}

	/**
	 * @param keyName the keyName to set
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	/**
	 * @return the keyLocation
	 */
	public File getKeyLocation() {
		return keyLocation;
	}

	/**
	 * @param keyLocation the keyLocation to set
	 */
	public void setKeyLocation(File keyLocation) {
		this.keyLocation = keyLocation;
	}

	/**
	 * @return the algorithmName
	 */
	public String getAlgorithmName() {
		return algorithmName;
	}

	/**
	 * @param algorithmName the algorithmName to set
	 */
	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	/**
	 * @return the usingDatabase
	 */
	public boolean isUsingDatabase() {
		return usingDatabase;
	}

	/**
	 * @param usingDatabase if the group uses a database
	 */
	public void setUsingDatabase(boolean usingDatabase) {
		this.usingDatabase = usingDatabase;
	}

	/**
	 * @return the secure
	 */
	public boolean isSecure() {
		return secure;
	}

	/**
	 * @param secure the secure to set
	 */
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	/**
	 * @return the keyHash
	 */
	public byte[] getKeyHash() {
		return keyHash;
	}

	/**
	 * @param keyHash the keyHash to set
	 */
	public void setKeyHash(byte[] keyHash) {
		this.keyHash = keyHash;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}
}
