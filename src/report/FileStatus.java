package report;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import archive.CreationJobFileState;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * The file status class stores information about the progress and state
 * of an input file as it is written to an archive file.
 */
public class FileStatus
{
	private static final long MIN_PROGRESS_UPDATE_WAIT = 100;
	private static final long MIN_STATE_UPDATE_WAIT = 100;
	private File f_file;
	private long f_bytesLeft;
	private CreationJobFileState f_state;
	private double f_progress;
	private Set<FileStatus> f_children;
	private Integer f_childrenCount;
	private long f_lastProgressUpdate;
	private long f_lastStateUpdate;
	
	/**
	 * Creates a file status object
	 * @param p_file The file this file status is for
	 */
	public FileStatus(File p_file)
	{
		f_file = p_file;
		f_state = CreationJobFileState.NOT_STARTED;
		
		f_lastProgressUpdate = 0;
		f_lastStateUpdate = 0;
		
		File parent = p_file.getParentFile();
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
		return f_file;
	}

	/**
	 * @return the bytesLeft
	 */
	public long getBytesLeft()
	{
		return f_bytesLeft;
	}

	/**
	 * @param p_bytesLeft the bytesLeft to set
	 */
	public void setBytesLeft(long p_bytesLeft)
	{
		f_bytesLeft = p_bytesLeft;
	}

	/**
	 * Gets the file state of this file which may be partially determined based on the
	 * states of children to this file status. This calculation is only redone if it
	 * has been long enough since the last time the calculation ran. The recursion is
	 * only one level deep.
	 * @return the state
	 */
	public synchronized CreationJobFileState getState()
	{
		if (!stateUpdateNeeded())
			return f_state;
		
		if (f_children != null)
		{
			f_state = null;
			boolean allNotStarted = true;
			boolean allFinished = true;
			List<FileStatus> currentChildren = new LinkedList<FileStatus>(f_children);
			for (FileStatus childStatus : currentChildren)
			{
				CreationJobFileState childState = childStatus.getState();
				if (childState == CreationJobFileState.ERRORED)
				{
					f_state = CreationJobFileState.ERRORED;
					break;
				}
				else if (childState == CreationJobFileState.WRITING)
				{
					f_state = CreationJobFileState.WRITING;
					break;
				}
				else if (childState == CreationJobFileState.PAUSED)
				{
					f_state = CreationJobFileState.PAUSED;
					break;
				}
				else if (childState != CreationJobFileState.NOT_STARTED)
				{
					allNotStarted = false;
				}
				else if (childState != CreationJobFileState.FINISHED)
				{
					allFinished = false;
				}
			}
			
			if (f_state == null)
			{
				if (allFinished && !allNotStarted)
					f_state = CreationJobFileState.FINISHED;
				else if (allNotStarted && !allFinished)
					f_state = CreationJobFileState.NOT_STARTED;
				else
					f_state = CreationJobFileState.WRITING;
			}
		}
		
		f_lastStateUpdate = System.currentTimeMillis();
		
		return f_state;
	}

	/**
	 * @param p_state the state to set
	 */
	public void setState(CreationJobFileState p_state)
	{
		f_state = p_state;
	}
	
	/**
	 * Gets the progress of a file, which is how many bytes have been written
	 * divided by the total number of bytes. If the file status represents
	 * a directory, the progress will be the average progress of all files
	 * or folders in the folder. The recursion for this calculation does
	 * not go beyond one level. Also, the calculation will only be done if
	 * it has been significantly long since the last time the calculation ran.
	 * @return The progress value for this file/folder
	 */
	public synchronized double getProgress()
	{
		if (!progressUpdateNeeded())
			return f_progress;

		//no children, or none added yet
		if (f_children == null)
		{
			if (f_file.isDirectory())
			{
				f_progress = 0;
			}
			else if (f_file.length() == 0)
			{
				f_progress = 1;
			}
			else
			{
				BigDecimal bytesWritten = BigDecimal.valueOf(f_file.length() - f_bytesLeft);
				f_progress = Math.max(f_progress, bytesWritten.divide(BigDecimal.valueOf(f_file.length()), 
								6, RoundingMode.HALF_UP).doubleValue());
			}
		}
		else // children, so calculate based on average of child progress
		{
			double progressSum = 0;
			List<FileStatus> currentChildren = new LinkedList<FileStatus>(f_children);
			for (FileStatus childStatus : currentChildren)
				progressSum += childStatus.getProgress();
			
			f_progress = Math.max(f_progress, progressSum / f_childrenCount);
		}
		
		f_lastProgressUpdate = System.currentTimeMillis();
		
		return f_progress;
	}
	
	/**
	 * Adds a child file status to this file status. A child file status
	 * means a file or folder that is part of the creation job and is contained
	 * within the folder represented by this file status.
	 * @param p_child The child file status
	 */
	public void addChild(FileStatus p_child)
	{
		if (f_childrenCount == null)
			f_childrenCount = f_file.list().length;
		
		if (f_children == null)
			f_children = new HashSet<FileStatus>();
		
		f_children.add(p_child);
	}
	
	/**
	 * Tells if this file status has children file status's.
	 * @return If this file status has children
	 */
	public boolean hasChildren()
	{
		return f_childrenCount != null;
	}
	
	/**
	 * Determines if a progress update is needed based on whether or not
	 * it has been long enough since the last progress update.
	 * @return If a progress update is needed
	 */
	private boolean progressUpdateNeeded()
	{
		return System.currentTimeMillis() - f_lastProgressUpdate >= MIN_PROGRESS_UPDATE_WAIT;
	}
	
	/**
	 * Determines if a state update is needed based on whether or not
	 * it has been long enough since the last state update.
	 * @return If a state update is needed
	 */
	private boolean stateUpdateNeeded()
	{
		return System.currentTimeMillis() - f_lastStateUpdate >= MIN_STATE_UPDATE_WAIT;
	}
}
