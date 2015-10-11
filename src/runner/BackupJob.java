package runner;

import hibernate.Metadata;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductFactory;
import data.IndexWorker;
import data.ProductWorker;
import data.TrackingGroup;

public class BackupJob implements Runnable{
	private TrackingGroup group;
	private boolean shuttingDown = false;
	private boolean active = true;
	private int maxWaitingFiles;
	private BlockingQueue<Metadata> queue;
	private IndexWorker[] indexWorkers;
	private List<ProductWorker> productWorkers;
	private List<Thread> workerThreads;
	private int productWorkerCount;
	private int indexWorkerCount;
	private List<File> remainingFiles;
	
	public BackupJob(TrackingGroup group, int indexWorkerCount, int productWorkerCount)
	{
		this.group = group;
		remainingFiles = new LinkedList<File>(group.getTrackedFiles());
		this.indexWorkerCount = indexWorkerCount;
		this.productWorkerCount = productWorkerCount;
		
		//default for now
		maxWaitingFiles = 500;
		
		queue = new LinkedBlockingQueue<Metadata>();
		indexWorkers = new IndexWorker[Math.min(indexWorkerCount, remainingFiles.size())];
		productWorkers = new LinkedList<ProductWorker>();
		workerThreads = new LinkedList<Thread>();
		
		addIndexWorkers();
		addProductWorkers();
	}
	
	private void addIndexWorkers() {
		for (int i=0; i < indexWorkers.length; ++i)
			indexWorkers[i] = setupNewIndexWorker();
	}

	private void addProductWorkers() {
		for (int i=0; i < productWorkerCount; ++i)
			productWorkers.add(setupNewProductWorker());
	}

	private ProductWorker setupNewProductWorker() {
		Logger.log(LogLevel.k_debug, "Adding new Product Worker");
		ProductFactory<? extends Product> factory = group.getProductFactory();
		
		return new ProductWorker(queue, group.getName(), factory);
	}

	public void stopBackup()
	{
		
	}
	
	public boolean isFinished()
	{
		return true;
	}

	public void start() {
		Logger.log(LogLevel.k_debug, "Backup job starting...");
		
		for (ProductWorker pw: productWorkers)
		{
			Thread thread = new Thread(pw);
			workerThreads.add(thread);
			thread.start();
			sleep(100);
		}
		
		for (IndexWorker iw: indexWorkers)
		{
			Thread thread = new Thread(iw);
			workerThreads.add(thread);
			thread.start();
		}		
	}

	
	public void pauseLoading()
	{
		active = false;
	}
	
	public void resumeLoading()
	{
		active = true;
	}
	
	public void shutdown()
	{
		shuttingDown = true;
	}

	@Override
	public void run() {
		start();
		
		while (!shuttingDown)
		{
			while (active && !shuttingDown)
			{
				//search for a index worker that needs a new folder
				for (int i=0; i<indexWorkers.length && remainingFiles.size() > 0; ++i)
				{
					if (!indexWorkers[i].isActive())
						indexWorkers[i] = setupNewIndexWorker();
				}
				sleep(2000);
			}
			
			sleep(1000);
		}
		
		for (IndexWorker iw: indexWorkers)
		{
			iw.shutdown();
		}
		
		for (ProductWorker pw: productWorkers)
		{
			pw.shutdown();
		}
		
		for (Thread thread: workerThreads)
		{
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			

	}
	
	private IndexWorker setupNewIndexWorker() {
		Logger.log(LogLevel.k_debug, "Adding new Index Worker.");
		return new IndexWorker(queue, remainingFiles.remove(0), group);
	}

	private void sleep(long ms)
	{
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

	public int getMaxWaitingFiles() {
		return maxWaitingFiles;
	}

	public void setMaxWaitingFiles(int maxWaitingFiles) {
		this.maxWaitingFiles = maxWaitingFiles;
	}
}
