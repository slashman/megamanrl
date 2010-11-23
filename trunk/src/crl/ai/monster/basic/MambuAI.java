package crl.ai.monster.basic;

import sz.util.Debug;
import sz.util.Position;
import sz.util.Util;
import crl.action.Action;
import crl.action.Jump;
import crl.action.monster.MonsterWalk;
import crl.action.monster.basic.MambuShoot;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.level.Cell;
import crl.monster.Monster;


public class MambuAI extends MonsterAI{
	private boolean shelled = false;
	private Position var = Action.directionToVariation(Action.RIGHT);
	private int direction= Action.RIGHT;
	private boolean justFired = false;
	private int counter;
	
	public Action selectAction(Actor who){
		Monster aMonster = (Monster) who;
		if (aMonster.starePlayer() == -1)
			return null;
		if (shelled){
			Position current = new Position(who.getPosition());
			Position next = Position.add(current, var);
			
			Cell nextCell = who.getLevel().getMapCell(next);
			if (nextCell.isSolid() || Util.chance(20)){
				if (direction == Action.LEFT){
					direction = Action.RIGHT;
				} else{
					direction = Action.LEFT;
				}
				var = Action.directionToVariation(direction);
			}
			counter ++;
			if (counter > 8 && Util.chance(80)){
				who.getLevel().addMessage("The mambu opens its shell");
				shelled = false;
				aMonster.setInvulnerable(false);
				counter = 0;
			}
			Action ret = new MonsterWalk();
			ret.setDirection(direction);
			return ret;
			
		} else {
			if (!justFired && Util.chance(50)){
				justFired = true;
				return new MambuShoot();
			} 
			if (Util.chance(70)){
				justFired = false;
				shelled = true;
				aMonster.setInvulnerable(true);
				aMonster.setInvulnerableDescription("Shelled");
				who.getLevel().addMessage("The mambu shells itself!");
			}
			return null;
		}
	 }

	 public String getID(){
		 return "MambuAI";
	 }

     public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

     
}