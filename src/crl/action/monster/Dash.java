package crl.action.monster;

import sz.util.Position;
import crl.action.Action;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;

public class Dash extends Action{
	public String getID(){
		return "SLICE_DIVE";
	}
	
	public boolean needsDirection(){
		return true;
	}

	public void execute(){
		Monster aMonster = (Monster) performer;
		targetDirection = aMonster.starePlayer();
        Position var = directionToVariation(targetDirection);
        Level aLevel = performer.getLevel();
        Player aPlayer = aLevel.getPlayer();
        String message = "The "+aMonster.getDescription()+" dives to you!";
        //Cell currentCell = aLevel.getMapCell(performer.getPosition());
        Position destinationPoint = null;
        for (int i=0; i<5; i++){
			destinationPoint = Position.add(performer.getPosition(), var);
			//Cell destinationCell = aLevel.getMapCell(destinationPoint);
			if (!aLevel.isValidCoordinate(destinationPoint) || aLevel.isSolid(destinationPoint))
				break;
			if (aPlayer.getPosition().equals(destinationPoint) && 
					aPlayer.getStandingHeight() == aMonster.getStandingHeight()){
				message = "The "+aMonster.getDescription()+ " slices you!";
				if (aPlayer.damage(aMonster, aMonster.getAttack()))
					aPlayer.bounceBack(var, 1);
			}

			aMonster.setPosition(destinationPoint);
			//currentCell = aLevel.getMapCell(destinationPoint);
		}
		aLevel.addMessage(message);
	}

	public String getPromptDirection(){
		return "Where do you want to slice?";
	}

}