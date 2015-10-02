package product;

import java.util.HashMap;

public enum ProductMode {
	NORMAL("Normal"),
	SECURE("Secure"),
	STEALTH("Stealth");
	
	private String name;
	private static HashMap<String, ProductMode> modes;
	
	static
	{
		modes = new HashMap<String, ProductMode>();
		modes.put("normal", ProductMode.NORMAL);
		modes.put("secure", ProductMode.SECURE);
		modes.put("stealth", ProductMode.STEALTH);
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
}
