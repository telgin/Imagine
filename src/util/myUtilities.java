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
import java.util.List;

import config.Constants;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class myUtilities
{
	/**
	 * @update_comment
	 * @param p_out
	 * @param p_list
	 * @return
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
	 * @update_comment
	 * @param p_out
	 * @param p_str
	 * @return
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
	 * @update_comment
	 * @param p_in
	 * @return
	 */
	public static ArrayList<String> readListFromFile(File p_in)
	{
		ArrayList<String> text = new ArrayList<String>();
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
	 * @update_comment
	 * @param p_in
	 * @return
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
	 * @update_comment
	 * @param p_url
	 * @return
	 */
	public static ArrayList<String> readListFromURL(URL p_url)
	{

		ArrayList<String> text = new ArrayList<String>();
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
	 * @update_comment
	 * @param p_url
	 * @return
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
	 * @update_comment
	 * @param p_inFile
	 * @return
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
	 * @update_comment
	 * @param p_object
	 * @param p_outFile
	 * @return
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
	 * @update_comment
	 * @param p_d
	 * @param p_decimals
	 * @return
	 */
	public static String formatPercent(double p_d, int p_decimals)
	{
		NumberFormat format = NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(p_decimals);
		return format.format(p_d);
	}
	
	/**
	 * @update_comment
	 * @param p_millis
	 * @return
	 */
	public static String formatDateTimeFileSafe(long p_millis)
	{
		Date date = new Date(p_millis);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");
		return formatter.format(date);
	}
	
	/**
	 * @update_comment
	 * @param p_millis
	 * @return
	 */
	public static String formatDateTime(long p_millis)
	{
		Date date = new Date(p_millis);
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		return formatter.format(date);
	}
	
	/**
	 * @update_comment
	 * @param p_millis
	 * @return
	 */
	public static String formatDate(long p_millis)
	{
		Date date = new Date(p_millis);
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		return formatter.format(date);
	}
}
