package logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import config.Configuration;
import system.SystemManager;

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

		String path = Configuration.getLogFolder().getPath() + "/"
						+ System.currentTimeMillis() + ".log";
		
		logFile = new File(path);
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
		if (level.toInt() <= messageLevel.toInt())
		{
			String line = LogLevel.getLogHeader(level) + message;
			System.out.println(line);

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
