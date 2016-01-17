package product;

import java.io.File;

import config.Constants;
import data.Metadata;

public class FileContents
{
	private long fragmentNumber;
	private long remainingData;
	private Metadata metadata;

	public String toString()
	{
		String text = "FileContents:";
		text += "\nfragmentNumber " + fragmentNumber;
		text += "\nremainingData " + remainingData;
		text += "\nfilename " + metadata.toString();
		return text;
	}

	/**
	 * @return the fragmentNumber
	 */
	public long getFragmentNumber()
	{
		return fragmentNumber;
	}

	/**
	 * @param fragmentNumber
	 *            the fragmentNumber to set
	 */
	public void setFragmentNumber(long fragmentNumber)
	{
		this.fragmentNumber = fragmentNumber;
	}

	/**
	 * @return the remainingData
	 */
	public long getRemainingData()
	{
		return remainingData;
	}

	/**
	 * @param remainingData
	 *            the remainingData to set
	 */
	public void setRemainingData(long remainingData)
	{
		this.remainingData = remainingData;
	}

	/**
	 * @return the metadata
	 */
	public Metadata getMetadata()
	{
		return metadata;
	}

	/**
	 * @param metadata
	 *            the metadata to set
	 */
	public void setMetadata(Metadata metadata)
	{
		this.metadata = metadata;
	}

}
