package crl.levelgen;

import java.util.*;

import sz.util.*;
import crl.player.*;
import crl.level.*;
import crl.levelgen.patterns.BombManStage;
import crl.monster.*;
import crl.game.*;
import crl.item.Item;
import crl.item.ItemFactory;
import crl.feature.*;


public class PatternGenerator extends LevelGenerator{
	private static PatternGenerator singleton = new PatternGenerator();

	private Hashtable charMap;
	private Vector assignedFeatures = new Vector();
	private LevelFeature baseFeature;
	private boolean hasBoss;
	private String levelName;
	private String levelMusic;

	public void resetFeatures(){
		assignedFeatures.removeAllElements();
		baseFeature = null;
		hasBoss = false;
	}

	public void assignFeature(LevelFeature lf, Position where){
		assignedFeatures.add(new AssignedFeature(where, lf));
	}

	//private Position startPosition, endPosition;

	private Feature endFeature;

	public static PatternGenerator getGenerator(){
		return singleton;
    }

	public Level createLevel() throws CRLException{
		Debug.enterMethod(this, "createLevel");
		//draw the base feature
		StaticGenerator sg = StaticGenerator.getGenerator();
		sg.reset();
		sg.setCharMap(charMap);
		//sg.setFlatLevel(baseFeature.getALayout());
		sg.setLevel(baseFeature.getALayout());
		Level ret = sg.createLevel();
		
		Cell[][][] cmap = ret.getCells();
		Enumeration en = assignedFeatures.elements();
		while (en.hasMoreElements()){
			AssignedFeature af = (AssignedFeature) en.nextElement();
			drawFeature (af.getFeature(), af.getPosition(), ret);
		}

		ret.setCells(cmap);
		
		//ret.setPositions(startPosition, endPosition);
		/*if (!hasBoss){
			int keysOnLevel = placeKeys(ret);
			if (endFeature != null)
				endFeature.setKeyCost(keysOnLevel);
		} else
			if (endFeature != null)
				endFeature.setKeyCost(1);*/
		ret.setDescription(getLevelName());
		ret.setMusicKeyMorning(getLevelMusic());
		Debug.exitMethod(ret);
		return ret;
	}
	

	protected void drawFeature(LevelFeature what, Position where, Level level){
		Cell[][][] canvas = level.getCells();
		String [][] map = what.getALayout();
		for (int z = 0; z < map.length; z++)
			for (int y=0; y < map[0].length; y++){
				for (int x = 0; x < map[0][0].length(); x++) {
					if (map[z][y].charAt(x) == ' ')
						continue;
 					//Debug.say(map[z][y].charAt(x));
					String[] cmds = null;
					try {
						cmds = ((String)charMap.get(map[z][y].charAt(x)+"")).split(" ");
					} catch (NullPointerException npex){
						npex.printStackTrace();
						System.out.println("Critical Info: ");
						System.out.println("charMap "+charMap);
						System.out.println("map[z][y] {"+map[z][y]+"}");
						System.out.println("map[z][y].charAt(x) {"+map[z][y].charAt(x)+"}");
						System.out.println("charMap.get(map[z][y].charAt(x) "+charMap.get(map[z][y].charAt(x)));
						
						for (int yy=0; yy < map[0].length; yy++){
							System.out.println(map[0][yy]);
						}
						
						
					}
					if (!cmds[0].equals("NOTHING"))
						try {
							canvas[where.z+z][x+where.x][y+where.y] = MapCellFactory.getMapCellFactory().getMapCell(cmds[0]);
						} catch (CRLException crle){
							Debug.byebye("Exception creating the level "+crle);
						}
					if (cmds.length > 1){
						if (cmds[1].equals("FEATURE")){
							if (cmds.length < 4 || Util.chance(Integer.parseInt(cmds[3]))){
								Feature vFeature = FeatureFactory.getFactory().buildFeature(cmds[2]);
								vFeature.setPosition(x+where.x,y+where.y,where.z+z);
								if (cmds.length > 4){
									//Debug.say("Hi... i will set the cost");
									if (cmds[4].equals("COST")) {
										//Debug.say("Hi... i did it to "+vFeature);
										vFeature.setKeyCost(Integer.parseInt(cmds[5]));
									}
								}
								level.addFeature(vFeature);
							}

						} else
						if (cmds[1].equals("COST")){
							canvas[where.z+z][x+where.x][y+where.y].setKeyCost(Integer.parseInt(cmds[2]));
						} else
						if (cmds[1].equals("REMOVE_FEATURE")){
							level.destroyFeature(level.getFeatureAt(new Position(where.x+x, where.y+y, where.z+z)));
							
						} else
						if (cmds[1].equals("ITEM")){
							Item vItem = ItemFactory.getItemFactory().createItem(cmds[2]);
							if (vItem != null)
								level.addItem(new Position(where.x+x,where.y+y,where.z), vItem);
						}else
							
						if (cmds[1].equals("MONSTER")){
							int chance = 100;
							if (cmds.length > 3){
								chance = Integer.parseInt(cmds[3]);
							}
							if (Util.chance(chance)){
								Monster toAdd = MonsterFactory.getFactory().buildMonster(cmds[2]);
								toAdd.setPosition(x+where.x,y+where.y,z+where.z);
								level.addMonster(toAdd);
							}
						} else
						if (cmds[1].equals("ENEMYGEN")){
							int chance = 100;
							if (cmds.length > 4){
								chance = Integer.parseInt(cmds[4]);
							}
							if (Util.chance(chance)){
								EnemyGenerator toAdd = new EnemyGenerator(cmds[2], Integer.parseInt(cmds[3]));
								toAdd.setPosition(x+where.x,y+where.y,z+where.z);
								level.addActor(toAdd);
							}
						} else
							
						if (cmds[1].equals("EXIT")){
							level.addExit(new Position(x+where.x,y+where.y,z+where.z), cmds[2]);
						} else
						if (cmds[1].equals("EOL")){
							level.addExit(new Position(x+where.x,y+where.y,z+where.z), "_NEXT");
							endFeature = FeatureFactory.getFactory().buildFeature(cmds[2]);
							endFeature.setPosition(x+where.x,y+where.y,where.z+z);
							if (cmds.length > 3){
								//Debug.say("Hi... i will set the cost");
								if (cmds[3].equals("COST")) {
									//Debug.say("Hi... i did it to "+vFeature);
									endFeature.setKeyCost(Integer.parseInt(cmds[4]));
								}
							}
							level.addFeature(endFeature);
						}
					}
				}
			}
	}

	public void setCharMap(Hashtable value) {
		charMap = value;
	}

	public void setBaseFeature(LevelFeature value) {
		baseFeature = value;
	}

	public boolean hasBoss() {
		return hasBoss;
	}

	public void setHasBoss(boolean hasBoss) {
		this.hasBoss = hasBoss;
	}
	
	public void setup(XLevelMetaData md){
		setBaseFeature(md.getBase());
		for (LevelSectionInfo sectionInfo: md.getSections()){
			assignFeature(sectionInfo.getFeature(), sectionInfo.getPosition());
		}
		setLevelName(md.getLevelName()+" stage");
		setLevelMusic(md.getMusicKey());
		setCharMap(md.getCharmap());
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getLevelMusic() {
		return levelMusic;
	}

	public void setLevelMusic(String levelMusic) {
		this.levelMusic = levelMusic;
	}
}