package util;

import groovy.util.Node;
import groovy.util.XmlNodePrinter;
import groovy.util.XmlParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public abstract class ConfigUtil {
	
	public static Node loadConfig(File inFile){
		try {
			return new XmlParser().parse(inFile);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Node getNewRootNode(){
		try {
			return new XmlParser().parseText("<Configuration/>");
		} catch (IOException | SAXException | ParserConfigurationException e) {
			return null;
		}
	}
	
	public static boolean saveConfig(Node config, File outFile){
		try {
			new XmlNodePrinter(new PrintWriter(new FileWriter(outFile))).print(config);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
