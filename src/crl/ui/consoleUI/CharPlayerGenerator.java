package crl.ui.consoleUI;

import sz.csi.ConsoleSystemInterface;
import crl.game.PlayerGenerator;
import crl.player.Player;

public class CharPlayerGenerator extends PlayerGenerator{
	public CharPlayerGenerator(ConsoleSystemInterface si){
		this.si = si;
	}
	private ConsoleSystemInterface si;
	
	public Player generatePlayer(){
		si.cls();
		si.print(2,3, "Record name:", ConsoleSystemInterface.WHITE);
		si.refresh();
		si.locateCaret(3 +"Record name:".length(), 3);
		String name = si.input(10);
		si.cls();
    	return getPlayer(name);
	}
}