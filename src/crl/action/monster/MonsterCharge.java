package crl.action.monster;

import sz.util.Debug;
import sz.util.Position;
import crl.action.Action;
import crl.game.SFXManager;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.Player;

public class MonsterCharge extends Action{
	private int range;
	private String message;
	private String effectWav;

	private int damage;
	public String getID(){
		return "MONSTER_CHARGE";
	}
	
	public boolean needsDirection(){
		return true;
	}

	public void set(int pRange, String pMessage, int pDamage, String pEffectWav){
		message = pMessage;
		range = pRange;
		damage = pDamage;
		effectWav = pEffectWav;
	}
	
	public void execute(){
		Debug.doAssert(performer instanceof Monster, "Someone not a monster tried to JumpOver");
		Monster aMonster = (Monster) performer;
		targetDirection = aMonster.starePlayer();
        Position var = directionToVariation(targetDirection);
        Level aLevel = performer.getLevel();
        Player aPlayer = aLevel.getPlayer();
        aLevel.addMessage("The "+aMonster.getDescription()+" "+message+".");
        Cell currentCell = aLevel.getMapCell(performer.getPosition());
        Position destinationPoint = null;
        if (effectWav != null)
        	SFXManager.play(effectWav);
        for (int i=0; i<range; i++){
			destinationPoint = Position.add(aMonster.getPosition(), var);
			if (!aLevel.isValidCoordinate(destinationPoint))
				break;
			Position deepPoint  = aLevel.getDeepPosition(destinationPoint);
			if (deepPoint == null){
				aLevel.addMessage("The "+aMonster.getDescription()+ " falls into an endless pit!");
				performer.die();
				performer.getLevel().removeMonster(aMonster);
				break;
			}
			Cell destinationCell = aLevel.getMapCell(deepPoint);
			if (!aMonster.isEthereal() &&
					( destinationCell.isSolid() || destinationCell.getHeight() > currentCell.getHeight()+aMonster.getLeaping() +1
					)
				) {
				aLevel.addMessage("The "+aMonster.getDescription()+ " bumps into the "+destinationCell.getShortDescription());
				break;
			}

			if (!aMonster.isEthereal() && !aMonster.canSwim() && destinationCell.isShallowWater()){
				aLevel.addMessage("The "+aMonster.getDescription()+ " falls into the "+ destinationCell.getShortDescription() + "!");
				performer.die();
				performer.getLevel().removeMonster(aMonster);
				break;
			}
			if (aPlayer.getPosition().equals(destinationPoint)){
				if (aPlayer.getStandingHeight() == aMonster.getStandingHeight()){
					if (aPlayer.damage(aMonster, (damage==0?aMonster.getAttack():damage)))
						aPlayer.bounceBack(var, 1);
				} else if (aPlayer.getStandingHeight() > aMonster.getStandingHeight()){
					aLevel.addMessage("The "+aMonster.getDescription()+ "'s attack passes beneath you!");
				} else
					aLevel.addMessage("The "+aMonster.getDescription()+ "'s attack passes over you!");
				
			}
			aMonster.setPosition(destinationPoint);
		}
		
	}

	public String getPromptDirection(){
		return "Where do you want to whip?";
	}

	public int getCost(){
		Monster m = (Monster) performer;
		if (m.getAttackCost() == 0){
			Debug.say("quickie monster");
			return 10;
		}
		return m.getAttackCost();
	}
	
}