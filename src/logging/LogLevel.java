package logging;

import java.util.HashMap;

public enum LogLevel
{
	k_fatal(0),
	k_error(1),
	k_warning(2),
	k_general(3),
	k_info(4),
	k_debug(5),
	k_all(6);

	private int num;
	private static HashMap<LogLevel, String> logHeaders;

	static
	{
		logHeaders = new HashMap<LogLevel, String>();
		logHeaders.put(LogLevel.k_fatal, "[FATAL ERROR] : ");
		logHeaders.put(LogLevel.k_error, "[ERROR] : ");
		logHeaders.put(LogLevel.k_warning, "[WARNING] : ");
		logHeaders.put(LogLevel.k_general, "");
		logHeaders.put(LogLevel.k_info, "[INFO] : ");
		logHeaders.put(LogLevel.k_debug, "[DEBUG] : ");
		logHeaders.put(LogLevel.k_all, "[(Use Debug Level, not All)] : ");
	}

	LogLevel(int num)
	{
		this.num = num;
	}

	public int toInt()
	{
		return num;
	}

	public static String getLogHeader(LogLevel level)
	{
		return logHeaders.get(level);
	}
}
