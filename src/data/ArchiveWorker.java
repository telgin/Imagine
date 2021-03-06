package data;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import archive.ArchiveWriter;
import archive.ArchiveWriterFactory;
import archive.CreationJobFileState;
import archive.FileOutputManager;
import archive.ArchiveLoader;
import config.Settings;
import logging.LogLevel;
import logging.Logger;
import report.JobStatus;

/**
 * Dequeues from the given queue and loads files to the ArchiveLoader might not
 * need this class
 */
public class ArchiveWorker implements Runnable
{
	private boolean f_stopping = false;
	private BlockingQueue<Metadata> f_queue;
	private ArchiveLoader f_loader;

	/**
	 * Constructs an archive worker
	 * @param p_queue The queue to get file metadata from
	 * @param p_factory The writer factory which can create archive writers
	 * @param p_manager The file output manager which tells where files should
	 * be written to.
	 */
	public ArchiveWorker(BlockingQueue<Metadata> p_queue, 
		ArchiveWriterFactory<? extends ArchiveWriter> p_factory, FileOutputManager p_manager)
	{
		this.f_queue = p_queue;
		f_loader = new ArchiveLoader(p_factory, p_manager);
	}

	/**
	 * Tells if this archive worker is actively writing archive files
	 * @return If this worker is active
	 */
	public boolean isActive()
	{
		return !f_queue.isEmpty() && !f_stopping;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		int count = 0;
		while (!f_stopping)
		{
			if (count % 20 == 0)
				Logger.log(LogLevel.k_debug,
					"Archive worker waiting for queued metadata...");

			while (!f_queue.isEmpty() && !f_stopping)
			{
				Metadata taken;
				try
				{
					taken = f_queue.take();
					
					try
					{
						f_loader.writeFile(taken);
					}
					catch (IOException e)
					{
						//update status to show failure
						if (Settings.trackFileStatus())
							JobStatus.setCreationJobFileStatus(taken.getFile(), CreationJobFileState.ERRORED);
						
						Logger.log(LogLevel.k_error,
							"A file could not be written: " + taken.getFile().getName());
						Logger.log(LogLevel.k_error, e.getMessage());
						Logger.log(LogLevel.k_debug, e, false);
					}
				}
				catch (InterruptedException e)
				{
					Logger.log(LogLevel.k_error,
						"Archive worker failed to load a file from the queue.");
					Logger.log(LogLevel.k_error, e.getMessage());
					Logger.log(LogLevel.k_debug, e, false);
				}
			}
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e){}
			
			++count;
		}
		f_loader.shutdown();
		
		Logger.log(LogLevel.k_debug, "Archive worker is shutdown.");
	}

	/**
	 * Shuts this archive worker down. This is used to stop a job
	 * that is in progress.
	 */
	public void shutdown()
	{
		f_stopping = true;
	}
}
