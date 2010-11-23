package crl.ui.consoleUI;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTextArea;

import crl.Main;
import crl.player.GameSessionInfo;
import crl.player.HiScore;
import crl.player.Player;
import crl.player.advancements.Advancement;
import crl.ui.CommandListener;
import crl.ui.Display;
import crl.ui.UserInterface;
import crl.ui.consoleUI.cuts.CharChat;
import crl.level.Level;
import crl.level.LevelInfo;
import crl.levelgen.SectionedGenerator;
import crl.monster.Monster;
import crl.npc.*;
import crl.conf.console.data.CharCuts;
import crl.game.Game;
import crl.game.GameFiles;
import crl.game.MonsterRecord;
import crl.game.STMusicManagerNew;
import crl.item.ItemDefinition;
import crl.item.ItemFactory;
import sz.csi.CharKey;
import sz.csi.ConsoleSystemInterface;
import sz.csi.textcomponents.TextBox;
import sz.util.Position;
import sz.util.ScriptUtil;
import sz.util.Util;

public class CharDisplay extends Display{
	private ConsoleSystemInterface si;
	
	public CharDisplay(ConsoleSystemInterface si){
		this.si = si;
	}
	
	public int showTitleScreen(){
		si.cls();
		si.print(20,3,"    ____  _", ConsoleSystemInterface.BLUE);
		si.print(20,4,"    \\   \\| |", ConsoleSystemInterface.BLUE);
		si.print(20,5,"     | > | |", ConsoleSystemInterface.BLUE);
		si.print(20,6,"MEGAMAN  < \\_", ConsoleSystemInterface.BLUE);
		si.print(20,7,"     |_|\\_\\__|", ConsoleSystemInterface.BLUE);

		si.print(20,12, "a. New Game", ConsoleSystemInterface.WHITE);
		si.print(20,13, "b. Continue", ConsoleSystemInterface.WHITE);
		si.print(20,14, "c. HiScores", ConsoleSystemInterface.WHITE);
		si.print(20,15, "d. Quit", ConsoleSystemInterface.WHITE);
		
    	String messageX = "'Megaman' is a trademark of Capcom Corporation.";
		si.print((80 - messageX.length()) / 2,20, messageX, ConsoleSystemInterface.BLUE);
		messageX = "MegamanRL v"+Game.getVersion()+", Developed by Santiago Zapata 2008";
		si.print((80 - messageX.length()) / 2,21, messageX, ConsoleSystemInterface.WHITE);
		messageX = "Midi Tracks by Jorge E. Fuentes, Arrow and X";
		si.print((80 - messageX.length()) / 2,22, messageX, ConsoleSystemInterface.WHITE);

		si.refresh();
    	STMusicManagerNew.thus.playKey("TITLE");
    	
    	CharKey x = new CharKey(CharKey.NONE);
		while (x.code != CharKey.A && x.code != CharKey.a &&
				x.code != CharKey.C && x.code != CharKey.c &&
				x.code != CharKey.D && x.code != CharKey.d 
				)
			x = si.inkey();
		si.cls();
		switch (x.code){
		case CharKey.A: case CharKey.a:
			return 0;
		case CharKey.B: case CharKey.b:
			return 1;
		case CharKey.C: case CharKey.c:
			return 2;
		case CharKey.D: case CharKey.d:
			return 3;
		}
		return 0;
		
	}
	
	public void showIntro(Player player){
		si.cls();
		printBars();
		si.print(32,2, "Prologue", ConsoleSystemInterface.DARK_RED);
		
		TextBox tb1 = new TextBox(si);
		tb1.setPosition(2,4);
		tb1.setHeight(3);
		tb1.setWidth(76);
		tb1.setForeColor(ConsoleSystemInterface.RED);
		tb1.setText("In the year of 1691, a dark castle emerges from the cursed soils of the plains of Transylvannia."+
				" Chaos and death spread along the land, as the evil count Dracula unleases his powers, turning it into a pool of blood");
		
		TextBox tb2 = new TextBox(si);
		tb2.setPosition(2,8);
		tb2.setHeight(4);
		tb2.setWidth(76);
		tb2.setForeColor(ConsoleSystemInterface.RED);
		tb2.setText("The trip to the castle was long and harsh, after enduring many challenges through all Transylvannia, "+
		"you are close to the castle of chaos. You are almost at Castlevania, and you are here on business: " + 
		"To destroy forever the Curse of the Evil Count.");
		       
		TextBox tb = new TextBox(si);
		tb.setPosition(2,13);
		tb.setHeight(4);
		tb.setWidth(76);
		tb.setForeColor(ConsoleSystemInterface.RED);
		tb.setText(player.getPlot()+", "+player.getDescription()+" journeys to the cursed castle.");
		tb1.draw();
		tb2.draw();
		tb.draw();
		si.print(2,18, "[Press Space]", ConsoleSystemInterface.BLUE);
		si.refresh();
		si.waitKey(CharKey.SPACE);
		si.cls();
	}
	
	public boolean showResumeScreen(Player player){
		GameSessionInfo gsi = player.getGameSessionInfo();
		si.cls();
		
		String heshe = "It";
		
		si.print(2,3, "Dr. Light Experiment Record: "+player.getName(), ConsoleSystemInterface.BLUE);
		
		TextBox tb = new TextBox(si);
		tb.setPosition(2,5);
		tb.setHeight(3);
		tb.setWidth(70);
		tb.setForeColor(ConsoleSystemInterface.WHITE);
		tb.setText("This robot "+gsi.getDeathString()+" on the "+player.getLevel().getDescription()+"...");
		tb.draw();
		
		si.print(2,9, heshe+ " scored "+ player.getScore() +" points and survived for "+gsi.getTurns()+" turns ", ConsoleSystemInterface.WHITE);
		si.print(2,11, heshe + " destroyed "+gsi.getTotalDeathCount()+" enemies", ConsoleSystemInterface.WHITE);
/*
        int i = 0;
		Enumeration monsters = gsi.getDeathCount().elements();
		while (monsters.hasMoreElements()){
			MonsterDeath mons = (MonsterDeath) monsters.nextElement();
			si.print(5,11+i, mons.getTimes() +" "+mons.getMonsterDescription(), ConsoleSystemInterface.RED);
			i++;
		}
*/		si.print(2,14, "Store experiment info? [Y/N]");
		si.refresh();
		return UserInterface.getUI().prompt();
	}

	public void showEndgame(Player player){
		si.cls();
		printBars();
		String heshe = (player.getSex() == Player.MALE ? "he" : "she");
		String hisher = (player.getSex() == Player.MALE ? "his" : "her");

		si.print(2,3, "                           ", ConsoleSystemInterface.RED);
		
		TextBox tb = new TextBox(si);
		tb.setPosition(2,5);
		tb.setHeight(8);
		tb.setWidth(76);
		tb.setForeColor(ConsoleSystemInterface.RED);
		
		tb.setText(player.getName()+ " made many sacrifices, but now the long fight is over. Dracula is dead "+
				"and all other spirits are asleep. In the shadows, a person watches the castle fall. "+
				player.getName()+" must go for now but "+heshe+" hopes someday "+heshe+" will get the "+
				"respect that "+heshe+" deserves.    After this fight the new Belmont name shall be honored "
				+"by all people.");
		tb.draw();
		si.print(2,15, "You played the greatest role in this history... ", ConsoleSystemInterface.RED);
		si.print(2,16, "Thank you for playing.", ConsoleSystemInterface.RED);

		si.print(2,17, "CastlevaniaRL: v"+Game.getVersion(), ConsoleSystemInterface.RED);
		si.print(2,18, "Santiago Zapata 2005-2007", ConsoleSystemInterface.RED);

		si.print(2,20, "[Press Space]", ConsoleSystemInterface.BLUE);
		si.refresh();
		si.waitKey(CharKey.SPACE);
		si.cls();

	}
	
	public void showHiscores(HiScore[] scores){
		si.cls();


		si.print(2,1, "MegamanRL "+Game.getVersion(), ConsoleSystemInterface.BLUE);
		si.print(2,2, "Hi Scores", ConsoleSystemInterface.BLUE);

		si.print(13,4, "Score");
		//si.print(21,4, "Score");
		si.print(25,4, "Date");	
		si.print(36,4, "Turns");
		si.print(43,4, "Death");

		for (int i = 0; i < scores.length; i++){
			si.print(2,5+i, scores[i].getName(), ConsoleSystemInterface.BLUE);
			si.print(13,5+i, ""+scores[i].getScore());
			si.print(25,5+i, ""+scores[i].getDate());
			si.print(36,5+i, ""+scores[i].getTurns());
			si.print(43,5+i, (""+scores[i].getDeathString()+" on level "+scores[i].getDeathLevel()));

		}
		si.print(2,23, "[Press Space]");
		si.refresh();
		si.waitKey(CharKey.SPACE);
		si.cls();
	}
	
	public void showHelp(){
		si.cls();
		//printBars();

		
		si.print(0,1,  "                                                                                ");
		si.print(0,2,  "                                                                                ");
		si.print(0,3,  "                              * - HELP - *                                      ", ConsoleSystemInterface.RED);
		si.print(0,4,  "                                                                                ");
		si.print(0,5,  "                                                                                ");
		si.print(0,6,  "        -- ACTIONS --                             -- MOVEMENT --                ", ConsoleSystemInterface.RED);
		si.print(0,7,  "                                                                      7 8 9     ");
		si.print(0,8,  "  ( ) Action: Aim mystic or ranged weapon                              \\|/      ");
		si.print(0,9,  "  (a) Attack: Uses a weapon                     Arrow Keys or Numpad  4-@-6     ");
		si.print(0,10, "  (d) Drop: Drops an item                                              /|\\      ");
		si.print(0,11, "  (D) Dive: Dives into deep water                                     1 2 3     ");
		si.print(0,12, "                                                  -- COMMANDS --                ", ConsoleSystemInterface.RED);
		si.print(0,12, "  (e) Equip: Wears equipment               ");
		si.print(0,13, "  (f) Fire: Aims a ranged weapon                                                ");
		si.print(0,14, "  (g) Get: Picks up an item                (c) Character info: Player attributes");
		si.print(0,15, "  (j) Jump: Jumps, emerge, fly             (i) Inventory: Shows the inventory   ");
		si.print(0,16, "  (r) Reload: Reloads a given weapon       (l) Look: Identifies map symbols 	");
		si.print(0,17, "  (s) Skills: Use character skills         (m) Messages: Latest messages        ");
		si.print(0,18, "  (t) Throw: Throws an Item                (M) Map: Shows the castle map        ");
		si.print(0,19, "  (u) Use: Uses an Item                    (Q) Quit: Exits game                 ");
		si.print(0,19, "  (U) Unequip: Take off an item            (S) Save: Saves game                 ");
		si.print(0,20, "  (x) Switch: Primary for secondary weapon (T) Switch music: Turns on/off music ");
		si.print(0,21, "                                                                                ");
		si.print(0,22, "                                                                                ");
		si.print(0,23, "                                            [ Press Space to Continue ]         ", ConsoleSystemInterface.RED);
		si.print(0,24, "                                                                                ");

		si.refresh();
		si.waitKey(CharKey.SPACE);
	}
	
	public void init(ConsoleSystemInterface syst){
		si = syst;
	}
	
	public void printBars(){
 		si.print(0,0,  "[==============================================================================]", ConsoleSystemInterface.WHITE);
		si.print(0,1,  "  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]", ConsoleSystemInterface.WHITE);
		
		si.print(0,23, "  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]  [==]", ConsoleSystemInterface.WHITE);
		si.print(0,24, "[==============================================================================]", ConsoleSystemInterface.WHITE);
	}
	
	public void showDraculaSequence(){
		TextBox tb = new TextBox(si);
		tb.setBounds(3,4,40,10);
		tb.setBorder(true);
		tb.setText("Ahhh... a human... the first one to get this far. HAHAHAHA! Now it is time to die!");
		tb.draw();
		si.refresh();
		si.waitKey(CharKey.SPACE);
	}
	
	public void showNewDay(){
		TextBox tb = new TextBox(si);
		tb.setBounds(3,4,30,10);
		tb.setBorder(true);
		tb.setText("THE MORNING SUN HAS VANQUISHED THE HORRIBLE NIGHT...");
		tb.draw();
		si.refresh();
		si.waitKey(CharKey.SPACE);
	}
	
	public void showBoxedMessage(String title, String msg, int x, int y, int w, int h){
		TextBox tb = new TextBox(si);
		tb.setBounds(x,y,w,h);
		tb.setBorder(true);
		tb.setText(msg);
		tb.setTitle(title);
		tb.draw();
		si.refresh();
		si.waitKey(CharKey.SPACE);
	}
	
	public void showHorribleNight(){
		TextBox tb = new TextBox(si);
		tb.setBounds(3,4,30,10);
		tb.setBorder(true);
		tb.setText("... WHAT A HORRIBLE NIGHT TO HAVE A CURSE");
		tb.draw();
		si.refresh();
		si.waitKey(CharKey.SPACE);
	}
	
	public int showSavedGames(File[] saveFiles){
		si.cls();
		printBars();
		if (saveFiles == null || saveFiles.length == 0){
			si.print(3,6, "No adventurers available");
			si.print(4,8, "[Space to Cancel]");
			si.refresh();
			si.waitKey(CharKey.SPACE);
			return -1;
		}
			
		si.print(3,6, "Pick an adventurer");
		for (int i = 0; i < saveFiles.length; i++){
			si.print(5,7+i, (char)(CharKey.a+i+1)+ " - "+ saveFiles[i].getName());
		}
		si.print(3,9+saveFiles.length, "[Space to Cancel]");
		si.refresh();
		CharKey x = si.inkey();
		while ((x.code < CharKey.a || x.code > CharKey.a+saveFiles.length) && x.code != CharKey.SPACE){
			x = si.inkey();
		}
		si.cls();
		if (x.code == CharKey.SPACE)
			return -1;
		else
			return x.code - CharKey.a;
	}
	
	public void showHostageRescue(Hostage h){
		TextBox tb = new TextBox(si);
		tb.setBounds(3,4,30,10);
		tb.setBorder(true);
		
		String text = "Thanks for rescuing me! I will give you "+h.getReward()+" gold, it is all I have!";
		if (h.getItemReward() != null)
			text += "\n\n\nTake this, the "+h.getItemReward().getDescription()+", I found it inside the castle ";
		tb.setText(text);
		tb.draw();
		si.refresh();
		si.waitKey(CharKey.SPACE);	
	}
	
	public Advancement showLevelUp(Vector advancements){
		
		si.saveBuffer();
		si.cls();
		si.print(1,1, "You have gained a change to pick an advancement!", ConsoleSystemInterface.BLUE);
		
		for (int i = 0; i < advancements.size(); i++){
			si.print(1,3+i*2, ((char)('a'+i))+". "+((Advancement)advancements.elementAt(i)).getName());
			si.print(1,4+i*2, ((Advancement)advancements.elementAt(i)).getDescription());
		}
		si.refresh();
		int choice = readAlphaToNumber(advancements.size());
		si.restore();			
		return (Advancement)advancements.elementAt(choice);
		/*
		ItemDefinition[] defs = new ItemDefinition[soulIds.size()];
		for (int i = 0; i < defs.length; i++){
			defs[i] = ItemFactory.getItemFactory().getDefinition((String)soulIds.elementAt(i));
		}
		si.cls();
		printBars();
    
		si.print(2,3, " - - Level Up - -", ConsoleSystemInterface.RED);
		si.print(2,5,  "Please pick a spiritual memento");
		
		for (int i = 0; i < defs.length; i++){
			si.print(2,7+i, (char)('a'+i) + ") ", ConsoleSystemInterface.WHITE);
			si.print(5,7+i,  ((CharAppearance)defs[i].getAppearance()).getChar(), ((CharAppearance)defs[i].getAppearance()).getColor());
			si.print(7,7+i,  defs[i].getDescription() + ": " + defs[i].getMenuDescription());
		} 
		si.refresh();
		
		int choice = readAlphaToNumber(defs.length);
		si.cls();
		return choice;*/
	}
	
	private int readAlphaToNumber(int numbers){
		while (true){
			CharKey key = si.inkey();
			if (key.code >= CharKey.A && key.code <= CharKey.A + numbers -1){
				return key.code - CharKey.A;
			}
			if (key.code >= CharKey.a && key.code <= CharKey.a + numbers -1){
				return key.code - CharKey.a;
			}
		}
	}
	
	public void showChat(String chatID, Game game){
		si.saveBuffer();
		CharChat chat = CharCuts.thus.get(chatID);
		TextBox tb = new TextBox(si);
		tb.setBounds(3,4,40,10);
		tb.setBorder(true);
		String[] marks = new String[] {"%NAME", "%%INTRO_1", "%%CLARA1"};
		String[] replacements = new String[] {game.getPlayer().getName(), game.getPlayer().getCustomMessage("INTRO_1"), game.getPlayer().getCustomMessage("CLARA1")};
		for (int i = 0; i < chat.getConversations(); i++){
			tb.clear();
			tb.setText(ScriptUtil.replace(marks, replacements, chat.getConversation(i)));
			tb.setTitle(ScriptUtil.replace(marks, replacements, chat.getName(i)));
			tb.draw();
			si.refresh();
			si.waitKey(CharKey.SPACE);
		}
		si.restore();
	}
	
	public void showScreen(Object pScreen){
		si.saveBuffer();
		String screenText = (String) pScreen;
		TextBox tb = new TextBox(si);
		tb.setBounds(3,4,50,18);
		tb.setBorder(true);
		tb.setText(screenText);
		tb.draw();
		si.refresh();
		si.waitKey(CharKey.SPACE);
		si.restore();
	}
	
	private Hashtable locationKeys;
	{
		locationKeys = new Hashtable();
		locationKeys.put("TOWN", new Position(15,15));
		locationKeys.put("FOREST", new Position(23,15));
		locationKeys.put("BRIDGE", new Position(30,15));
		locationKeys.put("ENTRANCE", new Position(36,15));
		locationKeys.put("HALL", new Position(41,15));
		locationKeys.put("LAB", new Position(39,12));
		locationKeys.put("CHAPEL", new Position(37,9));
		locationKeys.put("RUINS", new Position(45,10));
		locationKeys.put("CAVES", new Position(46,18));
		locationKeys.put("VILLA", new Position(52,17));
		locationKeys.put("COURTYARD", new Position(52,17));
		locationKeys.put("DUNGEON", new Position(60,18));
		locationKeys.put("STORAGE", new Position(63,12));
		locationKeys.put("CLOCKTOWER", new Position(62,7));
		locationKeys.put("KEEP", new Position(52,5));
	}
	
	String[] mapImage = new String[]{
			"                                                                                ",
			" ''`.--..,''`_,''`.-''----.----..'     '`''''..,'''-'    `_,,'''`--- ./  ,'-.   ",
			" '                                                 /\\                   |,. `.  ",
			"  |                                               /  \\                    `.... ",
			"  |                                              | /-\\|      /'\\              | ",
			"  |                                               \\| |\\    .'   |             | ",
			"  |                            O    /\\             \\-/ \\   . /-\\`             | ",
			"  |                                |  |            `.===``/==| | \\           ,  ",
			" .\"                                |/-\\_              `===== \\-/  `.          \\ ",
			" |                                 `| |==.../-\\       .'      =    |          | ",
			" |                                 /\\-/ ====| ||  ,-.'.       ==   /         /  ",
			" \\.                                | =/-\\   \\-/| |   | '|     /-\\ /           \\ ",
			"  |                               .' =| |=   = | |   '..'     | | |           / ",
			"  |            ,-.     ..--.      |.  \\-/=   = |  |           \\-/  \\         |  ",
			"  |          _/-\\|.  ,/-\\  `./-\\   /-\\  /-\\  =  \\ |            =   |.        <. ",
			"  :|      ,,' | |=====| |====| |===| |==| |  ==  \\ `'\\.        =    `.        | ",
			"  |    _,'    \\-/     \\-/    \\-/   \\-/  \\-/   =   |/-\\|        =     |        | ",
			"   |.-'                                      /-\\ ==| |===  /-\\ =     `.      |  ",
			" .-' ''`''-.    ,'`..,''''''`.               | |== \\-/  ===| |==       ``.   `. ",
			" |.         ---'             `._,..,_        \\-/  ,..      \\-/   _  __   ___. | ",
			"  '                                  `.     .-''''   \\.   _....'' `'  \\''    ./ ",
			"  |                                    `_,.-          '`--'                  `. ",
			"  | ..           _                      .                                     | ",
			" |../'....,'-.../ ``...,''`--....''`..,' `\"-..,''`....,`...-'..''......'`...,-' ",
			"                                                                                "
	};

	
	public void showMap(String locationKey, String locationDescription) {
		si.saveBuffer();
		for (int i = 0; i < 25; i++){
			si.print(0,i, mapImage[i], CharAppearance.BROWN);
		}
		si.print(15, 11, locationDescription);
		if (locationKey != null){
			Position location = (Position) locationKeys.get(locationKey);
			if (location != null)
				si.print(location.x, location.y, "X", CharAppearance.RED);
		}
		si.waitKey(CharKey.SPACE);
		si.restore();
	}
	
	public void showMonsterScreen(Monster who, Player player) {
		CharAppearance app = (CharAppearance)who.getAppearance();
		si.cls();
		si.print(6,3, who.getDescription(), ConsoleSystemInterface.RED);
		si.print(4,3,app.getChar(),app.getColor());
		
		TextBox tb = new TextBox(si);
		tb.setPosition(3,5);
		tb.setHeight(8);
		tb.setWidth(70);
		tb.setForeColor(ConsoleSystemInterface.WHITE);
		if (who.getLongDescription() != null)
			tb.setText(who.getLongDescription());
		tb.draw();
		
		MonsterRecord record = Main.getMonsterRecordFor(who.getID());
		long baseKilled = 0;
		long baseKillers = 0;
		if (record != null){
			baseKilled = record.getKilled();
			baseKillers = record.getKillers();
		}
		si.print(2,17, "You have killed "+(baseKilled+player.getGameSessionInfo().getDeathCountFor(who))+" "+who.getDescription()+"s",ConsoleSystemInterface.WHITE);
		if (baseKillers == 0){
			si.print(2,18, "No "+who.getDescription()+"s have killed you",ConsoleSystemInterface.WHITE);
		} else {
			si.print(2,18, "You have been killed by "+baseKillers+" "+who.getDescription()+"s",ConsoleSystemInterface.WHITE);
		}
		si.print(2,20, "[Press Space]",ConsoleSystemInterface.WHITE);
		si.refresh();
		si.waitKey(CharKey.SPACE);
		si.restore();
		si.refresh();
	}
	
	public String pickStage(){
		STMusicManagerNew.thus.playKey("STAGESELECT");
		ArrayList<LevelInfo> levels = SectionedGenerator.thus.getLevelsInfo();
		si.cls();
		int y = 0;
		int xx = 0;
		for (int i = 0; i < levels.size(); i++){
			xx++;
			if (xx == 4){
				xx = 1;
				y++;
			}
			int color = levels.get(i).getColor();
			si.print(xx*26-18,y*6+1, levels.get(i).getPic1(), color);
			si.print(xx*26-18,y*6+2, levels.get(i).getPic2(), color);
			si.print(xx*26-18,y*6+3, levels.get(i).getPic3(), color);
			si.print(xx*26-18,y*6+4, levels.get(i).getPic4(), color);
			si.print(xx*26-18,y*6+5, levels.get(i).getPic5(), color);
			si.print(xx*26-18,y*6+6, ((char)('a'+i))+". "+levels.get(i).getName(), ConsoleSystemInterface.WHITE);
		}
		si.refresh();
    	CharKey x = new CharKey(CharKey.NONE);
		while (x.code != CharKey.A && x.code != CharKey.a &&
				x.code != CharKey.B && x.code != CharKey.b &&
				x.code != CharKey.C && x.code != CharKey.c &&
				x.code != CharKey.D && x.code != CharKey.d &&
				x.code != CharKey.E && x.code != CharKey.e
				)
			x = si.inkey();
		si.cls();
		switch (x.code){
		case CharKey.A: case CharKey.a:
			return levels.get(0).getId();
		case CharKey.B: case CharKey.b:
			return levels.get(1).getId();
		case CharKey.C: case CharKey.c:
			return levels.get(2).getId();
		case CharKey.D: case CharKey.d:
			return levels.get(3).getId();
		case CharKey.E: case CharKey.e:
			return levels.get(4).getId();
		}
		return null;
	}
}
