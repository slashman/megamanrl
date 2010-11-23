package crl.action.monster.basic;

import sz.util.Position;
import crl.action.Action;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.MonsterFactory;

public class BombManBomb extends Action{
	public String getID(){
		return "BombManBomb";
	}
		
	public void execute(){
		Level aLevel = performer.getLevel();
		aLevel.addMessage("BombMan throws a Bomb!");
		
		Position var = Action.directionToVariation(targetDirection);

		Position blastPosition = performer.getPosition();
		Monster b1 = MonsterFactory.getFactory().buildMonster("BOMBMANBOMB");
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
