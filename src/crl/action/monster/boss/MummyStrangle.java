package crl.action.monster.boss;

import crl.action.Action;
import crl.level.Level;
import crl.monster.Monster;

public class MummyStrangle extends Action{
	public String getID(){
		return "MUMMY_STRANGLE";
	}
	
	public void execute(){
        Level aLevel = performer.getLevel();
        aLevel.addMessage("Akmodan strangles you!");
        aLevel.getPlayer().damage((Monster)performer, 6);
	}
}