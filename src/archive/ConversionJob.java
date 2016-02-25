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
 * @update_comment
 */
public class ConversionJob implements Runnable
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
	 * @update_comment
	 * @param p_inputFiles
	 * @param p_algorithm
	 * @param p_key
	 * @param p_archiveWorkerCount
	 */
	public ConversionJob(List<File> p_inputFiles, Algorithm p_algorithm, Key p_key, int p_archiveWorkerCount)
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
	 * @update_comment
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
	 * @update_comment
	 * @return
	 */
	public boolean isFinished()
	{
		return f_finished;
	}

	/**
	 * @update_comment
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
	 * @update_comment
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
		
		Logger.log(LogLevel.k_debug, "Conversion job is shutdown.");
	}

	/**
	 * @update_comment
	 * @return
	 */
	private boolean allWorkersInactive()
	{
		return !f_indexWorker.isActive() && archiveWorkersInactive();
	}

	/**
	 * @update_comment
	 * @return
	 */
	private boolean archiveWorkersInactive()
	{
		for (ArchiveWorker worker : f_archiveWorkers)
			if (worker.isActive())
				return false;

		return true;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public int getMaxWaitingFiles()
	{
		return f_maxWaitingFiles;
	}

	/**
	 * @update_comment
	 * @param p_maxWaitingFiles
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
		return "ConversionJob [shuttingDown=" + f_shuttingDown + ", active=" + f_active
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
