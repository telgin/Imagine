package product;

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
import data.ProductWorker;
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
	private List<ProductWorker> f_productWorkers;
	private Thread[] f_workerThreads;
	private int f_productWorkerCount;
	private List<File> f_inputFiles;
	private FileOutputManager f_manager;
	private ProductWriterFactory<? extends ProductWriter> f_factory;

	/**
	 * @update_comment
	 * @param p_inputFiles
	 * @param p_algorithm
	 * @param p_key
	 * @param p_productWorkerCount
	 */
	public ConversionJob(List<File> p_inputFiles, Algorithm p_algorithm, Key p_key, int p_productWorkerCount)
	{
		f_inputFiles = p_inputFiles;
		f_productWorkerCount = p_productWorkerCount;
		f_factory = AlgorithmRegistry.getProductWriterFactory(p_algorithm, p_key);
		
		// default for now
		f_maxWaitingFiles = 500;

		f_queue = new LinkedBlockingQueue<Metadata>();
		f_productWorkers = new LinkedList<ProductWorker>();
		f_workerThreads = new Thread[1 + p_productWorkerCount]; //+1 for index worker
		f_manager = new FileOutputManager(Settings.getOutputFolder());

		addProductWorkers();
	}

	/**
	 * @update_comment
	 */
	private void addProductWorkers()
	{
		for (int i = 0; i < f_productWorkerCount; ++i)
		{
			Logger.log(LogLevel.k_debug, "Adding new Product Worker");
			f_productWorkers.add(new ProductWorker(f_queue, f_factory, f_manager));
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

		//start product workers first
		for (int i = 0; i < f_productWorkers.size(); ++i)
		{
			Thread thread = new Thread(f_productWorkers.get(i));
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
		f_workerThreads[f_productWorkers.size()] = thread;
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

		for (ProductWorker pw : f_productWorkers)
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
		return !f_indexWorker.isActive() && productWorkersInactive();
	}

	/**
	 * @update_comment
	 * @return
	 */
	private boolean productWorkersInactive()
	{
		for (ProductWorker worker : f_productWorkers)
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
			+ f_indexWorker + ", productWorkers=" + f_productWorkers
			+ ", workerThreads=" + Arrays.toString(f_workerThreads)
			+ ", productWorkerCount=" + f_productWorkerCount + ", inputFiles="
			+ f_inputFiles + ", manager=" + f_manager + ", factory=" + f_factory
			+ "queue.size()=" + f_queue.size()
			+ "indexWorker.isActive()=" + f_indexWorker.isActive()
			+ "productWorkersActive()=" + !productWorkersInactive()
			+ "]";
	}
}
