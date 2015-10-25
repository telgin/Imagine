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

public class Algorithm {
	private String name;
	private int versionNum;
	private HashMap<String, Parameter> parameters;
	private ProductFactoryCreation productFactoryCreation;
	
	public Algorithm(String name, int versionNum)
	{
		this.name = name;
		this.versionNum = versionNum;
		parameters = new HashMap<String, Parameter>();
	}
	
	public Algorithm(Element algoNode)
	{
		this.name = algoNode.getAttribute("name");
		this.versionNum = Integer.parseInt(algoNode.getAttribute("version"));
		
		parameters = new HashMap<String, Parameter>();
		for (Element paramNode : ConfigUtil.children(algoNode, "Parameter"))
		{
			addParameter(new Parameter(paramNode));
		}
	}
	
	public void setProductFactoryCreation(ProductFactoryCreation creation)
	{
		productFactoryCreation = creation;
	}

	public List<Parameter> getParameters()
	{
		return new ArrayList<Parameter>(parameters.values());
	}
	
	public ProductMode getProductSecurityLevel()
	{
		return ProductMode.getMode(getParameterValue("ProductMode"));
	}
	
	public void setParameter(String name, String value)
	{
		Parameter param = parameters.get(name.toLowerCase());
		if (param == null)
			Logger.log(LogLevel.k_error, "No such parameter '" + name + "' in algorithm '" + this.name + "'");
		else
			param.setValue(value);
	}
	
	public void setParameterEnabled(String name, boolean enabled)
	{
		Parameter param = parameters.get(name.toLowerCase());
		if (param == null)
			Logger.log(LogLevel.k_error, "No such parameter '" + name + "' in algorithm '" + this.name + "'");
		else
			param.setEnabled(enabled);
	}

	public void addParameter(Parameter param)
	{
		parameters.put(param.getName().toLowerCase(), param);
	}

	public Parameter getParameter(String name) {
		return parameters.get(name.toLowerCase());
	}

	public String getName() {
		return name;
	}
	
	public int getVersion()
	{
		return versionNum;
	}

	public String getParameterValue(String name) {
		Parameter param = parameters.get(name.toLowerCase());
		if (param == null)
		{
			Logger.log(LogLevel.k_error, "No such parameter '" + name + "' in algorithm '" + this.name + "'");
			return null;
		}

		return param.getValue();
	}
	
	public Element toElement(Document doc)
	{
		Element element = doc.createElement("Algorithm");
		element.setAttribute("name", name);
		element.setAttribute("version", Integer.toString(versionNum));
		
		for (Parameter param:getParameters())
		{
			element.appendChild(param.toElement(doc));
		}
		
		return element;
	}
	
	/**
	 * Does the other algorithm have the same parameters, and are those
	 * parameter values within the bounds of my options?
	 * (this = spec, other = input algo)
	 * 
	 * Logs fatal errors against the other algorithm
	 * 
	 * @param other
	 */
	public void validate(Algorithm other)
	{
		String header = "Algorithm " + name + "v" + versionNum + " validation: ";
		String otherId = other.getName() + "v" + other.getVersion();
		Logger.log(LogLevel.k_info, header);
		
		//are the names and versions the same?
		if (!name.equals(other.getName()) || versionNum != other.getVersion())
			Logger.log(LogLevel.k_error,
					header + "validating against different algorithm " + otherId);
			
		//check parameters
		for (Parameter pOther : other.getParameters())
		{
			Parameter pSpec = parameters.get(pOther.getName());
			
			//the spec must contain this parameter
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
		
		//see if there are any non-optional parameters not covered
		for (Parameter pSpec:getParameters())
		{
			Parameter pOther = other.getParameter(pSpec.getName());
			if (pOther == null && !pSpec.isOptional())
			{
				Logger.log(LogLevel.k_fatal,
						header + "Parameter " + pSpec.getName() +
						" does not exist, but it's not optional.");
			}
		}
		
	}

	public ProductReaderFactory<? extends ProductReader> getProductReaderFactory(Key key) {
		return productFactoryCreation.createReader(this, key);
	}
	
	public ProductWriterFactory<? extends ProductWriter> getProductWriterFactory(Key key) {
		return productFactoryCreation.createWriter(this, key);
	}
}
