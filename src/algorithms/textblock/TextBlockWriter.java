package algorithms.textblock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import algorithms.Algorithm;
import logging.LogLevel;
import logging.Logger;
import product.ProductWriter;
import stats.ProgressMonitor;
import stats.Stat;

public class TextBlockWriter extends TextBlock implements ProductWriter{

	public TextBlockWriter(Algorithm algo) {
		super(algo);
	}
	
	@Override
	public void newProduct() {
		buffer = new byte[blockSize];
		index = 0;
	}
	
	@Override
	public boolean write(byte b) {
		///System.out.println("Wrote: " + 1);
		try
		{
			buffer[index++] = b;
			return true;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
	}

	@Override
	public int write(byte[] bytes, int offset, int length)
	{
		int toWrite = Math.min((buffer.length - 1) - index, length);
		System.arraycopy(bytes, offset, buffer, index, toWrite);
		index += toWrite;
		return toWrite;
		//System.out.println("Wrote: " + bytes.length);
	}

	@Override
	public void saveFile(File productStagingFolder, String filename) {
		try {
			File toSave = new File(productStagingFolder.getAbsolutePath() + "/" +
					filename + ".txt");
			Logger.log(LogLevel.k_info, "Saving product file: " + toSave.getAbsolutePath());
			Files.write(toSave.toPath(), buffer);
			
			//update progress
			Stat stat = ProgressMonitor.getStat("productsCreated");
			if (stat != null)
				stat.incrementNumericProgress(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
