package logging;

public enum LogLevel {
	k_fatal(0),
	k_error(1),
	k_general(2),
	k_info(3),
	k_debug(4),
	k_all(5);
	
	private int num;
	private static HashMap<LogLevel, String> logHeaders
	
	static
	{
		logHeaders = [(LogLevel.k_fatal):'[FATAL ERROR] : ', 
			(LogLevel.k_error):'[ERROR] : ',
			(LogLevel.k_general):'',
			(LogLevel.k_info):'[INFO] : ',
			(LogLevel.k_debug):'[DEBUG] : ',
			(LogLevel.k_all):'[(Use Debug Level, not All)] : ']
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
		return logHeaders[level];
	}
}
