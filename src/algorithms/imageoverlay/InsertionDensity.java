package algorithms.imageoverlay;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The "insertion density" to use in an image overlay. These are
 * percents of bits per byte to use for file data. So, at 25%, two bits
 * are file data and six are image data. At 50%, four bits are file data and
 * four bits are image data.
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
	 * Gets the associated enum from a percent string
	 * @param p_string The display string representation of the enum
	 * @return The enum
	 */
	public static InsertionDensity parseDensity(String p_string)
	{
		return s_map.get(p_string.toLowerCase());
	}
}
