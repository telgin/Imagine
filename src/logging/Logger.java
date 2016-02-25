package logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import config.Configuration;
import config.Constants;
import system.SystemManager;
import ui.UIContext;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
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
		
		s_logFile = new File(Configuration.getLogFolder(), System.currentTimeMillis() + ".log");
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
	 * @update_comment
	 * @param p_logLevel
	 */
	public static void setMessageLogLevel(LogLevel p_logLevel)
	{
		s_messageLevel = p_logLevel;
	}

	/**
	 * @update_comment
	 * @param p_logLevel
	 */
	public static void setExceptionLogLevel(LogLevel p_logLevel)
	{
		s_exceptionLevel = p_logLevel;
	}

	/**
	 * @update_comment
	 * @param p_level
	 * @param p_message
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
	 * @update_comment
	 * @param p_level
	 * @param p_exception
	 * @param p_shutdown
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
	 * @update_comment
	 */
	public static void shutdown()
	{
		saveFile();
	}

	/**
	 * @update_comment
	 */
	private static void saveFile()
	{
		s_logFileStream.flush();
		s_logFileStream.close();
	}
}
