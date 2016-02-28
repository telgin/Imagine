package system;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Specifies the different actions a user can take when they run the program.
 */
public enum CmdAction
{
	k_open,
	k_embed,
	k_extract,
	k_editor,
	k_help,
	k_install,
	k_unspecified; //use unspecified instead of passing null
}
