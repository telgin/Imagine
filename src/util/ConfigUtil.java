package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Thomas Elgin (https://github.com/telgin)
 * @update_comment
 */
public abstract class ConfigUtil
{
	/**
	 * @update_comment
	 * @param p_inFile
	 * @return
	 */
	public static Document loadConfig(File p_inFile)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			return factory.newDocumentBuilder().parse(p_inFile);
		}
		catch (IOException | SAXException | ParserConfigurationException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @update_comment
	 * @return
	 */
	public static Document getNewDocument()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			return factory.newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e)
		{
			return null;
		}
	}

	/**
	 * @update_comment
	 * @param p_config
	 * @param p_outFile
	 * @return
	 */
	public static boolean saveConfig(Document p_config, File p_outFile)
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
							"4");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(p_config),
							new StreamResult(new FileOutputStream(p_outFile)));
			return true;
		}
		catch (IOException | TransformerException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @update_comment
	 * @param p_elements
	 * @return
	 */
	public static Element first(ArrayList<Element> p_elements)
	{
		if (p_elements.isEmpty())
			return null;
		return p_elements.get(0);
	}

	/**
	 * @update_comment
	 * @param p_elements
	 * @param p_name
	 * @param p_value
	 * @return
	 */
	public static ArrayList<Element> filterByAttribute(ArrayList<Element> p_elements,
					String p_name, String p_value)
	{
		ArrayList<Element> filtered = new ArrayList<Element>();
		for (Element e : p_elements)
			if (e.hasAttribute(p_name) && e.getAttribute(p_name).equals(p_value))
				filtered.add(e);

		return filtered;

	}

	/**
	 * @update_comment
	 * @param p_parent
	 * @param p_tag
	 * @return
	 */
	public static ArrayList<Element> children(Element p_parent, String p_tag)
	{
		ArrayList<Element> elements = new ArrayList<Element>();
		NodeList nodes = p_parent.getElementsByTagName(p_tag);

		for (int i = 0; i < nodes.getLength(); ++i)
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)
				elements.add((Element) nodes.item(i));

		return elements;
	}
}
