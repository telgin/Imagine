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

public abstract class ConfigUtil
{

	public static Document loadConfig(File inFile)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			return factory.newDocumentBuilder().parse(inFile);
		}
		catch (IOException | SAXException | ParserConfigurationException e)
		{
			e.printStackTrace();
			return null;
		}
	}

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

	public static boolean saveConfig(Document config, File outFile)
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
							"4");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(config),
							new StreamResult(new FileOutputStream(outFile)));
			return true;
		}
		catch (IOException | TransformerException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public static Element first(ArrayList<Element> elements)
	{
		if (elements.isEmpty())
			return null;
		return elements.get(0);
	}

	public static ArrayList<Element> filterByAttribute(ArrayList<Element> elements,
					String name, String value)
	{
		ArrayList<Element> filtered = new ArrayList<Element>();
		for (Element e : elements)
			if (e.hasAttribute(name) && e.getAttribute(name).equals(value))
				filtered.add(e);

		return filtered;

	}

	public static ArrayList<Element> children(Element parent, String tag)
	{
		ArrayList<Element> elements = new ArrayList<Element>();
		NodeList nodes = parent.getElementsByTagName(tag);

		for (int i = 0; i < nodes.getLength(); ++i)
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)
				elements.add((Element) nodes.item(i));

		return elements;
	}
}
