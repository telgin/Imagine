package database.derby;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.derby.tools.ij;
import data.Metadata;
import data.TrackingGroup;
import runner.ActiveComponent;
import util.ByteConversion;

/**
 * A class for queuing up jobs for the database which we don't want to block on.
 * Probably mostly saves.???????
 * 
 * @credit http://www.tutorialspoint.com/hibernate/hibernate_examples.htm
 * @credit http://www.codejava.net/java-se/jdbc/connect-to-apache-derby-java-db-
 *         via-jdbc
 */
public class EmbeddedDB implements ActiveComponent
{
	// private static SessionFactory factory;
	private Connection connection;
	private boolean shutdown = false;

	public EmbeddedDB()
	{
		try
		{
			//set the home path
			//derby will create a database here unless one already exists
			System.setProperty("derby.system.homeSystem.setProp", "./hashdb");
			
			//try get a connection
			String url = "jdbc:derby:hashdb;create=true";
			connection = DriverManager.getConnection(url);
			
			//tables are created if the database was just created
			//and they don't exist yet
			createTables();
			
			if (connection == null)
				throw new SQLException("Could not connect or create a new database.");
		}
		catch (Throwable ex)
		{
			throw new ExceptionInInitializerError(ex);
		}
	}

	public void saveProductUUID(Metadata fileMetadata, TrackingGroup group)
	{
		//save the f1 product uuid, get the id
		int f1uuidID = getF1_UUID_ID(fileMetadata.getProductUUID());
		if (f1uuidID == -1)
			f1uuidID = addF1_UUID_ID(fileMetadata.getProductUUID());
		
		//if the tracking group isn't there, add it
		int groupID = getGroupID(group);
		if (groupID == -1)
			groupID = addTrackingGroup(group);
		
		String sql = "UPDATE FILE_HASH SET F1ID=" + f1uuidID + " WHERE HASH='" 
						+ ByteConversion.bytesToBase64(fileMetadata.getFileHash())
						+ "' AND GROUPID=" + groupID;
		
		try
		{
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
		}
		catch (NullPointerException | SQLException e)
		{
			//if it errors because it already exists, that's fine
			e.printStackTrace();
		}
	}

	/**
	 * @update_comment
	 * @param productUUID
	 * @return
	 */
	private int addF1_UUID_ID(byte[] productUUID)
	{
		String sql = "INSERT INTO F1_UUID (UUID) VALUES('"
						+ ByteConversion.bytesToBase64(productUUID) + "')";
		
		try
		{
			Statement statement = connection.createStatement();
			statement.execute(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet results = statement.getGeneratedKeys();
			
			results.next();
			return results.getInt(1);
		}
		catch (NullPointerException e)
		{
			//this could happen if we can't connect
			e.printStackTrace();
			return -1;
		}
		catch (SQLException e)
		{
			//if it errors because it already exists, that's fine
			if (!DerbyExceptionHelper.duplicateInsert(e))
				e.printStackTrace();
			return -1;
		}
	}

	/**
	 * @update_comment
	 * @param productUUID
	 * @return
	 */
	private int getF1_UUID_ID(byte[] productUUID)
	{
		String sql = "SELECT ID FROM F1_UUID WHERE UUID = '" + 
						ByteConversion.bytesToBase64(productUUID) + "'";
		ResultSet results = executeQuery(sql);
		if (results == null)
			return -1;
		
		try
		{
			results.next();
			int id = results.getInt("ID");
			results.close();
			return id;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public void saveFileHash(Metadata fileMetadata, TrackingGroup group)
	{
		//if the tracking group isn't there, add it
		int groupID = getGroupID(group);
		if (groupID == -1)
			groupID = addTrackingGroup(group);
		
		//save the hash-group combination, with null for the f1 uuid
		String sql = "INSERT INTO FILE_HASH VALUES(NULL,'"
						+ ByteConversion.bytesToBase64(fileMetadata.getFileHash())
						+ "', " + groupID + ")";
		
		try
		{
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
		}
		catch (NullPointerException | SQLException e)
		{
			//if it errors because it already exists, that's fine
			e.printStackTrace();
		}
	}

	public Integer addTrackingGroup(TrackingGroup group)
	{
		//add the group name
		String sql = "INSERT INTO TRACKING_GROUP (NAME) VALUES('"
						+ group.getName() + "')";
		
		try
		{
			Statement statement = connection.createStatement();
			statement.execute(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet results = statement.getGeneratedKeys();
			
			results.next();
			return results.getInt(1);
		}
		catch (NullPointerException | SQLException e)
		{
			//if it errors because it already exists, that's fine
			e.printStackTrace();
			return -1;
		}

	}
	
	private boolean executeNonQuery(String sql)
	{
		try
		{
			Statement statement = connection.createStatement();
			return statement.execute(sql);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean containsFileHash(byte[] hash, TrackingGroup group)
	{
		int id = getGroupID(group);
		
		//if the group wasn't found, the hash-group combination can't exist
		if (id == -1)
			return false;
		
		String sql = "SELECT F1ID FROM FILE_HASH WHERE GROUPID = " + id +
						" AND HASH = '" + ByteConversion.bytesToBase64(hash) + "'";
		
		ResultSet rows = executeQuery(sql);
		try
		{
			//this indicates that a result was found
			rows.next();
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private int getGroupID(TrackingGroup group)
	{
		String sql = "SELECT ID FROM TRACKING_GROUP WHERE NAME = '" + 
						group.getName() + "'";
		ResultSet results = executeQuery(sql);
		if (results == null)
			return -1;
		
		try
		{
			results.next();
			int id = results.getInt("ID");
			results.close();
			return id;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see runner.ActiveComponent#shutdown()
	 */
	@Override
	public void shutdown()
	{
		try
		{
			//connection.close();
			DriverManager.getConnection("jdbc:derby:hashdb;shutdown=true");
		}
		catch (SQLException e)
		{
			//the shutdown exception is meaningless
		}
		shutdown = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see runner.ActiveComponent#isShutdown()
	 */
	@Override
	public boolean isShutdown()
	{
		return shutdown;
	}
	
	public void display() throws IOException
	{
		String sqlIn = "SELECT * FROM TRACKING_GROUP;"
						+ "SELECT * FROM F1_UUID;"
						+ "SELECT * FROM FILE_HASH;";
	    InputStream stream = new ByteArrayInputStream(sqlIn.getBytes(StandardCharsets.UTF_8));
	    
	    ij.runScript(connection, stream, StandardCharsets.UTF_8.name(), System.out, "UTF-8");
	    stream.close();
	}

	/**
	 * @update_comment
	 */
	public void save()
	{
		// TODO Auto-generated method stub

	}

	public void createTables() throws SQLException
	{
		//fragment 1 uuid table
		String sql = "CREATE TABLE F1_UUID (ID INT NOT NULL"
						+ " PRIMARY KEY GENERATED ALWAYS AS IDENTITY"
						+ " (START WITH 1, INCREMENT BY 1)"
						+ ", UUID CHAR(16) UNIQUE NOT NULL)";
		createTable(sql);

		//tracking group names table
		sql = "CREATE TABLE TRACKING_GROUP (ID INT NOT NULL"
						+ " PRIMARY KEY GENERATED ALWAYS AS IDENTITY"
						+ " (START WITH 1, INCREMENT BY 1)"
						+ ", NAME VARCHAR(50) UNIQUE NOT NULL)";
		createTable(sql);

		//file hash table
		sql = "CREATE TABLE FILE_HASH (F1ID INT DEFAULT NULL, "
						+ "HASH CHAR(88) NOT NULL, GROUPID INT NOT NULL,"
						+ " PRIMARY KEY (GROUPID, HASH), ";

		//hash table foreign key constraints
		sql += " CONSTRAINT HASH_F1_REF FOREIGN KEY (F1ID)"
						+ " REFERENCES F1_UUID(ID), ";
		sql += " CONSTRAINT HASH_GROUP_REF FOREIGN KEY (GROUPID)"
						+ " REFERENCES TRACKING_GROUP(ID))";
		createTable(sql);
	}
	
	private ResultSet executeQuery(String sql)
	{
		try
		{
			Statement statement = connection.createStatement();
			return statement.executeQuery(sql);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private ResultSet executeUpdate(String sql)
	{
		try
		{
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
			return statement.getGeneratedKeys();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private void createTable(String sql) throws SQLException
	{
		Statement statement = connection.createStatement();

		try
		{
			statement.executeUpdate(sql);
		}
		catch (SQLException e)
		{
			if (!DerbyExceptionHelper.tableExists(e))
				e.printStackTrace();
		}
	}

	/**
	 * @update_comment
	 * @param fileHash
	 * @param group
	 * @return
	 */
	public static byte[] getFragment1ProductUUID(byte[] fileHash, TrackingGroup group)
	{
		//TODO implement this
		
		//lookup f1id from hash table
		
		//get base64 string from f1uuid table
		
		//convert to byte array
		
		
		return new byte[12];
	}

}
