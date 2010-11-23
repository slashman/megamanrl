package crl.action;

import sz.util.Position;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.MonsterFactory;
import crl.player.Player;

public class PlasmaBuster extends HeartAction{
	public String getID(){
		return "PlasmaBuster";
	}
	
	public void execute(){
		super.execute();
		Level aLevel = performer.getLevel();
		Player aPlayer = (Player) performer;
		
		int inertiaX = 0;
        if (aPlayer.getLastFacing() == Player.FACING_RIGHT){
        	inertiaX = 1;
        } else {
        	inertiaX = -1;
        }
        
		aLevel.addMessage("You fire your "+ aPlayer.getWeaponDescription()+"!");
		Monster b1 = MonsterFactory.getFactory().buildMonster("PLASMASHOT");
		b1.setDamageMod(aPlayer.getWeapon().getAttack());
		b1.setPosition(new Position(performer.getPosition()));
		b1.setInertiaX(inertiaX);
		b1.setInertiaY(0);
		aLevel.addMonster(b1);
	}
	
	public String getSFX(){
		return "wav/lazrshot.wav";
	}
	
	public int getCost(){
		return 1;
	}
	
	@Override
	public int getHeartCost() {
		return 3;
	}
}
