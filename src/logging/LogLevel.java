package logging;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public enum LogLevel
{
	k_fatal(0),
	k_error(1),
	k_warning(2),
	k_general(3),
	k_info(4),
	k_debug(5),
	k_all(6);

	private int f_num;
	private static Map<LogLevel, String> s_logHeaders;

	static
	{
		s_logHeaders = new HashMap<LogLevel, String>();
		s_logHeaders.put(LogLevel.k_fatal, "[FATAL ERROR] : ");
		s_logHeaders.put(LogLevel.k_error, "[ERROR] : ");
		s_logHeaders.put(LogLevel.k_warning, "[WARNING] : ");
		s_logHeaders.put(LogLevel.k_general, "");
		s_logHeaders.put(LogLevel.k_info, "[INFO] : ");
		s_logHeaders.put(LogLevel.k_debug, "[DEBUG] : ");
		
		//all is used as a threshold, not as something you would output as
		s_logHeaders.put(LogLevel.k_all, "[(Use Debug Level, not All)] : ");
	}

	/**
	 * @update_comment
	 * @param p_num
	 */
	LogLevel(int p_num)
	{
		f_num = p_num;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public int toInt()
	{
		return f_num;
	}

	/**
	 * @update_comment
	 * @param p_level
	 * @return
	 */
	public static String getLogHeader(LogLevel p_level)
	{
		return s_logHeaders.get(p_level);
	}
}
