package crl.action;
import crl.player.Player;
import crl.ui.UserInterface;

public class BigEnergyPellet extends Action{
	public String getID(){
		return "BigEnergyPellet";
	}
	
	public void execute(){
		Player aPlayer = (Player) performer;
		aPlayer.recoverHits(10);
		aPlayer.getLevel().removeItemFrom(targetItem, performer.getPosition());
		UserInterface.getUI().refresh();
	}
	
	public String getSFX(){
		return "wav/lazrshot.wav";
	}
	
	public int getCost(){
		return 1;
	}
}
