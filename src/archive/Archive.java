package archive;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public interface Archive
{
	/**
	 * @update_comment
	 * @return
	 */
	public String getAlgorithmName();

	/**
	 * @update_comment
	 * @return
	 */
	public int getAlgorithmVersionNumber();

	/**
	 * @update_comment
	 * @param p_uuid
	 */
	public void setUUID(byte[] p_uuid);

	/**
	 * @update_comment
	 * @return
	 */
	public byte[] getUUID();

	/**
	 * @update_comment
	 */
	public void secureStream();
}