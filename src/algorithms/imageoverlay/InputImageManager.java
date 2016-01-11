package algorithms.imageoverlay;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class InputImageManager
{
	private File inputFolder;
	private List<File> contents;
	private int contentsIndex;
	private ConsumptionMode mode;
	private File doneFolder;
	private static Map<File, InputImageManager> managers;
	private static List<String> supportedImageTypes;
	
	static
	{
		managers = new HashMap<File, InputImageManager>();
		
		//standard imageio reader types:
		//png, jpg, jpeg, gif, bmp, wbmp
		supportedImageTypes = Arrays.asList(ImageIO.getReaderFormatNames());
	}

	private InputImageManager(File inputFolder, ConsumptionMode mode)
	{
		this.inputFolder = inputFolder;
		contents = Arrays.asList(inputFolder.listFiles());
		contentsIndex = -1;
		
		this.mode = mode;
		
		if (mode.equals(ConsumptionMode.k_move))
		{
			doneFolder = new File(inputFolder, "done");
			if (!doneFolder.exists())
				doneFolder.mkdir();
		}
		
	}
	
	public static InputImageManager getInstance(File inputFolder, ConsumptionMode mode)
	{
		if (managers.containsKey(inputFolder.getAbsoluteFile()))
		{
			return managers.get(inputFolder.getAbsoluteFile());
		}
		else
		{
			InputImageManager created = new InputImageManager(inputFolder.getAbsoluteFile(), mode);
			managers.put(inputFolder.getAbsoluteFile(), created);
			return created;
		}
	}

	public synchronized File nextImageFile()
	{
		File next = findNextImageFile();
		
		if (next == null)
		{
			//couldn't find an image file, try resetting the state
			//more files may have been added, or we're starting a new cycle
			contents = Arrays.asList(inputFolder.listFiles());
			contentsIndex = -1;

			//maybe it worked this time?
			//(someone could have since modified the file system)
			return findNextImageFile();
		}
		else
		{
			return next;
		}
	}
	
	private File findNextImageFile()
	{
		while (++contentsIndex < contents.size())
		{
			File next = contents.get(contentsIndex);
			if (!next.isDirectory())
			{
				if (next.getName().contains("."))
				{
					String[] parts = next.getName().split("\\.");
					String extension = parts[parts.length-1];
					if (supportedImageTypes.contains(extension))
						return next;
				}
			}
		}
		
		return null;
	}
	
	public synchronized void setFileUsed(File imageFile) throws IOException
	{
		if (mode.equals(ConsumptionMode.k_move))
		{
			Files.move(imageFile.toPath(), new File(doneFolder, imageFile.getName()).toPath());
		}
		else if (mode.equals(ConsumptionMode.k_delete))
		{
			Files.delete(imageFile.toPath());
		}
		
		//(k_cycle is implicitly handled)
	}
}
