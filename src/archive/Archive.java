package archive;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Details the interface for handling the reading/writing of archives
 */
public interface Archive
{
	/**
	 * Gets the name of the algorithm being used
	 * @return The algorithm name
	 */
	public String getAlgorithmName();

	/**
	 * Gets the version number of the algorithm being used
	 * @return The algorithm version number
	 */
	public int getAlgorithmVersionNumber();

	/**
	 * Sets the uuid of the archive
	 * @param p_uuid The archive uuid
	 */
	public void setUUID(byte[] p_uuid);

	/**
	 * Gets the uuid of the archive
	 * @return The archive uuid
	 */
	public byte[] getUUID();

	/**
	 * Reseeds the random number generator with the key hash
	 * and the uuid. This should be done right after the uuid
	 * is written or read.
	 */
	public void secureStream();
}