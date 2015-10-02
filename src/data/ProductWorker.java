package data;

import hibernate.Metadata;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import logging.LogLevel;
import logging.Logger;
import product.Product;
import product.ProductFactory;
import product.ProductLoader;

/**
 * Dequeues from the given queue and loads files to the ProductLoader
 * Might not need this class
 */
public class ProductWorker implements Runnable{

	// would only need to store this if there's ever a reason to reset
	//the product loader, which there might not be.
	//private final ProductFactory<? extends Product> factory;
	
	private boolean stopping = false;
	private BlockingQueue<Metadata> queue;
	private ProductLoader loader;
	
	public ProductWorker(BlockingQueue<Metadata> queue, String groupName, ProductFactory<? extends Product> factory)
	{
		this.queue = queue;
		loader = new ProductLoader(factory, groupName);
		//this.factory = factory;
	}
	
	@Override
	public void run() {
		while (!stopping)
		{
			Logger.log(LogLevel.k_debug, "Product worker waiting for queued metadata...");
			while (!queue.isEmpty() && !stopping)
			{
				try {
					loader.writeFile(queue.take());
				} catch (IOException | InterruptedException e) {
					Logger.log(LogLevel.k_error, "Product worker failed to load a file from the queue.");
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		
		loader.shutdown();
	}
	
	public void shutdown()
	{
		stopping = true;
	}

}
