package runner;

import gui.GUI;

import java.io.File;

import util.Hashing;
import data.TrackingGroup;

public abstract class Runner {

	private static GUI currentGUI;
	
	public static void setActiveGUI(GUI gui)
	{
		currentGUI = gui;
	}
	
	public static GUI getActiveGUI()
	{
		return currentGUI;
	}

	public abstract void shutdown();
	
}
