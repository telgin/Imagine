package ui.graphical.archiveviewer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import algorithms.Algorithm;
import api.ConfigurationAPI;
import api.ConversionAPI;
import api.UsageException;
import archive.ArchiveContents;
import key.FileKey;
import key.Key;
import key.PasswordKey;
import key.DefaultKey;
import logging.LogLevel;
import logging.Logger;
import ui.UIContext;
import ui.graphical.GUI;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The controller class for the archive viewer tab
 */
public class OpenArchiveController
{
	private OpenArchiveView f_view;
	private GUI f_gui;
	
	//state fields
	private List<String> f_presetNames;
	private Algorithm f_selectedAlgorithm = null;
	private boolean f_nonInitialFragment;
		
	/**
	 * Constructs an open archive controller for a given view
	 * @param p_view The open archive view
	 */
	public OpenArchiveController(OpenArchiveView p_view)
	{
		f_view = p_view;
		f_gui = (GUI) UIContext.getUI();
		
		f_presetNames = ConfigurationAPI.getAlgorithmPresetNames();
	}

	/**
	 * @return the selectedAlgorithm
	 */
	public Algorithm getSelectedAlgorithm()
	{
		return f_selectedAlgorithm;
	}

	/**
	 * @param p_selectedAlgorithm the selectedAlgorithm to set
	 */
	public void setSelectedAlgorithm(Algorithm p_selectedAlgorithm)
	{
		f_selectedAlgorithm = p_selectedAlgorithm;
	}
	
	/**
	 * Tries to open the specified archive and displays its contents in the table
	 */
	void openArchive()
	{
		try
		{
			f_view.setOpenButtonEnabled(false);
			f_view.setAlgorithmSelectionEnabled(false);
			
			ArchiveContents archiveContents = ConversionAPI.openArchive(f_selectedAlgorithm, getKey(), getInputFile());
			
			if (!archiveContents.getFileContents().isEmpty())
				f_nonInitialFragment = archiveContents.getFileContents().get(0).getFragmentNumber() > 1;
			else
				f_nonInitialFragment = false;
			
			f_view.setTableData(archiveContents.getFileContents());
			f_view.setExtractionButtonsEnabled(true);
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_error, e.getMessage());
			
			f_view.setOpenButtonEnabled(true);
			f_view.setAlgorithmSelectionEnabled(true);
			f_view.clearTable();
		}
		
		if (f_gui.hasErrors())
		{
			f_view.showErrors(f_gui.getErrors(), "archive parsing");
			f_gui.clearErrors();
		}
	}
	
	/**
	 * Gets the input file from the gui
	 * @return The input file location
	 * @throws UsageException If the input file is not defined
	 */
	private File getInputFile() throws UsageException
	{
		File inputFile = null;
		if (f_view.getInputFilePath() != null && !f_view.getInputFilePath().isEmpty())
			inputFile = new File(f_view.getInputFilePath());

		if (inputFile == null)
			throw new UsageException("The input file must exist.");
		
		return inputFile;
	}
	
	/**
	 * Gets the output folder from the gui
	 * @return The file location of the output folder
	 * @throws UsageException If the output folder is not defined
	 */
	private File getOutputFolder() throws UsageException
	{
		File outputFolder = null;
		if (f_view.getOutputFolderPath() != null && !f_view.getOutputFolderPath().isEmpty())
			outputFolder = new File(f_view.getOutputFolderPath());

		if (outputFolder == null)
			throw new UsageException("The output folder must exist.");
		
		return outputFolder;
	}
	
	/**
	 * Allows the user to select a key file and updates the gui
	 */
	public void chooseKeyFile()
	{
		File chosen = f_view.chooseFile();
		if (chosen != null)
		{
			f_view.setKeyFilePath(chosen.getAbsolutePath());
		}
	}

	/**
	 * Extracts all items regardless of selection state
	 */
	public void extractAll()
	{
		try
		{
			if (f_nonInitialFragment)
			{
				Logger.log(LogLevel.k_error, "The first file has a fragment number greater than 1.");
				Logger.log(LogLevel.k_error, "Only initial fragments may start an extraction chain, "
							+ "so this file will not be complete.");
			}
				
			ConversionAPI.extractAll(f_selectedAlgorithm, getKey(), getInputFile(), getOutputFolder());
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_error, e.getMessage());
		}
		
		if (f_gui.hasErrors())
		{
			f_view.showErrors(f_gui.getErrors(), "data extraction");
			f_gui.clearErrors();
		}
	}

	/**
	 * Creates a key object based on the key selection in the gui
	 * @return The derived key object
	 */
	private Key getKey()
	{
		Key key = null;
		if (f_view.keyFileEnabled())
		{
			key = new FileKey(new File(f_view.getKeyFilePath()));
		}
		else if (f_view.passwordEnabled())
		{
			key = new PasswordKey();
		}
		else //key section not enabled
		{
			key = new DefaultKey();
		}
		
		return key;
	}

	/**
	 * Attempts to extract the selected files
	 */
	public void extractSelected()
	{
		List<Integer> indices = f_view.getSelectedRows();
		
		try
		{
			File outputFolder = getOutputFolder();
			File inputFile = getInputFile();
			
			for (int index : indices)
			{
				if (index == 0 && f_nonInitialFragment)
				{
					Logger.log(LogLevel.k_error, "The first file has a fragment number greater than 1.");
					Logger.log(LogLevel.k_error, "Only initial fragments may start an extraction chain, "
									+ "so this file will not be complete.");
				}

				try
				{
					ConversionAPI.extractFile(f_selectedAlgorithm, getKey(), inputFile, outputFolder, index);
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
		
		if (f_gui.hasErrors())
		{
			f_view.showErrors(f_gui.getErrors(), "data extraction");
			f_gui.clearErrors();
		}
	}
	
	/**
	 * Updates the view to enable the correct components when a new
	 * algorithm is selected
	 * @param p_index The index of the algorithm selected
	 */
	public void algorithmSelected(int p_index)
	{
		if (p_index == -1)
		{
			//nothing selected
			f_view.setKeySectionEnabled(false);
			f_view.setOpenButtonEnabled(false);
		}
		else
		{
			try
			{
				f_selectedAlgorithm = ConfigurationAPI.getAlgorithmPreset(f_presetNames.get(p_index));
				f_view.setKeySectionEnabled(true);
				f_view.setOpenButtonEnabled(true);
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_error, e.getMessage());
				Logger.log(LogLevel.k_debug, e, false);
				
				f_view.setAlgorithmSelection(null);
			}
		}
		
		if (f_gui.hasErrors())
		{
			f_view.showErrors(f_gui.getErrors(), "algorithm lookup");
			f_gui.clearErrors();
		}
	}

	/**
	 * @return the algorithms
	 */
	public List<String> getPresetNames()
	{
		return f_presetNames;
	}

	/**
	 * Updates the list of presets if they have changed since the initial loading.
	 * @param p_focused The focused state of the algorithm preset selection choicebox
	 */
	public void algorithmSelectFocus(boolean p_focused)
	{
		//update the list if focused on
		if (p_focused)
		{
			List<String> currentPresetNames = ConfigurationAPI.getAlgorithmPresetNames();
			
			//update the list only if it changed
			if (f_presetNames.size() != currentPresetNames.size() || 
							!f_presetNames.containsAll(currentPresetNames))
			{
				//save algorithm modifications
				Algorithm previousSelection = f_selectedAlgorithm;
				
				//refresh the preset names to the new list
				f_presetNames = currentPresetNames;
				f_view.setAlgorithmPresets(f_presetNames);
			
				if (f_presetNames.contains(previousSelection.getPresetName()))
				{
					f_view.setAlgorithmSelection(previousSelection.getPresetName());
					f_selectedAlgorithm = previousSelection;
				}
				else
				{
					f_view.setAlgorithmSelection(null);
				}
			}
		}
	}

	/**
	 * Lets the user browse for an input file and updates the gui
	 */
	public void browseInputFile()
	{
		File file = f_view.chooseFile();
		
		if (file != null)
		{
			f_view.setInputFilePath(file.getAbsolutePath());
			
			//reset
			f_view.setOpenButtonEnabled(true);
			f_view.setAlgorithmSelectionEnabled(true);
			f_view.clearTable();
			f_view.setExtractionButtonsEnabled(false);
		}
	}

	/**
	 * Lets the user select an output folder and updates the gui
	 */
	public void browseOutputFolder()
	{
		File folder = f_view.chooseFolder();
		
		if (folder != null)
		{
			f_view.setOutputFolderPath(folder.getAbsolutePath());
		}
	}
}
