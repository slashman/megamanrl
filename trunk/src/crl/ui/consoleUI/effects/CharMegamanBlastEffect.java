package crl.ui.consoleUI.effects;

import sz.csi.ConsoleSystemInterface;
import sz.util.*;
import crl.ui.*;
import crl.ui.consoleUI.ConsoleUserInterface;
import crl.action.*;
import crl.*;

import java.util.*;

public class CharMegamanBlastEffect extends CharEffect{
	private String tiles;
	private int color;

	public CharMegamanBlastEffect(String ID, String tiles, int color, int delay){
    	super(ID);
    	setAnimationDelay(delay);
		this.tiles = tiles;
		this.color = color;
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
	

	public void drawEffect(ConsoleUserInterface ui, ConsoleSystemInterface si){
		UserInterface.getUI().refresh();
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position center = Position.add(ui.PC_POS, relative);
		si.saveBuffer();
		int tileIndex = 0;
		for (int i = 0; i < 20; i++){
			tileIndex++;
			if (tileIndex == tiles.length())
				tileIndex = 0;
			for (Position v: vars){
				Position nextPosition = Position.add(center, Position.mul(v,i+1));
				if (ui.insideViewPort(nextPosition))
					si.print(nextPosition.x, nextPosition.y, tiles.charAt(tileIndex), color);
			}
			animationPause();
			si.restore();
		}
	}

}