package runner;

import java.util.LinkedList;
import java.util.List;

import logging.Logger;

public class SystemManager {
	private static List<ActiveComponent> components;
	
	static
	{
		components = new LinkedList<ActiveComponent>();
	}
	
	public static void registerActiveComponent(ActiveComponent component)
	{
		components.add(component);
	}
	
	public static void shutdown()
	{
		for (ActiveComponent component:components)
		{
			component.shutdown();
		}
		
		//shutdown the log last
		Logger.shutdown();
	}
	
	public static boolean isShutdown()
	{
		for (ActiveComponent component:components)
		{
			if (!component.isShutdown())
				return false;
		}
		return true;
	}
}
