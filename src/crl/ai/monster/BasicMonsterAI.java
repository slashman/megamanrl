package crl.ai.monster;

import java.util.Iterator;

import sz.util.OutParameter;
import sz.util.Position;
import sz.util.Util;
import crl.action.Action;
import crl.action.ActionFactory;
import crl.action.monster.MonsterCharge;
import crl.action.monster.MonsterMissile;
import crl.action.monster.MonsterWalk;
import crl.action.monster.SummonMonster;
import crl.action.monster.Swim;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.monster.Monster;
import crl.player.Consts;

public class BasicMonsterAI extends MonsterAI{
	private boolean isStationary;
	private int waitPlayerRange;
	private int approachLimit = 0;
	private int patrolRange = 0;
	private int chargeCounter = 0;
	private int lastDirection = -1;
	private boolean changeDirection;
	
	public Action selectAction(Actor who){
		Monster aMonster = (Monster) who;
		if (aMonster.getEnemy() != null){
			if (!aMonster.getLevel().getMonsters().contains((Monster)aMonster.getEnemy())){
				aMonster.setEnemy(null);
			}
		}
		if (aMonster.getEnemy() != null || aMonster.hasCounter(Consts.C_MONSTER_CHARM)){
			
			int directionToMonster = -1;
			if (aMonster.getEnemy() != null){
				directionToMonster = aMonster.stareMonster((Monster)aMonster.getEnemy());
			} else {
				directionToMonster = aMonster.stareMonster();
			}
			
			if (directionToMonster == -1) {
//				Walk TO player except if will bump him
				directionToMonster = aMonster.starePlayer();
				if (directionToMonster == -1){
					return null;
				}
				else {
					Position targetPositionX = Position.add(who.getPosition(), Action.directionToVariation(directionToMonster));
					if (!who.getLevel().isWalkable(targetPositionX)){
						return null;
					}else{
						if (who.getLevel().getPlayer().getPosition().equals(targetPositionX)){
							return null;
						} else{
							Action ret = new MonsterWalk();
							ret.setDirection(directionToMonster);
							return ret;
						}
					}
				}
			} else {
				if (isStationary){
					return null;
				} else {
					Action ret = new MonsterWalk();
					if (!who.getLevel().isWalkable(Position.add(who.getPosition(), Action.directionToVariation(directionToMonster)))){
						directionToMonster = Util.rand(0,7); 
						while (true){
							if (!Position.add(who.getPosition(), Action.directionToVariation(directionToMonster)).equals(who.getLevel().getPlayer().getPosition()))
									break;
						}
						ret.setDirection(directionToMonster);
					} else {
						ret.setDirection(directionToMonster);
					}
					return ret;
				}
			} 
		}
		
		int directionToPlayer = aMonster.starePlayer();
		int playerDistance = Position.flatDistance(aMonster.getPosition(), aMonster.getLevel().getPlayer().getPosition());
		if (patrolRange >0 && playerDistance > patrolRange){
			if (lastDirection == -1 || changeDirection){
				lastDirection = Util.rand(0,7);
				changeDirection = false;
			}
			Action ret = new MonsterWalk();
	     	ret.setDirection(lastDirection);
	     	return ret;
		}
		
		if (directionToPlayer == -1) {
			if (isStationary || waitPlayerRange > 0) {
				return null;
			} else {
				int direction = Util.rand(0,7);
		     	if (aMonster.canSwim() && aMonster.isInWater()){
	                Action ret = new Swim();
		            ret.setDirection(direction);
			     	return ret;
				} else {
					Action ret = new MonsterWalk();
		            ret.setDirection(direction);
		            return ret;
				}
		     	
			}
		} else {
			if (waitPlayerRange > 0 && playerDistance > waitPlayerRange){
				return null;
			}
			
			
			if (playerDistance < approachLimit){
				//get away from player
				int direction = Action.toIntDirection(Position.mul(Action.directionToVariation(directionToPlayer), -1));
				if (aMonster.canSwim() && aMonster.isInWater()){
	                Action ret = new Swim();
		            ret.setDirection(direction);
			     	return ret;
				} else {
					Action ret = new MonsterWalk();
		            ret.setDirection(direction);
		            return ret;
				}
			} else {
				if (aMonster.canSwim() && aMonster.isInWater() && !aMonster.getLevel().getPlayer().isSwimming()){
	                Action ret = new Swim();
		            ret.setDirection(directionToPlayer);
			     	return ret;
				}
				//Randomly decide if will approach the player or attack
				if (aMonster.seesPlayer() && rangedAttacks != null && Util.chance(80)){
					//Try to attack the player
					inout: for (Iterator iter = rangedAttacks.iterator(); iter.hasNext();) {
						RangedAttack element = (RangedAttack) iter.next();
						if (element.getChargeCounter() > 0){
							if (chargeCounter == 0){
								
							}else{
								chargeCounter --;
								break inout;
							}
						}
					}
					for (Iterator iter = rangedAttacks.iterator(); iter.hasNext();) {
						RangedAttack element = (RangedAttack) iter.next();
						if (element.getRange() >= playerDistance && 
								Util.chance(element.getFrequency()) &&
								(
									element.getAttackType() == MonsterMissile.TYPE_DIRECT ||
									(element.getAttackType() != MonsterMissile.TYPE_DIRECT && aMonster.getStandingHeight() == aMonster.getLevel().getPlayer().getStandingHeight())
								)
							){
							//Perform the attack
							Action ret = ActionFactory.getActionFactory().getAction(element.getAttackId());
							if (element.getChargeCounter() > 0){
								if (chargeCounter > 0){
									continue;
								} else {
									chargeCounter = element.getChargeCounter();
								}
							}
							
							if (ret instanceof MonsterMissile){
								((MonsterMissile)ret).set(
										element.getAttackType(),
										element.getStatusEffect(),
										element.getRange(),
										element.getAttackMessage(),
										element.getEffectType(),
										element.getEffectID(),
										element.getDamage(),
										element.getEffectWav()
										);
							}else if (ret instanceof MonsterCharge){
								((MonsterCharge)ret).set(element.getRange(), element.getAttackMessage(), element.getDamage(),element.getEffectWav());
							}else if (ret instanceof SummonMonster){
								((SummonMonster)ret).set(element.getSummonMonsterId(), element.getAttackMessage());
							}
							ret.setPosition(who.getLevel().getPlayer().getPosition());
							
							return ret;
						}
					}
				}
				// Couldnt attack the player, so walk to him
				if (isStationary){
					return null;
				} else {
					Action ret = null;
					if (aMonster.canSwim() && aMonster.isInWater()){
						ret = new Swim();
					}else{
						ret = new MonsterWalk();
					}
					OutParameter direction1 = new OutParameter();
					OutParameter direction2 = new OutParameter();
					fillAlternateDirections(direction1, direction2, directionToPlayer);
					if (canWalkTowards(aMonster, directionToPlayer)){
						ret.setDirection(directionToPlayer);
					} else if (canWalkTowards(aMonster, direction1.getIntValue())){
						ret.setDirection(direction1.getIntValue());
					} else if (canWalkTowards(aMonster, direction2.getIntValue())){
						ret.setDirection(direction2.getIntValue());
					} else {
						ret.setDirection(Util.rand(0,7));
					}
		            return ret;
				}
			}
		}
	 }
	
	private void fillAlternateDirections(OutParameter direction1, OutParameter direction2, int generalDirection){
		Position var = Action.directionToVariation(generalDirection);
		Position d1 = null;
		Position d2 = null;
		if (var.x == 0){
			d1 = new Position(-1, var.y);
			d2 = new Position(1, var.y);
		} else if (var.y == 0){
			d1 = new Position(var.x, -1);
			d2 = new Position(var.x, 1);
		} else {
			d1 = new Position(var.x, 0);
			d2 = new Position(0, var.y);
		}
		direction1.setIntValue(Action.toIntDirection(d1));
		direction2.setIntValue(Action.toIntDirection(d2));
	}
	
	private boolean canWalkTowards(Monster aMonster, int direction){
		Position destination = Position.add(aMonster.getPosition(), Action.directionToVariation(direction));
		if (aMonster.getLevel().isAir(destination)){
			if (aMonster.isEthereal() || aMonster.isFlying())
				return true;
			else
				return false;
		}
		if (!aMonster.getLevel().isWalkable(destination)){
			if (aMonster.isEthereal())
				return true;
			else
				return false;
		} else
			return true;
	}

	 public String getID(){
		 return "BASIC_MONSTER_AI";
	 }

	 public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

	public void setApproachLimit(int limit){
		 approachLimit = limit;
	}
	
	public void setWaitPlayerRange(int limit){
		 waitPlayerRange = limit;
	}
	
	public void setPatrolRange(int limit){
		 patrolRange = limit;
	}
	
	public int getPatrolRange(){
		return patrolRange;
	}

	public void setStationary(boolean isStationary) {
		this.isStationary = isStationary;
	}

	public void setChangeDirection(boolean value) {
		changeDirection = value;
	}
	 
}