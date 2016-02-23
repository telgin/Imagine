package ui.graphical.embed;

import java.io.File;
import java.io.IOException;
import java.util.List;

import algorithms.Algorithm;
import algorithms.Option;
import algorithms.Parameter;
import algorithms.imageoverlay.Definition;
import api.ConfigurationAPI;
import api.ConversionAPI;
import api.UsageException;
import config.Settings;
import javafx.application.Platform;
import javafx.concurrent.Task;
import key.FileKey;
import key.Key;
import key.PasswordKey;
import key.StaticKey;
import logging.LogLevel;
import logging.Logger;
import product.ConversionJob;
import product.ConversionJobFileState;
import product.FileStatus;
import product.JobStatus;
import system.ActiveComponent;
import system.SystemManager;
import ui.UIContext;
import ui.graphical.FileProperty;
import ui.graphical.GUI;
import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class EmbedController implements ActiveComponent
{
	private EmbedView view;
	private GUI gui;
	
	//state variables
	private List<String> presetNames;
	private Algorithm selectedAlgorithm = null;
	private File selectedTargetFolder = null;
	private boolean structuredOutput = false;
	
	private boolean shuttingDown = false;
	private int totalFilesThisRun = 0;
	private int filesCreated = 0;
	private double conversionProgress = 0;
	private Thread guiUpdateDaemon;
		
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
			view.setInputSectionEnabled(false);
			view.setTargetSectionEnabled(false);
			view.setRunConversionEnabled(false);
			selectedAlgorithm = null;
		}
		//else if the selected algorithm is a new selection...
		else if (selectedAlgorithm == null || !selectedAlgorithm.getPresetName().equals(presetNames.get(index)))
		{
			try
			{
				selectedAlgorithm = ConfigurationAPI.getAlgorithmPreset(presetNames.get(index));
				view.setKeySectionEnabled(true);
				view.setInputSectionEnabled(true);
				view.setRunConversionEnabled(true);
				
				//Enable the target section if the algorithm uses it
				//TODO create a better way to handle this (or update it once more algos exist)
				Parameter imageFolder = selectedAlgorithm.getParameter(Definition.IMAGE_FOLDER_PARAM);
				if (imageFolder != null)
				{
					view.setTargetSectionEnabled(true);
					
					//reflect the set target folder if it exists
					if (imageFolder.getValue() != null && !imageFolder.getValue().equals(Option.PROMPT_OPTION.getValue()))
					{
						view.setTarget(new File(imageFolder.getValue()));
					}
				}
				else
				{
					view.setTargetSectionEnabled(false);
				}
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
			
				//try to restore the changes that were made
				if (previousSelection != null && presetNames.contains(previousSelection.getPresetName()))
				{
					view.setAlgorithmSelection(previousSelection.getPresetName());
					selectedAlgorithm = previousSelection;
					
					//reset the target section
					Parameter imageFolder = selectedAlgorithm.getParameter(Definition.IMAGE_FOLDER_PARAM);
					if (imageFolder != null)
					{
						//reflect the set target folder if it exists
						if (!imageFolder.getValue().equals(Option.PROMPT_OPTION.getValue()))
						{
							view.setTarget(new File(imageFolder.getValue()));
						}
					}
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
	public void inputAddFilePressed()
	{
		File file = view.chooseFile();

		if (file != null)
		{
			view.addInput(FileSystemUtil.relativizeByCurrentLocation(file));
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
			view.addInput(FileSystemUtil.relativizeByCurrentLocation(folder));
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
		try
		{
			File outputFolder = view.getOutputFolder();
			
			if (outputFolder == null)
			{
				Logger.log(LogLevel.k_error, "An output folder must be chosen.");
			}
			else if (selectedAlgorithm.getParameter(Definition.IMAGE_FOLDER_PARAM) != null && 
							(selectedTargetFolder == null || !selectedTargetFolder.exists()))
			{
				Logger.log(LogLevel.k_error, "A valid target folder must be chosen.");
			}
			else
			{
				List<File> inputFiles = view.getInputFileList();
				totalFilesThisRun = 0;
				try
				{
					for (File input : inputFiles)
						totalFilesThisRun += FileSystemUtil.countEligableFiles(input);
				}
				catch (IOException e1)
				{
					Logger.log(LogLevel.k_debug, "Could not count files for an entry.");
					totalFilesThisRun = 0;
				}
			
				SystemManager.reset();
				Settings.setOutputFolder(view.getOutputFolder());
				Settings.setUsingStructuredOutput(structuredOutput);
				Settings.setGenerateReport(false);
				Settings.setTrackFileStatus(true);

				ConversionJob job = ConversionAPI.runConversion(inputFiles, selectedAlgorithm, getKey(), 1);
	
				if (guiUpdateDaemon == null || !guiUpdateDaemon.isAlive())
				{
					guiUpdateDaemon = new Thread(() -> updateCSS(job));
					guiUpdateDaemon.setDaemon(true);
					guiUpdateDaemon.start();
				}
			}
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_error, "The conversion job failed to run.");
			Logger.log(LogLevel.k_debug, e, false);
		}
		
		if (gui.hasErrors())
		{
			view.showErrors(gui.getErrors(), "conversion");
			gui.clearErrors();
		}
	}
	
	public void updateCSS(ConversionJob job)
	{
		int run = 0;
		while (!shuttingDown && !job.isFinished())
		{
			cssUpdateLoop(run++);
		}
		
		//finalize any progress that was made one last time
		if (!shuttingDown && job.isFinished())
		{
			cssUpdateLoop(run++);
		}
		
		if (gui.hasErrors())
		{
			Platform.runLater(() -> 
			{
				view.showErrors(gui.getErrors(), "conversion");
				gui.clearErrors();
			});
		}
	}
	
	public void cssUpdateLoop(int run)
	{
		//System.out.println("Looping..." + filesCreated);
		
		//update things if they're different
		
		//files created
		int currentFilesCreated = JobStatus.getProductsCreated();
		if (currentFilesCreated != filesCreated)
		{
			filesCreated = JobStatus.getProductsCreated();
			view.setFilesCreated(filesCreated);
		}
		
		//conversion progress
		double currentConversionProgress = (double) JobStatus.getInputFilesProcessed() / totalFilesThisRun;
		if (conversionProgress != currentConversionProgress)
		{
			conversionProgress = currentConversionProgress;
			view.setConversionProgress(conversionProgress);
		}
		
		//update input file item status/progress for visible/loaded cells
		for (InputFileTreeItem item : view.getActiveInputItems().values())
		{
			updateInputItem(item);
		}
		
		//update target file item status for visible/loaded cells
		for (TargetFileTreeItem item : view.getActiveTargetItems().values())
		{
			updateTargetItem(item);
		}
		
		//update the cell css
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
	
	public synchronized void updateInputItem(InputFileTreeItem item)
	{
		if (item != null)
		{
			FileStatus fileStatus = JobStatus.getFileStatus(item.getFile());
			//System.err.println(fileStatus.getStatus() + "\t" + item.getFile().getName());

			item.setStatus(fileStatus.getState());
			item.setProgress(fileStatus.getProgress());
		}
	}
	
	public synchronized void updateTargetItem(TargetFileTreeItem item)
	{
		if (item != null)
		{
			FileStatus fileStatus = JobStatus.getFileStatus(item.getFile());
			//System.err.println(fileStatus.getStatus() + "\t" + item.getFile().getName());

			item.setStatus(fileStatus.getState());
		}
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
			selectedTargetFolder = folder;
			
			Parameter imageFolder = selectedAlgorithm.getParameter(Definition.IMAGE_FOLDER_PARAM);
			if (imageFolder != null)
			{
				//set the current algorithm's image folder to the selected one
				//this change should not be saved
				imageFolder.setValue(folder.getAbsolutePath());
			}
		}
	}
	
	public int getTotalFilesThisRun()
	{
		return totalFilesThisRun;
	}

	/* (non-Javadoc)
	 * @see system.ActiveComponent#shutdown()
	 */
	@Override
	public void shutdown()
	{
		shuttingDown = true;
	}

	/* (non-Javadoc)
	 * @see system.ActiveComponent#isShutdown()
	 */
	@Override
	public boolean isShutdown()
	{
		return true;
	}
}
