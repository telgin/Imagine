package testing;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class CodeTimer
{
	private long f_startMillis = 0;
	private long f_endMillis = 0;

	/**
	 * @update_comment
	 */
	public CodeTimer()
	{
	}

	/**
	 * @update_comment
	 */
	public void start()
	{
		f_startMillis = System.currentTimeMillis();
	}

	/**
	 * @update_comment
	 */
	public void end()
	{
		f_endMillis = System.currentTimeMillis();
	}

	/**
	 * @update_comment
	 * @return
	 */
	public long getElapsedTime()
	{
		return (f_endMillis - f_startMillis);
	}
}
