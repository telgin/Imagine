package data;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductMode;
import product.ProductReader;
import product.ProductReaderFactory;
import product.ProductWriter;
import product.ProductWriterFactory;

public class TrackingGroup {
	private String name;
	private HashSet<File> trackedFiles;
	private HashSet<File> untrackedFiles;
	private boolean usingDatabase;
	private Algorithm algorithm;
	private Key key;
	private File productStagingFolder;
	private File extractionFolder;
	
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
		addTrackedPath(file);
	}
	
	public void addTrackedPath(File file)
	{
		if (!file.exists())
		{
			Logger.log(LogLevel.k_error, "Warning: The path in tracking group '" + name + "' does not exist:");
			Logger.log(LogLevel.k_error, file.getPath());
		}
		else
		{
			trackedFiles.add(file);
		}
	}
	
	public void addUntrackedPath(String path)
	{
		File file = new File(path);
		addUntrackedPath(file);
	}
	
	public void addUntrackedPath(File file)
	{
		if (!file.exists())
		{
			Logger.log(LogLevel.k_error, "Warning: The path in tracking group '" + name + "' does not exist:");
			Logger.log(LogLevel.k_error, file.getPath());
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

	public ProductReaderFactory<? extends ProductReader> getProductReaderFactory() {
		return AlgorithmRegistry.getProductReaderFactory(algorithm, key);
	}
	
	public ProductWriterFactory<? extends ProductWriter> getProductWriterFactory() {
		return AlgorithmRegistry.getProductWriterFactory(algorithm, key);
	}

	/**
	 * @return the extractionFolder
	 */
	public File getExtractionFolder() {
		return extractionFolder;
	}

	/**
	 * @param extractionFolder the extractionFolder to set
	 */
	public void setExtractionFolder(File extractionFolder) {
		this.extractionFolder = extractionFolder;
	}

	/**
	 * @return the productStagingFolder
	 */
	public File getProductStagingFolder() {
		return productStagingFolder;
	}

	/**
	 * @param productStagingFolder the productStagingFolder to set
	 */
	public void setProductStagingFolder(File productStagingFolder) {
		this.productStagingFolder = productStagingFolder;
	}

	public void clearTrackedPaths() {
		trackedFiles = new HashSet<File>();
	}
	
	public void clearUntrackedPaths() {
		untrackedFiles = new HashSet<File>();
	}
}
