package ui.graphical;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import algorithms.Parameter;
import system.SystemManager;
import ui.ArgParseResult;
import ui.UI;
import ui.graphical.top.TopView;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The superclass for the graphical interface.
 */
public class GUI extends UI
{
	private List<String> f_errors;
	private TopView f_topView;
	private ArgParseResult f_args;
	
	/**
	 * Constructs a gui object
	 * @param p_args The parsed command line arguments
	 */
	public GUI(ArgParseResult p_args)
	{
		f_args = p_args;
		f_errors = new LinkedList<String>();
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptKeyFileLocation()
	 */
	@Override
	public File promptKeyFileLocation()
	{
		// the gui sends this, no need to prompt
		return null;
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptKey()
	 */
	@Override
	public String promptKey()
	{
		return f_topView.getPassword();
	}

	/* (non-Javadoc)
	 * @see ui.UI#processArgs()
	 */
	@Override
	public void init()
	{
		//reset static components
		SystemManager.reset();
		
		ApplicationWindow.launch(ApplicationWindow.class);
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptEnclosingFolder(java.io.File, java.io.File, java.lang.String)
	 */
	@Override
	public File promptEnclosingFolder(File p_curEnclosingFolder, File p_curArchiveFolder,
					String p_archiveSearchName)
	{
		//TODO make this better, handle case where folder selected
		//but file still wasn't found. (right now it just prompts again)
		return f_topView.getEnclosingFolder();
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptParameterValue(algorithms.Parameter)
	 */
	@Override
	public String promptParameterValue(Parameter p_parameter)
	{
		return f_topView.promptParameterValue(p_parameter);
	}

	/* (non-Javadoc)
	 * @see ui.UI#reportError(java.lang.String)
	 */
	@Override
	public void reportError(String p_message)
	{
		f_errors.add(p_message);
	}
	
	/**
	 * Gets the list of error messages
	 * @return The list of error messages
	 */
	public List<String> getErrors()
	{
		return f_errors;
	}
	
	/**
	 * Tells if there are outstanding errors
	 * @return If there are outstanding errors
	 */
	public boolean hasErrors()
	{
		return !f_errors.isEmpty();
	}
	
	/**
	 * Clears the list of error messages
	 */
	public void clearErrors()
	{
		f_errors.clear();
	}

	/* (non-Javadoc)
	 * @see ui.UI#reportMessage(java.lang.String)
	 */
	@Override
	public void reportMessage(String p_message)
	{
		//for the moment, there's nothing better to do
		//some kinds of notifications are handled elsewhere through stats
		System.out.println(p_message);
	}

	/**
	 * Gets the top view of the gui
	 * @return The top view
	 */
	public View getTopView()
	{
		return f_topView;
	}
	
	/**
	 * Sets the top view of the gui
	 * @param p_topView The top view to set
	 */
	public void setTopView(TopView p_topView)
	{
		f_topView = p_topView;
	}

	/**
	 * @return the result
	 */
	public ArgParseResult getArgParseResult()
	{
		return f_args;
	}

	/**
	 * @param p_result the result to set
	 */
	public void setArgParseResult(ArgParseResult p_result)
	{
		f_args = p_result;
	}

}
