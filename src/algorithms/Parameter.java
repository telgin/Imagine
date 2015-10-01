package algorithms;

import java.util.ArrayList;

import logging.LogLevel;
import logging.Logger;

public class Parameter {
	private ArrayList<Option> options;
	private String name;
	private String value;
	private boolean optional;
	private boolean enabled;
	
	public Parameter(String name, String value, boolean optional, boolean enabled)
	{
		options = new ArrayList<Option>();
		
		setName(name);
		setValue(value);
		setOptional(optional);
		setEnabled(enabled);
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
}
