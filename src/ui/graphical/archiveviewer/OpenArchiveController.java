package ui.graphical.archiveviewer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import algorithms.Algorithm;
import api.ConfigurationAPI;
import api.ConversionAPI;
import api.UsageException;
import javafx.beans.Observable;
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
	private File inputFile;
	private List<String> algorithms;
	private Algorithm selectedAlgorithm = null;
	
	//constants
	private final String noSelectionString = "None Selected";

	private String defaultAlgorithmSelection;
		
	/**
	 * @update_comment
	 * @param file
	 */
	public OpenArchiveController(OpenArchiveView view, File inputFile)
	{
		this.view = view;
		this.gui = (GUI) UIContext.getUI();
		this.inputFile = inputFile;
		
		reloadAlgorithmPresets();
		setDefaultAlgorithmSelection(noSelectionString);
	}

	/**
	 * @update_comment
	 */
	private void reloadAlgorithmPresets()
	{
		algorithms = ConfigurationAPI.getAlgorithmPresetNames();
		algorithms.add(0, noSelectionString);
	}
	/**
	 * @return the defaultAlgorithmSelection
	 */
	public String getDefaultAlgorithmSelection()
	{
		return defaultAlgorithmSelection;
	}

	/**
	 * @param defaultAlgorithmSelection the defaultAlgorithmSelection to set
	 */
	public void setDefaultAlgorithmSelection(String defaultAlgorithmSelection)
	{
		this.defaultAlgorithmSelection = defaultAlgorithmSelection;
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
	 * @return the file
	 */
	public File getInputFile()
	{
		return inputFile;
	}
	
	void openArchive()
	{
		try
		{
			view.setOpenButtonEnabled(false);
			view.setAlgorithmSelectionEnabled(false);

			ProductContents productContents = ConversionAPI.openArchive(selectedAlgorithm, getKey(), inputFile);
			
			view.setTableData(productContents.getFileContents());
			view.setExtractionButtonsEnabled(true);
		}
		catch (IOException | UsageException e)
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
		File extractionLocation = view.chooseFolder();
		if (extractionLocation != null)
		{
			try
			{
				ConversionAPI.extractAll(selectedAlgorithm, getKey(), inputFile, extractionLocation);
			}
			catch (IOException | UsageException e)
			{
				Logger.log(LogLevel.k_debug, e, false);
				Logger.log(LogLevel.k_error, e.getMessage());
			}
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
		
		File extractionLocation = view.chooseFolder();
		if (extractionLocation != null)
		{
			for (int index : indices)
			{
				try
				{
					ConversionAPI.extractFile(selectedAlgorithm, getKey(), inputFile, extractionLocation, index);
				}
				catch (IOException | UsageException e)
				{
					Logger.log(LogLevel.k_debug, e, false);
					Logger.log(LogLevel.k_error, e.getMessage());
				}
			}
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
			//nothing selected, select "No Selection"
			view.setAlgorithmSelection(defaultAlgorithmSelection);
		}
		else if (index == 0)
		{
			//'no selection' selected
			view.setKeySectionEnabled(false);
			view.setOpenButtonEnabled(false);
		}
		else
		{
			try
			{
				selectedAlgorithm = ConfigurationAPI.getAlgorithmPreset(algorithms.get(index));
				view.setKeySectionEnabled(true);
				view.setOpenButtonEnabled(true);
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_error, e.getMessage());
				Logger.log(LogLevel.k_debug, e, false);
				
				view.setAlgorithmSelection(noSelectionString);
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
	public List<String> getAlgorithms()
	{
		return algorithms;
	}

	/**
	 * @param algorithms the algorithms to set
	 */
	public void setAlgorithms(List<String> algorithms)
	{
		this.algorithms = algorithms;
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
			
			reloadAlgorithmPresets();
			view.setAlgorithmPresets(algorithms);
			
			if (algorithms.contains(previousSelection))
			{
				view.setAlgorithmSelection(previousSelection);
			}
		}
	}
	
}
