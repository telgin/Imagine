package system;

import java.util.LinkedList;
import java.util.List;

import config.Settings;
import logging.LogLevel;
import logging.Logger;
import product.JobStatus;
import report.Report;

public class SystemManager
{
	private static List<ActiveComponent> components;
	private static boolean shutdownCalled = false;

	static
	{
		reset();
	}
	
	public static void reset()
	{
		//release pointers
		components = new LinkedList<ActiveComponent>();
		
		//reset static components
		Settings.reset();
		JobStatus.reset();
		Report.reset();
	}

	public static void registerActiveComponent(ActiveComponent component)
	{
		components.add(component);
	}

	public static void shutdown()
	{
		if (!shutdownCalled)
		{
			shutdownCalled = true;
			
			Logger.log(LogLevel.k_debug, "System Manager shutting down.");
			
			for (ActiveComponent component : components)
			{
				component.shutdown();
			}
	
			// shutdown the log last
			Logger.shutdown();
		}
	}

	public static boolean isShutdown()
	{
		if (components.isEmpty())
			return true;
		
		for (ActiveComponent component : components)
		{
			if (!component.isShutdown())
			{
				return false;
			}
		}
		
		return true;
	}
	
	private static class Exiter implements Runnable
	{
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			while (!SystemManager.isShutdown())
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e){}
			}
			
			System.exit(0);
		}
	}
}
