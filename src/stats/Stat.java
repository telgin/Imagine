package stats;

public abstract class Stat
{
	private String name;
	private Double curDouble;
	private String curString;

	public Stat(String name)
	{
		setName(name);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getFormattedProgress()
	{
		String text = name + ": ";
		if (curDouble == null)
			if (curString == null)
				text += "null, null";
			else
				text += curString;
		else if (curString != null)
			text += curString + ", " + curDouble.toString();
		else
			text += curDouble.toString();

		return text;
	}

	protected Double getCurDouble()
	{
		return curDouble;
	}

	protected String getCurString()
	{
		return curString;
	}

	protected String getName()
	{
		return name;
	}

	public Double getNumericProgress()
	{
		return curDouble;
	}

	public String getStringProgress()
	{
		return curString;
	}

	public void updateProgress(double update)
	{
		curDouble = update;
	}

	public void updateProgress(String update)
	{
		curString = update;
	}

	public void incrementNumericProgress(double amount)
	{
		curDouble += amount;
	}

	public void decrementNumericProgress(double amount)
	{
		curDouble -= amount;
	}
}
