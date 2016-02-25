package ui.graphical.algorithmeditor;

import java.util.ArrayList;
import java.util.List;

import algorithms.Algorithm;
import algorithms.Option;
import algorithms.Parameter;
import api.ConfigurationAPI;
import api.UsageException;
import logging.LogLevel;
import logging.Logger;
import ui.UIContext;
import ui.graphical.GUI;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class AlgorithmEditorController
{
	private AlgorithmEditorView f_view;
	private GUI f_gui;
	
	private Algorithm f_storedAlgorithm;
	private Algorithm f_workingAlgorithm;
	private Parameter f_selectedParameter;
	private String f_optionValue;
	
	/**
	 * @update_comment
	 * @param p_view
	 */
	public AlgorithmEditorController(AlgorithmEditorView p_view)
	{
		f_view = p_view;
		f_gui = (GUI) UIContext.getUI();
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public List<String> getPresetNames()
	{
		return ConfigurationAPI.getAlgorithmPresetNames();
	}

	/**
	 * @update_comment
	 * @param p_index
	 */
	public void presetSelected(int p_index)
	{
		if (p_index == -1)
		{
			f_storedAlgorithm = null;
			f_workingAlgorithm = null;
		}
		else
		{
			String presetName = ConfigurationAPI.getAlgorithmPresetNames().get(p_index);
			try
			{
				f_storedAlgorithm = ConfigurationAPI.getAlgorithmPreset(presetName);
				
				//make a copy of the preset for editing
				f_workingAlgorithm = f_storedAlgorithm.clone();
				f_view.setPresetName(f_workingAlgorithm.getPresetName());
				f_view.setAlgorithmNames(ConfigurationAPI.getAlgorithmDefinitionNames());
				f_view.setEditsEnabled(true);
				f_view.setSelectedAlgorithm(f_workingAlgorithm.getName());
				f_view.setSelectedParameter(-1);
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_debug, e, false);
				Logger.log(LogLevel.k_error, e.getMessage());
			}
	
			if (f_gui.hasErrors())
			{
				f_view.showErrors(f_gui.getErrors(), "algorithm preset lookup");
				f_gui.clearErrors();
			}
		}
	}
	
	/**
	 * @update_comment
	 * @param p_index
	 */
	public void parameterSelected(int p_index)
	{
		f_view.removeParameterOptions();
		f_optionValue = "sdfgsdfgdsfg";
		
		if (p_index == -1)
		{
			f_selectedParameter = null;
			
			f_view.setParameterDescription(null);
		}
		else
		{
			String parameterName = getParameterNames().get(p_index);
			f_selectedParameter = f_workingAlgorithm.getParameter(parameterName);
			
			f_view.setParameterDescription(f_selectedParameter.getDescription());
			
			f_view.displayParameterOptions(f_selectedParameter);
		}
	}

	/**
	 * @update_comment
	 */
	public void createNewPressed()
	{
		String algoName = getAlgorithmNames().get(0);
		
		//set the working algorithm to the first default algorithm
		try
		{
			f_view.reset();
			
			f_storedAlgorithm = null;
			f_workingAlgorithm = ConfigurationAPI.getDefaultAlgorithm(algoName);
			f_view.setAlgorithmNames(ConfigurationAPI.getAlgorithmDefinitionNames());
			f_view.setEditsEnabled(true);
			f_view.setSelectedAlgorithm(algoName);
			f_view.setPresetName("New Algorithm Preset");
		}
		catch (UsageException e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_error, e.getMessage());
		}

		if (f_gui.hasErrors())
		{
			f_view.showErrors(f_gui.getErrors(), "new algorithm creation");
			f_gui.clearErrors();
		}

	}

	/**
	 * @update_comment
	 */
	public void savePressed()
	{
		if (f_workingAlgorithm != null)
		{
			if (f_storedAlgorithm == null)
			{
				try
				{
					//the working algorithm is a new preset, so just add it:
					
					//take the existing preset name
					f_workingAlgorithm.setPresetName(f_view.getPresetName());
					
					ConfigurationAPI.addNewAlgorithmPreset(f_workingAlgorithm);
				}
				catch (UsageException e)
				{
					Logger.log(LogLevel.k_debug, e, false);
					Logger.log(LogLevel.k_error, e.getMessage());
				}
			}
			else
			{
				//update the stored algorithm by deleting it and replacing it with the working copy
				try
				{
					ConfigurationAPI.deleteAlgorithmPreset(f_storedAlgorithm.getPresetName());
					
					try
					{
						ConfigurationAPI.addNewAlgorithmPreset(f_workingAlgorithm);
					}
					catch (UsageException e)
					{
						Logger.log(LogLevel.k_debug, e, false);
						Logger.log(LogLevel.k_error, e.getMessage());
						
						//failure adding working algorithm, so re-add stored algorithm
						ConfigurationAPI.addNewAlgorithmPreset(f_storedAlgorithm);
					}
				}
				catch (UsageException e)
				{
					Logger.log(LogLevel.k_debug, e, false);
					Logger.log(LogLevel.k_error, e.getMessage());
				}
			}
			
			if (f_gui.hasErrors())
			{
				f_view.showErrors(f_gui.getErrors(), "algorithm save");
				f_gui.clearErrors();
			}
			else
			{
				f_view.reset();
			}
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public List<String> getAlgorithmNames()
	{
		return ConfigurationAPI.getAlgorithmDefinitionNames();
	}

	/**
	 * @update_comment
	 * @param p_index
	 */
	public void algorithmSelected(int p_index)
	{
		if (p_index == -1)
		{
			f_workingAlgorithm = null;
		}
		else
		{
			String selectedName = ConfigurationAPI.getAlgorithmDefinitionNames().get(p_index);
			try
			{
				//set current algorithm unless it's the same algorithm type
				if (!f_workingAlgorithm.getName().equalsIgnoreCase(selectedName))
				{
					Algorithm defaultAlgorithm = ConfigurationAPI.getDefaultAlgorithm(selectedName);
					
					//keep the preset name
					defaultAlgorithm.setPresetName(f_workingAlgorithm.getPresetName());
					f_workingAlgorithm = defaultAlgorithm;
				}
	
				//set description
				f_view.setAlgorithmDescription(f_workingAlgorithm.getDescription());
			
				//set parameter list
				f_view.setParameterNames(getParameterNames());
				
				//un-select any selected item
				f_view.setSelectedParameter(-1);
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_debug, e, false);
				Logger.log(LogLevel.k_error, e.getMessage());
			}
	
			if (f_gui.hasErrors())
			{
				f_view.showErrors(f_gui.getErrors(), "algorithm definition lookup");
				f_gui.clearErrors();
			}
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public List<String> getParameterNames()
	{
		List<String> parameterNames = new ArrayList<String>();
		
		if (f_workingAlgorithm != null)
		{
			for (Parameter p : f_workingAlgorithm.getParameters())
				parameterNames.add(p.getName());
			parameterNames.sort(null);
		}
		
		return parameterNames;
	}

	/**
	 * @update_comment
	 * @param p_checked
	 */
	public void parameterEnabledChecked(boolean p_checked)
	{
		if (f_selectedParameter != null && p_checked != f_selectedParameter.isEnabled())
		{
			f_selectedParameter.setEnabled(p_checked);
			
			//errors may occur here if it gets disabled and it's not optional
			if (f_gui.hasErrors())
			{
				f_view.showErrors(f_gui.getErrors(), "algorithm definition lookup");
				f_gui.clearErrors();
			}
			
			//reflect the actual state of the parameter
			f_view.setParameterEnabled(f_selectedParameter.isEnabled());
		}
	}

	/**
	 * @update_comment
	 * @param p_index
	 */
	public void optionSelected(int p_index)
	{
		//get the string value of the selected option
		optionSelected(f_selectedParameter.getOptionDisplayValues().get(p_index));
	}
	
	/**
	 * @update_comment
	 * @param p_value
	 */
	public void optionSelected(String p_value)
	{
		boolean success = f_selectedParameter.setValue(p_value);
		f_view.setOptionSelectionErrorState(!success);
	}

	/**
	 * @update_comment
	 * @param p_checked
	 */
	public void promptOptionSelected(boolean p_checked)
	{
		f_view.setOptionSelectionEnabled(!p_checked);
		
		if (p_checked)
		{
			f_optionValue = f_selectedParameter.getValue();
			optionSelected(Option.PROMPT_OPTION.getValue());
		}
		else
		{
			if (f_optionValue != null && f_optionValue.equals(Option.PROMPT_OPTION.getValue()))
				f_optionValue = null;
			
			optionSelected(f_optionValue);
			f_optionValue = null;
		}
	}
}
