package algorithms.imageoverlay;

import algorithms.Algorithm;
import key.Key;
import product.ProductReaderFactory;
import product.ProductWriterFactory;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ImageOverlayFactory implements ProductReaderFactory<ImageOverlayReader>,
				ProductWriterFactory<ImageOverlayWriter>
{
	private Key f_key;
	private Algorithm f_algo;

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 */
	public ImageOverlayFactory(Algorithm p_algo, Key p_key)
	{
		f_key = p_key;
		f_algo = p_algo;
	}

	/* (non-Javadoc)
	 * @see product.ProductWriterFactory#createWriter()
	 */
	@Override
	public ImageOverlayWriter createWriter()
	{
		return new ImageOverlayWriter(f_algo, f_key);
	}

	/* (non-Javadoc)
	 * @see product.ProductReaderFactory#createReader()
	 */
	@Override
	public ImageOverlayReader createReader()
	{
		return new ImageOverlayReader(f_algo, f_key);
	}
}
