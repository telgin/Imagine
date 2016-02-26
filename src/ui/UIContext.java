package ui;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Offers a pointer to the current ui (gui or cmd)
 */
public abstract class UIContext
{
	private static UI s_uiContext;
	
	/**
	 * Gets the current ui
	 * @return The current ui
	 */
	public static UI getUI()
	{
		return s_uiContext;
	}
	
	/**
	 * Sets the current ui
	 * @param p_ui The current ui
	 */
	public static void setUI(UI p_ui)
	{
		s_uiContext = p_ui;
	}
}
