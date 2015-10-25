package runner;

import hibernate.Metadata;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import logging.LogLevel;
import logging.Logger;
import data.IndexWorker;
import data.ProductWorker;
import data.TrackingGroup;

public class BackupJob implements Runnable{
	private TrackingGroup group;
	private boolean shuttingDown = false;
	private boolean active = true;
	private boolean finished = false;
	private int maxWaitingFiles;
	private BlockingQueue<Metadata> queue;
	private IndexWorker[] indexWorkers;
	private List<ProductWorker> productWorkers;
	private Thread[] workerThreads;
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
		workerThreads = new Thread[indexWorkers.length + productWorkerCount];
		
		addIndexWorkers();
		addProductWorkers();
		
		//System.err.println("RemainingFiles: ");
		//for (File f:remainingFiles)
		//	System.err.println(f.getPath());
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
		
		return new ProductWorker(queue, group);
	}

	public void stopBackup()
	{
		
	}
	
	public boolean isFinished()
	{
		return finished;
	}

	public void start() {
		Logger.log(LogLevel.k_debug, "Backup job starting...");
		
		for (int i=indexWorkers.length; i<workerThreads.length; ++i)
		{
			Thread thread = new Thread(productWorkers.get(i-indexWorkers.length));
			workerThreads[i] = thread;
			thread.start();
			sleep(100);
		}
		
		for (int i=0; i<indexWorkers.length; ++i)
		{
			Thread thread = new Thread(indexWorkers[i]);
			workerThreads[i] = thread;
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
					{
						indexWorkers[i].shutdown();
						indexWorkers[i] = setupNewIndexWorker();
						Thread iThread = new Thread(indexWorkers[i]);
						workerThreads[i] = iThread;
						iThread.start();
					}
						
				}
				
				if (indexWorkersInactive() && remainingFiles.size() == 0 && queue.size() == 0)
				{
					active = false;
				}
				
				sleep(2000);
			}
			
			if (allWorkersInactive() && remainingFiles.size() == 0 && queue.size() == 0)
			{
				shuttingDown = true;
			}
			else
			{
				active = remainingFiles.size() == 0;
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
		
		finished = true;
	}
	
	private boolean allWorkersInactive()
	{
		return indexWorkersInactive() && productWorkersInactive();
	}
	
	private boolean productWorkersInactive() {
		for (ProductWorker worker:productWorkers)
			if (worker.isActive())
				return false;
		
		return true;
	}
	
	private boolean indexWorkersInactive() {
		for (IndexWorker worker:indexWorkers)
			if (worker.isActive())
				return false;
		
		return true;
	}

	private IndexWorker setupNewIndexWorker() {
		Logger.log(LogLevel.k_debug, "Adding new Index Worker.");
		File next = remainingFiles.remove(0);
		System.err.println("Adding index worker for " + next.getPath());
		System.err.println("remainingFiles.size()=" + remainingFiles.size());
		return new IndexWorker(queue, next, group);
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

	public void printState() {
		String text = "State:\n";
		text += "shuttingdown=" + shuttingDown + "\n";
		text += "active=" + active + "\n";
		text += "finished=" + finished + "\n";
		text += "queue.size()=" + queue.size() + "\n";
		text += "indexWorkersActive()=" + !indexWorkersInactive() + "\n";
		text += "productWorkersActive()=" + !productWorkersInactive();
		System.out.println(text);
	}
}
