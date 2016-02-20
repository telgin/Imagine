package product;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileStatus
{
	private File file;
	private long bytesLeft;
	private ConversionJobFileStatus status;

	
	public FileStatus(File file)
	{
		this.file = file;
	}

	/**
	 * @return the file
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * @return the bytesLeft
	 */
	public long getBytesLeft()
	{
		return bytesLeft;
	}

	/**
	 * @param bytesLeft the bytesLeft to set
	 */
	public void setBytesLeft(long bytesLeft)
	{
		this.bytesLeft = bytesLeft;
	}

	/**
	 * @return the status
	 */
	public ConversionJobFileStatus getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ConversionJobFileStatus status)
	{
		this.status = status;
	}
	
	public double getProgress()
	{
		if (file.isDirectory())
			return 0;
		
		if (file.length() == 0)
			return 1;
		
		BigDecimal bytesWritten = BigDecimal.valueOf(file.length() - bytesLeft);
		
		return bytesWritten.divide(BigDecimal.valueOf(file.length()), 6, RoundingMode.HALF_UP).doubleValue();
	}
}
