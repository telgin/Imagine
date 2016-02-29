package archive;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Defines states for files going through the archive creation process.
 * Specifically, this allows for tracking of the files added to archives
 * or and the files used to created archives when such a distinction can
 * be made.
 */
public enum CreationJobFileState
{
	NOT_STARTED,
	WRITING,
	PAUSED,
	FINISHED,
	ERRORED;
}
