package crl.action.monster.basic;

import sz.util.Position;
import crl.action.Action;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.MonsterFactory;
import crl.ui.Appearance;
import crl.ui.effects.EffectFactory;

public class BomBomBombExplode extends Action{
	public String getID(){
		return "BomBomBombExplode";
	}
		
	public void execute(){
		Level aLevel = performer.getLevel();
		aLevel.addMessage("The bombombomb explodes into four smaller bombs!");

		Position blastPosition = performer.getPosition();
		Monster b1 = MonsterFactory.getFactory().buildMonster("BOMBOMBOMBBOMB");
		b1.setPosition(new Position(blastPosition));
		b1.setInertiaX(-2);
		aLevel.addMonster(b1);
		
		Monster b2 = MonsterFactory.getFactory().buildMonster("BOMBOMBOMBBOMB");
		b2.setInertiaX(2);
		b2.setPosition(new Position(blastPosition));
		aLevel.addMonster(b2);
		
		Monster b3 = MonsterFactory.getFactory().buildMonster("BOMBOMBOMBBOMB");
		b3.setInertiaX(1);
		b3.setPosition(new Position(blastPosition));
		aLevel.addMonster(b3);
		
		Monster b4 = MonsterFactory.getFactory().buildMonster("BOMBOMBOMBBOMB");
		b4.setInertiaX(-1);
		b4.setPosition(new Position(blastPosition));
		aLevel.addMonster(b4);
		
		aLevel.removeMonster((Monster)performer);
		((Monster)performer).die();
	}
	
	public String getSFX(){
		return "wav/lazrshot.wav";
	}
	
	public int getCost(){
		return 50;
	}
}
