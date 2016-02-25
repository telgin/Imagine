package ui.cmd;

import java.util.ArrayList;
import java.util.List;

import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class Menu
{
	private String f_title;
	protected List<String> f_options;
	private int f_currentChoice;
	private String f_subtext;
	private boolean f_canceled;
	
	/**
	 * @update_comment
	 * @param p_title
	 */
	public Menu(String p_title)
	{
		f_title = p_title;
		f_options = new ArrayList<String>();
		f_canceled = false;
		f_currentChoice = 0;
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public int getChosenIndex()
	{
		return f_currentChoice;
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public String getChosenOption()
	{
		return f_options.get(f_currentChoice);
	}
	
	/**
	 * @update_comment
	 * @param p_option
	 */
	public void addOption(String p_option)
	{
		f_options.add(p_option);
	}
	
	/**
	 * @update_comment
	 * @param p_text
	 */
	public void setSubtext(String p_text)
	{
		f_subtext = p_text;
	}
	
	/**
	 * @update_comment
	 * @param p_option
	 * @return
	 */
	public int getIndexOfOption(String p_option)
	{
		return f_options.indexOf(p_option);
	}
	
	/**
	 * @update_comment
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
	 * @update_comment
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
	 * @update_comment
	 */
	protected void cancel()
	{
		f_canceled = true;
	}
	
	/**
	 * @update_comment
	 * @param p_choice
	 */
	protected void optionChosen(int p_choice){}
	
	/**
	 * @update_comment
	 * @param p_what
	 */
	protected void incorrectInput(String p_what)
	{
		System.out.println("Incorrect Input: " + p_what);
	}
}
