package ui.graphical.archiveviewer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import algorithms.Algorithm;
import api.ConfigurationAPI;
import api.ConversionAPI;
import api.UsageException;
import key.FileKey;
import key.Key;
import key.PasswordKey;
import key.StaticKey;
import logging.LogLevel;
import logging.Logger;
import product.ProductContents;
import ui.UIContext;
import ui.graphical.GUI;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class OpenArchiveController
{
	private OpenArchiveView view;
	private GUI gui;
	
	//state variables
	private List<String> presetNames;
	private Algorithm selectedAlgorithm = null;
	private boolean nonInitialFragment;
		
	/**
	 * @update_comment
	 * @param file
	 */
	public OpenArchiveController(OpenArchiveView view)
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
	
	void openArchive()
	{
		try
		{
			view.setOpenButtonEnabled(false);
			view.setAlgorithmSelectionEnabled(false);
			
			ProductContents productContents = ConversionAPI.openArchive(selectedAlgorithm, getKey(), getInputFile());
			
			if (!productContents.getFileContents().isEmpty())
				nonInitialFragment = productContents.getFileContents().get(0).getFragmentNumber() > 1;
			else
				nonInitialFragment = false;
			
			view.setTableData(productContents.getFileContents());
			view.setExtractionButtonsEnabled(true);
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_error, e.getMessage());
			
			view.setOpenButtonEnabled(true);
			view.setAlgorithmSelectionEnabled(true);
			view.clearTable();
		}
		
		if (gui.hasErrors())
		{
			view.showErrors(gui.getErrors(), "archive parsing");
			gui.clearErrors();
		}
	}
	
	
	private File getInputFile() throws UsageException
	{
		File inputFile = null;
		if (view.getInputFilePath() != null && !view.getInputFilePath().isEmpty())
			inputFile = new File(view.getInputFilePath());

		if (inputFile == null)
			throw new UsageException("The input file must exist.");
		
		return inputFile;
	}
	
	private File getOutputFolder() throws UsageException
	{
		File outputFolder = null;
		if (view.getOutputFolderPath() != null && !view.getOutputFolderPath().isEmpty())
			outputFolder = new File(view.getOutputFolderPath());

		if (outputFolder == null)
			throw new UsageException("The output folder must exist.");
		
		return outputFolder;
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
	public void extractAll()
	{
		try
		{
			if (nonInitialFragment)
			{
				Logger.log(LogLevel.k_error, "The first file has a fragment number greater than 1.");
				Logger.log(LogLevel.k_error, "Only initial fragments may start an extraction chain, "
							+ "so this file will not be complete.");
			}
				
			ConversionAPI.extractAll(selectedAlgorithm, getKey(), getInputFile(), getOutputFolder());
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_error, e.getMessage());
		}
		
		if (gui.hasErrors())
		{
			view.showErrors(gui.getErrors(), "data extraction");
			gui.clearErrors();
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

	/**
	 * @update_comment
	 * @return
	 */
	public void extractSelected()
	{
		List<Integer> indices = view.getSelectedRows();
		
		try
		{
			File outputFolder = getOutputFolder();
			File inputFile = getInputFile();
			
			for (int index : indices)
			{
				if (index == 0 && nonInitialFragment)
				{
					Logger.log(LogLevel.k_error, "The first file has a fragment number greater than 1.");
					Logger.log(LogLevel.k_error, "Only initial fragments may start an extraction chain, "
									+ "so this file will not be complete.");
				}

				try
				{
					ConversionAPI.extractFile(selectedAlgorithm, getKey(), inputFile, outputFolder, index);
				}
				catch (IOException | UsageException e)
				{
					Logger.log(LogLevel.k_debug, e, false);
					Logger.log(LogLevel.k_error, e.getMessage());
				}

			}
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_error, e.getMessage());
		}
		
		if (gui.hasErrors())
		{
			view.showErrors(gui.getErrors(), "data extraction");
			gui.clearErrors();
		}
	}
	
	public void algorithmSelected(int index)
	{
		if (index == -1)
		{
			//nothing selected
			view.setKeySectionEnabled(false);
			view.setOpenButtonEnabled(false);
		}
		else
		{
			try
			{
				selectedAlgorithm = ConfigurationAPI.getAlgorithmPreset(presetNames.get(index));
				view.setKeySectionEnabled(true);
				view.setOpenButtonEnabled(true);
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
			List<String> currentPresetNames = ConfigurationAPI.getAlgorithmPresetNames();
			
			//update the list only if it changed
			if (presetNames.size() != currentPresetNames.size() || 
							!presetNames.containsAll(currentPresetNames))
			{
				//save algorithm modifications
				Algorithm previousSelection = selectedAlgorithm;
				
				//refresh the preset names to the new list
				presetNames = currentPresetNames;
				view.setAlgorithmPresets(presetNames);
			
				if (presetNames.contains(previousSelection.getPresetName()))
				{
					view.setAlgorithmSelection(previousSelection.getPresetName());
					selectedAlgorithm = previousSelection;
				}
				else
				{
					view.setAlgorithmSelection(null);
				}
			}
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void browseInputFile()
	{
		File file = view.chooseFile();
		
		if (file != null)
		{
			view.setInputFilePath(file.getAbsolutePath());
			
			//reset
			view.setOpenButtonEnabled(true);
			view.setAlgorithmSelectionEnabled(true);
			view.clearTable();
			view.setExtractionButtonsEnabled(false);
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
			view.setOutputFolderPath(folder.getAbsolutePath());
		}
	}
}
