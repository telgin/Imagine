package ui.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Testing class which simulates user command line input.
 */
public abstract class CMDInput
{
	private static Queue<String> s_simulatedLines = new LinkedList<String>();
	private static BufferedReader s_cin = new BufferedReader(new InputStreamReader(System.in));
	
	/**
	 * Gets a line of input which may or may not be simulated
	 * @return The input line
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
	 * Simulates a line of input. These will stack up until they are all requested.
	 * @param p_line The line of user input to simulate
	 */
	public static void simulateLine(String p_line)
	{
		s_simulatedLines.add(p_line);
	}
}
