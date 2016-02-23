package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import api.UsageException;
import key.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductFactoryCreation;
import product.ProductReader;
import product.ProductReaderFactory;
import product.ProductWriter;
import product.ProductWriterFactory;
import util.ConfigUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class Algorithm
{
	private String f_name;
	private int f_versionNum;
	private String f_description;
	private Map<String, Parameter> f_parameters;
	private ProductFactoryCreation f_productFactoryCreation;
	private String f_presetName;


	/**
	 * @update_comment
	 * @param p_name
	 * @param p_versionNum
	 * @param p_description
	 */
	public Algorithm(String p_name, int p_versionNum, String p_description)
	{
		f_name = p_name;
		f_versionNum = p_versionNum;
		f_description = p_description;
		f_parameters = new HashMap<String, Parameter>();
	}

	/**
	 * @update_comment
	 * @param p_algoNode
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
		
		clone.setProductFactoryCreation(f_productFactoryCreation);
		
		return clone;
	}
	
	/**
	 * @update_comment
	 * @param p_creation
	 */
	public void setProductFactoryCreation(ProductFactoryCreation p_creation)
	{
		f_productFactoryCreation = p_creation;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public List<Parameter> getParameters()
	{
		return new ArrayList<Parameter>(f_parameters.values());
	}

	/**
	 * @update_comment
	 * @param p_name
	 * @param p_value
	 * @throws UsageException 
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
	 * @update_comment
	 * @param p_name
	 * @param p_enabled
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
	 * @update_comment
	 * @param p_param
	 */
	public void addParameter(Parameter p_param)
	{
		f_parameters.put(p_param.getName().toLowerCase(), p_param);
	}

	/**
	 * @update_comment
	 * @param p_name
	 * @return
	 */
	public Parameter getParameter(String p_name)
	{
		return f_parameters.get(p_name.toLowerCase());
	}

	/**
	 * @update_comment
	 * @return
	 */
	public String getName()
	{
		return f_name;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public int getVersion()
	{
		return f_versionNum;
	}

	/**
	 * @update_comment
	 * @param p_name
	 * @return
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
	 * @update_comment
	 * @param p_doc
	 * @return
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
	 * @update_comment
	 * @param p_key
	 * @return
	 */
	public ProductReaderFactory<? extends ProductReader> getProductReaderFactory(Key p_key)
	{
		return f_productFactoryCreation.createReader(this, p_key);
	}

	/**
	 * @update_comment
	 * @param p_key
	 * @return
	 */
	public ProductWriterFactory<? extends ProductWriter> getProductWriterFactory(Key p_key)
	{
		return f_productFactoryCreation.createWriter(this, p_key);
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
		this.f_presetName = p_presetName;
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
		this.f_description = p_description;
	}
}
