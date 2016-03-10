package logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import config.Constants;
import system.SystemManager;
import ui.UIContext;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A standard logging class which has different levels of output for exceptions and 
 * general messages. Logs are written to a file each run.
 */
public class Logger
{
	private static File s_logFile;
	private static PrintWriter s_logFileStream;
	private static LogLevel s_messageLevel;
	private static LogLevel s_exceptionLevel;

	static
	{
		s_messageLevel = Constants.DEFAULT_MESSAGE_LEVEL;
		s_exceptionLevel = Constants.DEFAULT_EXCEPTION_LEVEL;
		
		s_logFile = new File(Constants.LOG_FOLDER, System.currentTimeMillis() + ".log");
		if (!s_logFile.getParentFile().exists())
			s_logFile.getParentFile().mkdirs();
		s_logFile.getAbsoluteFile().getParentFile().mkdirs();

		try
		{
			s_logFileStream = new PrintWriter(s_logFile);
		}
		catch (FileNotFoundException e)
		{
			Logger.log(LogLevel.k_fatal, "Logger error :( Can't open log file stream.");
		}

	}

	/**
	 * Sets the message level which is the maximum level to log
	 * @param p_logLevel The log level
	 */
	public static void setMessageLogLevel(LogLevel p_logLevel)
	{
		s_messageLevel = p_logLevel;
	}

	/**
	 * Sets the exception level which is the maximum level to log
	 * @param p_logLevel The log level
	 */
	public static void setExceptionLogLevel(LogLevel p_logLevel)
	{
		s_exceptionLevel = p_logLevel;
	}

	/**
	 * Logs a message with the specified log level
	 * @param p_level The level of this message
	 * @param p_message The message text
	 */
	public static void log(LogLevel p_level, String p_message)
	{
		//only log if the level is low enough to pass the threshold
		if (p_level.toInt() <= s_messageLevel.toInt())
		{
			String line = LogLevel.getLogHeader(p_level) + p_message;
			
			//call different functions based on level being more than k_error
			if (p_level.toInt() > LogLevel.k_error.toInt())
			{
				UIContext.getUI().reportMessage(line);
			}
			else //message is an error or a fatal error
			{
				UIContext.getUI().reportError(line);
			}
			
			//write everything to the log file also
			s_logFileStream.write(line + System.lineSeparator());
			s_logFileStream.flush();

			if (p_level.equals(LogLevel.k_fatal))
			{
				SystemManager.shutdown();
			}
		}
	}

	/**
	 * Logs an exception with the specified level
	 * @param p_level The level of the exception
	 * @param p_exception The exception
	 * @param p_shutdown If this should cause the system to shut down
	 */
	public static void log(LogLevel p_level, Exception p_exception, boolean p_shutdown)
	{
		if (p_level.toInt() <= s_exceptionLevel.toInt() || p_shutdown)
		{
			String header = LogLevel.getLogHeader(p_level);
			System.out.print(header);
			p_exception.printStackTrace();

			s_logFileStream.write(header + p_exception.getMessage());
			p_exception.printStackTrace(s_logFileStream);
			s_logFileStream.flush();
			

			if (p_level.equals(LogLevel.k_fatal))
			{
				SystemManager.shutdown();
			}
		}
	}

	/**
	 * Shuts the logger down, which saves the log file
	 */
	public static void shutdown()
	{
		saveFile();
	}

	/**
	 * Saves the log file
	 */
	private static void saveFile()
	{
		s_logFileStream.flush();
		s_logFileStream.close();
	}
}
