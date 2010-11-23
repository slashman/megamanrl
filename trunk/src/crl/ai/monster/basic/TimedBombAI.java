package crl.ai.monster.basic;

import sz.util.Util;
import crl.action.Action;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.monster.MonsterAI;
import crl.monster.Monster;


public class TimedBombAI extends MonsterAI{
	private int explode = Util.rand(6, 8);
	private int flash  = Util.rand(4, 5);
	
	private int count;

	
	public Action selectAction(Actor who){
		count ++;
		Monster aMonster = (Monster) who;
		if (count > explode){
			who.getLevel().addMessage("The bomb explodes!");
			who.getLevel().monsterExplosion(aMonster, aMonster.getPosition());
			aMonster.die();
		} else if (count > flash){
			who.getLevel().addMessage("The bomb flashes!");
		} 
		return null;
	 }

	 public String getID(){
		 return "BomBomBombAI";
	 }

     public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

     
}