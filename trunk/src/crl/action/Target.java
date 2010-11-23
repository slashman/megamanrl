package crl.action;

import sz.util.Line;
import sz.util.Position;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.item.Item;
import crl.item.ItemDefinition;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;
import crl.ui.effects.Effect;
import crl.ui.effects.EffectFactory;

public class Target extends Action{
	private Player player;
	private Item weapon;
	private int reloadTime;
	public String getID(){
		return "Target";
	}
	
	public boolean needsPosition(){
		return true;
	}

	public void execute(){
        player = null;
        reloadTime = 0;
		try {
			player = (Player) performer;
		} catch (ClassCastException cce){
			return;
		}
		
		weapon = player.getWeapon();
		Level aLevel = performer.getLevel();
		int startHeight = aLevel.getMapCell(player.getPosition()).getHeight();
		if (!player.canAttack()){
			aLevel.addMessage("You can't attack!");
			return;
		}
		
		if (targetPosition.equals(performer.getPosition()) && aLevel.getMonsterAt(targetPosition)==null){
			aLevel.addMessage("That's a coward way to give up!");
			return;
        }

		if (weapon == null) {
			aLevel.addMessage("Don't be so careful with your blows");
			return;
		}
		
		if (weapon.getRange() < 2){
			aLevel.addMessage("Your "+weapon.getDescription()+" is not made for targetting");
			return;
		}

		ItemDefinition weaponDef = weapon.getDefinition();

		if (weapon.getReloadTurns() > 0)
			if (weapon.getRemainingTurnsToReload() == 0){
				/*
				aLevel.addMessage("You must reload the "+weapon.getDescription()+"!");
				return;*/
				if (!reload(weapon, player))
					return;
			}

		
		String [] sfx = weaponDef.getAttackSFX().split(" ");
		boolean cancelSFX = false;
		if (sfx.length > 0)
			if (sfx[0].equals("MISSILE")){
				Effect me = EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition, "SFX_WP_"+weaponDef.getID(), weaponDef.getRange());
				if (!weapon.isSlicesThrough()){
					int i = 0;
					Line path = new Line(performer.getPosition(), targetPosition);
					for (i=0; i<weapon.getRange(); i++){
						Position destinationPoint = path.next();
						Cell destinationCell = aLevel.getMapCell(destinationPoint);
			        	Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
	       	 			if (destinationFeature != null && destinationFeature.isDestroyable())
    	   	 				break;
						Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
						if (
							targetMonster != null &&
							destinationCell.getHeight() == aLevel.getMapCell(player.getPosition()).getHeight()
						){
							if (i==0 && weapon.getRange()>5)
								cancelSFX = true;
							break;
						}
					}
					me = EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition, "SFX_WP_"+weaponDef.getID(), i);
				}
				if (!cancelSFX)
					aLevel.addEffect(me);
			}


		boolean hitsSomebody = false;
		boolean hits = false;
		Line path = new Line(performer.getPosition(), targetPosition);
		for (int i=0; i<weapon.getRange(); i++){
			Position destinationPoint = path.next();
        	Cell destinationCell = aLevel.getMapCell(destinationPoint);
        	/*aLevel.addMessage("You hit the "+destinationCell.getID());*/

			String message = "";
        	Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
        	if (destinationFeature != null && destinationFeature.isDestroyable()){
        		hitsSomebody = true;
	        	message = "You hit the "+destinationFeature.getDescription();
				Feature prize = destinationFeature.damage(player, weapon.getAttack());
	        	if (prize != null){
		        	message += ", and destroy it!";
				}
				aLevel.addMessage(message);
			}

			Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
			message = "";

			if (targetMonster != null){
				if ((targetMonster.isInWater() && targetMonster.canSwim())|| destinationCell.getHeight() < startHeight-1){
					if (targetMonster.wasSeen())
						aLevel.addMessage("The dagger flies over the "+targetMonster.getDescription());
				} else {
					if (destinationCell.getHeight() > startHeight + 1){
						if (weapon.getVerticalRange()>0){
							hits = true;
						} else {
							if (targetMonster.wasSeen()){
								aLevel.addMessage("The dagger flies under the "+targetMonster.getDescription());
							}
						}
					} else {
						hits = true;
					}
				}
			}
			
			if (hits){
				hits = false;
				hitsSomebody = true;
				int penalty = (int)(Position.distance(targetMonster.getPosition(), player.getPosition())/4);
				int attack = player.getWeaponAttack();
				attack -= penalty;
				if (attack < 1)
					attack = 1;
				if (weapon.isHarmsUndead() && targetMonster.isUndead()){
					attack *= 2;
					if (targetMonster.wasSeen())
						message = "You critically damage the "+targetMonster.getDescription()+"!";
					else
						message = "You hit something!";
				} else {
					if (targetMonster.wasSeen())
						message = "You hit the "+targetMonster.getDescription();
					else
						message = "You hit something!";
				}
					
				//targetMonster.damage(player.getWhipLevel());
				aLevel.addMessage(message);
				targetMonster.damageWithWeapon(attack);
	        	
			}
			
			Cell targetMapCell = aLevel.getMapCell(destinationPoint);
			if (targetMapCell != null && targetMapCell.isSolid()){
				//aLevel.addMessage("You hit the "+targetMapCell.getShortDescription());
				hitsSomebody = true;
         	}
			
			if (hitsSomebody && !weapon.isSlicesThrough())
				break;
		}

		if (weapon.getReloadTurns() > 0 &&
				weapon.getRemainingTurnsToReload() > 0)
				weapon.setRemainingTurnsToReload(weapon.getRemainingTurnsToReload()-1);
		if (weaponDef.isSingleUse()){
			if (weapon.getReloadTurns() > 0){
				if (weapon.getRemainingTurnsToReload() == 0) {
					player.setWeapon(null);
				}
			}else {
				if (player.hasItem(weapon))
					player.reduceQuantityOf(weapon);
				else
					player.setWeapon(null);
			}
		}
	}

	public String getPromptPosition(){
		return "Where do you want to attack?";
	}

	public Position getPosition(){
		return targetPosition;
	}


	public String getSFX(){
		return "wav/rich_yah.wav";
	}

	public int getCost(){
		return player.getAttackCost()+weapon.getDefinition().getAttackCost()+reloadTime;
	}
	
	private boolean reload(Item weapon, Player aPlayer){
		if (weapon != null){
			if (aPlayer.getGold() < weapon.getDefinition().getReloadCostGold()){
				aPlayer.getLevel().addMessage("You can't reload the " + weapon.getDescription());
				return false;
			}else if (aPlayer.getHearts() < 2){
				aPlayer.getLevel().addMessage("You can't reload the " + weapon.getDescription());
				return false;
			}
			else {
				weapon.reload();
				aPlayer.reduceGold(weapon.getDefinition().getReloadCostGold());
				aPlayer.reduceHearts(2);
				aPlayer.getLevel().addMessage("You reload the " + weapon.getDescription()+" ("+weapon.getDefinition().getReloadCostGold()+" gold)");
				reloadTime = 10*weapon.getDefinition().getReloadTurns();
				return true;
			}
		} else
			aPlayer.getLevel().addMessage("You can't reload yourself");
		return false;
	}
	
	public boolean canPerform(Actor a){
        player = null;
		try {
			player = (Player) a;
		} catch (ClassCastException cce){
			return false;
		}
		Item weapon = player.getWeapon();
		if (!player.canAttack()){
			invalidationMessage = "You can't attack!";
			return false;
		}
		if (weapon == null) {
			invalidationMessage = "It is useless to target your own blows...";
			return false;
		}
		if (weapon.getRange() < 2){
			invalidationMessage = "You can´t target your "+weapon.getDescription();
			return false;
		}
		if (player.getWeapon() != null && player.getWeapon().getRange() > 5){
			Monster nearest = player.getNearestMonster();
			if (nearest != null){
				if (Position.flatDistance(nearest.getPosition(), player.getPosition()) < 2){
					invalidationMessage = "You can't aim your "+player.getWeapon().getDescription()+" right now!";
					return false;
				}
			}
		}
/*
		ItemDefinition weaponDef = weapon.getDefinition();

		/*if (weapon.getReloadTurns() > 0)
			if (weapon.getRemainingTurnsToReload() == 0){
				aLevel.addMessage("You must reload the "+weapon.getDescription()+"!");
				return false;
			}*/
		return true;
	}
}