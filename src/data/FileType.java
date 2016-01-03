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
	k_folder(2),
	k_reference(3);
	
	private int num;
	private static Map<Integer, FileType> map;
	
	static
	{
		map = new HashMap<Integer, FileType>();
		map.put(1, FileType.k_file);
		map.put(2, FileType.k_folder);
		map.put(3, FileType.k_reference);
	}
	
	FileType(int num)
	{
		this.num = num;
	}

	public int toInt()
	{
		return num;
	}
	
	public static FileType toFileType(int i)
	{
		return map.get(i);
	}
}
