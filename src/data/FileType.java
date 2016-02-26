package data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * A simple enum for whether a File is a file or a folder. This is needed
 * because it would otherwise become unclear what a file is when you're reading
 * it from an archive. Also, this defines standard codes for each type.
 */
public enum FileType
{
	k_file(1),
	k_folder(2);
	
	private int f_num;
	private static Map<Integer, FileType> s_map;
	
	static
	{
		s_map = new HashMap<Integer, FileType>();
		s_map.put(1, FileType.k_file);
		s_map.put(2, FileType.k_folder);
	}
	
	/**
	 * Creates a file type enum with a code
	 * @param p_num The code
	 */
	private FileType(int p_num)
	{
		this.f_num = p_num;
	}

	/**
	 * Gets the int code of this enum
	 * @return The int code
	 */
	public int toInt()
	{
		return f_num;
	}
	
	/**
	 * Gets the file type enum associated with the given code
	 * @param p_int The file type enum code
	 * @return The associated file type enum
	 */
	public static FileType toFileType(int p_int)
	{
		return s_map.get(p_int);
	}
}
