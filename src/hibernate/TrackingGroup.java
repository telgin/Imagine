package hibernate;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TrackingGroup
{
	private int id;
	private String name;
	
	public TrackingGroup() {}
	
	/**
	 * @update_comment
	 * @param name
	 */
	public TrackingGroup(String name)
	{
		this.setName(name);
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
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
