package algorithms.image;

import algorithms.Algorithm;
import key.Key;
import product.ProductReaderFactory;
import product.ProductWriterFactory;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ImageFactory implements ProductReaderFactory<ImageReader>,
				ProductWriterFactory<ImageWriter>
{
	private Key f_key;
	private Algorithm f_algo;

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 */
	public ImageFactory(Algorithm p_algo, Key p_key)
	{
		f_key = p_key;
		f_algo = p_algo;
	}

	/* (non-Javadoc)
	 * @see product.ProductWriterFactory#createWriter()
	 */
	@Override
	public ImageWriter createWriter()
	{
		return new ImageWriter(f_algo, f_key);
	}

	/* (non-Javadoc)
	 * @see product.ProductReaderFactory#createReader()
	 */
	@Override
	public ImageReader createReader()
	{
		return new ImageReader(f_algo, f_key);
	}
}
