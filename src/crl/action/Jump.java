package crl.action;

import crl.actor.Actor;
import crl.level.Cell;
import crl.player.Player;

public class Jump extends HeartAction{
	private int height;
	 
	public String getID(){
		return "Jump";
	}
	
	public Jump(int height) {
		this.height = height;
	}
	
	public String getInvalidationMessage(){
		return "You can't jump on midair!";
	}
	
	public boolean canPerform(Actor a) {
		if (!super.canPerform(a))
			return false;
		if (a.isFlying())
			return true;
   		Cell standingCell = a.getStandingCell();
   		Cell currentCell = a.getLevel().getMapCell(a.getPosition());
   		if (standingCell == null)
   			return false;
   		if (standingCell.isStair()){
   			if (currentCell.isStair())
   				return false;
   			else
   				return true;
   		} else if (standingCell.isSolid()){
   			return true;
   		} else {
   			return false;
   		}
   		
   		
   	}

	public String getSFX(){
		if (performer instanceof Player){
			Player p = (Player) performer;
			if (p.getSex() == Player.MALE){
				return "wav/jump_male.wav";
			} else {
				return "wav/jump_female.wav";
			}
		} else 
			return null;
		
	}

	public int getCost(){
		return 0;
	}

	public void execute(){
		super.execute();
		performer.setCounter("JUMPING", height);
	}
	
	@Override
	public int getHeartCost() {
		return 5;
	}
}
