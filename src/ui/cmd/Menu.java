package ui.cmd;

import java.util.ArrayList;
import java.util.List;

import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The menu class allows a user to pick from a formatted list of options.
 */
public class Menu
{
	private String f_title;
	protected List<String> f_options;
	private int f_currentChoice;
	private String f_subtext;
	private boolean f_canceled;
	
	/**
	 * Constructs a menu object
	 * @param p_title The title of the menu
	 */
	public Menu(String p_title)
	{
		f_title = p_title;
		f_options = new ArrayList<String>();
		f_canceled = false;
		f_currentChoice = 0;
	}
	
	/**
	 * Gets the index of the option which was chosen by the user
	 * @return The index of the option chosen
	 */
	public int getChosenIndex()
	{
		return f_currentChoice;
	}
	
	/**
	 * Adds an option to this menu
	 * @param p_option The text of the option
	 */
	public void addOption(String p_option)
	{
		f_options.add(p_option);
	}
	
	/**
	 * Sets the subtext for this menu
	 * @param p_text The subtext
	 */
	public void setSubtext(String p_text)
	{
		f_subtext = p_text;
	}
	
	/**
	 * Displays the menu and allows the user to input a choice
	 */
	protected void display()
	{
		System.out.println(f_title + ":\n");
		
		for (int i=0; i<f_options.size(); ++i)
		{
			System.out.println("\t" + (i+1) + "\t" + f_options.get(i));
		}
		
		if (f_subtext != null)
			System.out.println("\n" + f_subtext);
		
		inputChoice();
	}	
	
	/**
	 * Allows the user to pick an option from the menu
	 */
	public void inputChoice()
	{
		int choice = 0;
		while (choice == 0)
		{
			if (f_canceled)
				break;
			
			try
			{
				choice = Integer.parseInt(CMDInput.getLine());
				if (choice-1 >= 0 && choice-1 < f_options.size())
				{
					f_currentChoice = choice-1;
					optionChosen(choice-1);
					break;
				}
				else
				{
					incorrectInput("Enter one of the numbers in the menu.");
					choice = 0;
				}
			}
			catch (Exception e)
			{
				Logger.log(LogLevel.k_error, e, false);
				incorrectInput("Enter one of the numbers in the menu.");
				choice = 0;
			}
		}
		
		f_canceled = false;
	}
	
	/**
	 * Cancel this menu
	 */
	protected void cancel()
	{
		f_canceled = true;
	}
	
	/**
	 * Called when an option is chosen by the user
	 * @param p_choice The index of the user's choice
	 */
	protected void optionChosen(int p_choice){}
	
	/**
	 * Notifies the user of incorrect input
	 * @param p_what What the specific problem or input was
	 */
	protected void incorrectInput(String p_what)
	{
		System.out.println("Incorrect Input: " + p_what);
	}
}
