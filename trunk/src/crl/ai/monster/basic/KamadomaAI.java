package crl.ai.monster.basic;

import sz.util.Debug;
import sz.util.Util;
import crl.action.Action;
import crl.action.Jump;
import crl.action.monster.MonsterWalk;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.monster.Monster;


public class KamadomaAI extends MonsterAI{
	/**
	 * It will jump at MegaMan, crashing into him.
	 */
	public Action selectAction(Actor who){
		Debug.doAssert(who instanceof Monster, "WanderToPlayerAI selectAction");
		Monster aMonster = (Monster) who;
		int directionToPlayer = aMonster.starePlayer();
		if (directionToPlayer == -1){
			//Do nothing 
        	return null;
		} else {
			int horizontalDirection = Action.getHorizontalDirection(directionToPlayer);
			if (!who.isJumping()){
				Action ret = new Jump(Util.rand(4, 6));
				if (ret.canPerform(who) && Util.chance(40)){
					return ret;
				}
			} 
			Action ret = new MonsterWalk();
			ret.setDirection(horizontalDirection);
			return ret;
		}
	 }

	 public String getID(){
		 return "KamadomaAI";
	 }

     public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

     
}