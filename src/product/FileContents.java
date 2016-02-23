package product;

import data.Metadata;

public class FileContents
{
	private long fragmentNumber;
	private boolean isFragment = false;
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
		
		//a fragment number of 1 could be a fragment if it's the first fragment
		//this information may not be known if the data isn't parsed
		if (!isFragment)
		{
			setFragment(this.fragmentNumber > 1);
		}
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

	/**
	 * @return the isFragment
	 */
	public boolean isFragment()
	{
		return isFragment;
	}

	/**
	 * @param isFragment the isFragment to set
	 */
	public void setFragment(boolean isFragment)
	{
		this.isFragment = isFragment;
	}

}
