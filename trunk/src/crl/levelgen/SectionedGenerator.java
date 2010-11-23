package crl.levelgen;

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

import sz.util.Position;
import sz.util.Util;
import crl.level.LevelInfo;

public class SectionedGenerator {
	private final static int LEFT_TO_RIGHT = 0;
	private final static int LEFT_TO_UP = 1;
	private final static int DOWN_TO_UP = 2;
	private final static int DOWN_TO_RIGHT = 3;
	private final static int LEFT_TO_DOWN = 4;
	private final static int UP_TO_DOWN = 5;
	private final static int UP_TO_RIGHT = 6;
	
	
	public static SectionedGenerator thus = new SectionedGenerator();
	
	public SectionedGenerator(){
		initialize();
	}
	
	private Hashtable<String, LevelInfo> hashLevelsInfo = new Hashtable<String, LevelInfo>();
	private ArrayList<LevelInfo> levelsInfo = new ArrayList<LevelInfo>();
	public ArrayList<LevelInfo> getLevelsInfo (){
		return levelsInfo;
	}
	
	private final static Hashtable<String, Integer> compassDirections = new Hashtable<String, Integer>();
	static {
		compassDirections.put("l_r", LEFT_TO_RIGHT);
		compassDirections.put("l_d", LEFT_TO_DOWN);
		compassDirections.put("l_u", LEFT_TO_UP);
		compassDirections.put("d_u", DOWN_TO_UP);
		compassDirections.put("d_r", DOWN_TO_RIGHT);
		compassDirections.put("u_d", UP_TO_DOWN);
		compassDirections.put("u_r", UP_TO_RIGHT);
	}
	
	
	
	private final static Hashtable<String, Position> compassVars = new Hashtable<String, Position>();
	static {
		compassVars.put("l_r", new Position(1,0));
		compassVars.put("l_d", new Position(1,0));
		compassVars.put("l_u", new Position(1,0));
		compassVars.put("d_u", new Position(0,-1));
		compassVars.put("d_r", new Position(0,-1));
		compassVars.put("u_d", new Position(0,1));
		compassVars.put("u_r", new Position(0,1));
	}
	
	private boolean inSameDirection (int referenceDirection, int actualDirection){
		switch (referenceDirection){
		case LEFT_TO_RIGHT: case LEFT_TO_DOWN: case LEFT_TO_UP:
			return actualDirection == LEFT_TO_RIGHT || actualDirection == DOWN_TO_RIGHT || actualDirection == UP_TO_RIGHT;
		case DOWN_TO_UP: case DOWN_TO_RIGHT:
			return actualDirection == LEFT_TO_UP || actualDirection == DOWN_TO_UP;
		case UP_TO_DOWN: case UP_TO_RIGHT:
			return actualDirection == UP_TO_DOWN || actualDirection == LEFT_TO_DOWN;
		}
		return false;
	}
	public XLevelMetaData generateStructure(String levelId){
		LevelSectionInfo info = null;
		LevelInfo levelInfo = hashLevelsInfo.get(levelId);
		XLevelMetaData ret = new XLevelMetaData();
		ret.setLevelName(levelInfo.getName());
		ret.setMusicKey(levelInfo.getMusicKey());
		ArrayList<LevelSectionInfo> prelist = new ArrayList<LevelSectionInfo>();
		ret.setSections(prelist);
		
		ArrayList<Mosaic> mosaics = this.mosaics.get(levelId);
		
		int currentX = 0;
		int currentY = 0;
		int currentDirection = -1;
		for (int i = 0; i < mosaics.size(); i++){
			Mosaic m = mosaics.get(i);
			info = new LevelSectionInfo();
			if (m.getFeatureSet().equals("")){
				info.setFeature(readFeature(levelId, m.getFeature()));
				Position currentVar = compassVars.get(m.getCompass());
				currentX+=currentVar.x;
				currentY+=currentVar.y;
				info.setPosition(new Position(currentX, currentY));
				prelist.add(info);
				currentDirection = compassDirections.get(m.getCompass());
				
				
			} else {
				LevelFeature currentFeature = null;
				int currentSegments = 0;
				int segments = m.getSegments();
				int endingDirection = LEFT_TO_RIGHT;
				if (i-1 < mosaics.size()){
					Mosaic nextMosaic = mosaics.get(i+1);
					endingDirection = compassDirections.get(nextMosaic.getCompass());
				}
				String featureSet = m.getFeatureSet();
				Hashtable<String, Integer> chances = m.getChances();
				boolean finish = false;
				while (!finish){
					//if (endingDirection == currentDirection && currentSegments > segments){
					if (currentSegments > segments && inSameDirection(endingDirection,currentDirection)){	
						finish = true;
					}
					switch (currentDirection){
					case LEFT_TO_RIGHT: case DOWN_TO_RIGHT: case UP_TO_RIGHT:
						if (!finish && Util.chance(chances.get("l_u"))){
							currentDirection = LEFT_TO_UP;
							currentFeature = readFeature(levelId, featureSet+"_l_u");
						} else if (!finish && Util.chance(chances.get("l_d"))){
							currentDirection = LEFT_TO_DOWN;
							currentFeature = readFeature(levelId, featureSet+"_l_d");
						} else {
							currentDirection = LEFT_TO_RIGHT;
							currentFeature = readFeature(levelId, featureSet+"_l_r");
						}
						currentX++;
						break;
					case LEFT_TO_UP: case DOWN_TO_UP:
						if (!finish && Util.chance(chances.get("d_r"))){
							currentDirection = DOWN_TO_RIGHT;
							currentFeature = readFeature(levelId, featureSet+"_d_r");
						} else {
							currentDirection = DOWN_TO_UP;
							currentFeature = readFeature(levelId, featureSet+"_d_u");
						}
						currentY--;
						break;
					case LEFT_TO_DOWN: case UP_TO_DOWN:
						if (!finish && Util.chance(chances.get("u_r"))){
							currentDirection = UP_TO_RIGHT;
							currentFeature = readFeature(levelId, featureSet+"_u_r");
						} else {
							currentDirection = UP_TO_DOWN;
							currentFeature = readFeature(levelId, featureSet+"_u_d");
						}
						currentY++;
						break;
					}
					info = new LevelSectionInfo();
					info.setFeature(currentFeature);
					info.setPosition(new Position(currentX, currentY));
					prelist.add(info);
					currentSegments++;
				}
			}
		}
		ret.setBase(ret.normalize(20,12));
		ret.setCharmap(charmaps.get(levelId));
		return ret;
		

	}
	
	
	public static void main (String[] args){
		XLevelMetaData xlmd = thus.generateStructure("CUTMAN");
		ArrayList<LevelSectionInfo> s = xlmd.getSections();
		for (LevelSectionInfo info: s){
			System.out.println(info.getFeature() + " at "+info.getPosition().toString());
		}
	}
	
	public LevelFeature readFeature(String levelId, String featureId){
		return levels.get(levelId).get(featureId);
	}
	
	private Hashtable<String, Hashtable<String, LevelFeature>> levels = new Hashtable<String, Hashtable<String, LevelFeature>>(); 
	
	private Hashtable<String, Hashtable<String, String>> charmaps = new Hashtable<String, Hashtable<String, String>>();
	
	private Hashtable<String, ArrayList<Mosaic>> mosaics = new Hashtable<String, ArrayList<Mosaic>>();
	
	public void initialize(){
		try {
			File file = new File("data/levels.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList levelNodes = doc.getElementsByTagName("level");
			for (int s = 0; s < levelNodes.getLength(); s++) {
				Node levelNode = levelNodes.item(s);
				
				if (levelNode.getNodeType() == Node.ELEMENT_NODE) {
					Element levelElement = (Element) levelNode;
					LevelInfo li = new LevelInfo();
					li.setName (levelElement.getAttribute("name"));
					li.setPic1 (levelElement.getAttribute("pic1"));
					li.setPic2 (levelElement.getAttribute("pic2"));
					li.setPic3 (levelElement.getAttribute("pic3"));
					li.setPic4 (levelElement.getAttribute("pic4"));
					li.setPic5 (levelElement.getAttribute("pic5"));
					li.setMusicKey(levelElement.getAttribute("musicKey"));
					li.setId(levelElement.getAttribute("id"));
					li.setColor(Integer.parseInt(levelElement.getAttribute("color")));
					levelsInfo.add(li);
					hashLevelsInfo.put(li.getId(), li);
					
					String levelId = levelElement.getAttribute("id");
					
					Hashtable<String, String> charmap = new Hashtable<String, String>();
					NodeList charmappingNodes = levelElement.getElementsByTagName("charmapping");
					for (int t = 0; t < charmappingNodes.getLength(); t++) {
						Node charmappingNode = charmappingNodes.item(t);
						if (charmappingNode.getNodeType() == Node.ELEMENT_NODE) {
							Element charMappingElement = (Element) charmappingNode;
							charmap.put(charMappingElement.getAttribute("char"), charMappingElement.getAttribute("cell"));
						}
					}
					charmaps.put(levelId, charmap);
					
					ArrayList<Mosaic> mosaic = new ArrayList<Mosaic>();
					NodeList mosaicNodes = levelElement.getElementsByTagName("mosaic");
					for (int t = 0; t < mosaicNodes.getLength(); t++) {
						Node mosaicNode = mosaicNodes.item(t);
						if (mosaicNode.getNodeType() == Node.ELEMENT_NODE) {
							Element mosaicElement = (Element) mosaicNode;
							Mosaic m = new Mosaic();
							m.setCompass(mosaicElement.getAttribute("compass"));
							m.setFeature(mosaicElement.getAttribute("feature"));
							m.setFeatureSet(mosaicElement.getAttribute("featureSet"));
							if (mosaicElement.getAttribute("segments") != null && !mosaicElement.getAttribute("segments").equals(""))
								m.setSegments(Integer.parseInt(mosaicElement.getAttribute("segments")));
							
							NodeList chanceNodes = mosaicElement.getElementsByTagName("chance");
							Hashtable<String, Integer> chances = null;
							if (chanceNodes.getLength() > 0){
								chances = new Hashtable<String, Integer>();
							}
							for (int u = 0; u < chanceNodes.getLength(); u++) {
								Node chanceNode = chanceNodes.item(u);
								if (chanceNode.getNodeType() == Node.ELEMENT_NODE) {
									Element chanceElement = (Element) chanceNode;
									chances.put(chanceElement.getAttribute("compass"), Integer.parseInt(chanceElement.getAttribute("chance")));
								}
							}
							m.setChances(chances);
							mosaic.add(m);
						}
					}
					mosaics.put(levelId, mosaic);
					
					Hashtable<String, LevelFeature> levelFeatures = new Hashtable<String, LevelFeature>();
					NodeList segmentNodes = levelElement.getElementsByTagName("segment");
					for (int t = 0; t < segmentNodes.getLength(); t++) {
						Node segmentNode = segmentNodes.item(t);
						
						if (segmentNode.getNodeType() == Node.ELEMENT_NODE) {
							Element segmentElement = (Element) segmentNode;
							LevelFeature feature = new LevelFeature();
							String featureId = segmentElement.getAttribute("id");
							NodeList patternNodes = segmentElement.getElementsByTagName("pattern");
							for (int u = 0; u < patternNodes.getLength(); u++) {
								Node patternNode = patternNodes.item(u);
								ArrayList<String> rowsArrayList = new ArrayList<String>();
								if (patternNode.getNodeType() == Node.ELEMENT_NODE) {
									Element patternElement = (Element) patternNode;
									NodeList rowNodes = patternElement.getElementsByTagName("r");
									for (int v = 0; v < rowNodes.getLength(); v++) {
										String line = rowNodes.item(v).getTextContent();
										rowsArrayList.add(line);
									}
								}
								feature.addLayout(rowsArrayList);
							}
							levelFeatures.put(featureId, feature);
						}
					}
					levels.put(levelId, levelFeatures);
				}
			}
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}



