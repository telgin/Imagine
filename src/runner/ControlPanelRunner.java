package runner;

import java.io.File;

import config.Configuration;
import logging.LogLevel;
import logging.Logger;
import product.ProductContents;
import product.ProductReader;
import data.TrackingGroup;

public class ControlPanelRunner extends Runner{
	private BackupRunner backupRunner;
	
	private TrackingGroup curTrackingGroup;
	
	public ControlPanelRunner()
	{
	}

	public void start() {
		getActiveGUI().setRunner(this);
		getActiveGUI().runnerStartupMessage();
	}

	@Override
	public void runBackup() {
		if (backupRunner == null)
		{
			backupRunner = new BackupRunner();
			backupRunner.setControlPanelRunner(this);
		}
		
		getActiveGUI().setRunner(backupRunner);
		getActiveGUI().showBackupPanel();
		backupRunner.runBackup();
	}

	@Override
	public void shutdown() {
		System.out.println("Control Panel shutdown");
		// TODO Auto-generated method stub
		
	}
	
	public ProductContents extractAll(File productFile)
	{
		ProductReader reader = new ProductReader(getTrackingGroup().getProductFactory());
		
		ProductContents productContents = reader.extractAll(productFile);
		if (productContents == null)
		{
			Logger.log(LogLevel.k_error, "Failed to read product file: " + productFile.getName());
		}
		
		return productContents;		
	}

	private TrackingGroup getTrackingGroup() {
		while(curTrackingGroup == null)
			curTrackingGroup = Configuration.findTrackingGroup(getActiveGUI().promptTrackingGroup());
		
		return curTrackingGroup;
	}
}
