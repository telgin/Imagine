package ui;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class UIContext
{
	private static UI s_uiContext;
	
	/**
	 * @update_comment
	 * @return
	 */
	public static UI getUI()
	{
		return s_uiContext;
	}
	
	/**
	 * @update_comment
	 * @param p_gui
	 */
	public static void setUI(UI p_gui)
	{
		s_uiContext = p_gui;
	}
}
