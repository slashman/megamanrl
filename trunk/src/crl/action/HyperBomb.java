package crl.action;

import sz.util.Position;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.MonsterFactory;
import crl.player.Player;

public class HyperBomb extends Action{
	public String getID(){
		return "BombManBomb";
	}
		
	public void execute(){
		Level aLevel = performer.getLevel();
		Player aPlayer = (Player)performer;
		aLevel.addMessage("You throw a Bomb!");
        if (aPlayer.getLastFacing() == Player.FACING_RIGHT){
        	targetDirection = Action.UPRIGHT;
        } else {
        	targetDirection = Action.UPLEFT;
        }
		Position var = Action.directionToVariation(targetDirection);

		Position blastPosition = performer.getPosition();
		Monster b1 = MonsterFactory.getFactory().buildMonster("HYPERBOMB");
		b1.setPosition(new Position(blastPosition));
		if (var.y == -1){
			b1.setCounter("JUMPING", 4);
		}
		b1.setInertiaX(var.x);
		aLevel.addMonster(b1);
	}
	
	public String getSFX(){
		return "wav/lazrshot.wav";
	}
	
	public int getCost(){
		return 50;
	}
}
