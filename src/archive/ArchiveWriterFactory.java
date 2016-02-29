package archive;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Creates archive writers for various algorithms
 * @param <T> The archive mode
 */
public interface ArchiveWriterFactory<T extends ArchiveWriter>
{
	/**
	 * Creates an archive writer
	 * @return The archive writer
	 */
	T createWriter();
}
