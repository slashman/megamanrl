package crl.ai.monster.basic;

import sz.util.Util;
import crl.action.Action;
import crl.action.Jump;
import crl.action.monster.MonsterWalk;
import crl.action.monster.basic.BomBomBombExplode;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.monster.Monster;


public class BomBomBombAI extends MonsterAI{
	/**
	 * It will ascend out of an abyss, and explode, releasing 4 smaller bombs that 
	 * explode when they hit a surface or an enemy. It cannot be destroyed except 
	 * by its own self-destruction.
	 */
	public Action selectAction(Actor who){
		Monster aMonster = (Monster) who;
		int directionToPlayer = aMonster.starePlayer();
		if (directionToPlayer == -1){
			//Do nothing 
        	return null;
		} else {
			if (!who.hasCounter("BOMBOMBOMBEXPLODE")){
				who.setCounter("BOMBOMBOMBEXPLODE", Util.rand(6, 8));
			} else {
				if (who.getCounter("BOMBOMBOMBEXPLODE") == 1){
					return new BomBomBombExplode();
				}
			}
			if (!who.hasInertia()){
				who.setInertiaY(-2);
			} 
			return null;
		}
	 }

	 public String getID(){
		 return "BomBomBombAI";
	 }

     public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

     
}