package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * A class to hold XML configuration utilities
 */
public abstract class ConfigUtil
{
	/**
	 * Loads an xml document from the given file
	 * @param p_inFile The xml file
	 * @return The xml document
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
	 * Creates a new xml document
	 * @return The xml document
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
	 * Saves an xml document in the specified file. This function allows for
	 * consistent styling parameters.
	 * @param p_config The xml document
	 * @param p_outFile The file to save to
	 * @return Success status
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
	 * Gets the first element in a list of elements
	 * @param p_elements The list of elements
	 * @return The first element, or null if the list is empty
	 */
	public static Element first(List<Element> p_elements)
	{
		if (p_elements.isEmpty())
			return null;
		return p_elements.get(0);
	}

	/**
	 * Filters a list of elements by elements which have a given attribute name and value
	 * @param p_elements The list of elements
	 * @param p_name The attribute name
	 * @param p_value The attribute value
	 * @return The filtered list of elements
	 */
	public static List<Element> filterByAttribute(List<Element> p_elements,
					String p_name, String p_value)
	{
		List<Element> filtered = new ArrayList<Element>();
		for (Element e : p_elements)
			if (e.hasAttribute(p_name) && e.getAttribute(p_name).equals(p_value))
				filtered.add(e);

		return filtered;

	}

	/**
	 * Gets a list of the children of an element filtered by a given tag
	 * @param p_parent The parent element
	 * @param p_tag The search tag
	 * @return The list of children
	 */
	public static List<Element> children(Element p_parent, String p_tag)
	{
		List<Element> elements = new ArrayList<Element>();
		NodeList nodes = p_parent.getElementsByTagName(p_tag);

		for (int i = 0; i < nodes.getLength(); ++i)
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)
				elements.add((Element) nodes.item(i));

		return elements;
	}
}
