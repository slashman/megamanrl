package crl.action;

import java.util.Arrays;

import crl.actor.Actor;
import crl.item.Item;
import crl.item.ItemDefinition;
import crl.player.Player;

public class Equip extends Action{
	public String getID(){
		return "Equip";
	}
	
	public boolean needsItem(){
		return true;
	}

	public String getPromptItem(){
		return "Select weapon";
	}

	public boolean canPerform(Actor a) {
		Player player = getPlayer(a);
		if (!player.canWield()){
			invalidationMessage = "You can't wear anything";
			return false;
		}
		return true;
	}
	
	public void execute(){
		ItemDefinition def = targetItem.getDefinition();
		Player player = (Player)performer;
		if (!player.canWield()){
			performer.getLevel().addMessage("You can't wear anything!");
			return;
		}
		
		switch (def.getEquipCategory()){
			case 1:
				for (int i = 0 ; i < player.getBannedArmors().length; i++)
					if (player.getBannedArmors()[i].equals(def.getID())) {
						performer.getLevel().addMessage("You can't wear that kind of armor!");
						return;
					}
				Item currentArmor = player.getArmor();
				player.reduceQuantityOf(targetItem);
				if (currentArmor != null)
					player.addItem(currentArmor);
				performer.getLevel().addMessage("You Setup the "+def.getDescription());
				player.setArmor(targetItem);
				break;
			case 2:
				if (player.getPlayerClass() == Player.CLASS_VAMPIREKILLER && player.getFlag("ONLY_VK")){
					performer.getLevel().addMessage("You can't abandon the mystic whip");
					return;
				}
				Item currentWeapon = player.getWeapon();
				Item currentSecondaryWeapon = player.getSecondaryWeapon();
				player.reduceQuantityOf(targetItem);
				if (currentWeapon != null){
					player.setSecondaryWeapon(currentWeapon);
					if (currentSecondaryWeapon != null){
						player.addItem(currentSecondaryWeapon);
					}
				}
				performer.getLevel().addMessage("You setup the "+def.getDescription());
				player.setWeapon(targetItem);
				break;
			case 3: // SHIELDS
				Item currentShield = player.getShield();
				player.reduceQuantityOf(targetItem);
				if (currentShield != null)
					player.addItem(currentShield);
				performer.getLevel().addMessage("You wear the "+def.getDescription());
				player.setShield(targetItem);
				break;
		}
	}
	
	public int getCost(){
		return 50;
	}
}