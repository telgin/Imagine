package algorithms.stealthpng;

import java.io.File;

public class InputImageManager {
	private static File inputFolder = null;
	//private static 

	public static void setInputFolder(File folder)
	{
		if (inputFolder == null)
			inputFolder = folder;
	}
	
	public static void resetInputFolder()
	{
		inputFolder = null;
	}

	public static File nextImageFile() {
		return inputFolder.listFiles()[0];
	}
}
