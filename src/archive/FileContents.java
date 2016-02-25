package archive;

import data.Metadata;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileContents
{
	private long f_fragmentNumber;
	private boolean f_isFragment = false;
	private long f_remainingData;
	private Metadata f_metadata;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String text = "FileContents:";
		text += "\nfragmentNumber " + f_fragmentNumber;
		text += "\nremainingData " + f_remainingData;
		text += "\nfilename " + f_metadata.toString();
		return text;
	}

	/**
	 * @return the fragmentNumber
	 */
	public long getFragmentNumber()
	{
		return f_fragmentNumber;
	}

	/**
	 * @param p_fragmentNumber the fragmentNumber to set
	 */
	public void setFragmentNumber(long p_fragmentNumber)
	{
		f_fragmentNumber = p_fragmentNumber;
		
		//a fragment number of 1 could be a fragment if it's the first fragment
		//this information may not be known if the data isn't parsed
		if (!f_isFragment)
		{
			setFragment(f_fragmentNumber > 1);
		}
	}

	/**
	 * @return the remainingData
	 */
	public long getRemainingData()
	{
		return f_remainingData;
	}

	/**
	 * @param p_remainingData the remainingData to set
	 */
	public void setRemainingData(long p_remainingData)
	{
		f_remainingData = p_remainingData;
	}

	/**
	 * @return the metadata
	 */
	public Metadata getMetadata()
	{
		return f_metadata;
	}

	/**
	 * @param p_metadata the metadata to set
	 */
	public void setMetadata(Metadata p_metadata)
	{
		f_metadata = p_metadata;
	}

	/**
	 * @return if the file is a fragment
	 */
	public boolean isFragment()
	{
		return f_isFragment;
	}

	/**
	 * @param p_isFragment if the file is a fragment
	 */
	public void setFragment(boolean p_isFragment)
	{
		f_isFragment = p_isFragment;
	}

}
