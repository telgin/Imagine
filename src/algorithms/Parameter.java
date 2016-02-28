package algorithms;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import logging.LogLevel;
import logging.Logger;
import ui.UIContext;
import util.ConfigUtil;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * This class represents an algorithm parameter. Parameters have options, and their
 * values are set by first checking against these options.
 */
public class Parameter
{
	//types of parameters
	public static final String STRING_TYPE = "string";
	public static final String INT_TYPE = "int";
	public static final String LONG_TYPE = "long";
	public static final String BOOLEAN_TYPE = "boolean";
	public static final String DECIMAL_TYPE = "decimal";
	public static final String FILE_TYPE = "file";
	
	//parameter components
	private List<Option> f_options;
	private String f_name;
	private String f_type;
	private String f_value;
	private String f_description;
	private boolean f_optional;
	private boolean f_enabled;

	/**
	 * Constructs an algorithm parameter
	 * @param p_name The name of the parameter
	 * @param p_type The type of parameter (which influences how values are validated)
	 * @param p_optional If this parameter is optional for the algorithm
	 * @param p_enabled If this parameter is enabled
	 */
	public Parameter(String p_name, String p_type, boolean p_optional, boolean p_enabled)
	{
		f_options = new ArrayList<Option>();

		setName(p_name);
		setType(p_type);
		setOptional(p_optional);
		setEnabled(p_enabled);
		setDescription("");
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Parameter clone()
	{
		Parameter clone = new Parameter(f_name, f_type, f_optional, f_enabled);
		clone.setDescription(f_description);
		for (Option opt : f_options)
			clone.addOption(opt.clone());
		
		clone.setValue(f_value);
		
		return clone;
	}

	/**
	 * Constructs a parameter from an xml element
	 * @param p_paramElement The parameter xml element
	 */
	public Parameter(Element p_paramElement)
	{
		f_options = new ArrayList<Option>();

		setName(p_paramElement.getAttribute("name"));
		setDescription(p_paramElement.getAttribute("description"));
		setType(p_paramElement.getAttribute("type"));
		setOptional(Boolean.parseBoolean(p_paramElement.getAttribute("optional")));
		setEnabled(Boolean.parseBoolean(p_paramElement.getAttribute("enabled")));

		for (Element optionNode : ConfigUtil.children(p_paramElement, "Option"))
		{
			f_options.add(new Option(optionNode));
		}
		
		setValue(p_paramElement.getAttribute("value"));
	}

	/**
	 * Adds an option to this parameter
	 * @param p_opt The option to add
	 */
	public void addOption(Option p_opt)
	{
		f_options.add(p_opt);
	}
	
	/**
	 * Gets the list of options for this parameter.
	 * @return The list of this parameter's options
	 */
	public List<Option> getOptions()
	{
		return f_options;
	}
	
	/**
	 * Gets the list of the display strings for each option of this parameter
	 * @return The list of option display strings
	 */
	public List<String> getOptionDisplayValues()
	{
		List<String> displays = new ArrayList<String>();
		
		for (Option opt : f_options)
			displays.add(opt.toString());
		
		displays.sort(null);
		
		return displays;
	}

	/**
	 * Gets the name of this parameter
	 * @return The parameter name
	 */
	public String getName()
	{
		return f_name;
	}

	/**
	 * Sets the name of this parameter
	 * @param p_name The name to set
	 */
	public void setName(String p_name)
	{
		f_name = p_name;
	}

	/**
	 * Gets the parameter's value. This may result in prompting the ui for the value if
	 * that option is the current value.
	 * @return The value of this parameter
	 */
	public String getValue()
	{
		if (f_value != null && f_value.equals(Option.PROMPT_OPTION.getValue()))
		{
			f_value = UIContext.getUI().promptParameterValue(this);
		}
		
		return f_value;
	}

	/**
	 * Attempts to set the value of this parameter. The value will be validated
	 * and the success status returned.
	 * @param p_value The value to set
	 * @return The success status of this operation.
	 */
	public boolean setValue(String p_value)
	{
		boolean success = validate(p_value);
		
		if (success)
			f_value = p_value;
		
		return success;
	}

	/**
	 * Tells if this parameter is optional
	 * @return If this parameter is optional
	 */
	public boolean isOptional()
	{
		return f_optional;
	}

	/**
	 * Sets the optional state of this parameter
	 * @param p_optional The optional state
	 */
	public void setOptional(boolean p_optional)
	{
		f_optional = p_optional;
	}

	/**
	 * Tells if this parameter is enabled
	 * @return The enabled state
	 */
	public boolean isEnabled()
	{
		return f_enabled;
	}

	/**
	 * Sets the enabled state of this parameter
	 * @param p_enabled The enabled state
	 */
	public void setEnabled(boolean p_enabled)
	{
		if (!p_enabled && !f_optional)
		{
			Logger.log(LogLevel.k_error, "Cannot disable the "
				+ "required parameter: " + f_name);
			this.f_enabled = true;
		}
		else
		{
			this.f_enabled = p_enabled;
		}
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return f_type;
	}

	/**
	 * @param p_type the type to set
	 */
	public void setType(String p_type)
	{
		f_type = p_type.toLowerCase();
	}

	/**
	 * Creates an xml element representing this parameter
	 * @param p_doc The xml document
	 * @return The xml element for this parameter
	 */
	public Element toElement(Document p_doc)
	{
		Element element = p_doc.createElement("Parameter");
		element.setAttribute("name", f_name);
		element.setAttribute("description", f_description);
		element.setAttribute("type", f_type);
		element.setAttribute("value", f_value);
		element.setAttribute("optional", Boolean.toString(f_optional));
		element.setAttribute("enabled", Boolean.toString(f_enabled));

		for (Option opt : f_options)
			element.appendChild(opt.toElement(p_doc));

		return element;
	}

	/**
	 * Validates if the given string could be a valid parameter value selection. It is
	 * valid if it is valid for at least one option.
	 * @param p_value The parameter value to validate
	 * @return True if valid, false otherwise
	 */
	private boolean validate(String p_value)
	{
		// make sure the input value conforms to the constraints of the options:

		// for sanity, make sure the spec has at least one option
		if (f_options.size() == 0)
		{
			Logger.log(LogLevel.k_fatal, "The specification for parameter " + f_name
							+ " has no options");
			return false;
		}
		else
		{
			boolean found = false;
			for (Option opt : f_options)
			{
				if (opt.validate(p_value, f_type))
				{
					found = true;
					break;
				}
			}
			
			return found;
		}
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
		f_description = p_description;
	}
}
