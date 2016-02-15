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

	public static final Option PROMPT_OPTION = new Option("Prompt For Value");

	public Option(String value)
	{
		this.value = value;
	}
	
	public Option(String startRange, String endRange)
	{
		this.startRange = startRange;
		this.endRange = endRange;
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
		Option clone = new Option(value);
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
			if (this.value != null)
			{
				return this.value.equals("*") || this.value.equals(value);
			}

			return false; // no ranges for strings
		}
		else if (type.equals(Parameter.INT_TYPE))
		{
			if (this.value != null)
			{
				return this.value.equals("*") || this.value.equals(value);
			}
			else
			{
				try
				{
					int myStartRange = startRange.equals("*") ? Integer.MIN_VALUE
									: Integer.parseInt(startRange);
					int myEndRange = startRange.equals("*") ? Integer.MAX_VALUE
									: Integer.parseInt(endRange);
					int vOtherInt = Integer.parseInt(value);
	
					return vOtherInt >= myStartRange && vOtherInt <= myEndRange;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			}
		}
		else if (type.equals(Parameter.BOOLEAN_TYPE))
		{
			if (this.value != null)
			{
				return this.value.equals("*") || this.value.equals(value);
			}

			return false; // no ranges for booleans
		}
		else if (type.equals(Parameter.LONG_TYPE))
		{
			if (this.value != null)
			{
				return this.value.equals("*") || this.value.equals(value);
			}
			else
			{
				try
				{
					long myStartRange = startRange.equals("*") ? Long.MIN_VALUE
									: Long.parseLong(startRange);
					long myEndRange = startRange.equals("*") ? Long.MAX_VALUE
									: Long.parseLong(endRange);
					long vOtherLong = Long.parseLong(value);
	
					return vOtherLong >= myStartRange && vOtherLong <= myEndRange;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endRange == null) ? 0 : endRange.hashCode());
		result = prime * result + ((startRange == null) ? 0 : startRange.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Option other = (Option) obj;
		if (endRange == null)
		{
			if (other.endRange != null)
				return false;
		}
		else if (!endRange.equals(other.endRange))
			return false;
		if (startRange == null)
		{
			if (other.startRange != null)
				return false;
		}
		else if (!startRange.equals(other.startRange))
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}
}
