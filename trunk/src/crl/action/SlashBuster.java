package crl.action;

import java.util.ArrayList;

import sz.util.Position;
import crl.feature.Feature;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.MonsterFactory;
import crl.player.Player;
import crl.ui.UserInterface;
import crl.ui.effects.EffectFactory;

public class SlashBuster extends HeartAction{
	public String getID(){
		return "SlashBuster";
	}
	
	private final static ArrayList<Position> rvars = new ArrayList<Position>();
	static {
		rvars.add(new Position(1,0));
		rvars.add(new Position(2,0));
		rvars.add(new Position(2,-1));
	}
	
	private final static ArrayList<Position> lvars = new ArrayList<Position>();
	static {
		lvars.add(new Position(-1,0));
		lvars.add(new Position(-2,0));
		lvars.add(new Position(-2,-1));
	}
	
	public void execute(){
		super.execute();
		Level aLevel = performer.getLevel();
		Player aPlayer = (Player) performer;
		
		ArrayList<Position> vars = null; 
        if (aPlayer.getLastFacing() == Player.FACING_RIGHT){
        	vars = rvars;
        } else {
        	vars = lvars;
        }
        int damage = aPlayer.getWeapon().getAttack();
		aLevel.addMessage("You strike with your "+ aPlayer.getWeaponDescription()+"!");
		for (Position p: vars){
			hit(Position.add(aPlayer.getPosition(), p), damage);
		}
	}
	
	public String getSFX(){
		return "wav/lazrshot.wav";
	}
	
	public int getCost(){
		return 1;
	}
	
	@Override
	public int getHeartCost() {
		return 3;
	}
	
	private boolean hit (Position destinationPoint, int damage){
		String message = "";
		Level aLevel = performer.getLevel();
        Player aPlayer = aLevel.getPlayer();
        //UserInterface.getUI().drawEffect(new TileEffect(destinationPoint, '*', Appearance.RED, 100));
        UserInterface.getUI().drawEffect(EffectFactory.getSingleton().createLocatedEffect(destinationPoint, "SFX_RED_HIT"));
        
		//aLevel.addBlood(destinationPoint, 8);
		Feature destinationFeature = aLevel.getFeatureAt(destinationPoint);
        if (destinationFeature != null && destinationFeature.isDestroyable()){
	       	message = "You crush the "+destinationFeature.getDescription();

			Feature prize = destinationFeature.damage(aPlayer, damage);
	       	if (prize != null){
		       	message += ", breaking it appart!";
			}
			aLevel.addMessage(message);
        	return true;
		}
        Monster targetMonster = performer.getLevel().getMonsterAt(destinationPoint);
		Cell destinationCell = performer.getLevel().getMapCell(destinationPoint);
        if (
			targetMonster != null &&
			!(targetMonster.isInWater() && targetMonster.canSwim()) &&
				(destinationCell.getHeight() == aLevel.getMapCell(aPlayer.getPosition()).getHeight() ||
				destinationCell.getHeight() -1  == aLevel.getMapCell(aPlayer.getPosition()).getHeight() ||
				destinationCell.getHeight() == aLevel.getMapCell(aPlayer.getPosition()).getHeight()-1)
				){
        		if (targetMonster.wasSeen())
        			message = "You slash the "+targetMonster.getDescription();
				//targetMonster.damage(player.getWhipLevel());
				targetMonster.damage(damage);
	        	
				aLevel.addMessage(message);

				return true;
			}
		return false;
	}
}
