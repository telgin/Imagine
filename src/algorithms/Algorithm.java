package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private String name;
	private int versionNum;
	private String description;
	private HashMap<String, Parameter> parameters;
	private ProductFactoryCreation productFactoryCreation;
	private String presetName;

	/**
	 * @update_comment
	 * @param name
	 * @param versionNum
	 */
	public Algorithm(String name, int versionNum, String description)
	{
		this.name = name;
		this.versionNum = versionNum;
		this.description = description;
		parameters = new HashMap<String, Parameter>();
	}

	/**
	 * @update_comment
	 * @param algoNode
	 */
	public Algorithm(Element algoNode)
	{
		name = algoNode.getAttribute("name");
		versionNum = Integer.parseInt(algoNode.getAttribute("version"));
		description = AlgorithmRegistry.getDefaultAlgorithm(name).getDescription();
		presetName = algoNode.getAttribute("presetName");

		parameters = new HashMap<String, Parameter>();
		for (Element paramNode : ConfigUtil.children(algoNode, "Parameter"))
		{
			addParameter(new Parameter(paramNode));
		}
	}

	@Override
	public Algorithm clone()
	{
		Algorithm clone = new Algorithm(name, versionNum, description);
		clone.setPresetName(presetName);
		for (Parameter param : parameters.values())
			clone.addParameter(param.clone());
		
		clone.setProductFactoryCreation(productFactoryCreation);
		
		return clone;
	}
	
	/**
	 * @update_comment
	 * @param creation
	 */
	public void setProductFactoryCreation(ProductFactoryCreation creation)
	{
		productFactoryCreation = creation;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public List<Parameter> getParameters()
	{
		return new ArrayList<Parameter>(parameters.values());
	}

	/**
	 * @update_comment
	 * @param name
	 * @param value
	 * @throws UsageException 
	 */
	public void setParameter(String name, String value) throws UsageException
	{
		Parameter param = parameters.get(name.toLowerCase());
		if (param == null)
			throw new UsageException("No such parameter '" + name + "' in algorithm '"
				+ this.name + "'");
		else if (!param.setValue(value))
			throw new UsageException("Could not set parameter'" + name + "' to value '" + value + "'");
	}

	/**
	 * @update_comment
	 * @param name
	 * @param enabled
	 */
	public void setParameterEnabled(String name, boolean enabled)
	{
		Parameter param = parameters.get(name.toLowerCase());
		if (param == null)
			Logger.log(LogLevel.k_error, "No such parameter '" + name + "' in algorithm '"
				+ this.name + "'");
		else
			param.setEnabled(enabled);
	}

	/**
	 * @update_comment
	 * @param param
	 */
	public void addParameter(Parameter param)
	{
		parameters.put(param.getName().toLowerCase(), param);
	}

	/**
	 * @update_comment
	 * @param name
	 * @return
	 */
	public Parameter getParameter(String name)
	{
		return parameters.get(name.toLowerCase());
	}

	/**
	 * @update_comment
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public int getVersion()
	{
		return versionNum;
	}

	/**
	 * @update_comment
	 * @param name
	 * @return
	 */
	public String getParameterValue(String name)
	{
		Parameter param = parameters.get(name.toLowerCase());
		if (param == null)
		{
			Logger.log(LogLevel.k_error, "No such parameter '" + name + "' in algorithm '" 
				+ this.name + "'");
			return null;
		}

		return param.getValue();
	}

	/**
	 * @update_comment
	 * @param doc
	 * @return
	 */
	public Element toElement(Document doc)
	{
		Element element = doc.createElement("Algorithm");
		element.setAttribute("name", name);
		element.setAttribute("version", Integer.toString(versionNum));
		element.setAttribute("presetName", presetName);

		for (Parameter param : getParameters())
		{
			element.appendChild(param.toElement(doc));
		}

		return element;
	}

	/**
	 * @update_comment
	 * @param key
	 * @return
	 */
	public ProductReaderFactory<? extends ProductReader> getProductReaderFactory(Key key)
	{
		return productFactoryCreation.createReader(this, key);
	}

	/**
	 * @update_comment
	 * @param key
	 * @return
	 */
	public ProductWriterFactory<? extends ProductWriter> getProductWriterFactory(Key key)
	{
		return productFactoryCreation.createWriter(this, key);
	}

	/**
	 * @return the presetName
	 */
	public String getPresetName()
	{
		return presetName;
	}

	/**
	 * @param presetName the presetName to set
	 */
	public void setPresetName(String presetName)
	{
		this.presetName = presetName;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
}
