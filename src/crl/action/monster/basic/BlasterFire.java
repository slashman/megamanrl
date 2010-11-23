package crl.action.monster.basic;

import sz.util.Position;
import crl.action.Action;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.MonsterFactory;

public class BlasterFire extends Action{
	public String getID(){
		return "BlasterFire";
	}
		
	public void execute(){
		Level aLevel = performer.getLevel();
		aLevel.addMessage("The blaster fires!");
		int inertiaX = 0;
		if (targetDirection == Action.LEFT){
			inertiaX = -1;
		} else {
			inertiaX = 1;
		}
		Position blastPosition = performer.getPosition();
		
		Monster b1 = MonsterFactory.getFactory().buildMonster("ENEMYSHOT");
		b1.setPosition(new Position(blastPosition));
		b1.setInertiaX(inertiaX);
		b1.setInertiaY(-2);
		aLevel.addMonster(b1);
		
		Monster b2 = MonsterFactory.getFactory().buildMonster("ENEMYSHOT");
		b2.setInertiaX(inertiaX);
		b2.setPosition(new Position(blastPosition));
		b2.setInertiaY(-1);
		aLevel.addMonster(b2);
		
		Monster b3 = MonsterFactory.getFactory().buildMonster("ENEMYSHOT");
		b3.setInertiaX(inertiaX);
		b3.setPosition(new Position(blastPosition));
		b3.setInertiaY(1);
		aLevel.addMonster(b3);
		
		Monster b4 = MonsterFactory.getFactory().buildMonster("ENEMYSHOT");
		b4.setInertiaX(inertiaX);
		b4.setPosition(new Position(blastPosition));
		b4.setInertiaY(2);
		aLevel.addMonster(b4);
	}
	
	public String getSFX(){
		return "wav/lazrshot.wav";
	}
	
	public int getCost(){
		return 50;
	}
}
