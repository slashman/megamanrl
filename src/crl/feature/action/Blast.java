package crl.feature.action;

import sz.util.Position;
import crl.action.Action;
import crl.level.Level;
import crl.monster.Monster;
import crl.ui.Appearance;
import crl.ui.effects.EffectFactory;

public class Blast extends Action{
	public String getID(){
		return "Blast";
	}
		
	public void execute(){
		Level aLevel = performer.getLevel();
		int damage = 20 + aLevel.getPlayer().getShotLevel() + aLevel.getPlayer().getSoulPower();

		aLevel.addMessage("The crystal emits a holy blast!");

		Position blastPosition = performer.getPosition();
		
		//aLevel.addEffect(new SplashEffect(blastPosition, "Oo,.", Appearance.CYAN));
		aLevel.addEffect(EffectFactory.getSingleton().createLocatedEffect(blastPosition, "SFX_CRYSTAL_BLAST"));
		Position destinationPoint = new Position(0,0, performer.getPosition().z);
		for (int x = blastPosition.x -3; x <= blastPosition.x+3; x++)
			for (int y = blastPosition.y -3; y <= blastPosition.y+3; y++){
				destinationPoint.x = x;
				destinationPoint.y = y;
				Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
				if (targetMonster != null){
					if (targetMonster.wasSeen())
						aLevel.addMessage("The "+targetMonster.getDescription()+" is hit by the holy wave!");
					targetMonster.damage(damage);
				}
			}
	}
	
	public String getSFX(){
		return "wav/lazrshot.wav";
	}
	
	public int getCost(){
		return 50;
	}
}
