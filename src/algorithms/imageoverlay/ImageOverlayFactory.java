package algorithms.imageoverlay;

import algorithms.Algorithm;
import archive.ArchiveReaderFactory;
import archive.ArchiveWriterFactory;
import key.Key;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ImageOverlayFactory implements ArchiveReaderFactory<ImageOverlayReader>,
				ArchiveWriterFactory<ImageOverlayWriter>
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
