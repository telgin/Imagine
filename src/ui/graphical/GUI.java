package ui.graphical;

import java.io.File;
import java.util.List;

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
	public void runnerStartupMessage()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showControlPanel()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showBackupPanel()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String promptTrackingGroup()
	{
		// TODO Auto-generated method stub
		return null;
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

}