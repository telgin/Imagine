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
import archive.CreationJob;
import config.Settings;
import javafx.application.Platform;
import key.FileKey;
import key.Key;
import key.PasswordKey;
import key.DefaultKey;
import logging.LogLevel;
import logging.Logger;
import report.FileStatus;
import report.JobStatus;
import system.ActiveComponent;
import system.SystemManager;
import ui.UIContext;
import ui.graphical.GUI;
import util.FileSystemUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class EmbedController implements ActiveComponent
{
	private EmbedView f_view;
	private GUI f_gui;
	
	//state variables
	private List<String> f_presetNames;
	private Algorithm f_selectedAlgorithm = null;
	private File f_selectedTargetFolder = null;
	private boolean f_structuredOutput = false;
	private boolean f_shuttingDown = false;
	private int f_totalFilesThisRun = 0;
	private int f_filesCreated = 0;
	private double f_creationProgress = 0;
	private Thread f_guiUpdateDaemon;
		
	/**
	 * @update_comment
	 * @param file
	 */
	public EmbedController(EmbedView p_view)
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
	 * @update_comment
	 * @return
	 */
	void chooseKeyFile()
	{
		File chosen = f_view.chooseFile();
		if (chosen != null)
		{
			f_view.setKeyFilePath(chosen.getAbsolutePath());
		}
	}

	/**
	 * @update_comment
	 * @return
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
	 * @update_comment
	 * @param p_index
	 */
	public void algorithmSelected(int p_index)
	{
		if (p_index == -1)
		{
			//nothing selected
			f_view.setKeySectionEnabled(false);
			f_view.setInputSectionEnabled(false);
			f_view.setTargetSectionEnabled(false);
			f_view.setCreateArchivesEnabled(false);
			f_selectedAlgorithm = null;
		}
		//else if the selected algorithm is a new selection...
		else if (f_selectedAlgorithm == null || !f_selectedAlgorithm.getPresetName().equals(f_presetNames.get(p_index)))
		{
			try
			{
				f_selectedAlgorithm = ConfigurationAPI.getAlgorithmPreset(f_presetNames.get(p_index));
				f_view.setKeySectionEnabled(true);
				f_view.setInputSectionEnabled(true);
				f_view.setCreateArchivesEnabled(true);
				
				//Enable the target section if the algorithm uses it
				//TODO create a better way to handle this (or update it once more algos exist)
				Parameter imageFolder = f_selectedAlgorithm.getParameter(Definition.IMAGE_FOLDER_PARAM);
				if (imageFolder != null)
				{
					f_view.setTargetSectionEnabled(true);
					
					//reflect the algorithm's target folder if the parameter is set and it exists
					if (imageFolder.getValue() != null && !imageFolder.getValue().equals(Option.PROMPT_OPTION.getValue()))
					{
						f_view.setTarget(new File(imageFolder.getValue()));
					}
				}
				else
				{
					//this algorithm doesn't require a target folder
					f_view.setTargetSectionEnabled(false);
				}
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
	 * @update_comment
	 * @param b
	 * @return
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
			
				//try to restore the changes that were made
				if (previousSelection != null && f_presetNames.contains(previousSelection.getPresetName()))
				{
					f_view.setAlgorithmSelection(previousSelection.getPresetName());
					f_selectedAlgorithm = previousSelection;
					
					//reset the target section
					Parameter imageFolder = f_selectedAlgorithm.getParameter(Definition.IMAGE_FOLDER_PARAM);
					if (imageFolder != null)
					{
						//reflect the set target folder if it exists
						if (!imageFolder.getValue().equals(Option.PROMPT_OPTION.getValue()))
						{
							f_view.setTarget(new File(imageFolder.getValue()));
						}
					}
				}
				else
				{
					f_view.setAlgorithmSelection(null);
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
		File file = f_view.chooseFile();

		if (file != null)
		{
			f_view.addInput(FileSystemUtil.relativizeByCurrentLocation(file));
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void inputAddFolderPressed()
	{
		File folder = f_view.chooseFolder();

		if (folder != null)
		{
			f_view.addInput(FileSystemUtil.relativizeByCurrentLocation(folder));
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void removeInputPressed()
	{
		f_view.removeSelectedInput();
		
		if (f_gui.hasErrors())
		{
			f_view.showErrors(f_gui.getErrors(), "input entry removal");
			f_gui.clearErrors();
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void browseOutputFolder()
	{
		File folder = f_view.chooseFolder();
		
		if (folder != null)
		{
			f_view.setOutputFolder(folder);
		}
	}

	/**
	 * @update_comment
	 * @param b
	 * @return
	 */
	public void structuredOutputChecked(boolean p_checked)
	{
		f_structuredOutput = p_checked;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void createArchivesPressed()
	{
		try
		{
			File outputFolder = f_view.getOutputFolder();
			
			if (outputFolder == null)
			{
				Logger.log(LogLevel.k_error, "An output folder must be chosen.");
			}
			else if (f_selectedAlgorithm.getParameter(Definition.IMAGE_FOLDER_PARAM) != null && 
							(f_selectedTargetFolder == null || !f_selectedTargetFolder.exists()))
			{
				Logger.log(LogLevel.k_error, "A valid target folder must be chosen.");
			}
			else
			{
				//collect the input files
				List<File> inputFiles = f_view.getInputFileList();
				f_totalFilesThisRun = 0;
				try
				{
					for (File input : inputFiles)
						f_totalFilesThisRun += FileSystemUtil.countEligableFiles(input);
				}
				catch (IOException e1)
				{
					Logger.log(LogLevel.k_debug, "Could not count files for an entry.");
					f_totalFilesThisRun = 0;
				}
				
				//update the target folder param, make sure it's still the one selected
				Parameter imageFolder = f_selectedAlgorithm.getParameter(Definition.IMAGE_FOLDER_PARAM);
				if (imageFolder != null)
				{
					//set the current algorithm's image folder to the selected one
					//this change should not be saved
					imageFolder.setValue(f_selectedTargetFolder.getAbsolutePath());
				}
			
				//reset the components so file status doesn't carry over from last run.
				SystemManager.reset();
				
				//set system wide settings
				Settings.setOutputFolder(f_view.getOutputFolder());
				Settings.setUsingStructuredOutput(f_structuredOutput);
				
				//report creation not supported in GUI currently
				Settings.setGenerateReport(false);
				
				//status tracking required in GUI to update progress bars
				Settings.setTrackFileStatus(true);

				CreationJob job = ConversionAPI.createArchives(inputFiles, f_selectedAlgorithm, getKey(), 1);
	
				if (f_guiUpdateDaemon == null || !f_guiUpdateDaemon.isAlive())
				{
					f_guiUpdateDaemon = new Thread(() -> updateCSS(job));
					f_guiUpdateDaemon.setDaemon(true);
					f_guiUpdateDaemon.start();
				}
			}
		}
		catch (Exception e)
		{
			Logger.log(LogLevel.k_error, "The creation job failed to run.");
			Logger.log(LogLevel.k_debug, e, false);
		}
		
		if (f_gui.hasErrors())
		{
			f_view.showErrors(f_gui.getErrors(), "creation");
			f_gui.clearErrors();
		}
	}
	
	/**
	 * @update_comment
	 * @param p_job
	 */
	public void updateCSS(CreationJob p_job)
	{
		int run = 0;
		while (!f_shuttingDown && !p_job.isFinished())
		{
			cssUpdateLoop(run++);
		}
		
		//finalize any progress that was made one last time
		if (!f_shuttingDown && p_job.isFinished())
		{
			cssUpdateLoop(run++);
		}
		
		if (f_gui.hasErrors())
		{
			Platform.runLater(() -> 
			{
				f_view.showErrors(f_gui.getErrors(), "creation");
				f_gui.clearErrors();
			});
		}
	}
	
	/**
	 * @update_comment
	 * @param p_run
	 */
	public void cssUpdateLoop(int p_run)
	{
		//update things if they're different
		
		//files created
		int currentFilesCreated = JobStatus.getArchivesCreated();
		if (currentFilesCreated != f_filesCreated)
		{
			f_filesCreated = JobStatus.getArchivesCreated();
			f_view.setFilesCreated(f_filesCreated);
		}
		
		//creation progress
		double currentCreationProgress = (double) JobStatus.getInputFilesProcessed() / f_totalFilesThisRun;
		if (f_creationProgress != currentCreationProgress)
		{
			f_creationProgress = currentCreationProgress;
			f_view.setCreationProgress(f_creationProgress);
		}
		
		//update input file item status/progress for visible/loaded cells
		for (InputFileTreeItem item : f_view.getActiveInputItems().values())
		{
			updateInputItem(item);
		}
		
		//update target file item status for visible/loaded cells
		for (TargetFileTreeItem item : f_view.getActiveTargetItems().values())
		{
			updateTargetItem(item);
		}
		
		//update the cell css
		f_view.updateAllActiveCells();
		
		try
		{
			Thread.sleep(50);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @update_comment
	 * @param p_item
	 */
	public synchronized void updateInputItem(InputFileTreeItem p_item)
	{
		if (p_item != null)
		{
			FileStatus fileStatus = JobStatus.getFileStatus(p_item.getFile());
			p_item.setStatus(fileStatus.getState());
			p_item.setProgress(fileStatus.getProgress());
		}
	}
	
	/**
	 * @update_comment
	 * @param p_item
	 */
	public synchronized void updateTargetItem(TargetFileTreeItem p_item)
	{
		if (p_item != null)
		{
			FileStatus fileStatus = JobStatus.getFileStatus(p_item.getFile());
			p_item.setStatus(fileStatus.getState());
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void targetSelectFolderPressed()
	{
		File folder = f_view.chooseFolder();
		
		if (folder != null)
		{
			f_view.setTarget(folder);
			f_selectedTargetFolder = folder;
		}
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public int getTotalFilesThisRun()
	{
		return f_totalFilesThisRun;
	}

	/* (non-Javadoc)
	 * @see system.ActiveComponent#shutdown()
	 */
	@Override
	public void shutdown()
	{
		f_shuttingDown = true;
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
