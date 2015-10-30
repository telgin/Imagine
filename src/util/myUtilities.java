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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class myUtilities
{
	/**
	 * @update_comment
	 * @param out
	 * @param list
	 * @return
	 */
	public static boolean writeListToFile(File out, List<String> list)
	{
		try
		{
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(out));
			for (String line : list)
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
	 * @param in
	 * @return
	 */
	public static ArrayList<String> readListFromFile(File in)
	{

		ArrayList<String> text = new ArrayList<String>();
		try
		{
			InputStreamReader fisr = new InputStreamReader(new FileInputStream(in));
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
	 * @param url
	 * @return
	 */
	public static ArrayList<String> readListFromURL(URL url)
	{

		ArrayList<String> text = new ArrayList<String>();
		try
		{
			URLConnection urlc = url.openConnection();
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
	 * @param url
	 * @return
	 */
	public static String readStringFromURL(URL url)
	{

		String text = "";
		try
		{
			URLConnection urlc = url.openConnection();
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
	 * @param inFile
	 * @return
	 */
	public static Object readObject(File inFile)
	{
		Object in = null;
		try
		{
			FileInputStream fStream = new FileInputStream(inFile);
			ObjectInputStream oObject = new ObjectInputStream(fStream);
			in = oObject.readObject();

			oObject.close();
			fStream.close();

			System.out.println("File Loaded: " + inFile.getPath());
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
	 * @param o
	 * @param outFile
	 * @return
	 */
	public static boolean writeObject(Object o, File outFile)
	{
		try
		{
			FileOutputStream fStream = new FileOutputStream(outFile);
			ObjectOutputStream oObject = new ObjectOutputStream(fStream);

			oObject.writeObject(o);

			oObject.close();
			fStream.close();
			System.out.println("File Saved: " + outFile.getPath());
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
	 * @param d
	 * @param decimals
	 * @return
	 */
	public static String formatPercent(double d, int decimals)
	{
		NumberFormat format = NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(decimals);
		return format.format(d);
	}
}
