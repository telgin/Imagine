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
 * @update_comment
 */
public class Parameter
{
	public static final String STRING_TYPE = "string";
	public static final String INT_TYPE = "int";
	public static final String LONG_TYPE = "long";
	public static final String BOOLEAN_TYPE = "boolean";
	public static final String DECIMAL_TYPE = "decimal";
	public static final String FILE_TYPE = "file";
	
	private List<Option> f_options;
	private String f_name;
	private String f_type;
	private String f_value;
	private String f_description;
	private boolean f_optional;
	private boolean f_enabled;

	/**
	 * @update_comment
	 * @param p_name
	 * @param p_type
	 * @param p_optional
	 * @param p_enabled
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
	 * @update_comment
	 * @param p_paramElement
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
	 * @update_comment
	 * @param p_opt
	 */
	public void addOption(Option p_opt)
	{
		f_options.add(p_opt);
	}
	
	/**
	 * @update_comment
	 * @return
	 */
	public List<Option> getOptions()
	{
		return f_options;
	}
	
	/**
	 * @update_comment
	 * @return
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
	 * @update_comment
	 * @return
	 */
	public String getName()
	{
		return f_name;
	}

	/**
	 * @update_comment
	 * @param p_name
	 */
	public void setName(String p_name)
	{
		this.f_name = p_name;
	}

	/**
	 * @update_comment
	 * @return
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
	 * @update_comment
	 * @param p_value
	 * @return
	 */
	public boolean setValue(String p_value)
	{
		boolean success = validate(p_value);
		
		if (success)
			this.f_value = p_value;
		
		return success;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public boolean isOptional()
	{
		return f_optional;
	}

	/**
	 * @update_comment
	 * @param p_optional
	 */
	public void setOptional(boolean p_optional)
	{
		this.f_optional = p_optional;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public boolean isEnabled()
	{
		return f_enabled;
	}

	/**
	 * @update_comment
	 * @param p_enabled
	 */
	public void setEnabled(boolean p_enabled)
	{
		if (!p_enabled && !f_optional)
		{
			Logger.log(LogLevel.k_error,
							"Cannot disable the required parameter: " + f_name);
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
		this.f_type = p_type.toLowerCase();
	}

	/**
	 * @update_comment
	 * @param p_doc
	 * @return
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
	 * @update_comment
	 * @param p_value
	 * @return
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
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.f_description = description;
	}
}
