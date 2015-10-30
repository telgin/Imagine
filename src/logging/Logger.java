package logging;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import runner.SystemManager;

import util.myUtilities;

import config.Configuration;

public class Logger
{
	private static File logFile;
	private static List<String> lines;
	private static LogLevel messageLevel;
	private static LogLevel exceptionLevel;
	private static final LogLevel defaultMessageLevel = LogLevel.k_debug;
	private static final LogLevel defaultExceptionLevel = LogLevel.k_debug;

	static
	{
		messageLevel = defaultMessageLevel;
		exceptionLevel = defaultExceptionLevel;
		lines = new ArrayList<String>();

		String path = Configuration.getLogFolder().getPath() + "/"
						+ System.currentTimeMillis() + ".log";

		logFile = new File(path);
		logFile.getAbsoluteFile().getParentFile().mkdirs();
	}

	public static void setMessageLogLevel(LogLevel logLevel)
	{
		messageLevel = logLevel;
	}

	public static void setExceptionLogLevel(LogLevel logLevel)
	{
		exceptionLevel = logLevel;
	}

	public static void log(LogLevel level, String message)
	{
		if (level.toInt() <= messageLevel.toInt())
		{
			String line = LogLevel.getLogHeader(level) + message;
			System.out.println(line);
			lines.add(line);

			if (level.equals(LogLevel.k_fatal))
			{
				SystemManager.shutdown();
			}
		}
	}

	public static void log(LogLevel level, Exception e, boolean shutdown)
	{
		if (level.toInt() <= exceptionLevel.toInt() || shutdown)
		{
			String header = LogLevel.getLogHeader(level);
			System.out.print(header);
			e.printStackTrace();

			lines.add(header + e.getMessage());// TODO add the stack trace to
												// the log file

			if (level.equals(LogLevel.k_fatal))
			{
				SystemManager.shutdown();
			}
		}
	}

	public static void shutdown()
	{
		saveFile();
	}

	private static void saveFile()
	{
		myUtilities.writeListToFile(logFile, lines);
	}
}
