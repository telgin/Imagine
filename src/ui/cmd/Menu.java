package ui.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class Menu
{
	private String title;
	protected List<String> options;
	private int currentChoice;
	private String subtext;
	private boolean canceled;
	
	public Menu(String title)
	{
		this.title = title;
		options = new ArrayList<String>();
		canceled = false;
		currentChoice = 0;
	}
	
	public int getChosenIndex()
	{
		return currentChoice;
	}
	
	public String getChosenOption()
	{
		return options.get(currentChoice);
	}
	
	public void addOption(String option)
	{
		options.add(option);
	}
	
	public void setSubtext(String text)
	{
		subtext = text;
	}
	
	public int getIndexOfOption(String option)
	{
		return options.indexOf(option);
	}
	
	protected void display()
	{
		System.out.println(title + ":\n");
		
		for (int i=0; i<options.size(); ++i)
		{
			System.out.println("\t" + (i+1) + "\t" + options.get(i));
		}
		
		if (subtext != null)
			System.out.println("\n" + subtext);
		
		int choice = 0;
		while (choice == 0)
		{
			if (canceled)
				break;
			
			try
			{
				choice = Integer.parseInt(CMDInput.getLine());
				if (choice-1 >= 0 && choice-1 < options.size())
				{
					currentChoice = choice-1;
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
		
		canceled = false;
	}
	
	protected void cancel()
	{
		canceled = true;
	}
	
	protected void optionChosen(int choice){}
	
	protected void incorrectInput(String what)
	{
		System.out.println("Incorrect Input: " + what);
	}
}
