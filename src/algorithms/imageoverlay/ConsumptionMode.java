package algorithms.imageoverlay;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
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
	 * @update_comment
	 * @param p_str
	 * @return
	 */
	public static ConsumptionMode parseMode(String p_str)
	{
		return s_map.get(p_str.toLowerCase());
	}
}
