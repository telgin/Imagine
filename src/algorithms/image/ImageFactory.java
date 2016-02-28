package algorithms.image;

import algorithms.Algorithm;
import archive.ArchiveReaderFactory;
import archive.ArchiveWriterFactory;
import key.Key;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Creates the readers and writers for the image algorithm
 */
public class ImageFactory implements ArchiveReaderFactory<ImageReader>,
				ArchiveWriterFactory<ImageWriter>
{
	private Key f_key;
	private Algorithm f_algo;

	/**
	 * Constructs an image factory
	 * @param p_algo The associated algorithm which contains required
	 * parameters among other things.
	 * @param p_key The key which will be used to read or write archives
	 */
	public ImageFactory(Algorithm p_algo, Key p_key)
	{
		f_key = p_key;
		f_algo = p_algo;
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriterFactory#createWriter()
	 */
	@Override
	public ImageWriter createWriter()
	{
		return new ImageWriter(f_algo, f_key);
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveReaderFactory#createReader()
	 */
	@Override
	public ImageReader createReader()
	{
		return new ImageReader(f_algo, f_key);
	}
}
