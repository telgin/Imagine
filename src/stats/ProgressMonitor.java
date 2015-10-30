package stats;

import java.util.HashMap;

public abstract class ProgressMonitor
{

	private static HashMap<String, Stat> stats = new HashMap<String, Stat>();

	public static void addStat(Stat stat)
	{
		stats.put(stat.getName(), stat);
	}

	public static Stat getStat(String name)
	{
		return stats.get(name);
	}
}
