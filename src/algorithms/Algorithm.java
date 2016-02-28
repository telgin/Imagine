package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import api.UsageException;
import archive.ArchiveFactoryCreator;
import archive.ArchiveReader;
import archive.ArchiveReaderFactory;
import archive.ArchiveWriter;
import archive.ArchiveWriterFactory;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import util.ConfigUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The algorithm class holds information about a configured algorithm with
 * various parameters. Essentially, it's the container for a preset.
 * This class will create the reader and writer factories.
 */
public class Algorithm
{
	private String f_name;
	private int f_versionNum;
	private String f_description;
	private Map<String, Parameter> f_parameters;
	private ArchiveFactoryCreator f_archiveFactoryCreator;
	private String f_presetName;

	/**
	 * Constructs a blank algorithm of the type p_name
	 * @param p_name The algorithm definition name (not the preset name)
	 * @param p_versionNum The algorithm version number
	 * @param p_description The algorithm description text
	 */
	public Algorithm(String p_name, int p_versionNum, String p_description)
	{
		f_name = p_name;
		f_versionNum = p_versionNum;
		f_description = p_description;
		f_parameters = new HashMap<String, Parameter>();
	}

	/**
	 * Constructs an algorithm from a xml element
	 * @param p_algoNode The xml element to parse
	 */
	public Algorithm(Element p_algoNode)
	{
		f_name = p_algoNode.getAttribute("name");
		f_versionNum = Integer.parseInt(p_algoNode.getAttribute("version"));
		f_description = AlgorithmRegistry.getDefaultAlgorithm(f_name).getDescription();
		f_presetName = p_algoNode.getAttribute("presetName");

		f_parameters = new HashMap<String, Parameter>();
		for (Element paramNode : ConfigUtil.children(p_algoNode, "Parameter"))
		{
			addParameter(new Parameter(paramNode));
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Algorithm clone()
	{
		Algorithm clone = new Algorithm(f_name, f_versionNum, f_description);
		clone.setPresetName(f_presetName);
		for (Parameter param : f_parameters.values())
			clone.addParameter(param.clone());
		
		clone.setArchiveFactoryCreator(f_archiveFactoryCreator);
		
		return clone;
	}
	
	/**
	 * Sets the archive factory creator
	 * @param p_creator The creator to set
	 */
	public void setArchiveFactoryCreator(ArchiveFactoryCreator p_creator)
	{
		f_archiveFactoryCreator = p_creator;
	}

	/**
	 * Gets the list of parameters
	 * @return The list of parameters
	 */
	public List<Parameter> getParameters()
	{
		return new ArrayList<Parameter>(f_parameters.values());
	}

	/**
	 * Sets a parameter of the given name to the given value
	 * @param p_name The name of the parameter to set the value of
	 * @param p_value The value to set
	 * @throws UsageException If the value cannot be set
	 */
	public void setParameter(String p_name, String p_value) throws UsageException
	{
		Parameter param = f_parameters.get(p_name.toLowerCase());
		if (param == null)
			throw new UsageException("No such parameter '" + p_name + "' in algorithm '"
				+ this.f_name + "'");
		else if (!param.setValue(p_value))
			throw new UsageException("Could not set parameter'" + p_name + "' to value '" + p_value + "'");
	}

	/**
	 * Sets a parameter's enabled state
	 * @param p_name The name of the parameter
	 * @param p_enabled The new enabled state
	 */
	public void setParameterEnabled(String p_name, boolean p_enabled)
	{
		Parameter param = f_parameters.get(p_name.toLowerCase());
		if (param == null)
			Logger.log(LogLevel.k_error, "No such parameter '" + p_name + "' in algorithm '"
				+ this.f_name + "'");
		else
			param.setEnabled(p_enabled);
	}

	/**
	 * Adds a parameter to this algorithm
	 * @param p_param The parameter to add
	 */
	public void addParameter(Parameter p_param)
	{
		f_parameters.put(p_param.getName().toLowerCase(), p_param);
	}

	/**
	 * Gets the parameter by the given name
	 * @param p_name The parameter name to search for
	 * @return The parameter with the given name, or null if none exists
	 */
	public Parameter getParameter(String p_name)
	{
		return f_parameters.get(p_name.toLowerCase());
	}

	/**
	 * Gets the name of this algorithm (The algorithm definition name, not
	 * the preset name. Ex. 'image')
	 * @return The algorithm name
	 */
	public String getName()
	{
		return f_name;
	}

	/**
	 * Gets the version number of this algorithm
	 * @return The version number
	 */
	public int getVersion()
	{
		return f_versionNum;
	}

	/**
	 * Searches for a parameter with the given name and returns its value
	 * @param p_name The name to search for
	 * @return The parameter value, or null if no such parameter exists.
	 */
	public String getParameterValue(String p_name)
	{
		Parameter param = f_parameters.get(p_name.toLowerCase());
		if (param == null)
		{
			Logger.log(LogLevel.k_error, "No such parameter '" + p_name + "' in algorithm '" 
				+ this.f_name + "'");
			return null;
		}

		return param.getValue();
	}

	/**
	 * Creates an xml element to represent this algorithm
	 * @param p_doc The current xml document
	 * @return The xml element for this algorithm
	 */
	public Element toElement(Document p_doc)
	{
		Element element = p_doc.createElement("Algorithm");
		element.setAttribute("name", f_name);
		element.setAttribute("version", Integer.toString(f_versionNum));
		element.setAttribute("presetName", f_presetName);

		for (Parameter param : getParameters())
		{
			element.appendChild(param.toElement(p_doc));
		}

		return element;
	}

	/**
	 * Creates an archive reader factory
	 * @param p_key The key to be used
	 * @return The archive reader factory
	 */
	public ArchiveReaderFactory<? extends ArchiveReader> getArchiveReaderFactory(Key p_key)
	{
		return f_archiveFactoryCreator.createReader(this, p_key);
	}

	/**
	 * Creates an archive writer factory
	 * @param p_key The key to be used
	 * @return The archive writer factory
	 */
	public ArchiveWriterFactory<? extends ArchiveWriter> getArchiveWriterFactory(Key p_key)
	{
		return f_archiveFactoryCreator.createWriter(this, p_key);
	}

	/**
	 * @return the presetName
	 */
	public String getPresetName()
	{
		return f_presetName;
	}

	/**
	 * @param p_presetName the presetName to set
	 */
	public void setPresetName(String p_presetName)
	{
		f_presetName = p_presetName;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return f_description;
	}

	/**
	 * @param p_description the description to set
	 */
	public void setDescription(String p_description)
	{
		f_description = p_description;
	}
}
