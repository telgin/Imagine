package ui.graphical;

import java.io.File;
import java.util.List;

import algorithms.Parameter;
import data.TrackingGroup;
import ui.UI;

public class GUI extends UI
{
	private List<String> args;
	
	public GUI(List<String> args)
	{
		this.args = args;
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
		// TODO Auto-generated method stub
		
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

}
