package ui;

import java.io.File;

import algorithms.Parameter;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This superclass for the gui/cmdui facilitates getting required
 * information from the user during run time in a generic way.
 */
public abstract class UI
{
	/**
	 * Prompt the user for the key file location
	 * @return The key file location
	 */
	public abstract File promptKeyFileLocation();

	/**
	 * Prompt for the key (password)
	 * @return The key string
	 */
	public abstract String promptKey();

	/**
	 * Starts the execution of the command line arguments
	 */
	public abstract void init();

	/**
	 * Prompt for the enclosing folder of an archive we're looking to read from.
	 * @param p_curEnclosingFolder The current enclosing folder
	 * @param p_curArchiveFolder The current archive folder
	 * @param p_archiveSearchName The name of the archive we're looking for
	 * @return The file location of the archive we need
	 */
	public abstract File promptEnclosingFolder(File p_curEnclosingFolder, File p_curArchiveFolder,
					String p_archiveSearchName);

	/**
	 * Prompt for a parameter value
	 * @param p_parameter The parameter which needs a value
	 * @return The value for this parameter
	 */
	public abstract String promptParameterValue(Parameter p_parameter);
	
	/**
	 * Report a message to the user
	 * @param p_message The message text
	 */
	public abstract void reportMessage(String p_message);
	
	/**
	 * Report an error message to the user
	 * @param p_message The error message text
	 */
	public abstract void reportError(String p_message);
}
