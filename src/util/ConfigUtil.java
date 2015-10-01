package util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class ConfigUtil {
	
	public static Document loadConfig(File inFile){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			return factory.newDocumentBuilder().parse(inFile);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Document getNewDocument(){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			return factory.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			return null;
		}
	}
	
	public static boolean saveConfig(Document config, File outFile){
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			
			transformer.transform(new DOMSource(config), new StreamResult(new FileOutputStream(outFile)));
			return true;
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
			return false;
		}
	}
}
