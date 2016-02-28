package algorithms.imageoverlay;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Manages input images for the image overlay. Decides which image to be given as
 * input to the algorithm.
 */
public class InputImageManager
{
	private File f_inputFolder;
	private List<File> f_contents;
	private int f_contentsIndex;
	private ConsumptionMode f_mode;
	private File f_doneFolder;
	private static Map<File, InputImageManager> f_managers;
	private static List<String> f_supportedImageTypes;
	
	static
	{
		f_managers = new HashMap<File, InputImageManager>();
		
		//standard imageio reader types:
		//png, jpg, jpeg, gif, bmp, wbmp
		f_supportedImageTypes = Arrays.asList(ImageIO.getReaderFormatNames());
	}

	/**
	 * Constructs an input image manager
	 * @param p_inputFolder The folder containing input images
	 * @param p_mode The way to handle used input images
	 */
	private InputImageManager(File p_inputFolder, ConsumptionMode p_mode)
	{
		this.f_inputFolder = p_inputFolder;
		f_contents = Arrays.asList(p_inputFolder.listFiles());
		f_contentsIndex = -1;
		
		this.f_mode = p_mode;
		
		if (p_mode.equals(ConsumptionMode.k_move))
		{
			f_doneFolder = new File(p_inputFolder, "done");
			if (!f_doneFolder.exists())
				f_doneFolder.mkdir();
		}
		
	}
	
	/**
	 * Gets a manager for the given input folder with the specified mode. Only one manager may
	 * exist per input folder. If there already exists a manager for the folder but it has a
	 * different mode, it will be replaced with a new manager with the specified mode.
	 * @param p_inputFolder The folder of input images
	 * @param p_mode The consumption mode
	 * @return The input image manager for this folder
	 */
	public static InputImageManager getInstance(File p_inputFolder, ConsumptionMode p_mode)
	{
		if (f_managers.containsKey(p_inputFolder.getAbsoluteFile()))
		{
			InputImageManager found = f_managers.get(p_inputFolder.getAbsoluteFile());
			if (found.f_mode == p_mode)
			{
				return found;
			}
		}

		InputImageManager created = new InputImageManager(p_inputFolder.getAbsoluteFile(), p_mode);
		f_managers.put(p_inputFolder.getAbsoluteFile(), created);
		return created;
	}

	/**
	 * Gets the next input image file
	 * @return The next image file
	 */
	public synchronized File nextImageFile()
	{
		File next = findNextImageFile();
		
		if (next == null)
		{
			//couldn't find an image file, try resetting the state
			//more files may have been added, or we're starting a new cycle
			f_contents = Arrays.asList(f_inputFolder.listFiles());
			f_contentsIndex = -1;

			//maybe it worked this time?
			//(someone could have since modified the file system)
			return findNextImageFile();
		}
		else
		{
			return next;
		}
	}
	
	/**
	 * Finds the next image file in the input folder
	 * @return The next image file, or null if none remain
	 */
	private File findNextImageFile()
	{
		while (++f_contentsIndex < f_contents.size())
		{
			File next = f_contents.get(f_contentsIndex);
			if (!next.isDirectory())
			{
				if (next.getName().contains("."))
				{
					String[] parts = next.getName().split("\\.");
					String extension = parts[parts.length-1];
					if (f_supportedImageTypes.contains(extension))
						return next;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Sets an image file as used, which will be handled differently depending on the
	 * consumption mode of this manager
	 * @param p_imageFile The file to set as used
	 * @throws IOException If the file cannot be moved or deleted when that is attempted.
	 * (usually because of permissions or if it no longer exists.)
	 */
	public synchronized void setFileUsed(File p_imageFile) throws IOException
	{
		if (f_mode.equals(ConsumptionMode.k_move))
		{
			Files.move(p_imageFile.toPath(), new File(f_doneFolder, p_imageFile.getName()).toPath());
		}
		else if (f_mode.equals(ConsumptionMode.k_delete))
		{
			Files.delete(p_imageFile.toPath());
		}
		
		//(k_cycle is implicitly handled)
	}
}
