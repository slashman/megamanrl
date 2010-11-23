package crl.ai.monster.basic;

import sz.util.Util;
import crl.action.Action;
import crl.action.monster.basic.BlasterFire;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.monster.Monster;


public class BlasterAI extends MonsterAI{
	/**
	 * When MegaMan is near, at regular intervals it will open up and shoot 
	 * in 4 diagonal directions, and then shield itself. It gives 200 points 
	 * when destroyed.
	 */
	private boolean open = false;
	private int count = 0;
	public Action selectAction(Actor who){
		Monster aMonster = (Monster) who;
		if (open){
			count = 0;
			int directionToPlayer = aMonster.starePlayer();
			if (directionToPlayer != -1 && Util.chance(30)){
				int horizontalDirection = Action.getHorizontalDirection(directionToPlayer);
				if (horizontalDirection != Action.SELF){
					Action ret = new BlasterFire();
					ret.setDirection(horizontalDirection);
					return ret;
				} else {
					return null;
				}
			} else {
				if (Util.chance(80)){
					open = false;
					aMonster.setInvulnerable(true);
					aMonster.setInvulnerableDescription("Closed");
					who.getLevel().addMessage("The blaster closes its shell");
				}
				return null;
			}
		} else {
			int directionToPlayer = aMonster.starePlayer();
			if (directionToPlayer != -1){
				if (count > 4){
					if (Util.chance(80)){
						who.getLevel().addMessage("The blaster opens its shell");
						open = true;
						aMonster.setInvulnerable(false);
					}
					count = 0;
				} else {
					count++;
				}
			}
			return null;
		}
	 }

	 public String getID(){
		 return "BlasterAI";
	 }

     public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

     
}