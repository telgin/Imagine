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
 * Handles resetting components in the system between runs and shutting down
 * the system.
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
	 * Resets the static components and clears the list of active components.
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
	 * Adds an active component to the list of components which will
	 * be told to shut down when the system shuts down.
	 * @param p_component The active component to register
	 */
	public static void registerActiveComponent(ActiveComponent p_component)
	{
		s_components.add(p_component);
	}

	/**
	 * Shuts down all registered active components
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
	 * Tells if all active components are shut down
	 * @return If all active components are shut down
	 */
	public static boolean isShutdown()
	{
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
