package database.derby;

import java.sql.SQLException;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public class DerbyExceptionHelper
{
	public static boolean tableExists(SQLException e)
	{
		return e.getSQLState().equals("X0Y32");
	}
	
	public static boolean duplicateInsert(SQLException e)
	{
		return e.getSQLState().equals("23505");
	}
}
