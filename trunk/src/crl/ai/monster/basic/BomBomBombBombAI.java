package crl.ai.monster.basic;

import sz.util.Util;
import crl.action.Action;
import crl.action.Jump;
import crl.action.monster.MonsterWalk;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.monster.Monster;


public class BomBomBombBombAI extends MonsterAI{
	int count = 0;
	int delay = 0;
	/**
	 * It will ascend out of an abyss, and explode, releasing 4 smaller bombs that 
	 * explode when they hit a surface or an enemy. It cannot be destroyed except 
	 * by its own self-destruction.
	 */
	public Action selectAction(Actor who){
		Monster aMonster = (Monster) who;
		if (who.getStandingCell() == null || who.getStandingCell().isSolid()){
			who.getLevel().removeMonster(aMonster);
			aMonster.die();
		}
		return null;
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