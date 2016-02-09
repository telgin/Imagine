package ui;

import java.io.File;

import algorithms.Parameter;
import system.SystemManager;

public abstract class UI
{
	public abstract File promptKeyFileLocation();

	public abstract String promptKey();

	/**
	 * Starts the execution of the command line arguments
	 */
	public abstract void processArgs();

	/**
	 * @update_comment
	 * @param enclosingFolder
	 * @param curProductFolder
	 * @param productSearchName
	 * @return
	 */
	public abstract File promptEnclosingFolder(File curEnclosingFolder, File curProductFolder,
					String productSearchName);

	/**
	 * @update_comment
	 * @param parameter
	 * @return
	 */
	public abstract String promptParameterValue(Parameter parameter);
	
	public abstract void reportMessage(String message);
	
	public abstract void reportError(String message);
}
