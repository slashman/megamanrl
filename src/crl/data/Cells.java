package crl.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import crl.level.Cell;
import crl.ui.AppearanceFactory;

public class Cells {
	public static Cell [] getCellDefinitions(AppearanceFactory apf){
		
		try {
			File file = new File("data/cells.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList cellNodes = doc.getElementsByTagName("mapcell");
			ArrayList<Cell> cells = new ArrayList<Cell>();
			for (int s = 0; s < cellNodes.getLength(); s++) {
				Node cellNode = cellNodes.item(s);
				if (cellNode.getNodeType() == Node.ELEMENT_NODE) {
					Element cellElement = (Element) cellNode;
					Cell cell = new Cell();
					cell.setID(cellElement.getAttribute("id"));
					cell.setAppearanceID(cellElement.getAttribute("appearance"));
					cell.setShortDescription(cellElement.getAttribute("shortDescription"));
					cell.setDescription(cellElement.getAttribute("Metal Block"));
					cell.setSolid(cellElement.getAttribute("solid").equals("true"));
					cell.setOpaque(cellElement.getAttribute("opaque").equals("true"));
					cell.setIsStair(cellElement.getAttribute("stair").equals("true"));
					cell.setRound(cellElement.getAttribute("round").equals("true"));
					cell.setSpike(cellElement.getAttribute("spike").equals("true"));
					cells.add(cell);
				}
			}
			return cells.toArray(new Cell[cells.size()]);
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

}
