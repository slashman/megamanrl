package crl.monster;

import sz.util.*;

import crl.action.*;
import crl.item.*;
import crl.level.Emerger;
import crl.level.EmergerAI;
import crl.npc.NPC;
import crl.feature.*;
import crl.ui.*;
import crl.ui.effects.EffectFactory;
import crl.ai.monster.basic.BombManAI;
import crl.ai.monster.boss.DraculaAI;
import crl.player.Consts;
import crl.player.Player;
import crl.actor.*;

public class Monster extends Actor implements Cloneable{
	//Attributes
	private transient MonsterDefinition definition;
	private String defID;

	protected int hits;
	private int maxHits;
	private String featurePrize;
	private boolean visible = true;

	private boolean wasSeen = false;
	
	private Monster enemy;
	
	public String getWavOnHit(){
		return getDefinition().getWavOnHit();
	}
	
	public void setWasSeen(boolean value){
		wasSeen = true;
	}
	
	public boolean wasSeen(){
		return wasSeen;
	}

	public void increaseHits(int i){
		hits += i;
	}

	public void act(){
		if (hasCounter(Consts.C_MONSTER_FREEZE) || hasCounter(Consts.C_MONSTER_SLEEP)){
			setNextTime(50);
			updateStatus();
			return;
		}
		super.act();
		wasSeen = false;
	}

	public boolean isInWater(){
		if (level.getMapCell(getPosition())!= null)
			return level.getMapCell(getPosition()).isShallowWater();
		else
			return false;
	}

	public void freeze(int cont){
		setCounter(Consts.C_MONSTER_FREEZE, cont);
	}

	public int getFreezeResistance(){
		return 0; //placeholder
	}

	public Appearance getAppearance(){
		return getDefinition().getAppearance();
	}

	public Object clone(){
		try {
        	return super.clone();
		} catch (Exception x)
		{
			return null;
		}

	}

	

	/*public boolean playerInRow(){
		Position pp = level.getPlayer().getPosition();
		/*if (!playerInRange())
			return false;
		//Debug.say("pp"+pp);
		//Debug.say(getPosition());
		if (pp.x == getPosition().x || pp.y == getPosition().y)
			return true;
		if (pp.x - getPosition().x == pp.y - getPosition().y)
			return true;
		return false;
	}*/

	public int starePlayer(){
		/** returns the direction in which the player is seen */
		if (level.getPlayer() == null || level.getPlayer().isInvisible() || level.getPlayer().getPosition().z != getPosition().z)
			return -1;
		if (!level.getPlayer().sees(this))
			return -1;
		if (Position.flatDistance(level.getPlayer().getPosition(), getPosition()) <= getDefinition().getSightRange()){
			Position pp = level.getPlayer().getPosition();
			if (pp.x == getPosition().x){
				if (pp.y > getPosition().y){
					return Action.DOWN;
				} else {
                     return Action.UP;
				}
			} else
			if (pp.y == getPosition().y){
				if (pp.x > getPosition().x){
					return Action.RIGHT;
				} else {
					return Action.LEFT;
				}
			} else
			if (pp.x < getPosition().x){
				if (pp.y > getPosition().y)
					return Action.DOWNLEFT;
				else
					return Action.UPLEFT;
			} else {
                if (pp.y > getPosition().y)
					return Action.DOWNRIGHT;
				else
					return Action.UPRIGHT;
			}
		}
		return -1;
	}

	public void damageWithWeapon(int dam){
		if (isInvulnerable()){
			return;
		}
		Item wep = level.getPlayer().getWeapon();
		if (wep != null)
			level.getPlayer().increaseWeaponSkill(wep.getDefinition().getWeaponCategory());
		else
			level.getPlayer().increaseWeaponSkill(ItemDefinition.CAT_UNARMED);
		damage(dam);
	}
	
	public void damage(int dam){
		if (isInvulnerable()){
			return;
		}
		if (getSelector() instanceof DraculaAI){
			((DraculaAI)getSelector()).setOnBattle(true);
		}
		if (Util.chance(getEvadeChance())){
			if (wasSeen())
				level.addMessage("The "+getDescription()+" "+getEvadeMessage());
			return;
		}
		if (hasCounter(Consts.C_MONSTER_FREEZE))
			dam *= 2;
		
		hits -= dam;

		if (getDefinition().getBloodContent() > 0){
			if (level.getPlayer().hasCounter(Consts.C_BLOOD_THIRST) &&
					Position.flatDistance(getPosition(), level.getPlayer().getPosition()) < 3){
				level.addMessage("You drink some of the "+getDefinition().getDescription()+" blood!");
				level.getPlayer().recoverHits(1+(int)(getDefinition().getBloodContent()/30));
			}
			if (Util.chance(40)){
				getLevel().addBlood(getPosition(), Util.rand(0,1));
			}
		}
		if (level.getPlayer().getFlag("HEALTH_REGENERATION") && Util.chance(30)){
			level.getPlayer().recoverHits(1);
		}

		if (isDead()){
			if (this == level.getBoss()){
				//if (!level.isWalkable(getPosition())){
					//level.addMessage("You get a castle key!");
					level.getPlayer().addKeys(1);
				/*} else
					setFeaturePrize("KEY");*/
				//level.addEffect(new DoubleSplashEffect(getPosition(), "O....,,..,.,.,,......", Appearance.RED, ".,,,,..,,.,.,..,,,,,,", Appearance.WHITE));
				level.addEffect(EffectFactory.getSingleton().createLocatedEffect(getPosition(), "SFX_BOSS_DEATH"));
				level.addMessage("The whole level trembles with holy energy!");
				level.removeBoss();
				level.getPlayer().addHistoricEvent("vanquished the "+this.getDescription()+" on the "+level.getDescription());
				level.anihilate();
				level.removeRespawner();
				//level.getPlayer().addSoulPower(Util.rand(10,20)*level.getLevelNumber());
			} else {
				level.getPlayer().increaseMUpgradeCount();
				setPrize();
			}
			if (getPrize() != null && !level.getMapCell(getPosition()).isSolid()) {
				level.addItem(getPosition(), ItemFactory.getItemFactory().createItem(getPrize()));
			}
			
			
			if (getDefinition().isBleedable()){
				Position runner = new Position(-1,-1,getPosition().z);
		    	for (runner.x = -1; runner.x <= 1; runner.x++)
		    		for (runner.y = -1; runner.y <= 1; runner.y++)
		    			if (Util.chance(70))
							getLevel().addBlood(Position.add(getPosition(), runner), Util.rand(0,1));
			}

			die();
			level.getPlayer().addScore(getDefinition().getScore());
			level.getPlayer().addXP(getDefinition().getScore());
			//level.getPlayer().addSoulPower(Util.rand(0,3));
			level.getPlayer().getGameSessionInfo().addDeath(getDefinition());
		}
	}

	public int getScore(){
		return getDefinition().getScore();
		
	}
	public boolean isDead(){
		return hits <= 0;
	}

	public String getDescription(){
	//This may be flavored with specific monster daya
		
		return getDefinition().getDescription() + (isInvulnerable()?"("+getInvulnerableDescription()+")":"");
	}

	private MonsterDefinition getDefinition(){
		if (definition == null){
			if (this instanceof NPC)
				definition = NPC.NPC_MONSTER_DEFINITION;
			else
				definition = MonsterFactory.getFactory().getDefinition(defID);
		}
		return definition;
	}
	
	public boolean canSwim(){
		return getDefinition().isCanSwim();
	}

	public boolean isUndead(){
		return false;
	}

	public boolean isEthereal(){
		return getDefinition().isEthereal();
	}

	public int getHits(){
		return hits;
	}

 	public Monster (MonsterDefinition md){
 		definition = md;
 		defID = md.getID();
 		//selector = md.getDefaultSelector();
 		selector = md.getDefaultSelector().derive();
 		
 		hits = md.getMaxHits();
 		maxHits = md.getMaxHits();
	}
 	
 	

	/*public ActionSelector getSelector(){
		return selector;
		//return definition.getDefaultSelector();
	}*/

	public String getPrize() {
		return featurePrize;
	}

	public void setFeaturePrize(String value) {
		featurePrize = value;
	}

	public int getAttack(){
		return getDefinition().getAttack();
	}

	public int getLeaping(){
		return getDefinition().getLeaping();
	}
	
	public boolean waitsPlayer(){
		return false;
	}

	/*public ListItem getSightListItem(){
		return definition.getSightListItem();
	}*/

	private void setPrize(){
		if (getID().equals("BOMBMAN")){
			setFeaturePrize("HYPERBOMB");
			return;
		}
		if (Util.chance(60))
			return;
		String [] prizeList = null;
		
		
		
       
	        if (Util.chance(50))
    	    if (Util.chance(50))
        	if (Util.chance(40))
	        if (Util.chance(40))
    	    if (Util.chance(40))
	    	if (Util.chance(40))
    	    	prizeList = new String[]{"SLASH_BUSTER_K", "LASER_BUSTER_K", "PLASMA_BUSTER_K"};
			else
				prizeList = new String[]{"SLASH_BUSTER_Y", "LASER_BUSTER_Y", "PLASMA_BUSTER_Y"};
			else
				prizeList = new String[]{"SLASH_BUSTER_M", "LASER_BUSTER_M", "PLASMA_BUSTER_M"};
        	else
        		prizeList = new String[]{"SLASH_BUSTER_C", "LASER_BUSTER_C", "PLASMA_BUSTER_C"};
			else
				prizeList = new String[]{"BIGENERGYPELLET"};
			else
				prizeList = new String[]{"ENERGYPELLET"};
			else
				prizeList = new String[]{"WEAPONENERGY"};    	
    	

		if (prizeList != null)
			setFeaturePrize(Util.randomElementOf(prizeList));
	}
	
	public void die(){
		super.die();
		level.removeMonster(this);
		if (getAutorespawncount() > 0){
			Emerger em = new Emerger(MonsterFactory.getFactory().buildMonster(getDefinition().getID()), getPosition(), getAutorespawncount());
			level.addActor(em);
			em.setSelector(new EmergerAI());
			em.setLevel(getLevel());
		}
	}
	
	public void setVisible(boolean value){
		visible = value;
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public int getAttackCost() {
		return getDefinition().getAttackCost();
	}

	public int getWalkCost() {
		if (hasCounter("INCREASED_SPEED"))
			return (int)Math.round(getDefinition().getWalkCost()/2.0D);
		else
			return getDefinition().getWalkCost();
	}
	
	public String getID(){
		return getDefinition().getID();
		//return getDefinition().getID() + (isInvulnerable() ? "(I)" : "");
	}
	
	public int getEvadeChance(){
		return getDefinition().getEvadeChance();
	}
	
	public String getEvadeMessage(){
		return getDefinition().getEvadeMessage();
	}
	
	public int getAutorespawncount(){
		return getDefinition().getAutorespawnCount();
	}
	
	public boolean tryMagicHit(Player attacker, int magicalDamage, int magicalHit, boolean showMsg, String attackDesc, boolean isWeaponAttack, Position attackOrigin){
		if (isInvulnerable()){
			return false;
		}
		setCounter("DAMAGED", 1);
		int hitChance = 100 - getEvadeChance();
		hitChance = (int)Math.round((hitChance + magicalHit)/2.0d);
		int penalty = 0;
		if (isWeaponAttack){
			penalty = (int)(Position.distance(getPosition(), attackOrigin)/4);
			if (attacker.getWeapon().isHarmsUndead() && isUndead())
				magicalDamage *= 2;
			attacker.increaseWeaponSkill(attacker.getWeapon().getDefinition().getWeaponCategory());
				
		}
			
		magicalDamage -= penalty;
		int evasion = 100 - hitChance;
		
		if (evasion < 0)
			evasion = 0;
		
		if (hasCounter(Consts.C_MONSTER_CHARM))
			setCounter(Consts.C_MONSTER_CHARM, 0);
		if (hasCounter("SLEEP"))
			evasion = 0;
		//see if evades it
		if (Util.chance(evasion)){
			if (showMsg)
				level.addMessage("The "+getDescription()+" evades the "+attackDesc+"!");
			//moveRandomly();
			return false;
		} else {
			if (hasCounter("SLEEP")){
				level.addMessage("You wake up the "+getDescription()+ "!");
				setCounter("SLEEP", 0);
			}
			int baseDamage = magicalDamage;
			double damageMod = 1;
			String hitDesc = "";
			int damage = (int)(baseDamage * damageMod);
			double percent = (double)damage/(double)getDefinition().getMaxHits();
			if (percent > 1.0d)
				hitDesc = "The "+attackDesc+ " whacks the "+getDescription()+ " appart!!";
			else if (percent > 0.7d)
				hitDesc = "The "+attackDesc+ " smashes the "+getDescription()+ "!";
			else if (percent > 0.5d)
				hitDesc = "The "+attackDesc+ " grievously hits the "+getDescription()+ "!";
			else if (percent> 0.3d)
				hitDesc = "The "+attackDesc+ " hits the "+getDescription()+ ".";
			else
				hitDesc = "The "+attackDesc+ " barely scratches the "+getDescription()+ "...";
			if (showMsg)
				level.addMessage(hitDesc);
			damage((int)(baseDamage*damageMod));
			//attacker.setLastWalkingDirection(Action.SELF);
			return true;
		}
	}
	
	public String getLongDescription(){
		return definition.getLongDescription();
		
	}

	public Monster getEnemy() {
		return enemy;
	}

	public void setEnemy(Monster enemy) {
		this.enemy = enemy;
	}
	
	/** Returns the direction in which the nearest monster is seen */
	public int stareMonster(){
		Monster nearest = getNearestMonster();
		if (nearest == null)
			return -1;
		else
			return stareMonster(getNearestMonster());
	}
	
	public Monster getNearestMonster(){
		VMonster monsters = level.getMonsters();
		Monster nearMonster = null;
		int minDist = 150;
		for (int i = 0; i < monsters.size(); i++){
			Monster monster = (Monster) monsters.elementAt(i);
			int distance = Position.flatDistance(getPosition(), monster.getPosition());
			if (monster != this && distance < minDist){
				minDist = distance;
				nearMonster = monster;
			}
		}
		return nearMonster;
	}
	
	
	public int stareMonster(Monster who){
		if (who.getPosition().z != getPosition().z)
			return -1;
		if (Position.flatDistance(who.getPosition(), getPosition()) <= getDefinition().getSightRange()){
			Position pp = who.getPosition();
			if (pp.x == getPosition().x){
				if (pp.y > getPosition().y){
					return Action.DOWN;
				} else {
                     return Action.UP;
				}
			} else
			if (pp.y == getPosition().y){
				if (pp.x > getPosition().x){
					return Action.RIGHT;
				} else {
					return Action.LEFT;
				}
			} else
			if (pp.x < getPosition().x){
				if (pp.y > getPosition().y)
					return Action.DOWNLEFT;
				else
					return Action.UPLEFT;
			} else {
                if (pp.y > getPosition().y)
					return Action.DOWNRIGHT;
				else
					return Action.UPRIGHT;
			}
		}
		return -1;
	}
	
	public boolean seesPlayer(){
		if (wasSeen()){
			Line sight = new Line(getPosition(), level.getPlayer().getPosition());
			Position point = sight.next();
			while(!point.equals(level.getPlayer().getPosition())){
				if (level.getMapCell(point)!= null && level.getMapCell(point).isOpaque()){
					return false;
				}
				point = sight.next();
				if (!level.isValidCoordinate(point))
					return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean tryHit(Monster attacker){
		setEnemy(attacker);
		int evasion = getEvadeChance();
		//level.addMessage("Evasion "+evasion);
		if (hasCounter("SLEEP"))
			evasion = 0;
		//level.addMessage("Evasion "+evasion);
		//see if evades it
		int weaponAttack = attacker.getDefinition().getAttack();
		if (Util.chance(evasion)){
			level.addMessage("The "+getDescription()+ " dodges the "+attacker.getDescription()+" attack!");
			return false;
		} else {
			if (hasCounter(Consts.C_MONSTER_SLEEP)){
				level.addMessage("The "+attacker.getDescription()+" wakes up the "+getDescription()+ "!");
				setCounter(Consts.C_MONSTER_SLEEP, 0);
			}
			int baseDamage = weaponAttack;
			double damageMod = 1;
			 
			String hitDesc = "";
			int damage = (int)(baseDamage * damageMod);
			double percent = (double)damage/(double)getDefinition().getMaxHits();
			if (percent > 1.0d)
				hitDesc = "The "+attacker.getDescription()+" whacks the "+getDescription()+ " appart!!";
			else if (percent > 0.7d)
				hitDesc = "The "+attacker.getDescription()+" smashes the "+getDescription()+ "!";
			else if (percent > 0.5d)
				hitDesc = "The "+attacker.getDescription()+" grievously hits the "+getDescription()+ "!";
			else if (percent> 0.3d)
				hitDesc = "The "+attacker.getDescription()+" hits the "+getDescription()+ ".";
			else
				hitDesc = "The "+attacker.getDescription()+" barely scratches the "+getDescription()+ "...";
			level.addMessage(hitDesc);
			damage((int)(baseDamage*damageMod));
			return true;
		}
	}
	
	public int getMaxHits(){
		return getDefinition().getMaxHits();
	}
	
	public boolean isFlying(){
		return definition.isCanFly();
	}
	
	public void recoverHits(){
		hits = maxHits;
	}
	
	public boolean isDestroyOnImpact(){
		return definition.isDestroyOnImpact();
	}
	
	public int getBaseCost(){
		return getWalkCost();
	}
	
	private boolean invulnerable;

	public boolean isInvulnerable() {
		return invulnerable;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}
	
	private String invulnerableDescription;

	public String getInvulnerableDescription() {
		return invulnerableDescription;
	}

	public void setInvulnerableDescription(String invulnerableDescription) {
		this.invulnerableDescription = invulnerableDescription;
	}
	
	private int damageMod;

	public int getDamageMod() {
		return damageMod;
	}

	public void setDamageMod(int damageMod) {
		this.damageMod = damageMod;
	}

	public boolean damagesEnemies(){
		return getDefinition().isDamagesEnemies();
	}
	
}