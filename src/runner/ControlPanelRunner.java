package runner;

import java.io.File;

import config.Configuration;

import logging.LogLevel;
import logging.Logger;

import util.Hashing;

import data.TrackingGroup;

import algorithms.Product;
import algorithms.ProductContents;
import algorithms.ProductFactory;
import algorithms.ProductFactoryRegistry;
import algorithms.ProductReader;

import gui.GUI;



public class ControlPanelRunner extends Runner{
	private BackupRunner backupRunner;
	
	private TrackingGroup curTrackingGroup;
	
	public ControlPanelRunner(GUI gui)
	{
		this.gui = gui;
		gui.setRunner(this);
		start();
	}

	private void start() {
		gui.runnerStartupMessage();
	}

	@Override
	public void runBackup() {
		if (backupRunner == null)
		{
			backupRunner = new BackupRunner(gui);
			backupRunner.setControlPanelRunner(this);
		}
		
		gui.setRunner(backupRunner);
		gui.showBackupPanel();
		backupRunner.runBackup();
	}

	@Override
	public void shutdown() {
		System.out.println("Control Panel shutdown");
		// TODO Auto-generated method stub
		
	}
	
	public ProductContents extractAll(File productFile)
	{
		TrackingGroup trackingGroup = getTrackingGroup();
		if (trackingGroup.getKeyHash() == null && trackingGroup.isSecure())
		{
			updateKeyHash(trackingGroup);
		}
		ProductFactory<? extends Product> factory = ProductFactoryRegistry.getProductFactory(
						trackingGroup.getName(), trackingGroup.getProductSecurityLevel(), 
						trackingGroup.getKeyHash());
		ProductReader reader = new ProductReader(factory);
		
		ProductContents productContents = reader.extractAll(productFile);
		if (productContents == null)
		{
			Logger.log(LogLevel.k_error, "Failed to read product file: " + productFile.getName());
		}
		
		return productContents;		
	}

	private TrackingGroup getTrackingGroup() {
		while(curTrackingGroup == null)
			curTrackingGroup = Configuration.findTrackingGroup(gui.promptTrackingGroup());
		
		return curTrackingGroup;
	}
}
