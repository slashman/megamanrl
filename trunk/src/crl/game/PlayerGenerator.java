
package crl.game;

import java.util.Hashtable;

import sz.util.ScriptUtil;
import sz.util.Util;
import crl.item.Item;
import crl.item.ItemDefinition;
import crl.item.ItemFactory;
import crl.player.Consts;
import crl.player.Player;
import crl.ui.AppearanceFactory;

public abstract class PlayerGenerator {
	public static PlayerGenerator thus;
	public abstract Player generatePlayer();
	
	public Player createSpecialPlayer(String playerID){
		return null;
	}
	
	public Player getPlayer(String name){
		Player player = new Player();
		
		if (name.trim().equals("")){
			player.setName(Util.randomElementOf(MALE_NAMES));
		} else {
			player.setName(name);
		}
		player.setBaseSightRange(4);
		player.setGold(0);
		player.setWalkCost(50);
		player.setAttackCost(50);
		player.setAttack(1);
		player.setEvadeChance(5);
		player.setCastCost(50);
		player.setSoulPower(1);
		player.setCarryMax(15);
		player.setBreathing(35);
		player.setHitsMax(10);
		player.setHeartMax(10);
		player.setHearts(10);
		player.heal();
		AppearanceFactory apf = AppearanceFactory.getAppearanceFactory();
		Item buster = ItemFactory.getItemFactory().createItem("BUSTER");
		player.setWeapon(buster);
		player.setAppearance(apf.getAppearance("MEGAMAN"));
		return player;

	}
	
	protected String [] MALE_NAMES = new String [] {"Slash", "Rock", "Rockman", "Mega", "BlueBomber", "Blue"};
}
