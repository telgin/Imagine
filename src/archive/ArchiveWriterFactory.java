package archive;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 * @param <T> The archive mode
 */
public interface ArchiveWriterFactory<T extends ArchiveWriter>
{
	/**
	 * @update_comment
	 * @return
	 */
	T createWriter();
}
