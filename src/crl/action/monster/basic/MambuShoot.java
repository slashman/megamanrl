package crl.action.monster.basic;

import java.util.ArrayList;

import sz.util.Position;
import crl.action.Action;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.MonsterFactory;

public class MambuShoot extends Action{
	public String getID(){
		return "BlasterFire";
	}
	
	private final static ArrayList<Position> vars = new ArrayList<Position>();
	static {
		vars.add(new Position(0,1));
		vars.add(new Position(1,0));
		vars.add(new Position(1,1));
		vars.add(new Position(0,-1));
		vars.add(new Position(-1,0));
		vars.add(new Position(-1,-1));
		vars.add(new Position(1,-1));
		vars.add(new Position(-1,1));
	}
		
	public void execute(){
		Level aLevel = performer.getLevel();
		aLevel.addMessage("The mambu fires!");
		for (Position var: vars){
			Monster b1 = MonsterFactory.getFactory().buildMonster("ENEMYSHOT");
			b1.setPosition(new Position(performer.getPosition()));
			b1.setInertiaX(var.x);
			b1.setInertiaY(var.y);
			aLevel.addMonster(b1);
		}
	}
	
	public String getSFX(){
		return "wav/lazrshot.wav";
	}
	
	public int getCost(){
		return 50;
	}
}
