package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import config.Configuration;
import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductMode;
import product.ProductReader;
import product.ProductReaderFactory;
import product.ProductWriter;
import product.ProductWriterFactory;
import util.Constants;

public class TrackingGroup
{
	private String name;
	private HashSet<File> trackedFiles;
	private HashSet<File> untrackedFiles;
	private Algorithm algorithm;
	private Key key;
	private File staticOutputFolder;
	private File hashDBFile;
	
	//always turn this off for temporary tracking groups,
	//otherwise it's optional
	private boolean usingIndexFiles;
	
	//turn this off for temporary tracking groups,
	//otherwise leave it on
	private boolean usingAbsolutePaths;
	
	//turn this on if you expect that the number
	//of products output will be too large for your
	//file system to run smoothly. This will limit
	//the number of product files per folder, creating
	//a number of index files in the normal product
	//output folder
	private boolean usesStructuredProductOutput;

	public TrackingGroup(String name, boolean usesIndexFiles, Algorithm algo, Key key)
	{
		setName(name);
		setUsingIndexFiles(usesIndexFiles);
		this.algorithm = algo;
		this.key = key;
		trackedFiles = new HashSet<File>();
		untrackedFiles = new HashSet<File>();
		usingAbsolutePaths = true;
		hashDBFile = new File(Configuration.getDatabaseFolder(), name + "_hashdb");
		
		//if this is a temporary group, remove the old hashdb file
		if (name.equals(Constants.TEMP_RESERVED_GROUP_NAME))
		{
			try
			{
				Files.delete(hashDBFile.toPath());
			}
			catch (IOException e){}
		}
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
			Logger.log(LogLevel.k_error, "Warning: The path in tracking group '" + name
							+ "' does not exist:");
			Logger.log(LogLevel.k_error, file.getPath());
		}
		else
		{
			if (usingAbsolutePaths)
				trackedFiles.add(file.getAbsoluteFile());
			else
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
			Logger.log(LogLevel.k_error, "Warning: The path in tracking group '" + name
							+ "' does not exist:");
			Logger.log(LogLevel.k_error, file.getPath());
		}
		else
		{
			if (usingAbsolutePaths)
				untrackedFiles.add(file.getAbsoluteFile());
			else
				untrackedFiles.add(file);
		}
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the fileSet
	 */
	public HashSet<File> getTrackedFiles()
	{
		return trackedFiles;
	}

	/**
	 * @return the fileSet
	 */
	public HashSet<File> getUntrackedFiles()
	{
		return untrackedFiles;
	}

	public String toString()
	{
		String text = "Tracking Group: " + name;

		text += "Tracked Files: ";
		for (File f : trackedFiles)
		{
			try
			{
				text += "\n\t" + f.getCanonicalPath();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		text += "Untracked Files: ";
		for (File f : untrackedFiles)
		{
			try
			{
				text += "\n\t" + f.getCanonicalPath();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return text;
	}

	/**
	 * @return the algorithmName
	 */
	public Key getKey()
	{
		return key;
	}

	/**
	 * @return the usingDatabase
	 */
	public boolean isUsingIndexFiles()
	{
		return usingIndexFiles;
	}

	/**
	 * @param usingDatabase
	 *            if the group uses a database
	 */
	public void setUsingIndexFiles(boolean usingDatabase)
	{
		this.usingIndexFiles = usingDatabase;
	}

	public Algorithm getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm)
	{
		this.algorithm = algorithm;
	}

	public ProductReaderFactory<? extends ProductReader> getProductReaderFactory()
	{
		return AlgorithmRegistry.getProductReaderFactory(algorithm, key);
	}

	public ProductWriterFactory<? extends ProductWriter> getProductWriterFactory()
	{
		return AlgorithmRegistry.getProductWriterFactory(algorithm, key);
	}

	/**
	 * @return the productStagingFolder
	 */
	public File getStaticOutputFolder()
	{
		return staticOutputFolder;
	}

	/**
	 * @param productStagingFolder
	 *            the productStagingFolder to set
	 */
	public void setStaticOutputFolder(File productStagingFolder)
	{
		this.staticOutputFolder = productStagingFolder;
	}

	public void clearTrackedPaths()
	{
		trackedFiles = new HashSet<File>();
	}

	public void clearUntrackedPaths()
	{
		untrackedFiles = new HashSet<File>();
	}

	/**
	 * @update_comment
	 * @return
	 */
	public File getHashDBFile()
	{
		return hashDBFile;
	}

	/**
	 * @param hashDBFile the hashDBFile to set
	 */
	public void setHashDBFile(File hashDBFile)
	{
		this.hashDBFile = hashDBFile;
		if (!hashDBFile.exists())
		{
			try
			{
				hashDBFile.createNewFile();
			}
			catch (IOException e){}
		}
	}

	/**
	 * @return the usesAbsolutePaths
	 */
	public boolean usesAbsolutePaths()
	{
		return usingAbsolutePaths;
	}

	/**
	 * @param usesAbsolutePaths the usesAbsolutePaths to set
	 */
	public void setUsesAbsolutePaths(boolean usesAbsolutePaths)
	{
		this.usingAbsolutePaths = usesAbsolutePaths;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public boolean usesStructuredProductOutput()
	{
		return usesStructuredProductOutput;
	}
	
	/**
	 * @param usesStructuredProductOutput the usesStructuredProductOutput to set
	 */
	public void setUsesStructuredProductOutput(boolean usesStructuredProductOutput)
	{
		this.usesStructuredProductOutput = usesStructuredProductOutput;
	}
}
