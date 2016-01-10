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
	
	private static Map<String, ConsumptionMode> map;
	
	static
	{
		map = new HashMap<String, ConsumptionMode>();
		map.put("move", k_move);
		map.put("cycle", k_cycle);
		map.put("delete", k_delete);
	}
	
	public static ConsumptionMode parseMode(String str)
	{
		return map.get(str.toLowerCase());
	}
}
