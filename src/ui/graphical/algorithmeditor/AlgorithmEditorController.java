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
	private AlgorithmEditorView view;
	private GUI gui;
	
	private Algorithm storedAlgorithm;
	private Algorithm workingAlgorithm;
	
	private Parameter selectedParameter;
	
	private String optionValue;
	
	public AlgorithmEditorController(AlgorithmEditorView view)
	{
		this.view = view;
		this.gui = (GUI) UIContext.getUI();
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
	 * @param i
	 * @return
	 */
	public void presetSelected(int i)
	{
		if (i == -1)
		{
			storedAlgorithm = null;
			workingAlgorithm = null;
		}
		else
		{
			String presetName = ConfigurationAPI.getAlgorithmPresetNames().get(i);
			try
			{
				storedAlgorithm = ConfigurationAPI.getAlgorithmPreset(presetName);
				
				//make a copy of the preset for editing
				workingAlgorithm = storedAlgorithm.clone();
				view.setPresetName(workingAlgorithm.getPresetName());
				view.setAlgorithmNames(ConfigurationAPI.getAlgorithmDefinitionNames());
				view.setEditsEnabled(true);
				view.setSelectedAlgorithm(workingAlgorithm.getName());
				view.setSelectedParameter(-1);
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_debug, e, false);
				Logger.log(LogLevel.k_error, e.getMessage());
			}
	
			if (gui.hasErrors())
			{
				view.showErrors(gui.getErrors(), "algorithm preset lookup");
				gui.clearErrors();
			}
		}
	}
	
	/**
	 * @update_comment
	 * @param i
	 * @return
	 */
	public void parameterSelected(int index)
	{
		view.removeParameterOptions();
		optionValue = "sdfgsdfgdsfg";
		
		if (index == -1)
		{
			selectedParameter = null;
			
			view.setParameterDescription(null);
		}
		else
		{
			String parameterName = getParameterNames().get(index);
			selectedParameter = workingAlgorithm.getParameter(parameterName);
			
			view.setParameterDescription(selectedParameter.getDescription());
			
			view.displayParameterOptions(selectedParameter);
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void createNewPressed()
	{
		String algoName = getAlgorithmNames().get(0);
		
		//set the working algorithm to the first default algorithm
		try
		{
			view.reset();
			
			storedAlgorithm = null;
			workingAlgorithm = ConfigurationAPI.getDefaultAlgorithm(algoName);
			view.setAlgorithmNames(ConfigurationAPI.getAlgorithmDefinitionNames());
			view.setEditsEnabled(true);
			view.setSelectedAlgorithm(algoName);
			view.setPresetName("New Algorithm Preset");
		}
		catch (UsageException e)
		{
			Logger.log(LogLevel.k_debug, e, false);
			Logger.log(LogLevel.k_error, e.getMessage());
		}

		if (gui.hasErrors())
		{
			view.showErrors(gui.getErrors(), "new algorithm creation");
			gui.clearErrors();
		}

	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public void savePressed()
	{
		if (workingAlgorithm != null)
		{
			if (storedAlgorithm == null)
			{
				try
				{
					//the working algorithm is a new preset, so just add it:
					
					//take the existing preset name
					workingAlgorithm.setPresetName(view.getPresetName());
					
					ConfigurationAPI.addNewAlgorithmPreset(workingAlgorithm);
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
					ConfigurationAPI.deleteAlgorithmPreset(storedAlgorithm.getPresetName());
					
					try
					{
						ConfigurationAPI.addNewAlgorithmPreset(workingAlgorithm);
					}
					catch (UsageException e)
					{
						Logger.log(LogLevel.k_debug, e, false);
						Logger.log(LogLevel.k_error, e.getMessage());
						
						//failure adding working algorithm, so re-add stored algorithm
						ConfigurationAPI.addNewAlgorithmPreset(storedAlgorithm);
					}
				}
				catch (UsageException e)
				{
					Logger.log(LogLevel.k_debug, e, false);
					Logger.log(LogLevel.k_error, e.getMessage());
				}
			}
			
			if (gui.hasErrors())
			{
				view.showErrors(gui.getErrors(), "algorithm save");
				gui.clearErrors();
			}
			else
			{
				view.reset();
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
	 * @return
	 */
	public void algorithmSelected(int index)
	{
		if (index == -1)
		{
			workingAlgorithm = null;
		}
		else
		{
			String selectedName = ConfigurationAPI.getAlgorithmDefinitionNames().get(index);
			try
			{
				//set current algorithm unless it's the same algorithm type
				if (!workingAlgorithm.getName().equalsIgnoreCase(selectedName))
				{
					Algorithm defaultAlgorithm = ConfigurationAPI.getDefaultAlgorithm(selectedName);
					
					//keep the preset name
					defaultAlgorithm.setPresetName(workingAlgorithm.getPresetName());
					workingAlgorithm = defaultAlgorithm;
				}
	
				//set description
				view.setAlgorithmDescription(workingAlgorithm.getDescription());
			
				//set parameter list
				view.setParameterNames(getParameterNames());
				
				//un-select any selected item
				view.setSelectedParameter(-1);
			}
			catch (UsageException e)
			{
				Logger.log(LogLevel.k_debug, e, false);
				Logger.log(LogLevel.k_error, e.getMessage());
			}
	
			if (gui.hasErrors())
			{
				view.showErrors(gui.getErrors(), "algorithm definition lookup");
				gui.clearErrors();
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
		
		if (workingAlgorithm != null)
		{
			for (Parameter p : workingAlgorithm.getParameters())
				parameterNames.add(p.getName());
			parameterNames.sort(null);
		}
		
		return parameterNames;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void parameterEnabledChecked(boolean checked)
	{
		if (selectedParameter != null && checked != selectedParameter.isEnabled())
		{
			selectedParameter.setEnabled(checked);
			
			//errors may occur here if it gets disabled and it's not optional
			if (gui.hasErrors())
			{
				view.showErrors(gui.getErrors(), "algorithm definition lookup");
				gui.clearErrors();
			}
			
			//reflect the actual state of the parameter
			view.setParameterEnabled(selectedParameter.isEnabled());
		}
	}


	/**
	 * @update_comment
	 * @param e
	 * @return
	 */
	public void optionSelected(int index)
	{
		//get the string value of the selected option
		optionSelected(selectedParameter.getOptionDisplayValues().get(index));
	}
	
	public void optionSelected(String value)
	{
		boolean success = selectedParameter.setValue(value);
		view.setOptionSelectionErrorState(!success);
	}


	/**
	 * @update_comment
	 * @param b
	 * @return
	 */
	public void promptOptionSelected(boolean checked)
	{
		view.setOptionSelectionEnabled(!checked);
		
		if (checked)
		{
			optionValue = selectedParameter.getValue();
			optionSelected(Option.PROMPT_OPTION.getValue());
		}
		else
		{
			if (optionValue != null && optionValue.equals(Option.PROMPT_OPTION.getValue()))
				optionValue = null;
			
			optionSelected(optionValue);
			optionValue = null;
		}
	}
}
