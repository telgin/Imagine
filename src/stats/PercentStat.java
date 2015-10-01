package stats;

import logging.LogLevel;
import logging.Logger;
import util.myUtilities;

public class PercentStat extends Stat{
	private double total;
	
	public PercentStat(String name)
	{
		super(name);
	}
	
	public void setTotal(double total)
	{
		this.total = total;
		if (this.total == 0)
			Logger.log(LogLevel.k_warning, "setting percent stat total to zero");
	}

	@Override
	public String getFormattedProgress() {
		if (total == 0)
			return getName() + ": Undefined";
		
		double percent = (double) getCurDouble() / total;
		return getName() + ": " + myUtilities.formatPercent(percent, 2);
	}
	
	@Override
	public Double getNumericProgress()
	{
		if (total == 0)
			return total;
		
		double percent = (double) getCurDouble() / total;
		return percent;
	}

}
