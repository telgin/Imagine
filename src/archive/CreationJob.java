package archive;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import config.Settings;
import data.IndexWorker;
import data.Metadata;
import data.ArchiveWorker;
import key.Key;
import logging.LogLevel;
import logging.Logger;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Handles the high level process of loading input files and writing them to archives.
 */
public class CreationJob implements Runnable
{
	private boolean f_shuttingDown = false;
	private boolean f_active = true;
	private boolean f_finished = false;
	private int f_maxWaitingFiles;
	private BlockingQueue<Metadata> f_queue;
	private IndexWorker f_indexWorker;
	private List<ArchiveWorker> f_archiveWorkers;
	private Thread[] f_workerThreads;
	private int f_archiveWorkerCount;
	private List<File> f_inputFiles;
	private FileOutputManager f_manager;
	private ArchiveWriterFactory<? extends ArchiveWriter> f_factory;

	/**
	 * Constructs a creation job
	 * @param p_inputFiles The list of input files and folders
	 * @param p_algorithm The algorithm to use
	 * @param p_key The key to use
	 * @param p_archiveWorkerCount The number of archive workers to use. 
	 * Each will run on its own thread
	 */
	public CreationJob(List<File> p_inputFiles, Algorithm p_algorithm, 
		Key p_key, int p_archiveWorkerCount)
	{
		f_inputFiles = p_inputFiles;
		f_archiveWorkerCount = p_archiveWorkerCount;
		f_factory = AlgorithmRegistry.getArchiveWriterFactory(p_algorithm, p_key);
		
		// default for now
		f_maxWaitingFiles = 500;

		f_queue = new LinkedBlockingQueue<Metadata>();
		f_archiveWorkers = new LinkedList<ArchiveWorker>();
		f_workerThreads = new Thread[1 + p_archiveWorkerCount]; //+1 for index worker
		f_manager = new FileOutputManager(Settings.getOutputFolder());

		addArchiveWorkers();
	}

	/**
	 * Adds the number of archive workers specified. One will be added for every
	 * thread that is specified.
	 */
	private void addArchiveWorkers()
	{
		for (int i = 0; i < f_archiveWorkerCount; ++i)
		{
			Logger.log(LogLevel.k_debug, "Adding new Archive Worker");
			f_archiveWorkers.add(new ArchiveWorker(f_queue, f_factory, f_manager));
		}
	}

	/**
	 * Tells if the job is finished running.
	 * @return If the job is finished running
	 */
	public boolean isFinished()
	{
		return f_finished;
	}

	/**
	 * Starts the archive creation job. (Starts filling archives with
	 * input files.)
	 */
	public void start()
	{
		Logger.log(LogLevel.k_debug, "Backup job starting...");
		
		//setup index worker
		f_indexWorker = new IndexWorker(f_queue, f_inputFiles);

		//start archive workers first
		for (int i = 0; i < f_archiveWorkers.size(); ++i)
		{
			Thread thread = new Thread(f_archiveWorkers.get(i));
			f_workerThreads[i] = thread;
			thread.start();
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e){}
		}

		//start index worker
		Thread thread = new Thread(f_indexWorker);
		f_workerThreads[f_archiveWorkers.size()] = thread;
		thread.start();
	}

	/**
	 * Shuts the creation job down. Used for stopping a job before it is finished.
	 */
	public void shutdown()
	{
		f_shuttingDown = true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		start();

		while (!f_shuttingDown)
		{

			f_shuttingDown = allWorkersInactive() && f_queue.size() == 0;

			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e){}
		}

		//shutting down...
		
		f_indexWorker.shutdown();

		for (ArchiveWorker pw : f_archiveWorkers)
		{
			pw.shutdown();
		}

		for (Thread thread : f_workerThreads)
		{
			try
			{
				thread.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		f_finished = true;
		
		Logger.log(LogLevel.k_debug, "Creation job is shutdown.");
	}

	/**
	 * Tells if both the index worker and all archive workers are inactive.
	 * @return If everything is inactive
	 */
	private boolean allWorkersInactive()
	{
		return !f_indexWorker.isActive() && archiveWorkersInactive();
	}

	/**
	 * Tells if all archive workers are inactive
	 * @return If all archive workers are inactive
	 */
	private boolean archiveWorkersInactive()
	{
		for (ArchiveWorker worker : f_archiveWorkers)
			if (worker.isActive())
				return false;

		return true;
	}

	/**
	 * Gets the assigned maximum number of waiting files in the queue before
	 * the index worker pauses to let some be written.
	 * @return The maximum number of waiting files.
	 */
	public int getMaxWaitingFiles()
	{
		return f_maxWaitingFiles;
	}

	/**
	 * Sets the maximum queue size for files waiting to be loaded into archives.
	 * @param p_maxWaitingFiles The maximum number of waiting files
	 */
	public void setMaxWaitingFiles(int p_maxWaitingFiles)
	{
		f_maxWaitingFiles = p_maxWaitingFiles;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "CreationJob [shuttingDown=" + f_shuttingDown + ", active=" + f_active
			+ ", finished=" + f_finished + ", maxWaitingFiles="
			+ f_maxWaitingFiles + ", queue=" + f_queue + ", indexWorker="
			+ f_indexWorker + ", archiveWorkers=" + f_archiveWorkers
			+ ", workerThreads=" + Arrays.toString(f_workerThreads)
			+ ", archiveWorkerCount=" + f_archiveWorkerCount + ", inputFiles="
			+ f_inputFiles + ", manager=" + f_manager + ", factory=" + f_factory
			+ "queue.size()=" + f_queue.size()
			+ "indexWorker.isActive()=" + f_indexWorker.isActive()
			+ "archiveWorkersActive()=" + !archiveWorkersInactive()
			+ "]";
	}
}
