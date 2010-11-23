package crl.action;
import crl.player.Player;
import crl.ui.UserInterface;

public class WeaponEnergyPellet extends Action{
	public String getID(){
		return "WeaponEnergyPellet";
	}
	
	public void execute(){
		Player aPlayer = (Player) performer;
		aPlayer.reload(2);
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
