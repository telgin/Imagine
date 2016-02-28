package algorithms;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A class which represents an option for a parameter value
 */
public class Option
{
	private String f_value;
	private String f_startRange;
	private String f_endRange;

	public static final Option PROMPT_OPTION = new Option("Prompt For Value");

	/**
	 * Creates an option which represents choosing the specified value.
	 * @param p_value The value text
	 */
	public Option(String p_value)
	{
		f_value = p_value;
	}
	
	/**
	 * Creates an option which represents a range of values to choose from [start,end]
	 * @param p_startRange The start of the range (inclusive)
	 * @param p_endRange The end of the range (inclusive)
	 */
	public Option(String p_startRange, String p_endRange)
	{
		f_startRange = p_startRange;
		f_endRange = p_endRange;
	}

	/**
	 * Creates an option object from an xml element
	 * @param p_optionNode The xml element of an option
	 */
	public Option(Element p_optionNode)
	{
		if (p_optionNode.hasAttribute("value"))
			f_value = p_optionNode.getAttribute("value");
		
		if (p_optionNode.hasAttribute("startRange"))
			f_startRange = p_optionNode.getAttribute("startRange");
		
		if (p_optionNode.hasAttribute("endRange"))
			f_endRange = p_optionNode.getAttribute("endRange");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Option clone()
	{
		Option clone = new Option(f_value);
		clone.f_startRange = f_startRange;
		clone.f_endRange = f_endRange;

		return clone;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		if (f_value != null)
			return f_value;
		
		if (f_startRange != null && f_endRange != null)
			return "[" + f_startRange + ", " + f_endRange + "]";
		
		return "null";
	}
	
	/**
	 * @return the startRange
	 */
	public String getStartRange()
	{
		return f_startRange;
	}

	/**
	 * @return the endRange
	 */
	public String getEndRange()
	{
		return f_endRange;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return f_value;
	}

	/**
	 * Creates an xml element from this option object
	 * @param p_doc The current xml document
	 * @return The xml element representing this option
	 */
	public Element toElement(Document p_doc)
	{
		Element element = p_doc.createElement("Option");

		if (f_value != null)
		{
			element.setAttribute("value", f_value);
		}
		else
		{
			element.setAttribute("startRange", f_startRange);
			element.setAttribute("endRange", f_endRange);
		}

		return element;
	}

	/**
	 * Validates user input against this option.
	 * @param p_value The user input
	 * @param p_type The type of parameter value this option is for
	 * @return True if the value would be equivalent to choosing this option,
	 * false otherwise.
	 */
	public boolean validate(String p_value, String p_type)
	{
		if (p_type.equals(Parameter.STRING_TYPE))
		{
			if (this.f_value != null)
			{
				return this.f_value.equals("*") || this.f_value.equals(p_value);
			}

			return false; // no ranges for strings
		}
		else if (p_type.equals(Parameter.INT_TYPE))
		{
			if (this.f_value != null)
			{
				return this.f_value.equals("*") || this.f_value.equals(p_value);
			}
			else
			{
				try
				{
					int myStartRange = f_startRange.equals("*") ? Integer.MIN_VALUE
									: Integer.parseInt(f_startRange);
					int myEndRange = f_startRange.equals("*") ? Integer.MAX_VALUE
									: Integer.parseInt(f_endRange);
					int vOtherInt = Integer.parseInt(p_value);
	
					return vOtherInt >= myStartRange && vOtherInt <= myEndRange;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			}
		}
		else if (p_type.equals(Parameter.BOOLEAN_TYPE))
		{
			if (this.f_value != null)
			{
				return this.f_value.equals("*") || this.f_value.equals(p_value);
			}

			return false; // no ranges for booleans
		}
		else if (p_type.equals(Parameter.LONG_TYPE))
		{
			if (this.f_value != null)
			{
				return this.f_value.equals("*") || this.f_value.equals(p_value);
			}
			else
			{
				try
				{
					long myStartRange = f_startRange.equals("*") ? Long.MIN_VALUE
									: Long.parseLong(f_startRange);
					long myEndRange = f_startRange.equals("*") ? Long.MAX_VALUE
									: Long.parseLong(f_endRange);
					long vOtherLong = Long.parseLong(p_value);
	
					return vOtherLong >= myStartRange && vOtherLong <= myEndRange;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			}
		}
		else if (p_type.equals(Parameter.FILE_TYPE))
		{
			//for now, any non-empty string could be valid
			return p_value != null && !p_value.isEmpty();
		}
		else
		{
			Logger.log(LogLevel.k_fatal, "Unrecognized data type: " + p_type);
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
		result = prime * result + ((f_endRange == null) ? 0 : f_endRange.hashCode());
		result = prime * result + ((f_startRange == null) ? 0 : f_startRange.hashCode());
		result = prime * result + ((f_value == null) ? 0 : f_value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object p_obj)
	{
		if (this == p_obj)
			return true;
		if (p_obj == null)
			return false;
		if (getClass() != p_obj.getClass())
			return false;
		Option other = (Option) p_obj;
		if (f_endRange == null)
		{
			if (other.f_endRange != null)
				return false;
		}
		else if (!f_endRange.equals(other.f_endRange))
			return false;
		if (f_startRange == null)
		{
			if (other.f_startRange != null)
				return false;
		}
		else if (!f_startRange.equals(other.f_startRange))
			return false;
		if (f_value == null)
		{
			if (other.f_value != null)
				return false;
		}
		else if (!f_value.equals(other.f_value))
			return false;
		return true;
	}
}
