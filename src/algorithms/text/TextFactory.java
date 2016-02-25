package algorithms.text;

import algorithms.Algorithm;
import archive.ArchiveReaderFactory;
import archive.ArchiveWriterFactory;
import key.Key;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class TextFactory implements ArchiveReaderFactory<TextReader>,
	ArchiveWriterFactory<TextWriter>
{
	private Algorithm s_algo;
	private Key s_key;

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 */
	public TextFactory(Algorithm p_algo, Key p_key)
	{
		this.s_algo = p_algo;
		this.s_key = p_key;
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveWriterFactory#createWriter()
	 */
	@Override
	public TextWriter createWriter()
	{
		return new TextWriter(s_algo, s_key);
	}

	/* (non-Javadoc)
	 * @see archive.ArchiveReaderFactory#createReader()
	 */
	@Override
	public TextReader createReader()
	{
		return new TextReader(s_algo, s_key);
	}

}
