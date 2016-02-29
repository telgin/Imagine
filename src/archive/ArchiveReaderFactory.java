package archive;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Creates archive readers for various algorithms
 * @param <T> The archive mode
 */
public interface ArchiveReaderFactory<T extends ArchiveReader>
{
	/**
	 * Creates an archive reader
	 * @return The archive reader
	 */
	T createReader();
}
