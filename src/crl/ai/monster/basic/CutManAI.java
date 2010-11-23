package crl.ai.monster.basic;

import sz.util.Position;
import sz.util.Util;
import crl.action.Action;
import crl.action.Jump;
import crl.action.monster.MonsterWalk;
import crl.action.monster.basic.BombManBomb;
import crl.action.monster.basic.RollingCutter;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.level.Cell;
import crl.monster.Monster;


public class CutManAI extends MonsterAI{
	public final static int STRATEGY1 = 1, STRATEGY2 = 2;
	
	private int currentStrategy = STRATEGY1;
	private int recoverCutter = 0;
	
	private Action moveAway(int directionToPlayer){
		Action ret = new MonsterWalk();
		int direction = Action.getHorizontalDirection(directionToPlayer);
		if (direction == Action.LEFT){
			direction = Action.RIGHT;
		} else {
			direction = Action.LEFT;
		}
		ret.setDirection(direction);
		return ret;
	}
	
	private boolean canMoveAway(Actor who, int directionToPlayer){
		int direction = Action.getHorizontalDirection(directionToPlayer);
		if (direction == Action.LEFT){
			direction = Action.RIGHT;
		} else {
			direction = Action.LEFT;
		}
		Cell next = who.getLevel().getMapCell(Position.add(who.getPosition(), Action.directionToVariation(direction)));
		if (next.isSolid())
			return false;
		else
			return true;
	}
	
	private Action moveCloser(int directionToPlayer){
		Action ret = new MonsterWalk();
		int direction = Action.getHorizontalDirection(directionToPlayer);
		ret.setDirection(direction);
		return ret;
	}
	
	public Action selectAction(Actor who){
		Monster aMonster = (Monster) who;
		int directionToPlayer = aMonster.starePlayer();
		if (directionToPlayer == -1)
			return null;
		if (who.hasCounter("DAMAGED") && Util.chance(50)){
			if (currentStrategy == STRATEGY1){
				currentStrategy = STRATEGY2;
			} else {
				currentStrategy = STRATEGY1;
			}
		}
		if (Util.chance(10)){
			if (currentStrategy == STRATEGY1){
				currentStrategy = STRATEGY2;
			} else {
				currentStrategy = STRATEGY1;
			}
		}
		int distanceFromPlayer = Position.distance(who.getPosition(), who.getLevel().getPlayer().getPosition());
		if (currentStrategy == STRATEGY1){
			if (who.isJumping()){
				if (recoverCutter == 0 && distanceFromPlayer > 4){
					Action ret = new RollingCutter();
					ret.setDirection(directionToPlayer);
					recoverCutter = 10;
					return ret;
				} else {
					if (recoverCutter > 0 )
						recoverCutter--;
					if (canMoveAway(who, directionToPlayer)){
						return moveAway(directionToPlayer);
					} else {
						return moveCloser(directionToPlayer);
					}
				}
			} else {
				if (recoverCutter == 0 && distanceFromPlayer > 5){
					int direction = Action.getHorizontalDirection(directionToPlayer);
					if (direction == Action.LEFT){
						direction = Action.UPLEFT;
					} else {
						direction = Action.UPRIGHT;
					}
					Action ret = new RollingCutter();
					ret.setDirection(direction);
					recoverCutter = 10;
					return ret;
				} else {
					if (recoverCutter > 0 ){
						recoverCutter--;
					}
					if (Util.chance(50)){
						if (canMoveAway(who, directionToPlayer)){
							return moveAway(directionToPlayer);
						} else {
							return moveCloser(directionToPlayer);
						}
					} else {
						Action ret = new Jump(Util.rand(4, 5));
						if (ret.canPerform(who)){
							return ret;
						} else {
							return null;
						}
					}
				}
			}
		} else {
			if (who.isJumping()){
				return moveCloser(directionToPlayer);
			} else {
				if (Util.chance(70)){
					Action ret = new Jump(Util.rand(4, 5));
					if (ret.canPerform(who)){
						return ret;
					} else {
						return null;
					}
				} else {
					if (canMoveAway(who, directionToPlayer)){
						return moveAway(directionToPlayer);
					} else {
						return moveCloser(directionToPlayer);
					}
				}
			}
		}
	 }

	 public String getID(){
		 return "CutManAI";
	 }

     public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

     
}