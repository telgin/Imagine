package algorithms.imageoverlay;

import algorithms.Algorithm;
import archive.ArchiveReaderFactory;
import archive.ArchiveWriterFactory;
import key.Key;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Creates the readers and writers for the image overlay algorithm
 */
public class ImageOverlayFactory implements ArchiveReaderFactory<ImageOverlayReader>,
				ArchiveWriterFactory<ImageOverlayWriter>
{
	private Key f_key;
	private Algorithm f_algo;

	/**
	 * Constructs an image overlay factory
	 * @param p_algo The associated algorithm which contains required
	 * parameters among other things.
	 * @param p_key The key which will be used to read or write archives
	 */
	public ImageOverlayFactory(Algorithm p_algo, Key p_key)
	{
		f_key = p_key;
		f_algo = p_algo;
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriterFactory#createWriter()
	 */
	@Override
	public ImageOverlayWriter createWriter()
	{
		return new ImageOverlayWriter(f_algo, f_key);
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveReaderFactory#createReader()
	 */
	@Override
	public ImageOverlayReader createReader()
	{
		return new ImageOverlayReader(f_algo, f_key);
	}
}
