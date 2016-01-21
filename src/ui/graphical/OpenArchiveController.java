package ui.graphical;

import java.io.File;
import java.io.IOException;
import java.util.List;

import algorithms.Algorithm;
import api.ConfigurationAPI;
import api.ConversionAPI;
import api.UsageException;
import data.FileKey;
import data.PasswordKey;
import data.TrackingGroup;
import logging.LogLevel;
import logging.Logger;
import product.ProductContents;
import product.ProductMode;
import ui.UIContext;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class OpenArchiveController
{
	private OpenArchiveView view;
	private GUI gui;
	
	//state variables
	private File file;
	private List<String> profiles;
	private List<String> algorithms;
	private TrackingGroup selectedProfile = null;
	private Algorithm selectedAlgorithm = null;
	
	//constants
	private final String tempProfileString = "Temporary Profile";
	private final String noSelectionString = "None Selected";
	
	private String defaultProfileSelection;
	private String defaultAlgorithmSelection;
		
	/**
	 * @update_comment
	 * @param file
	 */
	public OpenArchiveController(OpenArchiveView view)
	{
		this.view = view;
		this.gui = (GUI) UIContext.getUI();
		
		setInputFile(processArgs());
		
		reloadProfiles();
		setDefaultProfileSelection(tempProfileString);
		
		reloadAlgorithmPresets();
		setDefaultAlgorithmSelection(noSelectionString);
	}
	
	/**
	 * @update_comment
	 */
	private File processArgs()
	{
		int fileIndex = view.getParameters().getUnnamed().indexOf("-i") + 1;
		return new File(view.getParameters().getUnnamed().get(fileIndex));
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
	 * @update_comment
	 */
	private void reloadProfiles()
	{
		profiles = ConfigurationAPI.getTrackingGroupNames();
		profiles.add(0, tempProfileString);
	}

	/**
	 * @return the defaultProfileSelection
	 */
	public String getDefaultProfileSelection()
	{
		return defaultProfileSelection;
	}

	/**
	 * @param defaultProfileSelection the defaultProfileSelection to set
	 */
	public void setDefaultProfileSelection(String defaultProfileSelection)
	{
		this.defaultProfileSelection = defaultProfileSelection;
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
	 * @return the selectedProfile
	 */
	public TrackingGroup getSelectedProfile()
	{
		return selectedProfile;
	}

	/**
	 * @param selectedProfile the selectedProfile to set
	 */
	public void setSelectedProfile(TrackingGroup selectedProfile)
	{
		this.selectedProfile = selectedProfile;
	}

	/**
	 * @return the file
	 */
	public File getInputFile()
	{
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setInputFile(File file)
	{
		this.file = file;
	}
	
	void openArchive()
	{
		try
		{
			view.setOpenButtonEnabled(false);
			ProductContents productContents = ConversionAPI.openArchive(selectedProfile, file);
			view.setTableData(productContents.getFileContents());
		}
		catch (IOException | UsageException e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_error, e.getMessage());
			
			view.setOpenButtonEnabled(true);
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
				ConversionAPI.extractAll(getTrackingGroup(), file, extractionLocation);
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
	private TrackingGroup getTrackingGroup()
	{
		if (selectedProfile != null)
		{
			return selectedProfile;
		}
		else
		{
			//TODO
			return null;
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void extractSelected()
	{
		
	}
	
	public void algorithmSelected(int index)
	{
		if (index == 0)
		{
			//'no selection' selected
			view.setKeySectionEnabled(false);
		}
		else
		{
			try
			{
				selectedAlgorithm = ConfigurationAPI.getAlgorithmPreset(algorithms.get(index));
				ProductMode mode = selectedAlgorithm.getProductSecurityLevel();
				if (mode.equals(ProductMode.k_basic))
				{
					view.setKeySectionEnabled(false);
				}
				else
				{
					view.setKeySectionEnabled(true);
				}
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_error, e.getMessage());
				Logger.log(LogLevel.k_debug, e, false);
				
				view.setAlgorithmSelection(noSelectionString);
			}
		}
	}
	
	public void profileSelected(int index)
	{
		view.clearKeySection();
		
		if (index == 0)
		{
			//the temporary profile is selected
			view.setAlgorithmSelection(noSelectionString);
			view.setAlgorithmSelectionEnabled(true);
			selectedProfile = null;
			selectedAlgorithm = null;
		}
		else
		{
			String groupName = profiles.get(index);
			try
			{
				selectedProfile = ConfigurationAPI.getTrackingGroup(groupName);
				selectedAlgorithm = selectedProfile.getAlgorithm();
				
				view.setAlgorithmSelection(selectedAlgorithm.getPresetName());
				//TODO handle case where preset name not found
				
				
				if (selectedAlgorithm.getProductSecurityLevel().isSecured())
				{
					view.setKeySectionEnabled(true);
					
					if (selectedProfile.getKey() instanceof FileKey)
					{
						view.toggleKeySection();
						
						FileKey key = (FileKey) selectedProfile.getKey();
						view.setKeyFilePath(key.getKeyFile().getAbsolutePath());
						//TODO handle case where key file not found
					}
					else if(selectedProfile.getKey() instanceof PasswordKey)
					{
						view.togglePasswordSection();
						view.setPasswordPrompt(selectedProfile.getKey().getName());
					}
				}
				else
				{
					view.setKeySectionEnabled(false);
				}
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_error, e.getMessage());
				Logger.log(LogLevel.k_debug, e, false);
				
				view.setAlgorithmSelection(noSelectionString);
				view.clearKeySection();
				view.setKeySectionEnabled(false);
			}
			
			//disable algorithm selection b/c it's defined in the profile
			view.setAlgorithmSelectionEnabled(false);
		}
		
	}

	/**
	 * @return the profiles
	 */
	public List<String> getProfiles()
	{
		return profiles;
	}

	/**
	 * @param profiles the profiles to set
	 */
	public void setProfiles(List<String> profiles)
	{
		this.profiles = profiles;
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
	
}
