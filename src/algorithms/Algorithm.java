package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import logging.LogLevel;
import logging.Logger;

public class Algorithm {
	private String name;
	private HashMap<String, Parameter> parameters;
	
	public Algorithm(String name)
	{
		this.name = name;
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

	public String getName() {
		return name;
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
}
