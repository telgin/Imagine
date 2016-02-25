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
	
	private static Map<String, InsertionDensity> s_map;
	
	static
	{
		s_map = new HashMap<String, InsertionDensity>();
		s_map.put("25%", k_25);
		s_map.put("50%", k_50);
	}
	
	/**
	 * @update_comment
	 * @param p_string
	 * @return
	 */
	public static InsertionDensity parseDensity(String p_string)
	{
		return s_map.get(p_string.toLowerCase());
	}
}
