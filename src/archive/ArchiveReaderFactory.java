package archive;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 * @param <T> The archive mode
 */
public interface ArchiveReaderFactory<T extends ArchiveReader>
{
	/**
	 * @update_comment
	 * @return
	 */
	T createReader();
}
