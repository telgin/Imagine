package util;

import java.io.File;

public abstract class Constants
{

	// program names
	public static final String APPLICATION_NAME_FULL = "Aliustra Backup Utility";
	public static final String APPLICATION_NAME_SHORT = "Aliustra";

	// program version
	public static final int APPLICATION_MAJOR_VERSION = 0;
	public static final int APPLICATION_MINOR_VERSION = 0;
	public static final int APPLICATION_UPDATE_VERSION = 1;
	public static final String APPLICATION_FORMATTED_VERSION =
					Integer.toString(APPLICATION_MAJOR_VERSION) + "."
									+ Integer.toString(APPLICATION_MINOR_VERSION) + "."
									+ Integer.toString(APPLICATION_UPDATE_VERSION);

	// file names
	public static final File configFile = new File("config.xml");

	// io system configuration
	public static final int MAX_READ_BUFFER_SIZE = 50000000;
	public static final long END_CODE = Long.MAX_VALUE;
	public static final long END_CODE_SIZE = 8;

	// io data field lengths
	public static final int STREAM_UUID_SIZE = 8;
	public static final int PRODUCT_SEQUENCE_NUMBER_SIZE = 4;
	public static final int PRODUCT_UUID_SIZE =
					STREAM_UUID_SIZE + PRODUCT_SEQUENCE_NUMBER_SIZE;
	public static final int PRODUCT_VERSION_NUMBER_SIZE = 1;
	public static final int FILE_HEADER_LENGTH_SIZE = 4;
	public static final int ALGORITHM_NAME_LENGTH_SIZE = 2;
	public static final int ALGORITHM_VERSION_NUMBER_SIZE = 1;
	public static final int GROUP_NAME_LENGTH_SIZE = 2;
	public static final int GROUP_KEY_NAME_LENGTH_SIZE = 2;
	public static final int FRAGMENT_NUMBER_SIZE = 8;
	public static final int FILE_HASH_SIZE = 64;
	public static final int FILE_NAME_LENGTH_SIZE = 2;
	public static final int DATE_CREATED_SIZE = 8;
	public static final int DATE_MODIFIED_SIZE = 8;
	public static final int PERMISSIONS_SIZE = 2;
	public static final int FILE_PERMISSIONS_SIZE = 1;
	public static final int METADATA_UPDATE_FLAG_SIZE = 1;
	public static final int FILE_LENGTH_REMAINING_SIZE = 8;
	public static final String INDEX_FOLDER_NAME =
					"." + Constants.APPLICATION_NAME_SHORT.toLowerCase();

}
