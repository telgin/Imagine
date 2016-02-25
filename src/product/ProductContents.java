package product;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ProductContents
{
	private int f_productVersionNumber;
	private long f_streamUUID;
	private int f_productSequenceNumber;
	private String f_algorithmName;
	private int f_algorithmVersionNumber;
	private String f_groupName;
	private String f_groupKeyName;
	
	private ArrayList<FileContents> f_files;

	/**
	 * @update_comment
	 */
	public ProductContents()
	{
		f_files = new ArrayList<FileContents>();
	}

	/**
	 * @update_comment
	 * @param p_contents
	 */
	public void addFileContents(FileContents p_contents)
	{
		f_files.add(p_contents);
	}

	/**
	 * @return the productVersionNumber
	 */
	public int getProductVersionNumber()
	{
		return f_productVersionNumber;
	}

	/**
	 * @param p_productVersionNumber the productVersionNumber to set
	 */
	public void setProductVersionNumber(int p_productVersionNumber)
	{
		this.f_productVersionNumber = p_productVersionNumber;
	}

	/**
	 * @return the productSequenceNumber
	 */
	public int getProductSequenceNumber()
	{
		return f_productSequenceNumber;
	}

	/**
	 * @param p_productSequenceNumber the productSequenceNumber to set
	 */
	public void setProductSequenceNumber(int p_productSequenceNumber)
	{
		this.f_productSequenceNumber = p_productSequenceNumber;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName()
	{
		return f_groupName;
	}

	/**
	 * @param p_groupName the groupName to set
	 */
	public void setGroupName(String p_groupName)
	{
		this.f_groupName = p_groupName;
	}

	/**
	 * @return the streamUUID
	 */
	public long getStreamUUID()
	{
		return f_streamUUID;
	}

	/**
	 * @param p_uuid the streamUUID to set
	 */
	public void setStreamUUID(long p_uuid)
	{
		this.f_streamUUID = p_uuid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String text = "Product Contents:";

		text += "\nProduct Version: " + f_productVersionNumber;
		text += "\nStreamUUID: " + f_streamUUID;
		text += "\nSequence Number: " + f_productSequenceNumber;
		text += "\nAlgorithm Version: " + f_algorithmVersionNumber;
		text += "\nAlgorithm Name: " + f_algorithmName;
		text += "\nGroup Name: " + f_groupName;
		text += "\nGroup Key Name: " + f_groupKeyName;
		for (FileContents fc : f_files)
			text += "\n" + fc.toString();

		return text;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public List<FileContents> getFileContents()
	{
		return f_files;
	}

	/**
	 * @return the algorithmName
	 */
	public String getAlgorithmName()
	{
		return f_algorithmName;
	}

	/**
	 * @param p_algorithmName the algorithmName to set
	 */
	public void setAlgorithmName(String p_algorithmName)
	{
		this.f_algorithmName = p_algorithmName;
	}

	/**
	 * @return the algorithmVersionNumber
	 */
	public int getAlgorithmVersionNumber()
	{
		return f_algorithmVersionNumber;
	}

	/**
	 * @param p_algorithmVersionNumber the algorithmVersionNumber to set
	 */
	public void setAlgorithmVersionNumber(int p_algorithmVersionNumber)
	{
		this.f_algorithmVersionNumber = p_algorithmVersionNumber;
	}

	/**
	 * @return the groupKeyName
	 */
	public String getGroupKeyName()
	{
		return f_groupKeyName;
	}

	/**
	 * @param p_groupKeyName the groupKeyName to set
	 */
	public void setGroupKeyName(String p_groupKeyName)
	{
		this.f_groupKeyName = p_groupKeyName;
	}
}
