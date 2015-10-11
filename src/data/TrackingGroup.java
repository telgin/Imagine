package data;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import product.Product;
import product.ProductFactory;
import product.ProductMode;

public class TrackingGroup {
	private String name;
	private HashSet<File> trackedFiles;
	private HashSet<File> untrackedFiles;
	private boolean usingDatabase;
	private Algorithm algorithm;
	private Key key;
	
	public TrackingGroup(String name, boolean usesDatabase, Algorithm algo, Key key)
	{
		setName(name);
		setUsingDatabase(usesDatabase);
		this.algorithm = algo;
		this.key = key;
		trackedFiles = new HashSet<File>();
		untrackedFiles = new HashSet<File>();
	}
	
	public void addTrackedPath(String path)
	{
		File file = new File(path);
		if (!file.exists())
		{
			System.err.println("Warning: The path in tracking group '" + name + "' does not exist:");
			System.err.println(file.getPath());
		}
		else
		{
			trackedFiles.add(file);
		}
	}
	
	public void addUntrackedPath(String path)
	{
		File file = new File(path);
		if (!file.exists())
		{
			System.err.println("Warning: The path in tracking group '" + name + "' does not exist:");
			System.err.println(file.getPath());
		}
		else
		{
			untrackedFiles.add(file);
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
	public HashSet<File> getTrackedFiles() {
		return trackedFiles;
	}
	
	/**
	 * @return the fileSet
	 */
	public HashSet<File> getUntrackedFiles() {
		return untrackedFiles;
	}



	public String toString()
	{
		String text = "Tracking Group: " + name;
		
		text += "Tracked Files: ";
		for(File f:trackedFiles)
		{
			try {
				text += "\n\t" + f.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		text += "Untracked Files: ";
		for(File f:untrackedFiles)
		{
			try {
				text += "\n\t" + f.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		
		return text;
	}

	/**
	 * @return the algorithmName
	 */
	public Key getKey() {
		return key;
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

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public ProductFactory<? extends Product> getProductFactory() {
		return AlgorithmRegistry.getProductFactory(algorithm, key);
	}
}
