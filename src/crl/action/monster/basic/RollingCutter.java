package crl.action.monster.basic;

import sz.util.Position;
import crl.action.Action;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.MonsterFactory;

public class RollingCutter extends Action{
	public String getID(){
		return "RollingCutter";
	}
		
	public void execute(){
		Level aLevel = performer.getLevel();
		aLevel.addMessage("CutMan throw his rolling cutter!");
		
		Position var = Action.directionToVariation(targetDirection);

		Position blastPosition = performer.getPosition();
		Monster b1 = MonsterFactory.getFactory().buildMonster("ROLLING_CUTTER");
		b1.setPosition(new Position(blastPosition));
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
