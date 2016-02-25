package data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
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
	 * @update_comment
	 * @param p_num
	 */
	private FileType(int p_num)
	{
		this.f_num = p_num;
	}

	/**
	 * @update_comment
	 * @return
	 */
	public int toInt()
	{
		return f_num;
	}
	
	/**
	 * @update_comment
	 * @param p_int
	 * @return
	 */
	public static FileType toFileType(int p_int)
	{
		return s_map.get(p_int);
	}
}
