package ui.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class CallbackMenu extends Menu
{
	private Map<Integer, Consumer<Void>> callbacks;
	private Map<Integer, Menu> menus;
	
	public CallbackMenu(String title)
	{
		super(title);
		callbacks = new HashMap<Integer, Consumer<Void>>();
		menus = new HashMap<Integer, Menu>();
	}
	
	public void addOption(String option, Consumer<Void> callback)
	{
		options.add(option);
		callbacks.put(options.size()-1,callback);
	}
	
	public void addOption(String option, Menu linkedMenu)
	{
		options.add(option);
		menus.put(options.size()-1, linkedMenu);
	}
	
	@Override
	protected void optionChosen(int choice)
	{
		if (callbacks.containsKey(choice))
		{
			callbacks.get(choice).accept(null);
		}
		else if (menus.containsKey(choice))
		{
			menus.get(choice).display();
		}
		
		//else it is just the option text, which the
		//super method will handle
	}
}
