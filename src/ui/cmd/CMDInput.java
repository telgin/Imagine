package ui.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class CMDInput
{
	private static Queue<String> s_simulatedLines = new LinkedList<String>();
	private static BufferedReader s_cin = new BufferedReader(new InputStreamReader(System.in));
	
	/**
	 * @update_comment
	 * @return
	 */
	public static String getLine()
	{
		//simulate the user entering a line if any are queued
		if (!s_simulatedLines.isEmpty())
		{
			String simulated = s_simulatedLines.poll();
			System.out.println("[SIMULATED USER INPUT]: \"" + simulated + "\"");
			return simulated;
		}
			
		//read the actual line from stdin
		try
		{
			return s_cin.readLine();
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	/**
	 * @update_comment
	 * @param p_line
	 */
	public static void simulateLine(String p_line)
	{
		s_simulatedLines.add(p_line);
	}
}
