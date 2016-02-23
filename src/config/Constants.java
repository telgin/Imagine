package config;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class Constants
{

	// program names
	public static final String APPLICATION_NAME_FULL = "Imagine Obfuscation Utility";
	public static final String APPLICATION_NAME_SHORT = "Imagine";

	// program version
	public static final int APPLICATION_MAJOR_VERSION = 0;
	public static final int APPLICATION_MINOR_VERSION = 0;
	public static final int APPLICATION_UPDATE_VERSION = 1;
	public static final String APPLICATION_FORMATTED_VERSION =
					Integer.toString(APPLICATION_MAJOR_VERSION) + "."
									+ Integer.toString(APPLICATION_MINOR_VERSION) + "."
									+ Integer.toString(APPLICATION_UPDATE_VERSION);

	// file names
	public static final File CONFIG_FILE = new File("config.xml");
	public static final String ASSEMBLY_FOLDER_NAME = "." +
					APPLICATION_NAME_SHORT.toLowerCase() + "_assembly";
	
	// misc system configuration
	public static final String TEMP_RESERVED_GROUP_NAME = APPLICATION_NAME_SHORT.toLowerCase();
	public static final int DEFAULT_THREAD_COUNT = 1;
	public static final Charset CHARSET = StandardCharsets.UTF_8;
	public static final long DEFAULT_SEED = 1337;
	public static final String TEMP_KEY_NAME = "not specified";
	public static final String FILE_DELIMITER = "/:"; //disallowed file characters in unix and windows
	
	// io system configuration
	public static final int MAX_READ_BUFFER_SIZE = 50000000;
	public static final long END_CODE = Long.MAX_VALUE;
	public static final long END_CODE_SIZE = 8;
	public static final String INDEX_FOLDER_NAME =
					"." + Constants.APPLICATION_NAME_SHORT.toLowerCase();
	public static final long FIRST_FRAGMENT_CODE = 1;
	public static final long NOT_FRAGMENT_CODE = -1;

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
	public static final int FILE_NAME_LENGTH_SIZE = 2;
	public static final int DATE_CREATED_SIZE = 8;
	public static final int DATE_MODIFIED_SIZE = 8;
	public static final int PERMISSIONS_SIZE = 2;
	public static final int FILE_PERMISSIONS_SIZE = 1;
	public static final int FILE_LENGTH_REMAINING_SIZE = 8;
	public static final int FILE_TYPE_SIZE = 1;
	


	
	
	
	
	

}
