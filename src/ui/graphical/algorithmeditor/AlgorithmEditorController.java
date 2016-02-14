package ui.graphical.algorithmeditor;

import java.util.ArrayList;
import java.util.List;

import algorithms.Algorithm;
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
	public void presetSelected(Integer i)
	{
		String presetName = ConfigurationAPI.getAlgorithmPresetNames().get(i);
		try
		{
			storedAlgorithm = ConfigurationAPI.getAlgorithmPreset(presetName);
			
			//make a copy of the preset for editing
			workingAlgorithm = storedAlgorithm.clone();
			view.setPresetName(workingAlgorithm.getPresetName());
			view.setSelectedAlgorithm(workingAlgorithm.getName());
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
	
	/**
	 * @update_comment
	 * @param i
	 * @return
	 */
	public void parameterSelected(int index)
	{
		view.removeParameterOptions();
		
		if (index == -1)
		{
			selectedParameter = null;
			
			view.setParameterDescription("");
			view.setParameterEnabled(false);
			view.allowParameterEnabledChange(true);
		}
		else
		{
			String parameterName = getParameterNames().get(index);
			selectedParameter = workingAlgorithm.getParameter(parameterName);
			
			view.setParameterDescription(selectedParameter.getDescription());
			view.setParameterEnabled(selectedParameter.isEnabled());
			view.allowParameterEnabledChange(selectedParameter.isOptional());
			
			view.displayParameterOptions(selectedParameter);
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public void createNewPressed()
	{
		//set to the first algorithm type, this will
		//set the working algorithm
		view.setSelectedAlgorithm(getAlgorithmNames().get(0));
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public void savePressed()
	{
		
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
		
	}
	
	public void optionSelected(String value)
	{
		
	}
}
