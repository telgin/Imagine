package system;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public interface ActiveComponent
{
	/**
	 * @update_comment
	 */
	public void shutdown();

	/**
	 * @update_comment
	 * @return
	 */
	public boolean isShutdown();
}
