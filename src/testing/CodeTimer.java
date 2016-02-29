package testing;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This is a simple class which times code execution. It is used in testing,
 * not in the actual program.
 */
public class CodeTimer
{
	private long f_startMillis = 0;
	private long f_endMillis = 0;

	/**
	 * Sets the start time to now.
	 */
	public void start()
	{
		f_startMillis = System.currentTimeMillis();
	}

	/**
	 * Sets the end time to now
	 */
	public void end()
	{
		f_endMillis = System.currentTimeMillis();
	}

	/**
	 * Gets the elapsed time in ms (end - start)
	 * @return The length between the start and end times
	 */
	public long getElapsedTime()
	{
		return (f_endMillis - f_startMillis);
	}
}
