package hibernate;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileHash
{
	private int groupid;
	private int f1uuid;
	private byte[] hash;
	
	public FileHash(){}
	
	/**
	 * @update_comment
	 * @param groupid
	 * @param f1uuid
	 * @param hash
	 */
	public FileHash(int groupid, int f1uuid, byte[] hash)
	{
		this.groupid = groupid;
		this.f1uuid = f1uuid;
		this.hash = hash;
	}
	
	/**
	 * @return the groupid
	 */
	public int getGroupid()
	{
		return groupid;
	}
	/**
	 * @param groupid the groupid to set
	 */
	public void setGroupid(int groupid)
	{
		this.groupid = groupid;
	}
	/**
	 * @return the f1uuid
	 */
	public int getF1uuid()
	{
		return f1uuid;
	}
	/**
	 * @param f1uuid the f1uuid to set
	 */
	public void setF1uuid(int f1uuid)
	{
		this.f1uuid = f1uuid;
	}
	/**
	 * @return the hash
	 */
	public byte[] getHash()
	{
		return hash;
	}
	/**
	 * @param hash the hash to set
	 */
	public void setHash(byte[] hash)
	{
		this.hash = hash;
	}
	
	
}
