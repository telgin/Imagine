package key;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The key interface allows all keys to be treated the same because
 * they are in essence all hashes which are used as seeds in the algorithms.
 */
public interface Key
{
	/**
	 * Gets the key hash of this key
	 * @return The key hash
	 */
	public byte[] getKeyHash();
}
