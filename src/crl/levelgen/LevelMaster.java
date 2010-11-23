package crl.levelgen;

import java.util.ArrayList;

import crl.levelgen.cave.*;
import crl.levelgen.featureCarve.CircularRoom;
import crl.levelgen.featureCarve.ColumnsRoom;
import crl.levelgen.featureCarve.FeatureCarveGenerator;
import crl.levelgen.featureCarve.RingRoom;
import crl.levelgen.featureCarve.RoomFeature;
import crl.levelgen.patterns.*;
import crl.item.*;
import sz.util.*;
import crl.level.*;
import crl.monster.Monster;
import crl.monster.MonsterFactory;
import crl.npc.*;
import crl.player.Player;
import crl.cuts.CaveEntranceSeal;
import crl.cuts.Unleasher;
import crl.feature.Feature;
import crl.feature.FeatureFactory;
import crl.game.*;

public class LevelMaster {
	//private static Dispatcher currentDispatcher;
	private static boolean firstCave = true;
	public static Level createLevel(LevelMetaData metadata, Player player) throws CRLException{
		
		String levelID = metadata.getLevelID();
		Debug.enterStaticMethod("LevelMaster", "createLevel");
		Debug.say("levelID "+levelID);
		boolean overrideLevelNumber = false;
		Level ret = null;
		PatternGenerator.getGenerator().resetFeatures();
		Respawner x = new Respawner(5, 90);		x
		.setSelector(new RespawnAI());
		boolean hasHostage = false;
		boolean isStatic = false;

		
		/*if (levelID.startsWith("BOMBMAN")){
			PatternGenerator.getGenerator().setup(SectionedGenerator.thus.generateStructure("BOMBMAN"));
			ret = PatternGenerator.getGenerator().createLevel();
			//ret.setDescription("Bombman Stage");
			//ret.setMusicKeyMorning("BOMBMAN");
			ret.setRutinary(false);
		}else if (levelID.startsWith("CUTMAN")){
			PatternGenerator.getGenerator().setup(SectionedGenerator.thus.generateStructure("CUTMAN"));
			ret = PatternGenerator.getGenerator().createLevel();
			ret.setDescription("Cutman Stage");
			ret.setMusicKeyMorning("CUTMAN");
			ret.setRutinary(false);
		}else if (levelID.startsWith("FIREMAN")){
			PatternGenerator.getGenerator().setup(SectionedGenerator.thus.generateStructure("FIREMAN"));
			ret = PatternGenerator.getGenerator().createLevel();
			ret.setDescription("Fireman Stage");
			ret.setMusicKeyMorning("FIREMAN");
			ret.setRutinary(false);
		} else {*/
			PatternGenerator.getGenerator().setup(SectionedGenerator.thus.generateStructure(levelID));
			ret = PatternGenerator.getGenerator().createLevel();
			ret.setRutinary(false);
		//}
		 
		
		
		if (hasHostage){
			Hostage hostage = NPCFactory.getFactory().buildHostage();
			hostage.setReward(100 * (Util.rand(100,150)/100));
			while (true){
				Position rand = new Position(Util.rand(5, ret.getWidth()), Util.rand(5, ret.getHeight()), Util.rand(0, ret.getDepth()));
				if (ret.isItemPlaceable(rand)){
					hostage.setPosition(rand);
					break;
				}
			}
			ret.addMonster(hostage);
		}
		ret.setID(levelID);
		if (!overrideLevelNumber)
			ret.setLevelNumber(metadata.getLevelNumber());
		
		if (ret.getExitFor("_BACK") != null){
			Position p = ret.getExitFor("_BACK");
			ret.removeExit("_BACK");
			ret.addExit(p, metadata.getExit("_BACK"));
			
		}
		
		if (ret.getExitFor("_NEXT") != null){
			Position p = ret.getExitFor("_NEXT");
			ret.removeExit("_NEXT");
			ret.addExit(p, metadata.getExit("_NEXT"));
		}
		ret.setMetaData(metadata);
		if (ret.isRutinary()){
			placeItems(ret, player);
			//ret.lightLights();
		}
		ret.addActor(new GravityActor(1));
		Debug.exitMethod(ret);
		return ret;

	}

	/*public static Dispatcher getCurrentDispatcher() {
		return currentDispatcher;
	}*/

	protected int placeKeys(Level ret){
		Debug.enterMethod(this, "placeKeys");
		//Place the magic Keys
		int keys = Util.rand(1,4);
		Position tempPosition = new Position(0,0);
		for (int i = 0; i < keys; i++){
			int keyx = Util.rand(1,ret.getWidth()-1);
			int keyy = Util.rand(1,ret.getHeight()-1);
			int keyz = Util.rand(0, ret.getDepth()-1);
			tempPosition.x = keyx;
			tempPosition.y = keyy;
			tempPosition.z = keyz;
			if (ret.isItemPlaceable(tempPosition)){
				Feature keyf = FeatureFactory.getFactory().buildFeature("KEY");
				keyf.setPosition(tempPosition.x, tempPosition.y, tempPosition.z);
				ret.addFeature(keyf);
			} else {
				i--;
			}
		}
		Debug.exitMethod(keys);
		return keys;
		
	}
	public static void placeItems(Level ret, Player player){
		int items = Util.rand(8,12);
		//int items = 300;
		for (int i = 0; i < items; i++){
			Item item = ItemFactory.getItemFactory().createItemForLevel(ret, player);
			if (item == null)
				break;
			int xrand = 0;
			int yrand = 0;
			Position pos = null;
			do {
				xrand = Util.rand(1, ret.getWidth()-1);
				yrand = Util.rand(1, ret.getHeight()-1);
				pos = new Position(xrand, yrand);
			} while (!ret.isItemPlaceable(pos));
			ret.addItem(pos, item);
		}
	}
	
	public static void lightCandles(Level l){
		int candles = (l.getHeight() * l.getWidth())/200;
		Position temp = new Position(0,0);
		for (int i = 0; i < candles; i++){
			temp.x = Util.rand(1, l.getWidth() -1);
			temp.y = Util.rand(1, l.getHeight() -1);
			if (!l.isItemPlaceable(temp)){
				i--;
				continue;
			}
				
			Feature vFeature = FeatureFactory.getFactory().buildFeature("CANDLE");
			vFeature.setPosition(temp.x, temp.y, temp.z);
			l.addFeature(vFeature);
		}
	}
	
	private static ArrayList getInnerQuartersRooms(){
		int rooms = Util.rand(12,15);
		//rooms = 3;
		crl.levelgen.featureCarve.Feature room = null;
		ArrayList ret = new ArrayList();
		String wall = "QUARTERS_WALL";
		String floor = "QUARTERS_FLOOR";
		String column = "MARBLE_COLUMN";
		String candle = "F_QUARTERS_FLOOR CANDLE";
		for (int i = 0; i < rooms; i++){
			switch (Util.rand(1,5)){
			case 1:
				room = new ColumnsRoom(Util.rand(5,12), Util.rand(5,12), floor, column);
				break;
			case 2:
				room = new CircularRoom(Util.rand(6,10), Util.rand(6,10), floor, wall);
				break;
			case 3:
				room = new RingRoom(Util.rand(6,12), Util.rand(6,12), floor, wall);
				break;
			case 4:
				room = new RoomFeature(Util.rand(5,12), Util.rand(5,12), floor);
				break;
			case 5:
				room = new ColumnsRoom(Util.rand(5,12), Util.rand(5,12), floor, candle);
				break;
			}
			ret.add(room);
		}
		return ret;
		
	}

	private static ArrayList getDungeonRooms(){
		int rooms = Util.rand(12,15);
		//rooms = 3;
		crl.levelgen.featureCarve.Feature room = null;
		ArrayList ret = new ArrayList();
		String floor = "DUNGEON_FLOOR";
		String candle = "F_DUNGEON_FLOOR CANDLE";
		String column = "DUNGEON_WALL";
		for (int i = 0; i < rooms; i++){
			switch (Util.rand(1,3)){
			case 1:
				room = new ColumnsRoom(Util.rand(5,12), Util.rand(5,12), floor, column);
				break;
			case 2:
				room = new RoomFeature(Util.rand(5,12), Util.rand(5,12), floor);
				break;
			case 3:
				room = new ColumnsRoom(Util.rand(5,12), Util.rand(5,12), floor, candle);
				break;
			}
			ret.add(room);
		}
		return ret;
		
	}
	
	private static ArrayList getClockTowerRooms(){
		int rooms = Util.rand(12,15);
		//rooms = 3;
		crl.levelgen.featureCarve.Feature room = null;
		ArrayList ret = new ArrayList();
		String wall = "TOWER_WALL";
		String floor = "TOWER_FLOOR";
		String candle = "F_TOWER_FLOOR CANDLE";
		
		for (int i = 0; i < rooms; i++){
			switch (Util.rand(1,3)){
			case 1:
				room = new CircularRoom(Util.rand(6,10), Util.rand(6,10), floor, wall);
				break;
			case 2:
				room = new RingRoom(Util.rand(6,12), Util.rand(6,12), floor, wall);
				break;
			case 3:
				room = new ColumnsRoom(Util.rand(5,12), Util.rand(5,12), floor, candle);
				break;
			}
			ret.add(room);
		}
		return ret;
		
	}
	
	private static ArrayList getWareHouseRooms(){
		int rooms = Util.rand(12,15);
		//rooms = 3;
		crl.levelgen.featureCarve.Feature room = null;
		
		ArrayList ret = new ArrayList();
		String floor = "WAREHOUSE_FLOOR";
		String column = "WAREHOUSE_WALL";
		String candle = "F_WAREHOUSE_FLOOR CANDLE";
		
		for (int i = 0; i < rooms; i++){
			switch (Util.rand(1,3)){
			case 1:
				room = new ColumnsRoom(Util.rand(5,12), Util.rand(5,12), floor, column);
				break;
			case 2:
				room = new RoomFeature(Util.rand(5,12), Util.rand(5,12), floor);
				break;
			case 3:
				room = new ColumnsRoom(Util.rand(5,12), Util.rand(5,12), floor, candle);
				break;
			}
			ret.add(room);
		}
		
		return ret;
		
	}
	
	private static ArrayList getSewersRooms(){
		int rooms = Util.rand(4,6);
		crl.levelgen.featureCarve.Feature room = null;
		
		ArrayList ret = new ArrayList();
		String floor = "SEWERS_FLOOR";
		String column = "SEWERS_WALL";
		
		for (int i = 0; i < rooms; i++){
			switch (Util.rand(1,2)){
			case 1:
				room = new ColumnsRoom(Util.rand(5,12), Util.rand(5,12), floor, column);
				break;
			case 2:
				room = new RingRoom(Util.rand(6,12), Util.rand(6,12), floor, column);;
				break;
			}
			ret.add(room);
		}
		
		return ret;
	}
	
	private static ArrayList getDeepSewersRooms(){
		int rooms = Util.rand(4,6);
		crl.levelgen.featureCarve.Feature room = null;
		
		ArrayList ret = new ArrayList();
		String floor = "SEWERS_FLOOR_WATER";
		String column = "SEWERS_WALL_WATER";
		
		for (int i = 0; i < rooms; i++){
			switch (Util.rand(1,2)){
			case 1:
				room = new CircularRoom(Util.rand(5,12), Util.rand(5,12), floor, column);
				break;
			case 2:
				room = new RingRoom(Util.rand(6,12), Util.rand(6,12), floor, column);;
				break;
			}
			ret.add(room);
		}
		
		return ret;
	}
}