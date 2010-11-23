package crl.ai.monster.basic;

import sz.util.Position;
import crl.action.Action;
import crl.action.monster.MonsterWalk;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.level.Cell;
import crl.monster.Monster;


public class GabyoallAI extends MonsterAI{
	/**
	 * Gabyoall will slowly slide back and forth along the floor, 
	 * but attack MegaMan when he is on the same level by sliding very fast, 
	 * crashing into him. Its armour is also resistant to the Mega Buster, 
	 * as it only stuns the robot for several seconds. It gives 300 points when 
	 * destroyed.
	 */
	private boolean chasing = false;
	private boolean moving = false;
	private Position var;
	private int direction;
	
	public Action selectAction(Actor who){
		Monster aMonster = (Monster) who;
		if (chasing)
			((Monster)who).setCounter("INCREASED_SPEED", 2);
		int directionToPlayer = aMonster.starePlayer();
		if (directionToPlayer == Action.LEFT || directionToPlayer == Action.RIGHT){
			chasing = true;
		} else {
			chasing = false;
		}
		if (moving){
			Position current = new Position(who.getPosition());
			Position next = Position.add(current, var);
			who.setPosition(next);
			Cell nextCell = who.getLevel().getMapCell(next);
			if (!who.getStandingCell().isSolid() || nextCell.isSolid()){
				if (direction == Action.LEFT){
					direction = Action.RIGHT;
				} else{
					direction = Action.LEFT;
				}
				var = Action.directionToVariation(direction);
			}
			who.setPosition(current);
			Action ret = new MonsterWalk();
			ret.setDirection(direction);
			if (chasing){
				who.getLevel().addMessage("The gabyoall rushes at you!");
			}
			return ret;
		} else {
			if (directionToPlayer != -1){
				direction = Action.LEFT;
				var = Action.directionToVariation(direction);
				moving = true;
				Action ret = new MonsterWalk();
				ret.setDirection(direction);
				if (chasing){
					who.getLevel().addMessage("The gabyoall rushes at you!");
				}
				return ret;
			} else {
	        	return null;
			} 
		}
	 }

	 public String getID(){
		 return "GabyoallAI";
	 }
	 
     public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

     
}