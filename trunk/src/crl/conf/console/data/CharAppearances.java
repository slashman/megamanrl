package crl.conf.console.data;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import crl.ui.*;
import crl.ui.consoleUI.CharAppearance;

public class CharAppearances {
	public Appearance[] getAppearances() {

		try {
			File file = new File("data/charAppearances.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList appearancesNodes = doc.getElementsByTagName("charAppearance");
			ArrayList<CharAppearance> appearances = new ArrayList<CharAppearance>();
			for (int s = 0; s < appearancesNodes.getLength(); s++) {
				Node appearanceNode = appearancesNodes.item(s);
				if (appearanceNode.getNodeType() == Node.ELEMENT_NODE) {
					Element appearanceElement = (Element) appearanceNode;
					appearances.add(new CharAppearance(appearanceElement.getAttribute("id"), appearanceElement.getAttribute("char").charAt(0), colors.get(appearanceElement.getAttribute("color"))));
				}
			}
			return appearances.toArray(new CharAppearance[appearances.size()]);
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private final static Hashtable<String, Integer> colors = new Hashtable<String, Integer>();
	static {
		colors.put ("BLACK", 0);
		colors.put ("DARK_BLUE", 1);
		colors.put ("GREEN", 2);
		colors.put ("TEAL", 3);
		colors.put ("DARK_RED", 4);
		colors.put ("PURPLE", 5);
		colors.put ("BROWN", 6);
		colors.put ("LIGHT_GRAY", 7);
		colors.put ("GRAY", 8);
		colors.put ("BLUE", 9);
		colors.put ("LEMON", 10);
		colors.put ("CYAN", 11);
		colors.put ("RED", 12);
		colors.put ("MAGENTA", 13);
		colors.put ("YELLOW", 14);
		colors.put ("WHITE", 15);
	}
}