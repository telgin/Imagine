package util;

import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Wrapper for System.currentTimeMillis() so that every time given is unique.
 */
public class Clock
{
	/**
	 * Gets a unique ms time near the time of the function call.
	 * @return
	 */
	public static synchronized long getUniqueTime()
	{
		long time = System.currentTimeMillis();

		Logger.log(LogLevel.k_debug, "Unique Clock Time Given: " + time);

		// wait to make sure these are all unique
		try
		{
			Thread.sleep(5);
		}
		catch (InterruptedException e)
		{
			// nothing to do
		}

		return time;
	}
}
