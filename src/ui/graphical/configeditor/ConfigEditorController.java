package ui.graphical.configeditor;

import java.util.List;

import api.ConfigurationAPI;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ConfigEditorController
{

	/**
	 * @update_comment
	 * @return
	 */
	public List<String> getProfiles()
	{
		return ConfigurationAPI.getTrackingGroupNames();
	}

	/**
	 * @update_comment
	 * @param e
	 * @return
	 */
	public void algorithmSelected(int index)
	{
		System.out.println("Algo selected: " + index);
	}

	/**
	 * @update_comment
	 * @param b
	 * @return
	 */
	public void keyFileChecked(boolean checked)
	{
		System.out.println("Key file checked: " + checked);
	}

	/**
	 * @update_comment
	 * @param b
	 * @return
	 */
	public void useIndexFilesChecked(Boolean b)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @update_comment
	 * @param b
	 * @return
	 */
	public void useAbsolutePathsChecked(Boolean b)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @update_comment
	 * @param b
	 * @return
	 */
	public void useStructuredOutputChecked(Boolean b)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void browseKeyFile()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void browseStaticOutputFolder()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void browseHashDBFile()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @param b
	 * @return
	 */
	public void staticOutputFolderChecked(Boolean b)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @param b
	 * @return
	 */
	public void customDBChecked(Boolean b)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void addTrackedFilePressed()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void addTrackedFolderPressed()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void removeTrackedFilePressed()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void removeUntrackedFilePressed()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void addUntrackedFolderPressed()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void addUntrackedFilePressed()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void saveProfilePressed()
	{
		// TODO Auto-generated method stub

	}

}
