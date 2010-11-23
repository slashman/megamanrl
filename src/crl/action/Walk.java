package crl.action;

import sz.util.Debug;
import sz.util.Position;
import crl.actor.Actor;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;

public class Walk extends Action{
	private Player aPlayer;
	
	public String getID(){
		return "Walk";
	}
	
	public boolean needsDirection(){
		return true;
	}

	public void execute(){
		Debug.enterMethod(this, "execute");
		aPlayer = (Player) performer;
		boolean vertical = false;
		switch (targetDirection){
			case Action.SELF:
				return;
			case Action.UP: case Action.DOWN: case Action.UPLEFT: case Action.UPRIGHT:
				vertical = true;
		}
		
        Position var = directionToVariation(targetDirection);
        if (var.x != 0){
        	if (var.x == 1){
        		if (aPlayer.getLastFacing() == Player.FACING_LEFT){
        			aPlayer.setLastFacing(Player.FACING_RIGHT);
        			return;
        		}
        	}
        	else if (var.x == -1) {
        		if (aPlayer.getLastFacing() == Player.FACING_RIGHT){
        			aPlayer.setLastFacing(Player.FACING_LEFT);
        			return;
        		}
        	}
        	
        }
        Position destinationPoint = Position.add(performer.getPosition(), var);
        Level aLevel = performer.getLevel();
        Cell destinationCell = aLevel.getMapCell(destinationPoint);
        Cell currentCell = aLevel.getMapCell(performer.getPosition());
        
        if (vertical){
        	if (currentCell.isStair()){
        		
        	} else if (!destinationCell.isStair()){
        		return;
        	}
        }
        
        if (destinationCell == null){
			return;
		}
        
        if (destinationCell.isRound() && !vertical){
        	Position varx = directionToVariation(Action.UP);
        	destinationPoint = Position.add(destinationPoint, varx);
            destinationCell = aLevel.getMapCell(destinationPoint);
        }
        
        if (destinationCell.isSolid() || !aLevel.isWalkable(destinationPoint)){
			aLevel.addMessage("You bump into the "+destinationCell.getShortDescription());
			return;
		}
			
        Actor aActor = aLevel.getActorAt(destinationPoint);
        if (aActor != null){
        	if (aActor instanceof Monster){
        		if (aPlayer.isInvincible()){
        			//aLevel.addMessage("You are hit by the "+aMonster.getDescription()+"!");
        		} else {
        			Monster aMonster = (Monster) aActor;
        			if (aPlayer.hasEnergyField()){
						aLevel.addMessage("You shock the "+aMonster.getDescription()+"!");
						aMonster.damage(aPlayer.getAttack());
					} else {
						if (aPlayer.damage(aMonster, aMonster.getAttack())){
							aLevel.getPlayer().bounceBack(Position.mul(var, -1), 2);
							if (aPlayer.getPosition().equals(aMonster.getPosition())){
								//The player wasnt bounced back..
								aLevel.addMessage("You are hit by the "+aMonster.getDescription()+"!");
							} else {
								aLevel.addMessage("You are bounced back by the "+aMonster.getDescription()+"!");
								//aPlayer.landOn(destinationPoint);
							}
						}
					}
        		}
        	} else {
        		aPlayer.landOn(destinationPoint);
        	}
        } else {
        	aPlayer.landOn(destinationPoint);
        }
       	Debug.exitMethod();
	}

	public int getCost(){
		return aPlayer.getWalkCost();
	}
}