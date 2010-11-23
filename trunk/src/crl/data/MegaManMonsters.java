package crl.data;

import crl.ai.ActionSelector;
import crl.feature.ai.NullSelector;
import crl.game.CRLException;
import crl.monster.*;
import crl.ui.AppearanceFactory;
import java.util.*;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MegaManMonsters {
	public static MonsterDefinition[] getBaseMonsters(String monsterFile) throws CRLException{
		BufferedReader br = null;
		try {
			Vector<MonsterDefinition> vecMonsters = new Vector<MonsterDefinition>(10);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(monsterFile)));
			String line = br.readLine();
			line = br.readLine();
			while (line != null){
				String[] data = line.split(";");
				MonsterDefinition def = new MonsterDefinition(data[0]);
				def.setAppearance(AppearanceFactory.getAppearanceFactory().getAppearance(data[1]));
				def.setDescription(data[2]);
				def.setLongDescription(data[3]);
				def.setWavOnHit(data[4]);
				def.setBloodContent(Integer.parseInt(data[5]));
				def.setDestroyOnImpact(data[6].equals("true"));
				def.setEthereal(data[7].equals("true"));
				def.setDamagesEnemies(data[8].equals("true"));
				def.setCanFly(data[9].equals("true"));
				def.setScore(Integer.parseInt(data[10]));
				def.setSightRange(Integer.parseInt(data[11]));
				def.setMaxHits(Integer.parseInt(data[12]));
				def.setAttack(Integer.parseInt(data[13]));
				def.setWalkCost(Integer.parseInt(data[14]));
				def.setAttackCost(Integer.parseInt(data[15]));
				def.setEvadeChance(Integer.parseInt(data[16]));
				def.setEvadeMessage(data[17]);
				def.setAutorespawnCount(Integer.parseInt(data[18]));
				
				vecMonsters.add(def);
				line = br.readLine();
			}
			return (MonsterDefinition[])vecMonsters.toArray(new MonsterDefinition[vecMonsters.size()]);
		} catch (IOException ioe){
			throw new CRLException("Error while loading data from monster file");
		} finally {
			try {
				br.close();
			} catch (IOException ioe){
				throw new CRLException("Error while loading data from monster file");
			}
		}
	}
	
	public static MonsterDefinition[] getMonsterDefinitions(String monsterDefFile, String monsterXMLAIFile) throws CRLException{
		try {
			MonsterDefinition[] monsters = getBaseMonsters(monsterDefFile);
			Hashtable<String, MonsterDefinition> hashMonsters = new Hashtable<String, MonsterDefinition>();
			for (int i = 0; i < monsters.length; i++){
				hashMonsters.put(monsters[i].getID(), monsters[i]);
			}
			
			
			File file = new File(monsterXMLAIFile);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList monsterNodes = doc.getElementsByTagName("monster");
			for (int s = 0; s < monsterNodes.getLength(); s++) {
				Node monsterNode = monsterNodes.item(s);
				if (monsterNode.getNodeType() == Node.ELEMENT_NODE) {
					Element monsterElement = (Element) monsterNode;
					MonsterDefinition def = hashMonsters.get(monsterElement.getAttribute("id"));
					String selectorClass = monsterElement.getAttribute("selectorClass");
					if (selectorClass != null && !selectorClass.equals("")){
						try {
							ActionSelector currentSelector = (ActionSelector) Class.forName(selectorClass).newInstance();
							def.setDefaultSelector(currentSelector);
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					} else {
						NodeList selectorNodes = monsterElement.getElementsByTagName("selector");
						Node selectorNode = selectorNodes.item(0);
						NodeList selectorImplNodes = selectorNode.getChildNodes();
						ActionSelector currentSelector = null;
						for (int t = 0; t < selectorImplNodes.getLength(); t++) {
							Node selectorImplNode = selectorImplNodes.item(t);
							if (selectorImplNode.getNodeType() == Node.ELEMENT_NODE) {
								Element selectorImplElement = (Element) selectorImplNode;
								String localName = selectorImplElement.getNodeName();
								if (localName.equals("sel_null")){
						        	currentSelector = new NullSelector();
						        } 
							}
						}
						def.setDefaultSelector(currentSelector);
					}
				}
			}
			return monsters;
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
