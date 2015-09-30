package runner;

import gui.GUI;

import java.io.File;

import util.Hashing;
import data.TrackingGroup;

public abstract class Runner {

	protected GUI gui;
	
	public abstract void runBackup();

	public abstract void shutdown();
	
	protected void updateKeyHash(TrackingGroup trackingGroup) {
		if (trackingGroup.getKeyLocation() != null)
		{
			if (trackingGroup.getKeyLocation().exists() && !trackingGroup.getKeyLocation().isDirectory())
			{
				trackingGroup.setKeyHash(Hashing.hash(trackingGroup.getKeyLocation()));
			}
			else
			{
				File keyFileLocation = gui.promptKeyFileLocation(trackingGroup);
				//TODO: make a loop to make sure this always exists
				trackingGroup.setKeyHash(Hashing.hash(keyFileLocation));
				trackingGroup.setKeyLocation(keyFileLocation);
			}
		}
		else
		{
			trackingGroup.setKeyHash(Hashing.hash(gui.promptKey(trackingGroup).getBytes()));
		}
	}

}
