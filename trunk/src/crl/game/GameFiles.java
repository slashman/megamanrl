package crl.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import sz.util.Debug;
import sz.util.FileUtil;
import sz.util.SerializableChecker;
import crl.item.ItemDefinition;
import crl.monster.MonsterDefinition;
import crl.player.Equipment;
import crl.player.GameSessionInfo;
import crl.player.HiScore;
import crl.player.MonsterDeath;
import crl.player.Player;
import crl.player.Skill;
import crl.ui.Appearance;
import crl.ui.UserInterface;

public class GameFiles {
	public static HiScore[] loadScores(String hiscorefile){
		Debug.enterStaticMethod("GameFiles", "loadScores");
		HiScore[] ret = new HiScore[10];
        try{
            BufferedReader lectorArchivo = FileUtil.getReader(hiscorefile);
            for (int i = 0; i < 10; i++) {
            	String line = lectorArchivo.readLine();
            	String [] regs = line.split(";");
            	if (regs == null){
            		Game.crash("Invalid or corrupt hiscore table");
            	}
            	HiScore x = new HiScore();
            	x.setName(regs[0]);
            	x.setPlayerClass(regs[1]);
            	x.setScore(Integer.parseInt(regs[2]));
            	x.setDate(regs[3]);
            	x.setTurns(regs[4]);
            	x.setDeathString(regs[5]);
            	x.setDeathLevel(Integer.parseInt(regs[6]));
            	ret[i] = x;
            }
            Debug.exitMethod(ret);
            return ret;
        }catch(IOException ioe){
        	Game.crash("Invalid or corrupt hiscore table", ioe);
    	}
    	return null;
	}
	
	public static void saveHiScore (Player player, String hiscoreFile){
		Debug.enterStaticMethod("GameFiles", "saveHiscore");
		int score = player.getScore();
		String name = player.getName();
		String playerClass = "NONE";
		switch (player.getPlayerClass()){
		case Player.CLASS_INVOKER:
			playerClass="INV";
			break;
		case Player.CLASS_KNIGHT:
			playerClass="KNG";
			break;
		case Player.CLASS_MANBEAST:
			playerClass = "MNB";
			break;
		case Player.CLASS_RENEGADE:
			playerClass="RNG";
			break;
		case Player.CLASS_VAMPIREKILLER:
			playerClass="VKL";
			break;
		case Player.CLASS_VANQUISHER:
			playerClass = "VAN";
			break;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String now = sdf.format(new Date());

		HiScore [] scores = loadScores(hiscoreFile);

		try{
			BufferedWriter fileWriter = FileUtil.getWriter(hiscoreFile);
			for (int i = 0; i < 10; i++){
				if (score > scores[i].getScore()){
           			fileWriter.write(name+";"+playerClass+";"+score+";"+now+";"+player.getGameSessionInfo().getTurns()+";"+player.getGameSessionInfo().getShortDeathString()+";"+player.getGameSessionInfo().getDeathLevel());
           			fileWriter.newLine();
            		score = -1;
            		if (i == 9)
	            		break;
            	}
            	fileWriter.write(scores[i].getName()+";"+scores[i].getPlayerClass()+";"+scores[i].getScore()+";"+scores[i].getDate()+";"+scores[i].getTurns()+";"+scores[i].getDeathString()+";"+scores[i].getDeathLevel());
            	fileWriter.newLine();
            }
            fileWriter.close();
            Debug.exitMethod();
        }catch(IOException ioe){
        	ioe.printStackTrace(System.out);
			Game.crash("Invalid or corrupt hiscore table", ioe);
        }
	}

	public static void saveMemorialFile(Player player){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			String now = sdf.format(new Date());
			BufferedWriter fileWriter = FileUtil.getWriter("memorials/"+player.getName()+"("+now+").life");
			GameSessionInfo gsi = player.getGameSessionInfo();
			gsi.setDeathLevelDescription(player.getLevel().getDescription());
			String heshe = "It";
			
			fileWriter.write("/-----------------------------------");fileWriter.newLine();
			fileWriter.write(" MegamanRL"+Game.getVersion()+ " Post Mortem");fileWriter.newLine();
			fileWriter.write(" -----------------------------------/");fileWriter.newLine();
			fileWriter.newLine();fileWriter.newLine();
			fileWriter.write("Robot "+player.getName()+ " "+gsi.getDeathString()+" on the "+gsi.getDeathLevelDescription()+" ...");fileWriter.newLine();
			fileWriter.write(heshe+" survived for "+gsi.getTurns()+" turns and scored "+player.getScore()+" points" );fileWriter.newLine();
			fileWriter.newLine();
			Vector history = gsi.getHistory();
			for (int i = 0; i < history.size(); i++){
				fileWriter.write(heshe + " " + history.elementAt(i));
				fileWriter.newLine();
			}
			fileWriter.newLine();
			fileWriter.write(heshe +" destroyed "+gsi.getTotalDeathCount()+" enemies");fileWriter.newLine();
			
			int i = 0;
			Enumeration monsters = gsi.getDeathCount().elements();
			while (monsters.hasMoreElements()){
				MonsterDeath mons = (MonsterDeath) monsters.nextElement();
				fileWriter.write(mons.getTimes() +" "+mons.getMonsterDescription());fileWriter.newLine();
				
				i++;
			}
			fileWriter.newLine();
			Vector inventory = player.getInventory();
			fileWriter.newLine();
			fileWriter.write("-- Inventory --");fileWriter.newLine();
			fileWriter.write("Weapon    "+player.getEquipedWeaponDescription());fileWriter.newLine();
			
			for (Iterator iter = inventory.iterator(); iter.hasNext();) {
				Equipment element = (Equipment) iter.next();
				fileWriter.write(element.getQuantity()+ " - "+ element.getMenuDescription());fileWriter.newLine();
			}
			fileWriter.newLine();
			fileWriter.write("-- Last Messages --");fileWriter.newLine();
			Vector messages = UserInterface.getUI().getMessageBuffer();
			for (int j = 0; j < messages.size(); j++){
				fileWriter.write(messages.elementAt(j).toString());fileWriter.newLine();
			}
			
			fileWriter.close();
		} catch (IOException ioe){
			Game.crash("Error writing the memorial file", ioe);
		}
		
	}
	
	public static void saveGame(Game g, Player p){
		String filename = "savegame/"+p.getName()+".sav";
		p.setSelector(null);
		try {
			SerializableChecker sc = new SerializableChecker();
			sc.writeObject(g);
			sc.close();
			
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
			os.writeObject(g);
			os.close();
			
			
		} catch (IOException ioe){
			Game.crash("Error saving the game", ioe);
		}
	}
	
	public static void permadeath(Player p){
		String filename = "savegame/"+p.getName()+".sav";
		if (FileUtil.fileExists(filename)) {
			FileUtil.deleteFile(filename);
		}
	}
	
	public static void saveChardump(Player player){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			String now = sdf.format(new Date());
			BufferedWriter fileWriter = FileUtil.getWriter("memorials/"+player.getName()+"("+now+").life");
			GameSessionInfo gsi = player.getGameSessionInfo();
			gsi.setDeathLevelDescription(player.getLevel().getDescription());
			String heshe = "It";
			
			fileWriter.write("/-----------------------------------");fileWriter.newLine();
			fileWriter.write(" MegamanRL"+Game.getVersion()+ " Post Mortem");fileWriter.newLine();
			fileWriter.write(" -----------------------------------/");fileWriter.newLine();
			fileWriter.newLine();fileWriter.newLine();
			fileWriter.write("Robot "+player.getName()+ " "+gsi.getDeathString()+" on the "+gsi.getDeathLevelDescription()+" ...");fileWriter.newLine();
			fileWriter.write(heshe+" survived for "+gsi.getTurns()+" turns and scored "+player.getScore()+" points" );fileWriter.newLine();
			fileWriter.newLine();
			Vector history = gsi.getHistory();
			for (int i = 0; i < history.size(); i++){
				fileWriter.write(heshe + " " + history.elementAt(i));
				fileWriter.newLine();
			}
			fileWriter.newLine();
			fileWriter.write(heshe +" destroyed "+gsi.getTotalDeathCount()+" enemies");fileWriter.newLine();
			
			int i = 0;
			Enumeration monsters = gsi.getDeathCount().elements();
			while (monsters.hasMoreElements()){
				MonsterDeath mons = (MonsterDeath) monsters.nextElement();
				fileWriter.write(mons.getTimes() +" "+mons.getMonsterDescription());fileWriter.newLine();
				
				i++;
			}
			fileWriter.newLine();
			Vector inventory = player.getInventory();
			fileWriter.newLine();
			fileWriter.write("-- Inventory --");fileWriter.newLine();
			fileWriter.write("Weapon    "+player.getEquipedWeaponDescription());fileWriter.newLine();
			
			for (Iterator iter = inventory.iterator(); iter.hasNext();) {
				Equipment element = (Equipment) iter.next();
				fileWriter.write(element.getQuantity()+ " - "+ element.getMenuDescription());fileWriter.newLine();
			}
			fileWriter.newLine();
			fileWriter.write("-- Last Messages --");fileWriter.newLine();
			Vector messages = UserInterface.getUI().getMessageBuffer();
			for (int j = 0; j < messages.size(); j++){
				fileWriter.write(messages.elementAt(j).toString());fileWriter.newLine();
			}
			
			fileWriter.close();
		} catch (IOException ioe){
			Game.crash("Error writing the chardump", ioe);
		}
	}
			
	public static Hashtable getMonsterRecord(){
		Hashtable ret = new Hashtable();
        try{
            BufferedReader lectorArchivo = FileUtil.getReader("graveyard");
            String line = lectorArchivo.readLine();
            while (line != null){
            	String [] regs = line.split(",");
            	MonsterRecord x = new MonsterRecord();
            	x.setMonsterID(regs[0]);
            	x.setKilled(Integer.parseInt(regs[1]));
            	x.setKillers(Integer.parseInt(regs[2]));
            	ret.put(x.getMonsterID(), x);
            	line = lectorArchivo.readLine();
            }
            return ret;
        }catch(IOException ioe){
        	Game.crash("Invalid or corrupt graveyard", ioe);
    	}catch(NumberFormatException nfe){
        	Game.crash("Corrupt graveyard", nfe);
    	}
    	return null;
	}
	
	public static void updateGraveyard(Hashtable graveyard, GameSessionInfo gsi){
		Hashtable session = gsi.getDeathCount();
		Enumeration keys = session.keys();
		while (keys.hasMoreElements()){
			String monsterID = (String) keys.nextElement();
			MonsterDeath deaths = (MonsterDeath) gsi.getDeathCount().get(monsterID);
			MonsterRecord record = (MonsterRecord) graveyard.get(monsterID);
			if (record == null){
				record = new MonsterRecord();
				record.setMonsterID(monsterID);
				record.setKilled(deaths.getTimes());
				graveyard.put(monsterID, record);
			} else {
				record.setKilled(record.getKilled()+deaths.getTimes());
			}
		}
		if (gsi.getKillerMonster() != null){
			MonsterRecord record = (MonsterRecord) graveyard.get(gsi.getKillerMonster().getID());
			if (record == null){
				record = new MonsterRecord();
				record.setMonsterID(gsi.getKillerMonster().getID());
				record.setKillers(1);
				graveyard.put(gsi.getKillerMonster().getID(), record);
			}else{
				record.setKillers(record.getKillers()+1);
			}
		}
		
		// Save to file
		try{
			BufferedWriter fileWriter = FileUtil.getWriter("graveyard");
			Enumeration wKeys = graveyard.keys();
			while (wKeys.hasMoreElements()){
				MonsterRecord record = (MonsterRecord) graveyard.get(wKeys.nextElement());
				fileWriter.write(record.getMonsterID()+","+record.getKilled()+","+record.getKillers());
       			fileWriter.newLine();
			}
            fileWriter.close();
        }catch(IOException ioe){
        	ioe.printStackTrace(System.out);
			Game.crash("Invalid or corrupt graveyard", ioe);
        }
		
	}
}
