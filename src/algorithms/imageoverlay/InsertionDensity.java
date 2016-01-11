package algorithms.imageoverlay;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public enum InsertionDensity
{
	k_25,
	k_50;
	
	private static Map<String, InsertionDensity> map;
	
	static
	{
		map = new HashMap<String, InsertionDensity>();
		map.put("25%", k_25);
		map.put("50%", k_50);
	}
	
	public static InsertionDensity parseDensity(String str)
	{
		return map.get(str.toLowerCase());
	}
}
