package algorithms;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import logging.LogLevel;
import logging.Logger;

public class Option
{
	private String value;
	private String startRange;
	private String endRange;
	private String description;
	
	public static final Option PROMPT_OPTION = new Option("Prompt For Value", "Prompt for this value at run time.");

	public Option(String value, String description)
	{
		this.value = value;
		this.description = description;
	}
	
	public Option(String startRange, String endRange, String description)
	{
		this.startRange = startRange;
		this.endRange = endRange;
		this.description = description;
	}

	public Option(Element optionNode)
	{
		if (optionNode.hasAttribute("value"))
			this.value = optionNode.getAttribute("value");
		
		if (optionNode.hasAttribute("startRange"))
			this.startRange = optionNode.getAttribute("startRange");
		
		if (optionNode.hasAttribute("endRange"))
			this.endRange = optionNode.getAttribute("endRange");
	}

	@Override
	public Option clone()
	{
		Option clone = new Option(value, description);
		clone.startRange = startRange;
		clone.endRange = endRange;

		return clone;
	}
	
	public String toString()
	{
		if (value != null)
			return value;
		
		if (startRange != null && endRange != null)
			return "[" + startRange + ", " + endRange + "]";
		
		return "null";
	}
	
	/**
	 * @return the startRange
	 */
	public String getStartRange()
	{
		return startRange;
	}

	/**
	 * @return the endRange
	 */
	public String getEndRange()
	{
		return endRange;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	public Element toElement(Document doc)
	{
		Element element = doc.createElement("Option");

		if (value != null)
		{
			element.setAttribute("value", value);
		}
		else
		{
			element.setAttribute("startRange", startRange);
			element.setAttribute("endRange", endRange);
		}

		return element;
	}

	public boolean validate(String value, String type)
	{
		if (type.equals(Parameter.STRING_TYPE))
		{
			if (value != null)
			{
				return value.equals("*") || value.equals(value);
			}

			return false; // no ranges for strings
		}
		else if (type.equals(Parameter.INT_TYPE))
		{
			if (value != null)
			{
				return value.equals("*") || value.equals(value);
			}
			else
			{
				int myStartRange = startRange.equals("*") ? Integer.MIN_VALUE
								: Integer.parseInt(startRange);
				int myEndRange = startRange.equals("*") ? Integer.MAX_VALUE
								: Integer.parseInt(endRange);
				int vOtherInt = Integer.parseInt(value);

				return vOtherInt >= myStartRange && vOtherInt <= myEndRange;
			}
		}
		else if (type.equals(Parameter.BOOLEAN_TYPE))
		{
			if (value != null)
			{
				return value.equals("*") || value.equals(value);
			}

			return false; // no ranges for booleans
		}
		else if (type.equals(Parameter.LONG_TYPE))
		{
			if (value != null)
			{
				return value.equals("*") || value.equals(value);
			}
			else
			{
				long myStartRange = startRange.equals("*") ? Long.MIN_VALUE
								: Long.parseLong(startRange);
				long myEndRange = startRange.equals("*") ? Long.MAX_VALUE
								: Long.parseLong(endRange);
				long vOtherLong = Long.parseLong(value);

				return vOtherLong >= myStartRange && vOtherLong <= myEndRange;
			}
		}
		else if (type.equals(Parameter.FILE_TYPE))
		{
			//for now, any non-empty string could be valid
			return value != null && !value.isEmpty();
		}
		else
		{
			Logger.log(LogLevel.k_fatal, "Unrecognized data type: " + type);
			return false;
		}
	}
}
