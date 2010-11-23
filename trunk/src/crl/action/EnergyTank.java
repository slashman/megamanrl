package crl.action;

import crl.level.Level;
import crl.player.Player;

public class EnergyTank extends Action{
	public String getID(){
		return "EnergyTank";
	}
	
	public void execute(){
		Level aLevel = performer.getLevel();
		Player aPlayer = (Player) performer;
		aLevel.addMessage("You load an Energy Tank!");
		aPlayer.heal();
		aPlayer.reduceQuantityOf("ENERGYTANK");
	}
	
	public String getSFX(){
		return "wav/lazrshot.wav";
	}
	
	public int getCost(){
		return 1;
	}
}
