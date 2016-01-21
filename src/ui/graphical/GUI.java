package ui.graphical;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import algorithms.Parameter;
import data.TrackingGroup;
import ui.UI;

public class GUI extends UI
{
	private List<String> args;
	private List<String> errors;
	
	public GUI(List<String> args)
	{
		this.args = args;
		errors = new LinkedList<String>();
	}

	@Override
	public File promptKeyFileLocation(String keyName, String groupName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String promptKey(String keyName, String groupName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ui.UI#processArgs()
	 */
	@Override
	public void processArgs()
	{
		OpenArchiveView.launch(OpenArchiveView.class, args.toArray(new String[0]));
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptEnclosingFolder(java.io.File, java.io.File, java.lang.String)
	 */
	@Override
	public File promptEnclosingFolder(File curEnclosingFolder, File curProductFolder,
					String productSearchName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ui.UI#promptParameterValue(algorithms.Parameter)
	 */
	@Override
	public String promptParameterValue(Parameter parameter)
	{
		// TODO Auto-generated method stub
		return null;
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

}
