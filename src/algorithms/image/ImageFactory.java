package algorithms.image;

import algorithms.Algorithm;
import archive.ArchiveReaderFactory;
import archive.ArchiveWriterFactory;
import key.Key;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class ImageFactory implements ArchiveReaderFactory<ImageReader>,
				ArchiveWriterFactory<ImageWriter>
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
