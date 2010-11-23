package crl.ai.monster.basic;

import sz.util.Position;
import sz.util.Util;
import crl.action.Action;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.monster.Monster;


public class KillerBombAI extends MonsterAI{
	/**
	 * It travels toward MegaMan in a sine-wave pattern. When it is killed, 
	 * it will explode, harming MegaMan if he is near. After destruction it 
	 * will give 800 points.
	 */
	private boolean fired = false;
	private Position var;
	private int direction;
	private int switchCounter;
	
	public Action selectAction(Actor who){
		Monster aMonster = (Monster) who;
		if (!fired){
			int directionToPlayer = aMonster.starePlayer();
			if (directionToPlayer != -1){
				direction = Action.getHorizontalDirection(directionToPlayer);
				var = Action.directionToVariation(direction);
				who.setInertiaX(var.x);
				who.setInertiaY(-1);
				fired = true;
			} else {
				return null;
			}
		}
		switchCounter++;
		if (switchCounter > 3 && Util.chance(70)){
			if (who.getInertiaY() == 1)
				who.setInertiaY(-1);
			else
				who.setInertiaY(1);
			switchCounter = 0;
		}
		return null;
	 }

	 public String getID(){
		 return "KillerBombAI";
	 }
	 
     public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

     
}