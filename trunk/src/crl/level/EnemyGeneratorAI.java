package crl.level;

import sz.util.Position;
import crl.action.*;
import crl.ai.*;
import crl.actor.*;
import crl.monster.Monster;

public class EnemyGeneratorAI implements ActionSelector {
	private int counter;

	public String getID(){
		return "Emerge";
	}

	public Action selectAction(Actor who) {
		EnemyGenerator x = (EnemyGenerator) who;
		counter++;
		if (counter > x.getCounter()){
			Monster m = x.getLevel().getMonsterAt(x.getPosition());
			if (m != null){
				return null;
			}
			if (Position.flatDistance(x.getPosition(), x.getLevel().getPlayer().getPosition()) > 10){
				counter = 0;
				return new EnemyGeneratorAction(x.getMonsterId());
			}
			else
				return null; 
    	}
		return null;
	}

	 public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}
}