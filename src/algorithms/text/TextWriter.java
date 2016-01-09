package algorithms.text;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import algorithms.Algorithm;
import data.Key;
import logging.LogLevel;
import logging.Logger;
import product.ProductIOException;
import product.ProductWriter;
import stats.ProgressMonitor;
import stats.Stat;
import util.ByteConversion;
import util.Constants;

public class TextWriter extends Text implements ProductWriter
{
	public TextWriter(Algorithm algo, Key key)
	{
		super(algo, key);
	}

	@Override
	public void newProduct()
	{
		buffer = new byte[blockSize];
		reset();
	}

	@Override
	public boolean write(byte b)
	{
		try
		{
			byte val = ByteConversion.intToByte(b ^ random.nextByte());
			buffer[order.next()] = val;
			return true;
		}
		catch (ProductIOException e)
		{
			return false;
		}
	}

	@Override
	public int write(byte[] bytes, int offset, int length)
	{
		Logger.log(LogLevel.k_debug, "Writing " + (length) + " bytes.");

		for (int x = offset; x < offset + length; ++x)
		{
			if (!write(bytes[x]))
				return x - offset;
		}

		return length;
	}

	@Override
	public void saveFile(File productStagingFolder, String filename)
	{

		// write random bytes to fill up the buffer
		fillToEnd();

		try
		{
			File toSave = new File(productStagingFolder.getAbsolutePath(), filename + ".txt");
			Logger.log(LogLevel.k_info,
							"Saving product file: " + toSave.getAbsolutePath());

			PrintWriter writer = new PrintWriter(toSave);
			if (algorithm.getParameter("encoding").getValue().equals(Definition.base64Encoding))
				writer.print(ByteConversion.bytesToBase64(buffer));
			else
				writer.print(ByteConversion.bytesToHex(buffer));

			writer.close();

			// update progress
			Stat stat = ProgressMonitor.getStat("productsCreated");
			if (stat != null)
				stat.incrementNumericProgress(1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @update_comment
	 */
	private void fillToEnd()
	{
		while (order.hasRemainingNumbers())
		{
			write(random.nextByte());
		}
	}
}
