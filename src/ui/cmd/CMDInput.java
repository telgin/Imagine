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
	private static Queue<String> simulatedLines = new LinkedList<String>();
	private static BufferedReader cin= new BufferedReader(new InputStreamReader(System.in));
	
	public static String getLine()
	{
		//simulate the user entering a line if any are queued
		if (!simulatedLines.isEmpty())
		{
			String simulated = simulatedLines.poll();
			System.out.println("[SIMULATED USER INPUT]: \"" + simulated + "\"");
			return simulated;
		}
			
		//read the actual line from stdin
		try
		{
			return cin.readLine();
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	public static void simulateLine(String line)
	{
		simulatedLines.add(line);
	}
}
