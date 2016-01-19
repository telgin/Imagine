package product;

import java.util.HashMap;

public enum ProductMode
{
	k_basic("Basic"),
	k_trackable("Trackable"),
	k_secure("Secure");

	private String name;
	private static HashMap<String, ProductMode> modes;

	static
	{
		modes = new HashMap<String, ProductMode>();
		modes.put("basic", ProductMode.k_basic);
		modes.put("trackable", ProductMode.k_trackable);
		modes.put("secure", ProductMode.k_secure);
	}

	ProductMode(String name)
	{
		this.name = name;
	}

	public String toString()
	{
		return name;
	}

	public static ProductMode getMode(String name)
	{
		return modes.get(name.toLowerCase());
	}

	/**
	 * @update_comment
	 * @return
	 */
	public boolean isSecured()
	{
		return this.equals(k_secure) || this.equals(k_trackable);
	}
}
