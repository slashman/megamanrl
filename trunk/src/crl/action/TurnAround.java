package crl.action;

import sz.util.Position;
import crl.actor.Actor;
import crl.level.Cell;
import crl.player.Player;

public class TurnAround extends Action{
	public String getID(){
		return "TurnAround";
	}

	public boolean canPerform(Actor a) {
   		return true;
   	}

	public int getCost(){
		return 0;
	}

	public void execute(){
		if (((Player)performer).getLastFacing() == Player.FACING_RIGHT){
			((Player)performer).setLastFacing(Player.FACING_LEFT);
		} else{
			((Player)performer).setLastFacing(Player.FACING_RIGHT);
		}
	}
}
