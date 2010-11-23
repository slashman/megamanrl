package crl.ui;

import java.io.File;
import java.util.Vector;

import crl.player.GameSessionInfo;
import crl.player.HiScore;
import crl.player.Player;
import crl.player.advancements.Advancement;
import crl.monster.Monster;
import crl.npc.*;
import crl.game.Game;
import crl.item.ItemDefinition;
import crl.item.ItemFactory;
import sz.csi.CharKey;
import sz.csi.ConsoleSystemInterface;
import sz.csi.textcomponents.TextBox;

public abstract class Display {
	public static Display thus;
	public abstract int showTitleScreen();	
	public abstract void showIntro(Player player);
	public abstract boolean showResumeScreen(Player player);
	public abstract void showEndgame(Player player);	
	public abstract void showHiscores(HiScore[] scores);
	public abstract void showHelp();
	public abstract void showDraculaSequence();
	public abstract void showNewDay();
	public abstract void showHorribleNight();
	public abstract int showSavedGames(File[] saveFiles);
	public abstract void showHostageRescue(Hostage h);
	public abstract Advancement showLevelUp(Vector soulIds);
	public abstract void showChat(String chatID, Game game);
	public abstract void showScreen(Object screenID);
	public abstract void showMap(String locationKey, String locationDescription);
	public abstract void showMonsterScreen(Monster who, Player player);
	
	public abstract String pickStage();
}
