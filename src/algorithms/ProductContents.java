package algorithms;

import java.util.ArrayList;

public class ProductContents {

	private int productVersionNumber;
	private long streamUUID;
	private int productSequenceNumber;
	private String algorithmName;
	private int algorithmVersionNumber;
	private String groupName;
	private String groupKeyName;
	
	private ArrayList<FileContents> files;
	
	public ProductContents()
	{
		files = new ArrayList<FileContents>();
	}
	
	public void addFileContents(FileContents contents)
	{
		files.add(contents);
	}
	
	/**
	 * @return the productVersionNumber
	 */
	public int getProductVersionNumber() {
		return productVersionNumber;
	}
	/**
	 * @param productVersionNumber the productVersionNumber to set
	 */
	public void setProductVersionNumber(int productVersionNumber) {
		this.productVersionNumber = productVersionNumber;
	}
	/**
	 * @return the productSequenceNumber
	 */
	public int getProductSequenceNumber() {
		return productSequenceNumber;
	}
	/**
	 * @param productSequenceNumber the productSequenceNumber to set
	 */
	public void setProductSequenceNumber(int productSequenceNumber) {
		this.productSequenceNumber = productSequenceNumber;
	}
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return the streamUUID
	 */
	public long getStreamUUID() {
		return streamUUID;
	}
	/**
	 * @param uuid the streamUUID to set
	 */
	public void setStreamUUID(long uuid) {
		this.streamUUID = uuid;
	}
	
	public String toString()
	{
		String text = "Product Contents:";
		
		text += "\nProduct Version: " + productVersionNumber;
		text += "\nStreamUUID: " + streamUUID;
		text += "\nSequence Number: " + productSequenceNumber;
		text += "\nGroup Name: " + groupName;
		for (FileContents fc:files)
			text += fc.toString();
		
		return text;
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
	 * @return the algorithmVersionNumber
	 */
	public int getAlgorithmVersionNumber() {
		return algorithmVersionNumber;
	}

	/**
	 * @param algorithmVersionNumber the algorithmVersionNumber to set
	 */
	public void setAlgorithmVersionNumber(int algorithmVersionNumber) {
		this.algorithmVersionNumber = algorithmVersionNumber;
	}

	/**
	 * @return the groupKeyName
	 */
	public String getGroupKeyName() {
		return groupKeyName;
	}

	/**
	 * @param groupKeyName the groupKeyName to set
	 */
	public void setGroupKeyName(String groupKeyName) {
		this.groupKeyName = groupKeyName;
	}
}
