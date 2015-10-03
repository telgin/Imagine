package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import logging.LogLevel;
import logging.Logger;

public class Algorithm {
	private String name;
	private int versionNum;
	private HashMap<String, Parameter> parameters;
	
	public Algorithm(String name, int versionNum)
	{
		this.name = name;
		this.versionNum = versionNum;
		parameters = new HashMap<String, Parameter>();
	}

	public List<Parameter> getParameters()
	{
		return new ArrayList<Parameter>(parameters.values());
	}
	
	public void setParameter(String name, String value)
	{
		Parameter param = parameters.get(name);
		if (param == null)
			Logger.log(LogLevel.k_error, "No such parameter '" + name + "' in algorithm '" + this.name + "'");
		else
			param.setValue(value);
	}
	
	public void setParameterEnabled(String name, boolean enabled)
	{
		Parameter param = parameters.get(name);
		if (param == null)
			Logger.log(LogLevel.k_error, "No such parameter '" + name + "' in algorithm '" + this.name + "'");
		else
			param.setEnabled(enabled);
	}

	public void addParameter(Parameter param)
	{
		parameters.put(param.getName(), param);
	}

	public Parameter getParameter(String name) {
		return parameters.get(name);
	}

	public String getName() {
		return name;
	}
	
	public int getVersion()
	{
		return versionNum;
	}

	public String getParameterValue(String string) {
		Parameter param = parameters.get(name);
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
}
