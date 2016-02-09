package data;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import logging.LogLevel;
import logging.Logger;
import product.FileOutputManager;
import product.ProductLoader;
import product.ProductWriter;
import product.ProductWriterFactory;

/**
 * Dequeues from the given queue and loads files to the ProductLoader Might not
 * need this class
 */
public class ProductWorker implements Runnable
{

	private boolean stopping = false;
	private BlockingQueue<Metadata> queue;
	private ProductLoader loader;

	public ProductWorker(BlockingQueue<Metadata> queue, 
					ProductWriterFactory<? extends ProductWriter> factory,
					FileOutputManager manager)
	{
		this.queue = queue;
		loader = new ProductLoader(factory, manager);
	}

	public boolean isActive()
	{
		return !queue.isEmpty() && !stopping;
	}

	@Override
	public void run()
	{
		int count = 0;
		while (!stopping)
		{
			if (count % 4 == 0)
				Logger.log(LogLevel.k_debug,
								"Product worker waiting for queued metadata...");

			while (!queue.isEmpty() && !stopping)
			{
				try
				{
					loader.writeFile(queue.take());
				}
				catch (IOException | InterruptedException e)
				{
					Logger.log(LogLevel.k_error,
									"Product worker failed to load a file from the queue.");
					Logger.log(LogLevel.k_error, e.getMessage());
					Logger.log(LogLevel.k_debug, e, false);
				}
			}
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e){}
			
			count++;
		}
		loader.shutdown();
		
		Logger.log(LogLevel.k_debug, "Product worker is shutdown.");
	}

	public void shutdown()
	{
		stopping = true;
	}

}
