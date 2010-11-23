package crl.ai.monster.basic;

import sz.util.Debug;
import sz.util.Position;
import sz.util.Util;
import crl.action.Action;
import crl.action.Jump;
import crl.action.monster.MonsterWalk;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.level.Cell;
import crl.monster.Monster;


public class SuzyBotAI extends MonsterAI{
	/**
	 * Adhering Suzy will go from side to side or up and down, and adhere to whatever 
	 * floor, ceiling or wall it comes in contact with. It will give 300 points when 
	 * destroyed.
	 */
	private boolean moving = false;
	private Position var;
	private int direction;
	private boolean stick = false;
	
	public Action selectAction(Actor who){
		if (stick)
			return null;
		Monster aMonster = (Monster) who;
		if (moving){
			Position next = Position.add(who.getPosition(), var);
			Cell nextCell = who.getLevel().getMapCell(next);
			if (nextCell == null || nextCell.isSolid()){
				stick = true;
				return null;
			} else {
				Action ret = new MonsterWalk();
				ret.setDirection(direction);
				return ret;
			}
		} else {
			int directionToPlayer = aMonster.starePlayer();
			if (directionToPlayer == Action.DOWN || 
					directionToPlayer == Action.UP || 
					directionToPlayer == Action.LEFT || 
					directionToPlayer == Action.RIGHT){
				direction = directionToPlayer;
				var = Action.directionToVariation(direction);
				moving = true;
				Action ret = new MonsterWalk();
				ret.setDirection(direction);
				return ret;
			} else {
	        	return null;
			} 
		}
	 }

	 public String getID(){
		 return "SuzyBotAI";
	 }

     public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

     
}