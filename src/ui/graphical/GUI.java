package ui.graphical;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import algorithms.Parameter;
import ui.ArgParseResult;
import ui.UI;
import ui.graphical.top.TopView;

public class GUI extends UI
{
	private List<String> errors;
	private TopView topView;
	private ArgParseResult args;
	
	public GUI(ArgParseResult args)
	{
		this.args = args;
		errors = new LinkedList<String>();
	}

	@Override
	public File promptKeyFileLocation()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String promptKey()
	{
		return topView.getPassword();
	}

	/* (non-Javadoc)
	 * @see ui.UI#processArgs()
	 */
	@Override
	public void init()
	{
		ApplicationWindow.launch(ApplicationWindow.class);
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptEnclosingFolder(java.io.File, java.io.File, java.lang.String)
	 */
	@Override
	public File promptEnclosingFolder(File curEnclosingFolder, File curProductFolder,
					String productSearchName)
	{
		//TODO make this better, handle case where folder selected
		//but file still wasn't found. (right now it just prompts again)
		return topView.getEnclosingFolder();
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptParameterValue(algorithms.Parameter)
	 */
	@Override
	public String promptParameterValue(Parameter parameter)
	{
		return topView.promptParameterValue(parameter);
	}

	/* (non-Javadoc)
	 * @see ui.UI#reportError(java.lang.String)
	 */
	@Override
	public void reportError(String message)
	{
		errors.add(message);
	}
	
	public List<String> getErrors()
	{
		return errors;
	}
	
	public boolean hasErrors()
	{
		return !errors.isEmpty();
	}
	
	public void clearErrors()
	{
		errors.clear();
	}

	/* (non-Javadoc)
	 * @see ui.UI#reportMessage(java.lang.String)
	 */
	@Override
	public void reportMessage(String message)
	{
		//for the moment, there's nothing better to do
		//some kinds of notifications are handled elsewhere through stats
		System.out.println(message);
	}

	/**
	 * @update_comment
	 * @return
	 */
	public View getTopView()
	{
		return topView;
	}
	
	public void setTopView(TopView topView)
	{
		this.topView = topView;
	}

	/**
	 * @return the result
	 */
	public ArgParseResult getArgParseResult()
	{
		return args;
	}

	/**
	 * @param result the result to set
	 */
	public void setArgParseResult(ArgParseResult result)
	{
		this.args = result;
	}

}
