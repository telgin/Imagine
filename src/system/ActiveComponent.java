package system;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This interface describes a way to shutdown the components of the system
 * and verify that they have shutdown.
 */
public interface ActiveComponent
{
	/**
	 * Gives the signal for the active component to shutdown
	 */
	public void shutdown();

	/**
	 * Tells if the active component is shutdown.
	 * @return The shutdown state
	 */
	public boolean isShutdown();
}
