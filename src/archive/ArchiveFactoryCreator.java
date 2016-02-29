package archive;

import algorithms.Algorithm;
import key.Key;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Defines how to create archive factories
 */
public interface ArchiveFactoryCreator
{
	/**
	 * Creates an archive reader factory for some algorithm and key
	 * @param p_algo The algorithm to use
	 * @param p_key The key to use
	 * @return The specific archive reader factory
	 */
	public ArchiveReaderFactory<? extends ArchiveReader>
		createReader(Algorithm p_algo, Key p_key);

	/**
	 * Creates an archive writer factory for some algorithm and key
	 * @param p_algo The algorithm to use
	 * @param p_key The key to use
	 * @return The specific archive writer factory
	 */
	public ArchiveWriterFactory<? extends ArchiveWriter> 
		createWriter(Algorithm p_algo, Key p_key);
}
