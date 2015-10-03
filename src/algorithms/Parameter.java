package algorithms;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import logging.LogLevel;
import logging.Logger;

public class Parameter {
	private ArrayList<Option> options;
	private String name;
	private String type;
	private String value;
	private boolean optional;
	private boolean enabled;
	
	public Parameter(String name, String type, String value, boolean optional, boolean enabled)
	{
		options = new ArrayList<Option>();
		
		setName(name);
		setType(type);
		setValue(value);
		setOptional(optional);
		setEnabled(enabled);
	}
	
	public Parameter(String name, String type, String value, boolean optional)
	{
		this(name, type, value, optional, !optional);
	}
	
	public void addOption(Option opt)
	{
		options.add(opt);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (!enabled && !optional)
			Logger.log(LogLevel.k_error, "Cannot disable the required parameter: " + name);
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type.toLowerCase();
	}
	
	public Element toElement(Document doc)
	{
		Element element = doc.createElement("Parameter");
		element.setAttribute("name", name);
		element.setAttribute("type", type);
		element.setAttribute("value", value);
		element.setAttribute("optional", Boolean.toString(optional));
		
		if (optional)
			element.setAttribute("enabled", Boolean.toString(enabled));
		
		for (Option opt:options)
			element.appendChild(opt.toElement(doc));
		
		return element;
	}
	
	public void validate(Parameter other)
	{
		//for sanity, the optional states should always be the same
		if (optional != other.isOptional())
		{
			Logger.log(LogLevel.k_fatal,
					"Parameter " + other.getName() + 
					" does not have the same optional designation as the spec.");
		}
		
		//(mismatching optional/enabled states are handled upon parameter creation)
		
		//make sure the data types are the same
		if (!type.toLowerCase().equals(other.getType().toLowerCase()))
		{
			Logger.log(LogLevel.k_fatal,
					"Parameter " + other.getName() + " should be of type " + type);
		}
		
		//make sure the input value conforms to the constraints of the options:
		
		//for sanity, make sure the spec has at least one option
		if (options.size() == 0)
		{
			Logger.log(LogLevel.k_fatal,
					"The specification for parameter " + name + " has no options");
		}
			
		boolean found = false;
		for(Option opt:options)
		{
			if (opt.validate(other.getValue(), other.getType()))
			{
				found = true;
				break;
			}
		}
		
		if (!found)
		{
			Logger.log(LogLevel.k_fatal,
					"Parameter " + other.getName() + " has invalid value: " + other.getValue());
		}
	}
}
