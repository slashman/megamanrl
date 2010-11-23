package crl.ai.monster.basic;

import sz.util.Debug;
import sz.util.Position;
import sz.util.Util;
import crl.action.Action;
import crl.action.Jump;
import crl.action.monster.MonsterWalk;
import crl.action.monster.basic.BombManBomb;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.level.Cell;
import crl.monster.Monster;


public class BombManAI extends MonsterAI{
	public final static int STRATEGY1 = 1, STRATEGY2 = 2;
	
	private int currentStrategy = STRATEGY1;
	private boolean shotBomb = false;
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
				if (distanceFromPlayer > 4){
					if (!shotBomb){
						Action ret = new BombManBomb();
						ret.setDirection(directionToPlayer);
						shotBomb = true;
						return ret;
					} else {
						shotBomb = false;
						return null;
					}
				} else {
					if (canMoveAway(who, directionToPlayer)){
						return moveAway(directionToPlayer);
					} else {
						return moveCloser(directionToPlayer);
					}
				}
			} else {
				if (distanceFromPlayer > 5){
					if (!shotBomb){
						int direction = Action.getHorizontalDirection(directionToPlayer);
						if (direction == Action.LEFT){
							direction = Action.UPLEFT;
						} else {
							direction = Action.UPRIGHT;
						}
						Action ret = new BombManBomb();
						ret.setDirection(direction);
						shotBomb = true;
						return ret;
					} else {
						shotBomb = false;
						return null;
					}
				} else {
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
		 return "BombManAI";
	 }

     public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

     
}