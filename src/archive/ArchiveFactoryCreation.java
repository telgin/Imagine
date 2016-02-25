package archive;

import algorithms.Algorithm;
import key.Key;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public interface ArchiveFactoryCreation
{
	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @return
	 */
	public ArchiveReaderFactory<? extends ArchiveReader>
		createReader(Algorithm p_algo, Key p_key);

	/**
	 * @update_comment
	 * @param p_algo
	 * @param p_key
	 * @return
	 */
	public ArchiveWriterFactory<? extends ArchiveWriter> 
		createWriter(Algorithm p_algo, Key p_key);
}
