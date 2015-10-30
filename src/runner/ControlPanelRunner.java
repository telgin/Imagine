package runner;

import java.io.File;

import config.Configuration;
import logging.LogLevel;
import logging.Logger;
import product.ProductContents;
import product.ProductExtractor;
import data.TrackingGroup;

public class ControlPanelRunner extends Runner
{
	private BackupRunner backupRunner;

	public ControlPanelRunner()
	{
	}

	public void start()
	{
		getActiveGUI().setRunner(this);
		getActiveGUI().runnerStartupMessage();
	}

	@Override
	public void shutdown()
	{
		Logger.log(LogLevel.k_debug, "Control Panel shutdown");
		// TODO Auto-generated method stub

	}

	public ProductContents extractAll(File productFile, TrackingGroup group)
	{
		ProductExtractor reader = new ProductExtractor(group.getProductReaderFactory());

		ProductContents productContents = reader.extractAll(productFile);
		if (productContents == null)
		{
			Logger.log(LogLevel.k_error,
							"Failed to read product file: " + productFile.getName());
		}

		return productContents;
	}
}
