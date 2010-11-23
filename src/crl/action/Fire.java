package crl.action;

import crl.action.ProjectileSkill;
import crl.player.Player;

public class Fire extends FireProjectileSkill{
	public String getID(){
		return "Fire";
	}

	public String getSFX(){
		return "wav/dagger.wav";
	}
	
	public int getCost(){
		Player p = (Player) performer;
		if (p.getWeapon() == null)
			return p.getAttackCost();
		else
			return p.getWeapon().getAttackCost();
	}

	public int getDamage() {
		Player p = (Player) performer;
		if (p.getWeapon() == null)
			return p.getAttack();
		else
			return p.getWeapon().getAttack();
	}

	public int getHit() {
		return 100;
	}

	public int getPathType() {
		return ProjectileSkill.PATH_LINEAR;
	}

	public int getRange() {
		Player p = (Player) performer;
		if (p.getWeapon() == null)
			return 1;
		else
			return p.getWeapon().getRange();
	}

	public String getSelfTargettedMessage() {
		return "";
	}

	public String getSFXID() {
        return "SFX_WHITE_DAGGER";
	}

	public String getShootMessage() {
		return "*Zap!*";
	}
		
	public String getSpellAttackDesc() {
		return "buster shoot";
	}
	
	@Override
	public int getHeartCost() {
		return 5;
	}
}