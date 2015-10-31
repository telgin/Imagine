package database.derby;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class F1UUID
{
	private int id;
	private byte[] uuid;
	
	public F1UUID(){}
	
	public F1UUID(byte[] uuid)
	{
		this.setUuid(uuid);
	}

	/**
	 * @return the uuid
	 */
	public byte[] getUuid()
	{
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(byte[] uuid)
	{
		this.uuid = uuid;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}
}
