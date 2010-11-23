package crl.level;

import crl.action.*;
import crl.monster.*;

public class EnemyGeneratorAction extends Action{
	private String monsterId;
	public EnemyGeneratorAction (String mId){
		this.monsterId = mId;
	}
	public String getID(){
		return "EnemyGeneratorAction";
	}
	
	public void execute(){
		Level level = performer.getLevel();
		Monster monster = MonsterFactory.getFactory().buildMonster(monsterId);
		monster.setPosition(performer.getPosition());
		level.addMonster(monster);
	}
}