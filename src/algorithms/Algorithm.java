package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductFactoryCreation;
import product.ProductMode;
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
	private HashMap<String, Parameter> parameters;
	private ProductFactoryCreation productFactoryCreation;
	private String presetName;

	/**
	 * @update_comment
	 * @param name
	 * @param versionNum
	 */
	public Algorithm(String name, int versionNum)
	{
		this.name = name;
		this.versionNum = versionNum;
		parameters = new HashMap<String, Parameter>();
	}

	/**
	 * @update_comment
	 * @param algoNode
	 */
	public Algorithm(Element algoNode)
	{
		this.name = algoNode.getAttribute("name");
		this.versionNum = Integer.parseInt(algoNode.getAttribute("version"));
		this.presetName = algoNode.getAttribute("presetName");

		parameters = new HashMap<String, Parameter>();
		for (Element paramNode : ConfigUtil.children(algoNode, "Parameter"))
		{
			addParameter(new Parameter(paramNode));
		}
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
	 * @return
	 */
	public ProductMode getProductSecurityLevel()
	{
		return ProductMode.getMode(getParameterValue("ProductMode"));
	}

	/**
	 * @update_comment
	 * @param name
	 * @param value
	 */
	public void setParameter(String name, String value)
	{
		Parameter param = parameters.get(name.toLowerCase());
		if (param == null)
			Logger.log(LogLevel.k_error, "No such parameter '" + name + "' in algorithm '"
							+ this.name + "'");
		else
			param.setValue(value);
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
	 * Does the other algorithm have the same parameters, and are those
	 * parameter values within the bounds of my options? (this = spec, other =
	 * input algo)
	 * 
	 * Logs fatal errors against the other algorithm
	 * 
	 * @param other
	 */
	/**
	 * @update_comment
	 * @param other
	 */
	public void validate(Algorithm other)
	{
		String header = "Algorithm " + name + "v" + versionNum + " validation: ";
		String otherId = other.getName() + "v" + other.getVersion();
		Logger.log(LogLevel.k_info, header);

		// are the names and versions the same?
		if (!name.equals(other.getName()) || versionNum != other.getVersion())
			Logger.log(LogLevel.k_error,
							header + "validating against different algorithm " + otherId);

		// check parameters
		for (Parameter pOther : other.getParameters())
		{
			Parameter pSpec = parameters.get(pOther.getName());

			// the spec must contain this parameter
			if (pSpec == null)
			{
				Logger.log(LogLevel.k_fatal,
								header + "unknown parameter '" + pOther.getName() + "'");
			}

			if (pOther.isEnabled())
			{
				pSpec.validate(pOther);
			}
		}

		// see if there are any non-optional parameters not covered
		for (Parameter pSpec : getParameters())
		{
			Parameter pOther = other.getParameter(pSpec.getName());
			if (pOther == null && !pSpec.isOptional())
			{
				Logger.log(LogLevel.k_fatal, header + "Parameter " + pSpec.getName()
								+ " does not exist, but it's not optional.");
			}
		}

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
}
