package crl.action;

import sz.util.Position;
import sz.util.Util;
import crl.action.Attack.WhipFireball;
import crl.actor.Actor;
import crl.feature.Feature;
import crl.item.Item;
import crl.item.ItemDefinition;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Consts;
import crl.player.Player;
import crl.ui.effects.Effect;
import crl.ui.effects.EffectFactory;

public class UseWeapon extends Action{
	public String getID(){
		return "Attack";
	}
	
	private int reloadTime;
	private Item weapon;
	public void execute(){
        Player player = null;
        reloadTime = 0;
		try {
			player = (Player) performer;
		} catch (ClassCastException cce){
			return;
		}
		
		weapon = player.getWeapon();
		Level aLevel = performer.getLevel();
		if (!player.canAttack() || weapon == null){
			aLevel.addMessage("You can't attack!");
			return;
		}
		
		ItemDefinition weaponDef = weapon.getDefinition();

		if (weapon.getReloadTurns() > 0)
			if (weapon.getRemainingTurnsToReload() == 0){
				if (!reload(weapon, player))
					return;
			}
		
		String action = weapon.getAction();
		Action actionObject = null;
		
		try {
			actionObject = (Action)Class.forName(action).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if (!actionObject.canPerform(performer))
			return;
		
		actionObject.setPerformer(performer);
		actionObject.execute();
		
		// Here
		
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

	
	private boolean reload(Item weapon, Player aPlayer){
		if (weapon != null){
			if (weapon.getDefinition().isSingleUse()){
				aPlayer.getLevel().addMessage("You can't reload the " + weapon.getDescription());
				return false;
			} else {
				weapon.reload();
				aPlayer.getLevel().addMessage("You reload the " + weapon.getDescription());
				reloadTime = 10*weapon.getDefinition().getReloadTurns();
				return true;
			}
		} else {
			aPlayer.getLevel().addMessage("Nothing to reload");
		}
		return false;
	}
	
	public int getCost(){
		Player player = (Player) performer;
		if (weapon != null){
			return weapon.getAttackCost()+reloadTime;
		} else {
			return (int)(player.getAttackCost() * 1.5);
		}
	}

	public String getSFX(){
		Player p = (Player) performer;
		weapon = p.getWeapon();
		if (weapon != null && !weapon.getAttackSound().equals("DEFAULT")){
			return weapon.getAttackSound();
		} else {
			if (((Player)performer).getSex() == Player.MALE)
				return "wav/punch_male.wav";
			else
				return "wav/punch_female.wav";
		}
	}
	
	public boolean canPerform(Actor a) {
		Player player = getPlayer(a);
		if (!player.canAttack()){
			invalidationMessage = "You can't attack";
			return false;
		}
		/*if (player.getWeapon() != null && player.getWeapon().getRange() > 5){
			Monster nearest = player.getNearestMonster();
			if (nearest != null){
				if (Position.distance(nearest.getPosition(), player.getPosition()) < 2){
					invalidationMessage = "You can't aim your "+player.getWeapon().getDescription()+" right now!";
					return false;
				}
			}
		}*/
		return true;
		 
	}
}