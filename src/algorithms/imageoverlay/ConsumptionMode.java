package algorithms.imageoverlay;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * How to consume input files
 */
public enum ConsumptionMode
{
	k_move,
	k_cycle,
	k_delete;
	
	private static Map<String, ConsumptionMode> s_map;
	
	static
	{
		s_map = new HashMap<String, ConsumptionMode>();
		s_map.put("move", k_move);
		s_map.put("cycle", k_cycle);
		s_map.put("delete", k_delete);
	}
	
	/**
	 * Gets the enum associated with the display string
	 * @param p_str The display string associated with the enum
	 * @return The enum
	 */
	public static ConsumptionMode parseMode(String p_str)
	{
		return s_map.get(p_str.toLowerCase());
	}
}
