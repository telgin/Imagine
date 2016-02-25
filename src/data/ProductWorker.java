package data;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import config.Settings;
import logging.LogLevel;
import logging.Logger;
import product.ConversionJobFileState;
import product.FileOutputManager;
import product.ProductLoader;
import product.ProductWriter;
import product.ProductWriterFactory;
import report.JobStatus;

/**
 * Dequeues from the given queue and loads files to the ProductLoader might not
 * need this class
 */
public class ProductWorker implements Runnable
{
	private boolean f_stopping = false;
	private BlockingQueue<Metadata> f_queue;
	private ProductLoader f_loader;

	/**
	 * @update_comment
	 * @param p_queue
	 * @param p_factory
	 * @param p_manager
	 */
	public ProductWorker(BlockingQueue<Metadata> p_queue, 
		ProductWriterFactory<? extends ProductWriter> p_factory, FileOutputManager p_manager)
	{
		this.f_queue = p_queue;
		f_loader = new ProductLoader(p_factory, p_manager);
	}

	/**
	 * @update_comment
	 * @return
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
					"Product worker waiting for queued metadata...");

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
							JobStatus.setConversionJobFileStatus(taken.getFile(), ConversionJobFileState.ERRORED);
						
						Logger.log(LogLevel.k_error,
							"A file could not be written: " + taken.getFile().getName());
						Logger.log(LogLevel.k_error, e.getMessage());
						Logger.log(LogLevel.k_debug, e, false);
					}
				}
				catch (InterruptedException e)
				{
					Logger.log(LogLevel.k_error,
						"Product worker failed to load a file from the queue.");
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
		
		Logger.log(LogLevel.k_debug, "Product worker is shutdown.");
	}

	/**
	 * @update_comment
	 */
	public void shutdown()
	{
		f_stopping = true;
	}
}
