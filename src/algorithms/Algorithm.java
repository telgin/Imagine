package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import logging.LogLevel;
import logging.Logger;

public class Algorithm {
	
	//static storage variables
	private static HashMap<String, Algorithm> defaults;
	
	//instance variables
	private String name;
	private ProductMode securityLevel;
	private boolean usesDatabase;
	private HashMap<String, Parameter> parameters; 
	
	//define algorithm defaults
	static
	{
		defaults = new HashMap<String, Algorithm>();
		
		//TODO: get default algorithm / get algorithm definition
		//pull defaults and definitions from package specific Definition class
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
	
	
	private Algorithm(){}
	
	private void addParameter(Parameter param)
	{
		parameters.put(param.getName(), param);
	}
	
	public Algorithm getDefaultAlgorithm(String name)
	{
		return defaults.get(name);
	}
}
