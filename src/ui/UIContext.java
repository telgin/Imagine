package ui;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class UIContext
{
	public static UI uiContext;
	
	public static UI getUI()
	{
		return uiContext;
	}
	
	public static void setUI(UI gui)
	{
		uiContext = gui;
	}
}
