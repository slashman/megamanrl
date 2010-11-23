package crl.level;

import java.util.ArrayList;

import crl.action.*;
import crl.actor.Actor;
import crl.monster.Monster;
import crl.monster.VMonster;

public class GravityAction extends Action{
	private static GravityAction singleton = new GravityAction();
	
	public String getID(){
		return "Gravity Action";
	}
	
	public void execute(){
		Level level = performer.getLevel();
		//ArrayList<Actor> actors = level.getActors();
		//for(Actor x: actors){ 
		VMonster actors = level.getMonsters();
		for (int i = 0; i < actors.size(); i++){
			Monster x = actors.elementAt(i);
			x.fall();
		}
		level.getPlayer().fall();
		
	}

	public static GravityAction getAction(){
		return singleton;
	}
	
	@Override
	public int getCost() {
		return 5;
	}
}