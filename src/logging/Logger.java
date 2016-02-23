package logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import config.Configuration;
import system.SystemManager;
import ui.UIContext;

public class Logger
{
	private static File logFile;
	private static PrintWriter logFileStream;
	private static LogLevel messageLevel;
	private static LogLevel exceptionLevel;
	private static final LogLevel defaultMessageLevel = LogLevel.k_debug;
	private static final LogLevel defaultExceptionLevel = LogLevel.k_debug;

	static
	{
		messageLevel = defaultMessageLevel;
		exceptionLevel = defaultExceptionLevel;
		
		logFile = new File(Configuration.getLogFolder(), System.currentTimeMillis() + ".log");
		logFile.getAbsoluteFile().getParentFile().mkdirs();

		try
		{
			logFileStream = new PrintWriter(logFile);
		}
		catch (FileNotFoundException e)
		{
			Logger.log(LogLevel.k_fatal, "Logger error :( Can't open log file stream.");
		}

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
		//only log if the level is low enough to pass the threshold
		if (level.toInt() <= messageLevel.toInt())
		{
			String line = LogLevel.getLogHeader(level) + message;
			
			//call different functions based on level being more than k_error
			if (level.toInt() > LogLevel.k_error.toInt())
			{
				UIContext.getUI().reportMessage(line);
			}
			else //message is an error or a fatal error
			{
				UIContext.getUI().reportError(line);
			}
			
			//write everything to the log file also
			logFileStream.write(line + System.lineSeparator());
			logFileStream.flush();

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

			logFileStream.write(header + e.getMessage());
			e.printStackTrace(logFileStream);
			logFileStream.flush();
			

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
		logFileStream.flush();
		logFileStream.close();
	}
}
