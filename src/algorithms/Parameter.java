package algorithms;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import logging.LogLevel;
import logging.Logger;
import ui.UIContext;
import util.ConfigUtil;

public class Parameter
{
	public static final String STRING_TYPE = "string";
	public static final String INT_TYPE = "int";
	public static final String LONG_TYPE = "long";
	public static final String BOOLEAN_TYPE = "boolean";
	public static final String DECIMAL_TYPE = "decimal";
	public static final String FILE_TYPE = "file";
	
	private List<Option> options;
	private String name;
	private String type;
	private String value;
	private String description;
	private boolean optional;
	private boolean enabled;

	public Parameter(String name, String type, boolean optional, boolean enabled)
	{
		options = new ArrayList<Option>();

		setName(name);
		setType(type);
		setOptional(optional);
		setEnabled(enabled);
		setDescription("");
	}
	
	@Override
	public Parameter clone()
	{
		Parameter clone = new Parameter(name, type, optional, enabled);
		clone.setDescription(description);
		for (Option opt : options)
			clone.addOption(opt.clone());
		
		clone.setValue(value);
		
		return clone;
	}

	public Parameter(Element paramElement)
	{
		options = new ArrayList<Option>();

		setName(paramElement.getAttribute("name"));
		setDescription(paramElement.getAttribute("description"));
		setType(paramElement.getAttribute("type"));
		setOptional(Boolean.parseBoolean(paramElement.getAttribute("optional")));
		setEnabled(Boolean.parseBoolean(paramElement.getAttribute("enabled")));

		for (Element optionNode : ConfigUtil.children(paramElement, "Option"))
		{
			options.add(new Option(optionNode));
		}
		
		setValue(paramElement.getAttribute("value"));
	}

	public void addOption(Option opt)
	{
		options.add(opt);
	}
	
	public List<Option> getOptions()
	{
		return options;
	}
	
	public List<String> getOptionDisplayValues()
	{
		List<String> displays = new ArrayList<String>();
		
		for (Option opt : options)
			displays.add(opt.toString());
		
		displays.sort(null);
		
		return displays;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		if (value != null && value.equals(Option.PROMPT_OPTION.getValue()))
		{
			value = UIContext.getUI().promptParameterValue(this);
		}
		
		return value;
	}

	public void setValue(String value)
	{
		if (validate(value))
			this.value = value;
	}

	public boolean isOptional()
	{
		return optional;
	}

	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		if (!enabled && !optional)
		{
			Logger.log(LogLevel.k_error,
							"Cannot disable the required parameter: " + name);
			this.enabled = true;
		}
		else
		{
			this.enabled = enabled;
		}
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.type = type.toLowerCase();
	}

	public Element toElement(Document doc)
	{
		Element element = doc.createElement("Parameter");
		element.setAttribute("name", name);
		element.setAttribute("description", description);
		element.setAttribute("type", type);
		element.setAttribute("value", value);
		element.setAttribute("optional", Boolean.toString(optional));
		element.setAttribute("enabled", Boolean.toString(enabled));

		for (Option opt : options)
			element.appendChild(opt.toElement(doc));

		return element;
	}

	public boolean validate(String value)
	{
		// make sure the input value conforms to the constraints of the options:

		// for sanity, make sure the spec has at least one option
		if (options.size() == 0)
		{
			Logger.log(LogLevel.k_fatal, "The specification for parameter " + name
							+ " has no options");
		}

		boolean found = false;
		for (Option opt : options)
		{
			if (opt.validate(value, type))
			{
				found = true;
				break;
			}
		}

		if (!found)
		{
			Logger.log(LogLevel.k_error, "Parameter " + name + " cannot be set to: " + value);
		}
		
		return found;
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
