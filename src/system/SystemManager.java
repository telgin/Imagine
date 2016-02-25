package system;

import java.util.LinkedList;
import java.util.List;

import config.Settings;
import logging.LogLevel;
import logging.Logger;
import report.JobStatus;
import report.Report;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class SystemManager
{
	private static List<ActiveComponent> s_components;
	private static boolean s_shutdownCalled = false;

	static
	{
		reset();
	}
	
	/**
	 * @update_comment
	 */
	public static void reset()
	{
		//release pointers
		s_components = new LinkedList<ActiveComponent>();
		
		//reset static components
		Settings.reset();
		JobStatus.reset();
		Report.reset();
	}

	/**
	 * @update_comment
	 * @param p_component
	 */
	public static void registerActiveComponent(ActiveComponent p_component)
	{
		s_components.add(p_component);
	}

	/**
	 * @update_comment
	 */
	public static void shutdown()
	{
		if (!s_shutdownCalled)
		{
			s_shutdownCalled = true;
			
			Logger.log(LogLevel.k_debug, "System Manager shutting down.");
			
			for (ActiveComponent component : s_components)
			{
				component.shutdown();
			}
	
			// shutdown the log last
			Logger.shutdown();
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public static boolean isShutdown()
	{
		if (s_components.isEmpty())
			return true;
		
		for (ActiveComponent component : s_components)
		{
			if (!component.isShutdown())
			{
				return false;
			}
		}
		
		return true;
	}
}
