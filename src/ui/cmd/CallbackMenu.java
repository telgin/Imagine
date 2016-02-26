package ui.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Creates a menu with callbacks for each item
 */
public class CallbackMenu extends Menu
{
	private Map<Integer, Consumer<Void>> f_callbacks;
	private Map<Integer, Menu> f_menus;
	
	/**
	 * Constructs a callback menu
	 * @param p_title The menu title
	 */
	public CallbackMenu(String p_title)
	{
		super(p_title);
		f_callbacks = new HashMap<Integer, Consumer<Void>>();
		f_menus = new HashMap<Integer, Menu>();
	}
	
	/**
	 * Adds a menu option
	 * @param p_option The option text
	 * @param p_callback The callback for when this option is chosen
	 */
	public void addOption(String p_option, Consumer<Void> p_callback)
	{
		f_options.add(p_option);
		f_callbacks.put(f_options.size()-1,p_callback);
	}
	
	/**
	 * Adds an option which links to another menu
	 * @param p_option The option text
	 * @param p_linkedMenu The menu to go to when this option is chosen
	 */
	public void addOption(String p_option, Menu p_linkedMenu)
	{
		f_options.add(p_option);
		f_menus.put(f_options.size()-1, p_linkedMenu);
	}
	
	/* (non-Javadoc)
	 * @see ui.cmd.Menu#optionChosen(int)
	 */
	@Override
	protected void optionChosen(int p_choice)
	{
		if (f_callbacks.containsKey(p_choice))
		{
			f_callbacks.get(p_choice).accept(null);
		}
		else if (f_menus.containsKey(p_choice))
		{
			f_menus.get(p_choice).display();
		}
		
		//else it is just the option text, which the
		//super method will handle
	}
}
