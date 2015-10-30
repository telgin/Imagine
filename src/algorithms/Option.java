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

	public Option(String value)
	{
		this.value = value;
	}

	public Option(Element optionNode)
	{
		this.value = optionNode.getAttribute("value");
	}

	public Option(String startRange, String endRange)
	{
		this.startRange = startRange;
		this.endRange = endRange;
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
			element.setAttribute("valueStartRange", startRange);
			element.setAttribute("valueEndRange", endRange);
		}

		return element;
	}

	public boolean validate(String vOther, String type)
	{
		if (type.equals("string"))
		{
			if (value != null)
			{
				return value.equals("*") || value.equals(vOther);
			}

			return false; // no ranges for strings
		}
		else if (type.equals("int"))
		{
			if (value != null)
			{
				return value.equals("*") || value.equals(vOther);
			}
			else
			{
				int myStartRange = startRange.equals("*") ? Integer.MIN_VALUE
								: Integer.parseInt(startRange);
				int myEndRange = startRange.equals("*") ? Integer.MAX_VALUE
								: Integer.parseInt(endRange);
				int vOtherInt = Integer.parseInt(vOther);

				return vOtherInt >= myStartRange && vOtherInt <= myEndRange;
			}
		}
		else if (type.equals("boolean"))
		{
			if (value != null)
			{
				return value.equals("*") || value.equals(vOther);
			}

			return false; // no ranges for booleans
		}
		else if (type.equals("long"))
		{
			if (value != null)
			{
				return value.equals("*") || value.equals(vOther);
			}
			else
			{
				long myStartRange = startRange.equals("*") ? Long.MIN_VALUE
								: Long.parseLong(startRange);
				long myEndRange = startRange.equals("*") ? Long.MAX_VALUE
								: Long.parseLong(endRange);
				long vOtherLong = Long.parseLong(vOther);

				return vOtherLong >= myStartRange && vOtherLong <= myEndRange;
			}
		}
		else
		{
			Logger.log(LogLevel.k_fatal, "Unrecognized data type: " + type);
			return false;
		}
	}
}
