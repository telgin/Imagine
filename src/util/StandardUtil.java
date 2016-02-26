package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import config.Constants;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * Misc. utilities for convenience. Mostly IO or formatting related.
 */
public abstract class StandardUtil
{
	/**
	 * Writes a list of strings to a file with one string per line
	 * @param p_out The output file
	 * @param p_list The list of lines
	 * @return The success status
	 */
	public static boolean writeListToFile(File p_out, List<String> p_list)
	{
		try
		{
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(p_out));
			for (String line : p_list)
			{
				osw.write(line + System.lineSeparator());
				osw.flush();
			}
			osw.close();
		}
		catch (FileNotFoundException e)
		{
			return false;
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Writes a string to a file
	 * @param p_out The output file
	 * @param p_str The string to write
	 * @return The success status
	 */
	public static boolean writeStringToFile(File p_out, String p_str)
	{
		try
		{
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(p_out));
			osw.write(p_str + System.lineSeparator());
			osw.flush();
			osw.close();
		}
		catch (FileNotFoundException e)
		{
			return false;
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}

	/**
	 * Reads a text file and gives a list of its lines
	 * @param p_in The input file
	 * @return The list of lines
	 */
	public static List<String> readListFromFile(File p_in)
	{
		List<String> text = new LinkedList<String>();
		try
		{
			InputStreamReader fisr = new InputStreamReader(new FileInputStream(p_in));
			BufferedReader br = new BufferedReader(fisr);

			String inLine = br.readLine();
			while (inLine != null)
			{
				text.add(inLine);
				inLine = br.readLine();
			}

			br.close();
		}
		catch (IOException e)
		{
			return null;
		}
		return text;
	}
	
	/**
	 * Reads a file in as a single string
	 * @param p_in The input file
	 * @return The string of the file
	 */
	public static String readStringFromFile(File p_in)
	{
		try
		{
			byte[] bytes = Files.readAllBytes(p_in.toPath());
			return new String(bytes, Constants.CHARSET);
		}
		catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * Reads a list of lines from a url
	 * @param p_url The input url
	 * @return The list of string lines
	 */
	public static List<String> readListFromURL(URL p_url)
	{
		List<String> text = new LinkedList<String>();
		try
		{
			URLConnection urlc = p_url.openConnection();
			InputStreamReader fisr = new InputStreamReader(urlc.getInputStream());
			BufferedReader br = new BufferedReader(fisr);

			String inLine = br.readLine();
			while (inLine != null)
			{
				text.add(inLine);
				inLine = br.readLine();
			}
		}
		catch (IOException e)
		{
			return null;
		}
		return text;
	}

	/**
	 * Reads in the contents of a url as a single string
	 * @param p_url The input url
	 * @return The content of the url as a string
	 */
	public static String readStringFromURL(URL p_url)
	{
		String text = "";
		try
		{
			URLConnection urlc = p_url.openConnection();
			InputStreamReader fisr = new InputStreamReader(urlc.getInputStream());
			BufferedReader br = new BufferedReader(fisr);

			String inLine = br.readLine();
			while (inLine != null)
			{
				text += inLine;
				inLine = br.readLine();
			}
		}
		catch (IOException e)
		{
			return null;
		}
		return text;

	}

	/**
	 * Reads in a serialized object from a file
	 * @param p_inFile The input file
	 * @return The object
	 */
	public static Object readObject(File p_inFile)
	{
		Object in = null;
		try
		{
			FileInputStream fStream = new FileInputStream(p_inFile);
			ObjectInputStream oObject = new ObjectInputStream(fStream);
			in = oObject.readObject();

			oObject.close();
			fStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return in;
	}

	/**
	 * Writes an object to a file using object serialization.
	 * @param p_object The object to write. (must support serialization)
	 * @param p_outFile The file to write to
	 * @return The success status
	 */
	public static boolean writeObject(Object p_object, File p_outFile)
	{
		try
		{
			FileOutputStream fStream = new FileOutputStream(p_outFile);
			ObjectOutputStream oObject = new ObjectOutputStream(fStream);

			oObject.writeObject(p_object);

			oObject.close();
			fStream.close();
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Formats a percent as a string in a standard way
	 * @param p_double The double to be interpreted as a percent
	 * @param p_decimals The number of decimals to use
	 * @return The formatted string
	 */
	public static String formatPercent(double p_double, int p_decimals)
	{
		NumberFormat format = NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(p_decimals);
		return format.format(p_double);
	}
	
	/**
	 * Formats an epoch date and time in a standard way: yyyy-MM-dd_HH.mm.ss.SSS
	 * Specifically, this way is more file safe because it contains no colon characters,
	 * which are disallowed in some file systems.
	 * @param p_millis The epoch date/time
	 * @return The formatted string
	 */
	public static String formatDateTimeFileSafe(long p_millis)
	{
		Date date = new Date(p_millis);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");
		return formatter.format(date);
	}
	
	/**
	 * Formats an epoch date and time in a standard way: MM/dd/yyyy HH:mm:ss.SSS
	 * @param p_millis The epoch date/time
	 * @return The formatted string
	 */
	public static String formatDateTime(long p_millis)
	{
		Date date = new Date(p_millis);
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		return formatter.format(date);
	}
	
	/**
	 * Formats an epoch date in a standard way: MM/dd/yyyy
	 * @param p_millis The epoch date/time
	 * @return The formatted string
	 */
	public static String formatDate(long p_millis)
	{
		Date date = new Date(p_millis);
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		return formatter.format(date);
	}
}
