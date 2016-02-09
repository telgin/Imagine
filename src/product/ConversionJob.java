package product;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import algorithms.Algorithm;
import algorithms.AlgorithmRegistry;
import config.Settings;
import logging.LogLevel;
import logging.Logger;
import data.IndexWorker;
import data.Metadata;
import data.ProductWorker;
import key.Key;

public class ConversionJob implements Runnable
{
	private boolean shuttingDown = false;
	private boolean active = true;
	private boolean finished = false;
	private int maxWaitingFiles;
	private BlockingQueue<Metadata> queue;
	private IndexWorker indexWorker;
	private List<ProductWorker> productWorkers;
	private Thread[] workerThreads;
	private int productWorkerCount;
	private List<File> inputFiles;
	private FileOutputManager manager;
	private ProductWriterFactory<? extends ProductWriter> factory;

	public ConversionJob(List<File> inputFiles, Algorithm algorithm, Key key, int productWorkerCount)
	{
		this.inputFiles = inputFiles;
		this.productWorkerCount = productWorkerCount;
		factory = AlgorithmRegistry.getProductWriterFactory(algorithm, key);
		
		// default for now
		maxWaitingFiles = 500;

		queue = new LinkedBlockingQueue<Metadata>();
		productWorkers = new LinkedList<ProductWorker>();
		workerThreads = new Thread[1 + productWorkerCount]; //+1 for index worker
		manager = new FileOutputManager(Settings.getOutputFolder());

		addProductWorkers();
	}

	private void addProductWorkers()
	{
		for (int i = 0; i < productWorkerCount; ++i)
		{
			Logger.log(LogLevel.k_debug, "Adding new Product Worker");
			productWorkers.add(new ProductWorker(queue, factory, manager));
		}
	}

	public boolean isFinished()
	{
		return finished;
	}

	public void start()
	{
		Logger.log(LogLevel.k_debug, "Backup job starting...");
		
		//setup index worker
		indexWorker = new IndexWorker(queue, inputFiles);

		//start product workers first
		for (int i = 0; i < productWorkers.size(); ++i)
		{
			Thread thread = new Thread(productWorkers.get(i));
			workerThreads[i] = thread;
			thread.start();
			sleep(100);
		}

		//start index worker
		Thread thread = new Thread(indexWorker);
		workerThreads[productWorkers.size()] = thread;
		thread.start();
	}

	public void shutdown()
	{
		shuttingDown = true;
	}

	@Override
	public void run()
	{
		start();

		while (!shuttingDown)
		{

			shuttingDown = allWorkersInactive() && queue.size() == 0;

			sleep(1000);
		}

		//shutting down...
		
		indexWorker.shutdown();

		for (ProductWorker pw : productWorkers)
		{
			pw.shutdown();
		}

		for (Thread thread : workerThreads)
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
		
		finished = true;
		
		Logger.log(LogLevel.k_debug, "Conversion job is shutdown.");
	}

	private boolean allWorkersInactive()
	{
		return !indexWorker.isActive() && productWorkersInactive();
	}

	private boolean productWorkersInactive()
	{
		for (ProductWorker worker : productWorkers)
			if (worker.isActive())
				return false;

		return true;
	}

	private void sleep(long ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch (InterruptedException e)
		{
		}
	}

	public int getMaxWaitingFiles()
	{
		return maxWaitingFiles;
	}

	public void setMaxWaitingFiles(int maxWaitingFiles)
	{
		this.maxWaitingFiles = maxWaitingFiles;
	}

	public void printState()
	{
		String text = "State:\n";
		text += "shuttingdown=" + shuttingDown + "\n";
		text += "active=" + active + "\n";
		text += "finished=" + finished + "\n";
		text += "queue.size()=" + queue.size() + "\n";
		text += "indexWorker.isActive()=" + indexWorker.isActive() + "\n";
		text += "productWorkersActive()=" + !productWorkersInactive();
		System.out.println(text);
	}
}
