package product;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.w3c.dom.Element;

import logging.LogLevel;
import logging.Logger;
import treegenerator.TreeGenerator;
import data.IndexWorker;
import data.Metadata;
import data.ProductWorker;
import data.TrackingGroup;

public class ConversionJob implements Runnable
{
	private TrackingGroup group;
	private boolean shuttingDown = false;
	private boolean active = true;
	private boolean finished = false;
	private int maxWaitingFiles;
	private BlockingQueue<Metadata> queue;
	private IndexWorker indexWorker;
	private List<ProductWorker> productWorkers;
	private Thread[] workerThreads;
	private int productWorkerCount;
	private TreeGenerator generator;
	private FileOutputManager manager;

	public ConversionJob(TrackingGroup group, int productWorkerCount)
	{
		this.group = group;
		this.productWorkerCount = productWorkerCount;

		// default for now
		maxWaitingFiles = 500;

		queue = new LinkedBlockingQueue<Metadata>();
		productWorkers = new LinkedList<ProductWorker>();
		workerThreads = new Thread[1 + productWorkerCount]; //+1 for index worker
		generator = new TreeGenerator(group);
		manager = new FileOutputManager(group, group.getStaticOutputFolder());

		addProductWorkers();
	}

	private void addProductWorkers()
	{
		for (int i = 0; i < productWorkerCount; ++i)
			productWorkers.add(setupNewProductWorker());
	}

	private ProductWorker setupNewProductWorker()
	{
		Logger.log(LogLevel.k_debug, "Adding new Product Worker");

		return new ProductWorker(queue, group, manager);
	}

	public void stopBackup()
	{
		//TODO
	}

	public boolean isFinished()
	{
		return finished;
	}

	public void start()
	{
		Logger.log(LogLevel.k_debug, "Backup job starting...");
		
		//create the tree
		generator.generateTree();
		Element root = generator.getRoot();
		Element pcElement = (Element) root.getFirstChild();
		
		//initial save: TODO remove this
		generator.save(new File("testing/highlevel/tree.xml"));
		
		//setup index worker
		indexWorker = new IndexWorker(queue, pcElement, group);

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

		
		//save tree
		//TODO change to correct path
		generator.save(new File("testing/highlevel/tree.xml"));
		
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
