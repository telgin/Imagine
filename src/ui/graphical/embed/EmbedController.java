package ui.graphical.embed;

import java.io.File;
import java.util.List;

import algorithms.Algorithm;
import api.ConfigurationAPI;
import api.UsageException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import key.FileKey;
import key.Key;
import key.PasswordKey;
import key.StaticKey;
import logging.LogLevel;
import logging.Logger;
import ui.UIContext;
import ui.graphical.FileProperty;
import ui.graphical.GUI;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class EmbedController
{
	private EmbedView view;
	private GUI gui;
	
	//state variables
	private List<String> presetNames;
	private Algorithm selectedAlgorithm = null;
	private boolean structuredOutput = false;
		
	/**
	 * @update_comment
	 * @param file
	 */
	public EmbedController(EmbedView view)
	{
		this.view = view;
		this.gui = (GUI) UIContext.getUI();
		
		presetNames = ConfigurationAPI.getAlgorithmPresetNames();
	}

	/**
	 * @return the selectedAlgorithm
	 */
	public Algorithm getSelectedAlgorithm()
	{
		return selectedAlgorithm;
	}

	/**
	 * @param selectedAlgorithm the selectedAlgorithm to set
	 */
	public void setSelectedAlgorithm(Algorithm selectedAlgorithm)
	{
		this.selectedAlgorithm = selectedAlgorithm;
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	void chooseKeyFile()
	{
		File chosen = view.chooseFile();
		if (chosen != null)
		{
			view.setKeyFilePath(chosen.getAbsolutePath());
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	private Key getKey()
	{
		Key key = null;
		if (view.keyFileEnabled())
		{
			key = new FileKey(new File(view.getKeyFilePath()));
		}
		else if (view.passwordEnabled())
		{
			key = new PasswordKey();
		}
		else //key section not enabled
		{
			key = new StaticKey();
		}
		
		return key;
	}
	
	public void algorithmSelected(int index)
	{
		if (index == -1)
		{
			//nothing selected
			view.setKeySectionEnabled(false);
		}
		else
		{
			try
			{
				selectedAlgorithm = ConfigurationAPI.getAlgorithmPreset(presetNames.get(index));
				view.setKeySectionEnabled(true);
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_error, e.getMessage());
				Logger.log(LogLevel.k_debug, e, false);
				
				view.setAlgorithmSelection(null);
			}
		}
		
		if (gui.hasErrors())
		{
			view.showErrors(gui.getErrors(), "algorithm lookup");
			gui.clearErrors();
		}
	}

	/**
	 * @return the algorithms
	 */
	public List<String> getPresetNames()
	{
		return presetNames;
	}

	/**
	 * @update_comment
	 * @param b
	 * @return
	 */
	public void algorithmSelectFocus(boolean focused)
	{
		//update the list if focused on
		if (focused)
		{
			String previousSelection = view.getAlgorithmSelection();
			
			presetNames = ConfigurationAPI.getAlgorithmPresetNames();
			view.setAlgorithmPresets(presetNames);
			
			if (presetNames.contains(previousSelection))
			{
				view.setAlgorithmSelection(previousSelection);
			}
			else
			{
				view.setAlgorithmSelection(null);
			}
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void inputAddFilePressed()
	{
		File file = view.chooseFile();
		
		if (file != null)
		{
			view.addInput(file);
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void inputAddFolderPressed()
	{
		File folder = view.chooseFolder();
		
		if (folder != null)
		{
			view.addInput(folder);
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void removeInputPressed()
	{
		view.removeSelectedInput();
		
		if (gui.hasErrors())
		{
			view.showErrors(gui.getErrors(), "input entry removal");
			gui.clearErrors();
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void browseOutputFolder()
	{
		File folder = view.chooseFolder();
		
		if (folder != null)
		{
			view.setOutputFolder(folder);
		}
	}

	/**
	 * @update_comment
	 * @param b
	 * @return
	 */
	public void structuredOutputChecked(boolean checked)
	{
		structuredOutput = checked;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void runConversionPressed()
	{
		runProgressTest();
	}
	
	private void runProgressTest()
	{
		Task<Void> task = new Task<Void>()
		{ 
			@Override
			public Void call()
			{
				double x = 0;
				for (; x <= 1.000001; x += .005)
				{
					view.setAll(x);
					System.out.println(x);
					view.updateAllActiveCells();
					try
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
				return null;
			}
		};
		
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void targetSelectFolderPressed()
	{
		File folder = view.chooseFolder();
		
		if (folder != null)
		{
			view.setTarget(folder);
		}
	}
}
