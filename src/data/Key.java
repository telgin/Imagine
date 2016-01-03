package data;

public interface Key
{
	public boolean isSecure();

	public byte[] getKeyHash();

	public String getName();

	/**
	 * @update_comment
	 * @return
	 */
	public String getType();
}
