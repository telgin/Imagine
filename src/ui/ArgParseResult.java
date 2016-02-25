package ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import system.CmdAction;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ArgParseResult
{
	private String f_presetName;
	private List<File> f_inputFiles;
	private List<String[]> f_parameters;
	private File f_outputFolder;
	private File f_keyFile;
	private File f_resultFile;
	private boolean f_usingPassword;
	private boolean f_guiMode;
	private CmdAction f_action;
	
	/**
	 * @update_comment
	 */
	public ArgParseResult()
	{
		f_presetName = null;
		f_inputFiles = new ArrayList<File>();
		f_parameters = new ArrayList<String[]>();
		f_outputFolder = null;
		f_keyFile = null;
		f_resultFile = null;
		f_usingPassword = false;
		f_guiMode = false;
		f_action = null;
	}
	
	/**
	 * @return the presetName
	 */
	public String getPresetName()
	{
		return f_presetName;
	}

	/**
	 * @return the inputFiles
	 */
	public List<File> getInputFiles()
	{
		return f_inputFiles;
	}

	/**
	 * @return the parameters
	 */
	public List<String[]> getParameters()
	{
		return f_parameters;
	}

	/**
	 * @return the outputFolder
	 */
	public File getOutputFolder()
	{
		return f_outputFolder;
	}

	/**
	 * @return the keyFile
	 */
	public File getKeyFile()
	{
		return f_keyFile;
	}

	/**
	 * @return the resultFile
	 */
	public File getResultFile()
	{
		return f_resultFile;
	}

	/**
	 * @return the usingPassword
	 */
	public boolean isUsingPassword()
	{
		return f_usingPassword;
	}

	/**
	 * @return the guiMode
	 */
	public boolean isGuiMode()
	{
		return f_guiMode;
	}

	/**
	 * @return the action
	 */
	public CmdAction getAction()
	{
		return f_action;
	}

	/**
	 * @param p_presetName the presetName to set
	 */
	public void setPresetName(String p_presetName)
	{
		f_presetName = p_presetName;
	}

	/**
	 * @param p_outputFolder the outputFolder to set
	 */
	public void setOutputFolder(File p_outputFolder)
	{
		f_outputFolder = p_outputFolder;
	}

	/**
	 * @param p_keyFile the keyFile to set
	 */
	public void setKeyFile(File p_keyFile)
	{
		f_keyFile = p_keyFile;
	}

	/**
	 * @param p_resultFile the resultFile to set
	 */
	public void setResultFile(File p_resultFile)
	{
		f_resultFile = p_resultFile;
	}

	/**
	 * @param p_usingPassword the usingPassword to set
	 */
	public void setUsingPassword(boolean p_usingPassword)
	{
		f_usingPassword = p_usingPassword;
	}

	/**
	 * @param p_guiMode the guiMode to set
	 */
	public void setGuiMode(boolean p_guiMode)
	{
		f_guiMode = p_guiMode;
	}

	/**
	 * @param p_action the action to set
	 */
	public void setAction(CmdAction p_action)
	{
		f_action = p_action;
	}
	
	/**
	 * @update_comment
	 * @param file
	 */
	public void addInputFile(File file)
	{
		f_inputFiles.add(file);
	}
	
	/**
	 * @update_comment
	 * @param nameValuePair
	 */
	public void addParameter(String[] nameValuePair)
	{
		f_parameters.add(nameValuePair);
	}
}
