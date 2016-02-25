package ui;

import java.io.File;

import algorithms.Parameter;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class UI
{
	/**
	 * @update_comment
	 * @return
	 */
	public abstract File promptKeyFileLocation();

	/**
	 * @update_comment
	 * @return
	 */
	public abstract String promptKey();

	/**
	 * Starts the execution of the command line arguments
	 */
	public abstract void init();

	/**
	 * @update_comment
	 * @param enclosingFolder
	 * @param p_curArchiveFolder
	 * @param p_archiveSearchName
	 * @return
	 */
	public abstract File promptEnclosingFolder(File p_curEnclosingFolder, File p_curArchiveFolder,
					String p_archiveSearchName);

	/**
	 * @update_comment
	 * @param p_parameter
	 * @return
	 */
	public abstract String promptParameterValue(Parameter p_parameter);
	
	/**
	 * @update_comment
	 * @param p_message
	 */
	public abstract void reportMessage(String p_message);
	
	/**
	 * @update_comment
	 * @param p_message
	 */
	public abstract void reportError(String p_message);
}
