package product;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class FileStatus
{
	private static final long MIN_PROGRESS_UPDATE_WAIT = 100;
	private static final long MIN_STATE_UPDATE_WAIT = 100;
	private File file;
	private long bytesLeft;
	private ConversionJobFileState state;
	private double progress;
	private Set<FileStatus> children;
	private Integer childrenCount;
	private long lastProgressUpdate = 0;
	private long lastStateUpdate = 0;

	
	public FileStatus(File file)
	{
		this.file = file;
		state = ConversionJobFileState.NOT_STARTED;
		
		File parent = file.getParentFile();
		if (parent != null)
		{
			JobStatus.getFileStatus(parent).addChild(this);
		}
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
	public synchronized ConversionJobFileState getState()
	{
		if (!stateUpdateNeeded())
			return state;
		
		if (children != null)
		{
			state = null;
			boolean allNotStarted = true;
			boolean allFinished = true;
			List<FileStatus> currentChildren = new LinkedList<FileStatus>(children);
			for (FileStatus childStatus : currentChildren)
			{
				ConversionJobFileState childState = childStatus.getState();
				if (childState == ConversionJobFileState.ERRORED)
				{
					state = ConversionJobFileState.ERRORED;
					break;
				}
				else if (childState == ConversionJobFileState.WRITING)
				{
					state = ConversionJobFileState.WRITING;
					break;
				}
				else if (childState == ConversionJobFileState.PAUSED)
				{
					state = ConversionJobFileState.PAUSED;
					break;
				}
				else if (childState != ConversionJobFileState.NOT_STARTED)
				{
					allNotStarted = false;
				}
				else if (childState != ConversionJobFileState.FINISHED)
				{
					allFinished = false;
				}
			}
			
			if (state == null)
			{
				if (allFinished && !allNotStarted)
					state = ConversionJobFileState.FINISHED;
				else if (allNotStarted && !allFinished)
					state = ConversionJobFileState.NOT_STARTED;
				else
					state = ConversionJobFileState.WRITING;
			}
		}
		
		lastStateUpdate = System.currentTimeMillis();
		
		return state;
	}

	/**
	 * @param status the status to set
	 */
	public void setState(ConversionJobFileState state)
	{
		this.state = state;
	}
	
	public synchronized double getProgress()
	{
		if (!progressUpdateNeeded())
			return progress;

		//no children, or none added yet
		if (children == null)
		{
			if (file.isDirectory())
			{
				progress = 0;
			}
			else if (file.length() == 0)
			{
				progress = 1;
			}
			else
			{
				BigDecimal bytesWritten = BigDecimal.valueOf(file.length() - bytesLeft);
				progress = Math.max(progress, bytesWritten.divide(BigDecimal.valueOf(file.length()), 
								6, RoundingMode.HALF_UP).doubleValue());
			}
		}
		else // children, so calculate based on average of child progress
		{
			double progressSum = 0;
			List<FileStatus> currentChildren = new LinkedList<FileStatus>(children);
			for (FileStatus childStatus : currentChildren)
				progressSum += childStatus.getProgress();
			
			progress = Math.max(progress, progressSum / childrenCount);
		}
		
		lastProgressUpdate = System.currentTimeMillis();
		
		return progress;
	}
	
	public void addChild(FileStatus child)
	{
		if (childrenCount == null)
			childrenCount = file.list().length;
		
		if (children == null)
			children = new HashSet<FileStatus>();
		
		children.add(child);
	}
	
	public boolean hasChildren()
	{
		return childrenCount != null;
	}
	
	private boolean progressUpdateNeeded()
	{
		return System.currentTimeMillis() - lastProgressUpdate >= MIN_PROGRESS_UPDATE_WAIT;
	}
	
	private boolean stateUpdateNeeded()
	{
		return System.currentTimeMillis() - lastStateUpdate >= MIN_STATE_UPDATE_WAIT;
	}
}
