	package crl.player;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import sz.fov.FOV;
import sz.util.Counter;
import sz.util.Debug;
import sz.util.Position;
import sz.util.Util;
import crl.action.Action;
import crl.action.Fire;
import crl.actor.Actor;
import crl.ai.ActionSelector;
import crl.ai.SelectorFactory;
import crl.feature.Feature;
import crl.game.Game;
import crl.game.SFXManager;
import crl.item.Item;
import crl.item.ItemDefinition;
import crl.level.Cell;
import crl.monster.Monster;
import crl.monster.VMonster;
import crl.npc.Hostage;
import crl.npc.NPC;
import crl.player.advancements.Advancement;
import crl.ui.Appearance;
import crl.ui.AppearanceFactory;
import crl.ui.UserInterface;
import crl.ui.effects.EffectFactory;

public class Player extends Actor {
	private Game game;
	private boolean doNotRecordScore = false;
	private static int HITMAX = 60;
	
	                                 
		
	// Attributes
	private String name;
	private int sex;
    private int playerClass;
    private String plot;
    private String plot2;
    private String description;

	//Status
    private int playerLevel = 1;
    private int xp;
    private int nextLevelXP = 1000; //5000
    private int hearts;
    private int heartMax;
    private int score;
	private int keys;
	private int carryMax;
	private int hits;
	private int hitsMax;
	private int baseSightRange;
	private int breathing = 25;
	private int gold;
	private int soulPower;
	private Hashtable weaponSkillsCounters = new Hashtable();
	private Hashtable weaponSkills = new Hashtable();
	private Hashtable customMessages = new Hashtable(); 
	/*private int[] weaponSkillsCounters = new int[13];
	private int[] weaponSkills = new int[13];*/

	private int walkCost = 50;
	private int attackCost = 50;
	private int castCost = 50;
	private int evadeChance;
	private int attack;
	private int coolness;
	
	//Vampire Killer
	private int daggerLevel;
	private int currentMysticWeapon;
	private int whipLevel;
	private int shotLevel;
	private int minorHeartCount;
	private int mUpgradeCount;
	
	private GameSessionInfo gameSessionInfo;
	
	//Status Auxiliars
	private int invisibleCount;
	private int poisonCount;
	private int stunCount;
	private int petrifyCount;
	private int faintCount;
	
	//Relationships
	private transient PlayerEventListener playerEventListener;

	private Hostage currentHostage;
	private Monster enemy;
	
	public void setAdvancementLevels(int[] advancementLevels) {
		this.advancementLevels = advancementLevels;
		statAdvancementLevels = new int[advancementLevels.length-1];
		for (int i = 0; i < advancementLevels.length-1; i++){
			statAdvancementLevels[i]= (int)Math.ceil((advancementLevels[i]+advancementLevels[i+1])/2.0D);
		}
	}

	public Monster getEnemy() {
		return enemy;
	}

	public void setEnemy(Monster enemy) {
		this.enemy = enemy;
	}

	public void addHistoricEvent(String description){
		gameSessionInfo.addHistoryItem(description);
	}
	
	public void setHostage(Hostage who){
		currentHostage = who;
	}
	
	public boolean hasHostage(){
		return currentHostage != null;
	}
	
	public Hostage getHostage(){
		return currentHostage;
	}

	public void addKeys(int x){
		keys += x;
	}

	public void addGold(int x){
		gold += x;
		addScore(x);
		gameSessionInfo.addGold(x);
	}
	
	public void setGold(int x){
		gold = x;
	}

	public int getGold(){
		return gold;
	}

	public void reduceGold(int q){
		gold -= q;
	}

	public void addScore(int x){
		score+=x;
	}
	
	private int[] advancementLevels;
	public boolean deservesAdvancement(int level){
		return false;
	}
	
	private int[] statAdvancementLevels;
	public boolean deservesStatAdvancement(int level){
		return false;
	}
	
	private boolean deservesLevelUp = false;
	public void addXP(int x){
		xp += x;
		if (xp>=nextLevelXP){
			deservesLevelUp = false;
		}
	}
	
	public static final String 
		INCREMENT_HITS = "hits",
		INCREMENT_HEARTS = "hearts",
		INCREMENT_ATTACK = "attack",
		INCREMENT_COMBAT = "combat",
		INCREMENT_INVOKATION = "invok",
		INCREMENT_SPEED = "speed",
		INCREMENT_SOUL = "soul",
		INCREMENT_CARRYING = "carry",
		INCREMENT_DEFENSE = "defense",
		INCREMENT_EVADE = "evade";
	
	public String getLastIncrementString(){
		int temp = 0;
		String tempStr = "";
		temp = getLastIncrement(INCREMENT_HITS);
		if (temp > 0){
			tempStr+=" Hits+"+temp;
		}
		temp = getLastIncrement(INCREMENT_HEARTS);
		if (temp > 0){
			tempStr+=" Hearts+"+temp;
		}
		temp = getLastIncrement(INCREMENT_ATTACK);
		if (temp > 0){
			tempStr+=" Atk+"+temp;
		}
		temp = getLastIncrement(INCREMENT_COMBAT);
		if (temp > 0){
			tempStr+=" Combat+"+temp;
		}
		temp = getLastIncrement(INCREMENT_INVOKATION);
		if (temp > 0){
			tempStr+=" Invoke+"+temp;
		}
		temp = getLastIncrement(INCREMENT_SPEED);
		if (temp > 0){
			tempStr+=" Speed+"+temp;
		}
		temp = getLastIncrement(INCREMENT_SOUL);
		if (temp > 0){
			tempStr+=" Soul+"+temp;
		}
		temp = getLastIncrement(INCREMENT_CARRYING);
		if (temp > 0){
			tempStr+=" Carrying+"+temp;
		}
		temp = getLastIncrement(INCREMENT_DEFENSE);
		if (temp > 0){
			tempStr+=" Defense+"+temp;
		}
		temp = getLastIncrement(INCREMENT_EVADE);
		if (temp > 0){
			tempStr+=" Evade+"+temp;
		}
		return tempStr;
	}
	
	private int getNeededXP(int level){
		return getAvgEnemies(level) * getAvgXP(level);
	}
	
	private int getAvgEnemies(int level){
		if (level == 1)
			return 25;
		else return getAvgEnemies(level-1)+getIncreaseOnEnemies(level);
	}
	
	private int getIncreaseOnEnemies(int level){
		if (level == 1)
			return 0;
		if (level == 2)
			return 20;
		else return getIncreaseOnEnemies(level-1)+5;
	}
	
	private int getAvgXP(int level){
		if (level == 1)
			return 100;
		else return getAvgXP(level-1)+50;
	}

	/*public void finishLevel(){
		playerEventListener.informEvent(EVT_FORWARD);
	}*/

	public int getScore(){
		return score;
	}

	public Player (){
		hitsMax = 20;
		hits = hitsMax;
		heartMax = 20;
		carryMax = 15;
		hearts = 5;
		gold = 0;
		currentMysticWeapon = -1;
		for (int i = 0; i < ItemDefinition.CATS.length; i++){
			resetWeaponSkillLevel(ItemDefinition.CATS[i]);
		}
	}
	
	public static Item LEATHER_WHIP, CHAIN_WHIP, VAMPIRE_WHIP, THORN_WHIP, FLAME_WHIP, LIT_WHIP;

	public int getMysticWeapon() {
		return currentMysticWeapon;
	}

	public void informPlayerEvent(int code){
		Debug.enterMethod(this, "informPlayerEvent", code+"");
		if (playerEventListener != null)
			playerEventListener.informEvent(code);
		Debug.exitMethod();
	}

	public void informPlayerEvent(int code, Object param){
		playerEventListener.informEvent(code, param);
	}

	public int getMorphDefense(){
		if (hasCounter(Consts.C_MYSTMORPH) || hasCounter(Consts.C_MYSTMORPH2)){
			return 1;
		} else if (hasCounter(Consts.C_LUPINEMORPH))
			return 1;
		else if (hasCounter(Consts.C_BEARMORPH))
			return 1;
		else if (hasCounter(Consts.C_BEASTMORPH))
			return 2;
		else if (hasCounter(Consts.C_DEMONMORPH))
			return 3;
		else if (hasCounter(Consts.C_WEREWOLFMORPH))
			return 4;
		return 0;
	}
	
	private void damage(int dam){
		if (!level.isDay())
			dam++;
		
		if (hasCounter(Consts.C_ENERGYSHIELD)){
			level.addMessage("The energy shield covers you!");
			dam = (int)Math.ceil(dam * 2.0d/3.0d);
		}
		
		if (hasCounter(Consts.C_TURTLESHELL)){
			level.addMessage("The turtle soul covers you!");
			dam = (int)Math.ceil(dam * 2.0d/3.0d);
		}
		
		dam -= getArmorDefense();
		dam -= getDefenseBonus();
		
		if (dam <= 0){
			if (Util.chance(70)){
				level.addMessage("You withstand the attack.");
				return;
			} 
			dam = 1;
		}
		if (isInvincible()){
			level.addMessage("You are invincible!");
			return;
		}
		if (getSex()==MALE){
			if (Util.chance(50)){
				SFXManager.play("wav/hurt_male.wav");				
			} else {
				SFXManager.play("wav/hurt_male2.wav");
			}
		} else {
			if (Util.chance(50)){
				SFXManager.play("wav/hurt_female.wav");				
			} else {
				SFXManager.play("wav/hurt_female2.wav");
			}
		}
		
		hits -= dam;
		if (Util.chance(50))
			decreaseWhip();
		if (Util.chance(40))
			level.addBlood(getPosition(), Util.rand(0,1));
	}

	public void selfDamage(int damageType, int dam){
		damage(dam);
		if (hits < 0){
			switch (damageType){
				case Player.DAMAGE_MORPHED_WITH_STRONG_ARMOR:
					gameSessionInfo.setDeathCause(GameSessionInfo.STRANGLED_BY_ARMOR);
					break;
				case Player.DAMAGE_WALKED_ON_LAVA:
					gameSessionInfo.setDeathCause(GameSessionInfo.BURNED_BY_LAVA);
					break;
			}
		}

	}

	public void increaseWeaponSkill(String category){
		Counter c = ((Counter)weaponSkillsCounters.get(category));
		Counter s = ((Counter)weaponSkills.get(category));
		
		c.increase();
		if (c.getCount() > s.getCount()*80+10){
			c.reset();
			if (s.getCount() == 9){
				if (getFlag("WEAPON_MASTER") && getPlayerClass() != CLASS_KNIGHT){
					
				} else {
					UserInterface.getUI().showImportantMessage("You have become a master with "+ItemDefinition.getCategoryDescription(category)+"!");
					s.increase();
					setFlag("WEAPON_MASTER", true);
				}
			} else if (s.getCount() < 10){
				UserInterface.getUI().showImportantMessage("You become better with "+ItemDefinition.getCategoryDescription(category));
				s.increase();
			}
		}
	}

	public void increaseWeaponSkillLevel(String category){
		Counter c = ((Counter)weaponSkillsCounters.get(category));
		Counter s = ((Counter)weaponSkills.get(category));
		c.reset();
		if (s.getCount() < 10){
			s.increase();
		}
	}
	
	public void resetWeaponSkillLevel(String category){
		weaponSkills.put(category, new Counter(0));
		weaponSkillsCounters.put(category, new Counter(0));
	}

	private int getBackFlipChance(){
		return 20 + getAttack();
	}
	
	private int blockDirection, blockDirection1, blockDirection2;
	
	public void setShieldGuard(int direction, int turns){
		setCounter("SHIELD_GUARD", turns);
		switch (direction){
		case Action.UP:
			blockDirection = Action.DOWN;
			blockDirection1 = Action.DOWNLEFT;
			blockDirection2 = Action.DOWNRIGHT;
			break;
		case Action.DOWN:
			blockDirection = Action.UP;
			blockDirection1 = Action.UPLEFT;
			blockDirection2 = Action.UPRIGHT;
			break;
		case Action.LEFT:
			blockDirection = Action.RIGHT;
			blockDirection1 = Action.UPRIGHT;
			blockDirection2 = Action.DOWNRIGHT;
			break;
		case Action.RIGHT:
			blockDirection = Action.LEFT;
			blockDirection1 = Action.UPLEFT;
			blockDirection2 = Action.DOWNLEFT;
			break;
		case Action.UPLEFT:
			blockDirection = Action.DOWNRIGHT;
			blockDirection1 = Action.RIGHT;
			blockDirection2 = Action.DOWN;
			break;
		case Action.UPRIGHT:
			blockDirection = Action.DOWNLEFT;
			blockDirection1 = Action.LEFT;
			blockDirection2 = Action.DOWN;
			break;
		case Action.DOWNRIGHT:
			blockDirection = Action.UPLEFT;
			blockDirection1 = Action.LEFT;
			blockDirection2 = Action.UP;
			break;
		case Action.DOWNLEFT:
			blockDirection = Action.UPRIGHT;
			blockDirection1 = Action.RIGHT;
			blockDirection2 = Action.UP;
			break;
		}
	}
	
	public boolean damage (Monster who, int dam){
		int attackDirection = Action.getGeneralDirection(who.getPosition(), getPosition());
		if (hasEnergyField()){
			level.addMessage("The "+who.getDescription()+" is shocked!");
			who.damage(1);
			return false;
		}
		
		if (Util.chance(getEvadeChance())) {
			level.addMessage("You jump and avoid the "+who.getDescription()+" attack");
			return false;
		}
		
		if (getFlag("PASIVE_BACKFLIP") && Util.chance(getBackFlipChance()) && Util.chance(evadeChance)){
			level.addMessage("You backflip and avoid the "+who.getDescription()+" attack!");
			return false;
		}

		
		if (getWeapon() != null && Util.chance(getWeapon().getCoverage())){
			level.addMessage("You parry the attack with your "+getWeapon().getDescription());
			return false;
		}
		
		if (getShield() != null &&
				(getWeapon() == null || (getWeapon()!= null && !getWeapon().isTwoHanded()))
				){
			int blockChance = getShieldBlockChance();
			int coverageChance = getShieldCoverageChance();
			
			if (hasCounter("SHIELD_GUARD")){
				if (attackDirection == blockDirection ||
					attackDirection == blockDirection1 ||
					attackDirection == blockDirection2){
					level.addMessage("You withstand the attack!");
					blockChance *= 3;
					coverageChance = 100;
				} 
			} 
			
			if (Util.chance(blockChance)){
				level.addMessage("You completely block the attack with your "+getShield().getDescription());
				increaseWeaponSkill(ItemDefinition.CAT_SHIELD);
				return false;
			}
			
			if (Util.chance(coverageChance)){
				level.addMessage("Your "+getShield().getDescription()+" is hit.");
				dam -= getShield().getDefense();
			}
		}

		damage(dam);
		if (hits < 0){
			if (getSex() == MALE)
				SFXManager.play("wav/die_male.wav");
			else
				SFXManager.play("wav/die_female.wav");
				
			gameSessionInfo.setDeathCause(GameSessionInfo.KILLED);
			gameSessionInfo.setKillerMonster(who);
			gameSessionInfo.setDeathLevel(level.getLevelNumber());
		}
		return true;
	}

	public void checkDeath(){
		if (hits < 0) informPlayerEvent (DEATH);
	}

	public void setMysticWeapon(int value) {
		currentMysticWeapon = value;
	}

	private Hashtable inventory = new Hashtable();

	public String getSecondaryWeaponDescription(){
		if (getPlayerClass() == CLASS_VAMPIREKILLER){
			if (getMysticWeapon() != -1)
				return weaponName(getMysticWeapon());
			else
				return "None";
		} else {
			if (getSecondaryWeapon() != null)
				return getSecondaryWeapon().getAttributesDescription();
			else
				return "";
		}		
	}
	
	public String getEquipedWeaponDescription(){
		if (weapon != null)
			return (weapon.hasCounter(Consts.C_WEAPON_ENCHANTMENT) ? "Enchanted ":"") + weapon.getAttributesDescription();
		else
			return "Nothing";
	}

	public String getArmorDescription(){
		if (armor != null)
			return armor.getAttributesDescription();
		else
			return "Nothing";
	}

	public String getAccDescription(){
		if (shield == null)
			return "Nothing";
		else
			return shield.getAttributesDescription();
	}


	public void addItem(Item toAdd){
		if (!canCarry()){
			if (level != null)
				level.addMessage("You can't carry anything more");
			return;
		}
		String[] effectOnAcquire = toAdd.getEffectOnAcquire().split(" ");
		if (effectOnAcquire[0].equals("KEYS"))
			addKeys(Integer.parseInt(effectOnAcquire[1]));
		else
		if (effectOnAcquire[0].equals("HEARTMAX"))
			increaseHeartMax(Integer.parseInt(effectOnAcquire[1]));
		else
		if (effectOnAcquire[0].equals("HITSMAX"))
			increaseHitsMax(Integer.parseInt(effectOnAcquire[1]));
		else
		if (effectOnAcquire[0].equals("ENABLE")){
			if (effectOnAcquire[1].equals("LITSPELL"))
				setFlag(Consts.C_SPELL_LIT, true);
			else
			if (effectOnAcquire[1].equals("FLAMESPELL"))
				setFlag(Consts.C_SPELL_FIRE, true);
			else
			if (effectOnAcquire[1].equals("ICESPELL"))
				setFlag(Consts.C_SPELL_ICE, true);
			if (effectOnAcquire[1].equals("SILVERDAGGER")){
				if (daggerLevel == 0)
					daggerLevel = 1;
			} else
			if (effectOnAcquire[1].equals("GOLDDAGGER")){
				daggerLevel = 2;
			}
		}else
		if (effectOnAcquire[0].equals("CARRY"))
			setCarryMax (Integer.parseInt(effectOnAcquire[1]));

		if (!effectOnAcquire[0].equals("") && toAdd.getDefinition().isSingleUse()) ;

		else {
			if (canCarry()){
				String toAddID = toAdd.getFullID();
				Equipment equipmentx = (Equipment) inventory.get(toAddID);
				if (equipmentx == null)
					inventory.put(toAddID, new Equipment(toAdd, 1));
				else
					equipmentx.increaseQuantity();
			}
		}
	}

	private Item weapon;
	private Item secondaryWeapon;
	private Item armor;
	private Item shield;

	public int getItemCount(){
		int eqCount = 0;
		Enumeration en = inventory.elements();
		while (en.hasMoreElements())
			eqCount += ((Equipment)en.nextElement()).getQuantity();
		return eqCount;
	}
	public boolean canCarry(){
		return getItemCount() < carryMax;
		//return true;
	}

	private void removeItem(Equipment toRemove){
		
		inventory.remove(toRemove.getItem().getFullID());
	}
	
	public boolean hasItem (Item item){
		return inventory.containsKey(item.getFullID());
	}
	
	public boolean hasItemByID (String itemID){
		return inventory.containsKey(itemID);
	}

	/*public void removeItem(Item toRemove){
		inventory.remove(toRemove.getDefinition().getID());
	}*/

	public Vector getInventory(){
		Vector ret = new Vector();
		Enumeration x = inventory.elements();
		while (x.hasMoreElements())
			ret.add(x.nextElement());
		return ret;
	}


	public void addHearts(int howMuch){
		minorHeartCount++;
		hearts += howMuch;
		if (hearts > heartMax)
			hearts = heartMax;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int value) {
		hits = value;
		if (hits > hitsMax)
			hits = hitsMax;
	}

	public int getHearts() {
		return hearts;
	}

	public void setHearts(int value) {
		hearts = value;
	}

	public String getName() {
		return name;
	}

	private String classString;
	public String getClassString(){
		return classString;
	}

	public void setName(String value) {
		name = value;
	}

	public PlayerEventListener getPlayerEventListener() {
		return playerEventListener;
	}

	public void setPlayerEventListener(PlayerEventListener value) {
		playerEventListener = value;
	}

	public void reduceHearts(int jijiji){
		hearts -= jijiji;
	}

	public static String weaponName(int code){
		switch (code){
			case AXE:
				return "Axe";
			case CROSS:
				return "Cross";
			case DAGGER:
				return "Mystic Dagger";
			case HOLY:
				return "Holy water";
			case STOPWATCH:
				return "Stopwatch";
			case BIBLE:
				return "Holy Bible";
			case SACRED_CRYSTAL:
				return "Crystal";
			case SACRED_FIST:
				return "Sacred Fist";
			case SACRED_REBOUND:
				return "Rebound Crystal";
			default:
				return "No Weapon";
		}
	}

	public void bounceBack(Position var, int dep){
		Debug.enterMethod(this, "bounceBack", var +","+dep);
		int startingHeight = level.getMapCell(getPosition()).getHeight();
		out: for (int i = 1; i < dep; i++){
        	Position destinationPoint = Position.add(getPosition(), var);
        	Cell destinationCell = level.getMapCell(destinationPoint);
        	/*if (destinationCell == null)
        		break out;*/
        	if (destinationCell == null){
        		if (!level.isValidCoordinate(destinationPoint)){
        			destinationPoint = Position.subs(destinationPoint, var);
        			landOn(destinationPoint);
					break out;
        		}
        		if (i < dep-1){
					setPosition(destinationPoint);
					continue out;
        		}
				else{
					landOn(destinationPoint);
					break out;
				}
        		
        	}
        	Feature destinationFeature = level.getFeatureAt(destinationPoint);
        	if (destinationFeature != null && destinationFeature.getKeyCost() > getKeys()){
        		land();
        		break out;
        	}
       		if (destinationCell.getHeight() > startingHeight+2){
				land();
				break out;
			} else {
				if (!destinationCell.isSolid()) {
					if (i < dep-1)
						setPosition(destinationPoint);
					else
						landOn(destinationPoint);
				} else {
					level.addMessage("You bump into the "+destinationCell.getShortDescription());
					land();
					break out;
				}
			}
		}
		Debug.exitMethod();
	}
/*		if (standsOnPlace() || level.getMapCell(getPosition()).isStair() || isInvincible() || hasEnergyField())
			return;
		Position landingPoint = null;
		
		for (int run = 0; run < dep; run++){
			landingPoint = Position.add(getPosition(), variation);
			if (!level.isValidCoordinate(landingPoint)){
				land();
				return;
			}
			Cell landingCell = getLevel().getMapCell(landingPoint);
			if (landingCell == null){
				if (run < dep-1){
					//setPosition(landingPoint);
					landOn(landingPoint);
				} else {
					landingPoint = level.getDeepPosition(landingPoint);
					if (landingPoint == null){
						level.addMessage("You are thrown into a endless pit!");
						gameSessionInfo.setDeathCause(GameSessionInfo.ENDLESS_PIT);
						hits = -1;
						informPlayerEvent(Player.DEATH);
						return;
					} else {
						landOn(landingPoint);
						return;
					}
				}
			} else {
				if (!landingCell.isSolid()
				 && landingCell.getHeight() <= getLevel().getMapCell(getPosition()).getHeight()) {
					if (run < dep-1){
						landOn(landingPoint);
					} else {
						landOn(landingPoint);
						return;
					}
				} else {
					return;
				}
			}
		}
	}*/
	
	public boolean isSwimming(){
		Cell mapcell = level.getMapCell(getPosition());
		return mapcell != null && (mapcell.isWater() || mapcell.isShallowWater());
	}

	public GameSessionInfo getGameSessionInfo() {
		return gameSessionInfo;
	}

	public void setGameSessionInfo(GameSessionInfo value) {
		gameSessionInfo = value;
	}

	public int getKeys() {
		return keys;
	}

	public void increaseKeys(){
		keys++;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int value) {
		sex = value;
	}

	public void updateStatus(){
		if (getCounter(Consts.C_BATMORPH) == 1 || getCounter(Consts.C_BATMORPH2) == 1){
			level.addMessage("You regain your human shape!");
			land();
		}
		
		if (getCounter(Consts.C_MYSTMORPH) == 1 || 
			getCounter(Consts.C_MYSTMORPH2) == 1 ||
			getCounter(Consts.C_BEARMORPH) == 1 ||
			getCounter(Consts.C_LUPINEMORPH) == 1 ||
			getCounter(Consts.C_BEASTMORPH) == 1 ||
			getCounter(Consts.C_DEMONMORPH) == 1 ||
			getCounter(Consts.C_WEREWOLFMORPH) == 1){
			level.addMessage("You regain your human shape!");
			land();
		}
		
		if (getCounter("REGAIN_SHAPE") == 1){
			setSelector(originalSelector);
			setFlag("KEEPMESSAGES", false);
		}
		
		for (int i = 0; i < counteredItems.size(); i++){
			Item item = (Item)counteredItems.elementAt(i);
			item.reduceCounters(this);
			if (!item.hasCounters()){
				counteredItems.remove(item);
			}
		}
			
		super.updateStatus();
		
			
		if (hasIncreasedDefense()) defenseCounter--;
		if (isInvisible()) invisibleCount--;
    	if (hasIncreasedJumping()) jumpingCounter--;
    	if (isInvincible()) invincibleCount--;
    	if (hasEnergyField()) energyFieldCounter--;
    	
    	if (isPoisoned()){
    		poisonCount--;
    		if (!isPoisoned())
    			level.addMessage("The poison leaves your blood.");
    	}
    	if (isStunned()) stunCount--;
    	if (isPetrified()) petrifyCount--;
    	if (isFainted()) faintCount--;
    	
    	if (isPoisoned()){
    		if (Util.chance(40)){
    			level.addMessage("You feel the poison coursing through your veins!");
    			selfDamage(Player.DAMAGE_POISON, 3);
    		}
    	}
    	if (getHoverHeight() > 0)
    		if (hasCounter(Consts.C_BATMORPH) || hasCounter(Consts.C_BATMORPH2))
    			;
    		else
    			setHoverHeight(getHoverHeight()-4);
    	if (level.getMapCell(getPosition()) != null && level.getMapCell(getPosition()).isWater()){
    		if (getFlag("PLAYER_SWIMMING")){
    			if (getCounter("OXYGEN") == 0){
    				drown();
    			} else if (getCounter("OXYGEN") == 5){
   					level.addMessage("You are almost drown!");
    			} else if (getCounter("OXYGEN") == 15){
    				level.addMessage("You are drowning!");
    			}
    		} else {
    			setCounter("OXYGEN", getBreathing());
    			level.addMessage("You start swimming!");
    			setFlag("PLAYER_SWIMMING", true);
    		}
    	} else {
    		setFlag("PLAYER_SWIMMING", false);
    	}
        //regen();
	}

	private void levelUp(){
		nextLevelXP += getNeededXP(playerLevel);
		if (playerLevel % 2 == 0){
			hitsMax++;
			addLastIncrement(INCREMENT_HITS,1);
		}
		if (playerLevel % 3 == 0){
			soulPower++;
			addLastIncrement(INCREMENT_SOUL,1);
		}
		if (playerLevel % 3 == 0){
			attack++;
			addLastIncrement(INCREMENT_ATTACK,1);
		}
		if (playerLevel % 5 == 0){
			defense++;
			addLastIncrement(INCREMENT_DEFENSE,1);
		}
		heartMax += 1;
		addLastIncrement(INCREMENT_HEARTS,1);
		SFXManager.play("wav/levelup.wav");
		informPlayerEvent(EVT_LEVELUP);
		increaseCoolness(20);
		playerLevel++;
		deservesLevelUp = false;		
	}
	
	public void act(){
		if (deservesLevelUp){
			levelUp();
		}
		addHearts(1);
		Vector<Item> items = level.getItemsAt(getPosition());
		if (items != null){
			Vector<Item> itemsx = new Vector<Item>(items);
			for(Item item: itemsx){
				if (item.isAutoUse()){
					Action actionObject = null;
					try {
						actionObject = (Action)Class.forName(item.getAction()).newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					actionObject.setItem(item);
					if (!actionObject.canPerform(this))
						continue;
					
					actionObject.setPerformer(this);
					actionObject.execute();
				}
			}
		}
		if (false) {
			//setJustJumped(false);
		} else if (isStunned()){
			if (Util.chance(40)){
				level.addMessage("You cannot move!");
				updateStatus();
			}
			else
				super.act();
		} else if (isPetrified()){
			level.addMessage("You are petrified!");
			updateStatus();
			see();
			UserInterface.getUI().refresh();
		} else if (isFainted()){
			updateStatus();
			see();
			UserInterface.getUI().refresh();
		} else {
			super.act();
		}
		 
	}

	public void land(){
		Debug.enterMethod(this, "land");
		landOn (getPosition());
		Debug.exitMethod();
	}

	private Position getFreeSquareAround(Position destinationPoint){
		Position tryP = Position.add(destinationPoint, Action.directionToVariation(Action.UP));
		if (level.getMapCell(tryP) != null && !level.getMapCell(tryP).isSolid()){
			return tryP;
		} 
		
		tryP = Position.add(destinationPoint, Action.directionToVariation(Action.DOWN));
		if (level.getMapCell(tryP) != null && !level.getMapCell(tryP).isSolid()){
			return tryP;
		}
		
		tryP = Position.add(destinationPoint, Action.directionToVariation(Action.LEFT));
		if (level.getMapCell(tryP) != null && !level.getMapCell(tryP).isSolid()){
			return tryP;
		}
					
		tryP = Position.add(destinationPoint, Action.directionToVariation(Action.RIGHT));
		if (level.getMapCell(tryP) != null && !level.getMapCell(tryP).isSolid()){
			return tryP;
		}
		return null;
	}
		
	public void landOn (Position destinationPoint){
		Debug.enterMethod(this, "landOn", destinationPoint);
		Cell destinationCell = level.getMapCell(destinationPoint);
        if (destinationCell == null || destinationCell.isEthereal()){
        	destinationPoint = level.getDeepPosition(destinationPoint);
        	if (destinationPoint == null) {
        		level.addMessage("You fall into a endless pit!");
				gameSessionInfo.setDeathCause(GameSessionInfo.ENDLESS_PIT);
				hits = -1;
				informPlayerEvent(Player.DEATH);
				Debug.exitMethod();
				return;
        	}else {
        		destinationCell = level.getMapCell(destinationPoint);
        	}
        }
        
        setPosition(destinationPoint);
        
		if (destinationCell.isSolid() && !isEthereal()){
			// Tries to land on a freesquare around
			Position tryp = getFreeSquareAround(destinationPoint);
			if (tryp == null){
				level.addMessage("You are smashed inside the "+destinationCell.getShortDescription()+"!");
				gameSessionInfo.setDeathCause(GameSessionInfo.SMASHED);
				hits = -1;
				informPlayerEvent(Player.EVT_SMASHED);
				Debug.exitMethod();
				return;
			} else {
				landOn(tryp);
				Debug.exitMethod();
				return;
			}
			
		}
		if (destinationCell.getDamageOnStep() > 0){
			if (!isInvincible()){
				level.addMessage("You are injured by the "+destinationCell.getShortDescription()+"!");
				selfDamage(Player.DAMAGE_WALKED_ON_LAVA, 2);
			}
		}

		if (destinationCell.getHeightMod() != 0){
			setPosition(Position.add(destinationPoint, new Position(0,0, destinationCell.getHeightMod())));
		}
		
		if (destinationCell.isShallowWater()){
			level.addMessage("You swim in the "+destinationCell.getShortDescription()+"!");
		}
		Vector destinationItems = level.getItemsAt(destinationPoint);
		if (destinationItems != null){
			if (destinationItems.size() == 1)
				level.addMessage("There is a "+((Item)destinationItems.elementAt(0)).getDescription()+" here");
			else 
				level.addMessage("There are several items here");
		}
		
		Actor aActor = level.getActorAt(destinationPoint);
		if (aActor instanceof Hostage){
			if (!hasHostage() && !((Hostage)aActor).isRescued()){
				setHostage((Hostage)aActor);
				addHistoricEvent("rescued "+aActor.getDescription()+" from the "+level.getDescription());
				level.removeMonster((Monster)aActor);
			}
		}
		
		Feature[] destinationFeatures = level.getFeaturesAt(destinationPoint);
		Feature destinationFeature = null;
		boolean played = false;
		if (destinationFeatures != null){
			for (int i = 0; i < destinationFeatures.length; i++){
				destinationFeature = destinationFeatures[i];
				if (destinationFeature.getKeyCost() > 0){
					reduceKeys(destinationFeature.getKeyCost());
					//Debug.say("I destroy the "+destinationFeature);
					level.destroyFeature(destinationFeature);
				}
				
				if (destinationFeature.getID().equals("TELEPORT")){
					if (getGold()>1000){
						UserInterface.getUI().showMessage("Drop a thousand in gold to return to Petra? [Y/N]");
						if (UserInterface.getUI().prompt()){
							if (getHostage() != null){
								abandonHostage();
							}
							SFXManager.play("wav/loutwarp.wav");
							informPlayerEvent(Player.EVT_GOTO_LEVEL, "TOWN0");
							getLevel().setLevelNumber(0);
							landOn(Position.add(getLevel().getExitFor("FOREST0"), new Position(-1,0,0)));
							reduceGold(1000);
							
							return;
						}
					} else {
						level.addMessage("There is something engraved here: \"Of Gold A Thousand Put Here And Be Gone\"");
					}
				}

				if (destinationFeature.getHeightMod() != 0){
					setPosition(Position.add(destinationPoint, new Position(0,0, destinationFeature.getHeightMod())));
				}
				if (destinationFeature.getHeartPrize() > 0){
					level.addMessage("You get "+destinationFeature.getHeartPrize()+" hearts");
					addHearts(destinationFeature.getHeartPrize());
					level.destroyFeature(destinationFeature);
					if (!played){
						played = true;
						SFXManager.play("wav/pickup.wav");
						
					}
				}

				if (destinationFeature.getScorePrize() > 0){
					level.addMessage("You pickup the "+destinationFeature.getDescription()+".");
					addGold(destinationFeature.getScorePrize());
					level.destroyFeature(destinationFeature);
					if (!played){
						played = true;
						SFXManager.play("wav/bonusblp.wav");
					}
				}

				if (destinationFeature.getKeyPrize() > 0){
					level.addMessage("You find "+destinationFeature.getKeyPrize()+" castle key!");
					addKeys(destinationFeature.getKeyPrize());
					level.destroyFeature(destinationFeature);
					if (!played){
						played = true;
						SFXManager.play("wav/bonusblp.wav");
					}
				}
				if (destinationFeature.getUpgradePrize() > 0){
					if (whipLevel < 2)
						increaseWhip();
					level.destroyFeature(destinationFeature);
					if (!played){
						played = true;
						SFXManager.play("wav/bonusblp.wav");
					}
				}

				if (destinationFeature.getMysticWeaponPrize() != -1){
					if (getMysticWeapon() != -1){
						Position tryp = getFreeSquareAround(destinationPoint);
						if (tryp != null){
							level.addFeature(getFeatureNameForMystic(getMysticWeapon()), tryp);
						}
					}
					level.addMessage("You get the "+ Player.weaponName(destinationFeature.getMysticWeaponPrize()) +"!");
					setMysticWeapon(destinationFeature.getMysticWeaponPrize());
					level.destroyFeature(destinationFeature);
					if (!played){
						played = true;
						SFXManager.play("wav/bonusblp.wav");
					}
				}

				if (destinationFeature.getHealPrize() > 0){
					level.addMessage("You eat the "+ destinationFeature.getDescription() +"!");
					setHits(getHits() + destinationFeature.getHealPrize());
					level.destroyFeature(destinationFeature);
					if (!played){
						played = true;
						SFXManager.play("wav/bonusblp.wav");
					}
				}

				if (destinationFeature.getEffect() != null)
					if (destinationFeature.getEffect().equals("ROSARY")){
						invokeRosary();
						level.destroyFeature(destinationFeature);
					} else
					if (destinationFeature.getEffect().equals("SPAWN_TREASURE")){
						level.addMessage("A treasure rises from the ground!");
						level.spawnTreasure();
						level.destroyFeature(destinationFeature);
					} else
					if (destinationFeature.getEffect().equals("MUPGRADE")){
						level.addMessage("Your "+getWeaponDescription()+" gets stronger");
						increaseShot();
						level.destroyFeature(destinationFeature);
						if (!played){
							played = true;
							SFXManager.play("wav/bonusblp.wav");
						}
					} else
					if (destinationFeature.getEffect().equals("INVISIBILITY")){
						level.addMessage("You drink the potion of invisibility!");
						setInvisible(30);
						level.destroyFeature(destinationFeature);
						if (!played){
							played = true;
							SFXManager.play("wav/bonusblp.wav");
						}
					}

				/*if (destinationFeature.getTrigger() != null)
					if (destinationFeature.getTrigger().equals("ENDGAME"))
						;
						/*if (aPlayer.getKeys() == 10)
							aPlayer.informPlayerEvent(Player.OPENEDCASTLE);*/
				Feature pred = destinationFeature;
				destinationFeature = level.getFeatureAt(destinationPoint);
				if (destinationFeature == pred)
					destinationFeature = null;
			}
		}
		
			
			
		if (level.isExit(getPosition())){
			String exit = level.getExitOn(getPosition());
			if (exit.equals("_START") || exit.startsWith("#")){
				//Do nothing. This must be changed with startsWith("_");
			} /*else if (exit.equals("_NEXT")){
				informPlayerEvent(Player.EVT_NEXT_LEVEL);
			} else if (exit.equals("_BACK")){
				informPlayerEvent(Player.EVT_BACK_LEVEL);
			} */else {
				informPlayerEvent(Player.EVT_GOTO_LEVEL, exit);
			}
			
		}
		Debug.exitMethod();
	}

	private void drown(){
		gameSessionInfo.setDeathCause(GameSessionInfo.DROWNED);
		gameSessionInfo.setDeathLevel(level.getLevelNumber());
		this.hits = -1;
		informPlayerEvent(Player.DROWNED);
	}
	
	public boolean deservesUpgrade(){
		if (getPlayerClass() != CLASS_VAMPIREKILLER )
			return false;
		if (getWeapon() == VAMPIRE_WHIP)
			return false;
		if (minorHeartCount > 5){
			minorHeartCount = 0;
			return true;
		}
		return false;
	}
	
	public boolean deservesMUpgrade(){
		if (getPlayerClass() != CLASS_VAMPIREKILLER )
			return false;
		//Debug.say(mUpgradeCount);
		if (shotLevel > 1)
			return false;
		if (mUpgradeCount > 50){
			mUpgradeCount = 0;
			return true;
		}
		return false;
	}
	
	

	private void invokeRosary(){
		level.addMessage("A blast of holy light surrounds you!");
		//level.addEffect(new SplashEffect(getPosition(), "****~~~~,,,,....", Appearance.WHITE));
		SFXManager.play("wav/lazrshot.wav");
		level.addEffect(EffectFactory.getSingleton().createLocatedEffect(getPosition(), "SFX_ROSARY_BLAST"));
		
		String message = "";

		Vector monsters = (Vector) level.getMonsters().getVector().clone();
		Vector removables = new Vector();
		for (int i = 0; i < monsters.size(); i++) {
			Monster monster = (Monster) monsters.elementAt(i);
			if (Position.flatDistance(getPosition(), monster.getPosition()) < 16){
				if (monster instanceof NPC || monster instanceof Hostage){
					
				} else {
					//targetMonster.damage(player.getWhipLevel());
					monster.damage(10);
		        	if (monster.isDead()){
		        		
		        		message = "The "+monster.getDescription()+" is shredded by the holy light!";
			        	removables.add(monster);
					} else {
						message = "The "+monster.getDescription()+" is purified by the holy light!";
					}
		        	if (monster.wasSeen())
		        		level.addMessage(message);
				}
			}
		}
		monsters.removeAll(removables);
		//level.removeMonster(monster);
 	}

	public boolean isFainted() {
		return faintCount > 0;
	}

	public void setFainted(int counter) {
		faintCount = counter;
	}
	
	public boolean isPetrified() {
		return petrifyCount > 0;
	}

	public void setPetrify(int counter) {
		petrifyCount = counter;
	}
	
	public boolean isInvisible() {
		return invisibleCount > 0;
	}

	public void setInvisible(int counter) {
		invisibleCount = counter;
	}

	public boolean isPoisoned(){
		return poisonCount > 0;
	}
	
	public void setPoison(int counter){
		poisonCount = counter;
	}
	
	public boolean isStunned(){
		return stunCount > 0;
	}
	
	public void setStun(int counter){
		stunCount = counter;
	}
	
	private int invincibleCount;

	public boolean isInvincible(){
		return invincibleCount > 0;
	}

	public void setInvincible(int counter) {
		invincibleCount = counter;
	}

	private int jumpingCounter;

	public void increaseJumping(int counter) {
		jumpingCounter = counter;
    }

    public boolean hasIncreasedJumping(){
    	return jumpingCounter > 0;
    }

	public boolean isThornWhip(){
		return weapon == THORN_WHIP;
	}

    public boolean isFireWhip(){
    	return weapon == FLAME_WHIP;
    }

    public boolean isLightingWhip(){
    	return weapon == LIT_WHIP;
    }

    public void setFireWhip(){
    	if (playerClass == CLASS_VAMPIREKILLER)
    		weapon = FLAME_WHIP;
    }
    
    public void setLitWhip(){
    	if (playerClass == CLASS_VAMPIREKILLER)
    		weapon = LIT_WHIP;
    }
    
    public void setThornWhip(){
    	if (playerClass == CLASS_VAMPIREKILLER)
    		weapon = THORN_WHIP;
    }
    
    public void increaseShot(){
    	if (playerClass == CLASS_VAMPIREKILLER && shotLevel < 2)
    		shotLevel++;
    }
    
    public int getShotLevel(){
    	return shotLevel;
    }
    
    public void increaseWhip(){
    	if (!(getPlayerClass() == CLASS_VAMPIREKILLER))
    		return;
    	if (whipLevel < 2)
    		whipLevel++;
    	else
    		return;
    	switch (whipLevel){
    	case 1:
    		level.addMessage("Your leather whip turns into a chain whip!");
    		weapon = CHAIN_WHIP;
    		break;
    	case 2:
    		level.addMessage("Your chain whip turns into the Vampire Killer!");
    		weapon = VAMPIRE_WHIP;
    	}
    }
    
    public void decreaseWhip(){
    	if (!(getPlayerClass() == CLASS_VAMPIREKILLER))
    		return;
    	if (shotLevel > 0)
    		shotLevel--;
    	if (whipLevel > 0)
    		whipLevel--;
    	else
    		return;
    	switch (whipLevel){
    	case 1:
    		level.addMessage("Your Vampire Killer turns into a chain whip!");
    		weapon = CHAIN_WHIP;
    		break;
    	case 0:
    		level.addMessage("Your chain whip turns into a leather whip!");
    		weapon = LEATHER_WHIP;
    	}
    }

    private int defense; //Temporary stat
	private int defenseCounter;

	public void increaseDefense(int counter){
		defense++;
		defenseCounter = counter;
	}

	public boolean hasIncreasedDefense(){
		return defenseCounter > 0;
	}

	private int energyFieldCounter;

	public void setEnergyField(int counter){
		energyFieldCounter = counter;
	}

	public boolean hasEnergyField(){
		return energyFieldCounter > 0;
	}

	public void heal(){
		hits = hitsMax;
	}

	public void reduceQuantityOf(Item what){
		String toAddID = what.getFullID();
		Equipment equipment = (Equipment)inventory.get(toAddID);
		equipment.reduceQuantity();
		if (equipment.isEmpty())
			removeItem(equipment);
	}
	
	public void reduceQuantityOf(String toAddID){
		Equipment equipment = (Equipment)inventory.get(toAddID);
		equipment.reduceQuantity();
		if (equipment.isEmpty())
			removeItem(equipment);
	}

	public int getDefenseCounter() {
		return defenseCounter;
	}

	public void setHeartMax(int value) {
		heartMax = value;
	}

	public void setCarryMax(int value) {
		carryMax = value;
	}

	public int getPlayerClass() {
		return playerClass;
	}

	public boolean isEthereal(){
		//return hasCounter(Consts.C_MYSTMORPH) || hasCounter(Consts.C_MYSTMORPH2);
		return false;
	}
	
	public boolean isFlying(){
		//return hasCounter(Consts.C_BATMORPH) || hasCounter(Consts.C_BATMORPH2) || isEthereal();
		return false;
	}

	public void recoverHits(int i){
		hits += i;
		if (hits > hitsMax)
			hits = hitsMax;
    }
	
	public void recoverHitsP(int p){
		int recovery = (int)Math.round((double)getHitsMax() * (p/100.0D));
		recoverHits(recovery);
	}

    public void reduceKeys(int k){
		keys-=k;
	}

    /*public void regen() {
    	if (regenRate > 0)
    		regenCont++;
    	if (regenCont > regenRate){
    		regenCont = 0;
    		recoverHits(1);
    	}
    }*/
    
	public void setPlayerClass(int value) {
		playerClass = value;
		switch (playerClass){
			case CLASS_VAMPIREKILLER:
				classString = "Vampire Killer";
				break;
			case CLASS_INVOKER:
				classString = "Soul Master";
				break;
			case CLASS_KNIGHT:
				classString = "Knight";
				break;
			case CLASS_MANBEAST:
				if (getSex() == MALE)
					classString = "Man Beast";
				else
					classString = "Woman Beast";
				break;
			case CLASS_RENEGADE:
				classString = "Renegade";
				break;
			case CLASS_VANQUISHER:
				classString = "Vanquisher";
				break;
		}
	}

    public Appearance getAppearance(){
    	if (hasCounter(Consts.C_BATMORPH))
			return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_BAT");
    	else if (hasCounter(Consts.C_BATMORPH2))
    		return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_BAT2");
		else if (hasCounter(Consts.C_LUPINEMORPH))
			return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_LUPINE");
		else if (hasCounter(Consts.C_BEARMORPH))
			return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_WEREBEAR");
		else if (hasCounter(Consts.C_BEASTMORPH))
			return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_WEREBEAST");
		else if (hasCounter(Consts.C_DEMONMORPH))
			return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_WEREDEMON");
		else if (hasCounter(Consts.C_WEREWOLFMORPH))
			return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_WEREWOLF");
		else if (hasCounter(Consts.C_WOLFMORPH))
			return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_WOLF");
		else if (hasCounter(Consts.C_WOLFMORPH2))
			return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_WOLF2");
		else if (hasCounter(Consts.C_MYSTMORPH))
			return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_MYST");
		else if (hasCounter(Consts.C_MYSTMORPH2))
			return AppearanceFactory.getAppearanceFactory().getAppearance("MORPHED_MYST2");
		else {
			Appearance ret = super.getAppearance();
			if (ret == null){
				setAppearance(AppearanceFactory.getAppearanceFactory().getAppearance("MEGAMAN"));
				ret = super.getAppearance();
			}
			return ret; 
		}
    }

	private Vector availableSkills = new Vector(10);

	
	public Vector getAvailableSkills(){
		availableSkills.removeAllElements();
		if (playerClass == CLASS_VAMPIREKILLER){
			if (getFlag("SKILL_WARP_DASH"))
				availableSkills.add(skills.get("WARP_DASH"));
			if (getFlag("SKILL_AIR_DASH"))
				availableSkills.add(skills.get("AIR_DASH"));
			if (getFlag("SKILL_SLIDEKICK"))
				availableSkills.add(skills.get("SLIDE_KICK"));
			if (getFlag("SKILL_ITEM_BREAK"))
				availableSkills.add(skills.get("ITEM_BREAK"));
			
			if (getFlag("SKILL_SOULBLAST"))
				availableSkills.add(skills.get("SKILL_SOULBLAST"));
			if (getFlag("SKILL_SOULFLAME"))
				availableSkills.add(skills.get("SKILL_SOULFLAME"));
			if (getFlag("SKILL_SOULICE"))
				availableSkills.add(skills.get("SKILL_SOULICE"));
			if (getFlag("SKILL_SOULSAINT"))
				availableSkills.add(skills.get("SKILL_SOULSAINT"));
			if (getFlag("SKILL_SOULWIND"))
				availableSkills.add(skills.get("SKILL_SOULWIND"));
			
			availableSkills.add(skills.get("MYSTIC_DAGGER"));
			availableSkills.add(skills.get("MYSTIC_AXE"));
			if (getFlag("MYSTIC_HOLY_WATER"))
				availableSkills.add(skills.get("MYSTIC_HOLYWATER"));
			if (getFlag("MYSTIC_HOLY_BIBLE"))
				availableSkills.add(skills.get("MYSTIC_BIBLE"));
			if (getFlag("MYSTIC_STOPWATCH"))
				availableSkills.add(skills.get("MYSTIC_STOPWATCH"));
			if (getFlag("MYSTIC_CROSS"))
				availableSkills.add(skills.get("MYSTIC_CROSS"));
			if (getFlag("MYSTIC_FIST"))
				availableSkills.add(skills.get("MYSTIC_FIST"));
			if (getFlag("MYSTIC_CRYSTAL"))
				availableSkills.add(skills.get("MYSTIC_CRYSTAL"));
			if (getFlag("PASIVE_BACKFLIP"))
				availableSkills.add(skills.get("BACKFLIP"));
			
		}else
		if (playerClass == CLASS_RENEGADE){
			availableSkills.add(skills.get("FIREBALL"));
			availableSkills.add(skills.get("SOULSTEAL"));
			if (getFlag("SKILL_SUMMONSOUL"))
				availableSkills.add(skills.get("SUMMON_SPIRIT"));
			if (getFlag("SKILL_SOULSSTRIKE"))
				availableSkills.add(skills.get("SKILL_SOULSSTRIKE"));
			if (getFlag("SKILL_FLAMESSHOOT"))
				availableSkills.add(skills.get("SKILL_FLAMESSHOOT"));
			if (getFlag("SKILL_HELLFIRE"))
				availableSkills.add(skills.get("SKILL_HELLFIRE"));
			
			availableSkills.add(skills.get("MINOR_JINX"));
			if (getFlag("SKILL_DARKMETAMORPHOSIS"))
				availableSkills.add(skills.get("BLOOD"));
			if (getFlag("SKILL_SHADETELEPORT"))
				availableSkills.add(skills.get("TELEPORT"));
			
			if (getFlag("SKILL_WOLFMORPH2"))
				availableSkills.add(skills.get("SKILL_WOLFMORPH2"));
			else if (getFlag("SKILL_WOLFMORPH"))
				availableSkills.add(skills.get("SKILL_WOLFMORPH"));
			if (getFlag("SKILL_MYSTMORPH2"))
				availableSkills.add(skills.get("SKILL_MYSTMORPH2"));
			else if (getFlag("SKILL_MYSTMORPH"))
				availableSkills.add(skills.get("SKILL_MYSTMORPH"));
			if (getFlag("SKILL_BATMORPH2"))
				availableSkills.add(skills.get("SKILL_BATMORPH2"));
			else if (getFlag("SKILL_BATMORPH"))
				availableSkills.add(skills.get("SKILL_BATMORPH"));
		} else
		if (playerClass == CLASS_INVOKER){
			availableSkills.add(skills.get("INVOKE_BIRD"));
			availableSkills.add(skills.get("INVOKE_TURTLE"));
			if (getFlag("SKILL_CATSOUL"))
				availableSkills.add(skills.get("INVOKE_CAT"));
			if (getFlag("SKILL_BIRDSEGG"))
				availableSkills.add(skills.get("THROW_EGG"));
			if (getFlag("SKILL_MANIPULATE"))
				availableSkills.add(skills.get("MANIPULATE"));
			if (getFlag("SKILL_DRAGONFIRE"))
				availableSkills.add(skills.get("INVOKE_DRAGON"));
			if (getFlag("SKILL_INVOKEBIRD"))
				availableSkills.add(skills.get("SUMMON_BIRD"));
			if (getFlag("SKILL_INVOKETURTLE"))
				availableSkills.add(skills.get("SUMMON_TURTLE"));
			if (getFlag("SKILL_INVOKECAT"))
				availableSkills.add(skills.get("SUMMON_CAT"));
			if (getFlag("SKILL_INVOKEEAGLE"))
				availableSkills.add(skills.get("SUMMON_EAGLE"));
			if (getFlag("SKILL_INVOKETORTOISE"))
				availableSkills.add(skills.get("SUMMON_TORTOISE"));
			if (getFlag("SKILL_INVOKETIGER"))
				availableSkills.add(skills.get("SUMMON_TIGER"));
			if (getFlag("SKILL_INVOKEDRAGON"))
				availableSkills.add(skills.get("SUMMON_DRAGON"));
			
			//availableSkills.add(skills.get("MAJOR_JINX"));
			
		} else
		if (playerClass == CLASS_MANBEAST){
			if (getFlag("SKILL_POWERBLOW3"))
				availableSkills.add(skills.get("IMPACT_BLOW3"));
			else if (getFlag("SKILL_POWERBLOW2"))
				availableSkills.add(skills.get("IMPACT_BLOW2"));
			else if (getFlag("SKILL_POWERBLOW"))
				availableSkills.add(skills.get("IMPACT_BLOW"));
			availableSkills.add(skills.get("CLAW_SWIPE"));
			if (getFlag("SKILL_ENERGYSCYTHE"))
				availableSkills.add(skills.get("ENERGY_SCYTHE"));
			if (getFlag("SKILL_CLAWASSAULT"))
				availableSkills.add(skills.get("CLAW_ASSAULT"));
			availableSkills.add(skills.get("LUPINE_MORPH"));
			if (getFlag("SKILL_BEARMORPH"))
				availableSkills.add(skills.get("BEAR_MORPH"));
			if (getFlag("SKILL_BEASTMORPH"))
				availableSkills.add(skills.get("BEAST_MORPH"));
			if (getFlag("SKILL_DEMONMORPH"))
				availableSkills.add(skills.get("DEMON_MORPH"));
			if (getFlag("SKILL_WEREWOLFMORPH"))
				availableSkills.add(skills.get("WEREWOLF_MORPH"));
			if (getFlag(Consts.F_COMPLETECONTROL))
				availableSkills.add(skills.get("COMPLETECONTROL"));
			else if (getFlag(Consts.F_SELFCONTROL))
				availableSkills.add(skills.get("SELFCONTROL"));
			if (getFlag("HEALTH_REGENERATION"))
				availableSkills.add(skills.get("REGEN"));
		} else
		if (playerClass == CLASS_VANQUISHER){
			availableSkills.add(skills.get("HOMING_BALL"));
			availableSkills.add(skills.get("CHARGE_BALL"));
			if (getFlag(Consts.C_SPELL_FIRE))
				availableSkills.add(skills.get("FLAME_SPELL"));
			if (getFlag(Consts.C_SPELL_ICE))
				availableSkills.add(skills.get("ICE_SPELL"));
			if (getFlag(Consts.C_SPELL_LIT))
				availableSkills.add(skills.get("LIT_SPELL"));
			if (getFlag("SKILL_MINDBLAST"))
				availableSkills.add(skills.get("MINDBLAST"));
			if (getFlag("SKILL_TELEPORT"))
				availableSkills.add(skills.get("TELEPORT"));
			if (getFlag("SKILL_RECOVER"))
				availableSkills.add(skills.get("RECOVER"));
			if (getFlag("SKILL_CURE"))
				availableSkills.add(skills.get("CURE"));
			if (getFlag("SKILL_ENCHANT"))
				availableSkills.add(skills.get("ENCHANT"));
			if (getFlag("SKILL_ENERGYSHIELD"))
				availableSkills.add(skills.get("ENERGYSHIELD"));
			if (getFlag("SKILL_LIGHT"))
				availableSkills.add(skills.get("LIGHT"));
			if (getFlag("SKILL_MINDLOCK"))
				availableSkills.add(skills.get("MINDLOCK"));
			if (getFlag("SKILL_MAJORJINX"))
				availableSkills.add(skills.get("MAJOR_JINX"));
			
		}else
		if (playerClass == CLASS_KNIGHT){
			availableSkills.add(skills.get("SHIELD_GUARD"));
		}
		if (weaponSkill(ItemDefinition.CAT_RINGS) > 1){
			availableSkills.add(skills.get("DIVING_SLIDE"));
		}
		if (weapon != null && weapon.getWeaponCategory().equals(ItemDefinition.CAT_RINGS) && weaponSkill(ItemDefinition.CAT_RINGS) == 10)
			availableSkills.add(skills.get("SPINNING_SLICE"));
		if (weapon != null && weapon.getWeaponCategory().equals(ItemDefinition.CAT_WHIPS) && weaponSkill(ItemDefinition.CAT_WHIPS) == 10)
			availableSkills.add(skills.get("WHIRLWIND_WHIP"));
		if (weapon != null && weapon.getWeaponCategory().equals(ItemDefinition.CAT_STAVES) && weaponSkill(ItemDefinition.CAT_STAVES) == 10)
			availableSkills.add(skills.get("ENERGY_BEAM"));
		if (weapon != null && weapon.getWeaponCategory().equals(ItemDefinition.CAT_SWORDS) && weaponSkill(ItemDefinition.CAT_SWORDS) == 10)
			availableSkills.add(skills.get("FINAL_SLASH"));
		if ((weapon == null || weapon.getWeaponCategory().equals(ItemDefinition.CAT_UNARMED)) && weaponSkill(ItemDefinition.CAT_UNARMED) == 10)
			availableSkills.add(skills.get("TIGER_CLAW"));
		if (weapon != null && weapon.getWeaponCategory().equals(ItemDefinition.CAT_PISTOLS) && weaponSkill(ItemDefinition.CAT_PISTOLS) == 10)
			availableSkills.add(skills.get("ENERGY_BURST"));
		
		return availableSkills;
	}
	
	public int weaponSkill(String catID){
		return ((Counter)weaponSkills.get(catID)).getCount();
	}

	public String getWeaponDescription(){
		if (getWeapon() != null)
			if (getWeapon().getReloadTurns() > 0)
				return getWeapon().getDescription()+"("+getWeapon().getRemainingTurnsToReload()+")";
			else
				return getWeapon().getDescription();
		else
			return "None";

	}

	private final static Hashtable skills = new Hashtable();

	public final static int DEATH = 0, WIN = 1, DROWNED = 2, KEYINMINENT = 3;

	public final static int
		EVT_FO23RWARD = 7,
		EVT_RE23TURN = 8, 
		EVT_MERCHANT = 9, 
		EVT_SMASHED = 10, 
		EVT_CHAT = 11, 
		EVT_LEVELUP = 12,
		EVT_NEXT_LEVEL_DEPRECATED = 13,
		EVT_BACK_LEVEL_DEPRECATED = 14,
		EVT_GOTO_LEVEL = 15,
		EVT_FORWARDTIME = 16,
		EVT_INN = 17;
	
	public final static int MALE = 1, FEMALE = 2;

	public final static int
		CLASS_VAMPIREKILLER = 0,
		CLASS_RENEGADE = 1,
		CLASS_VANQUISHER = 2,
		CLASS_INVOKER = 3,
		CLASS_MANBEAST = 4,
		CLASS_KNIGHT = 5;

	public final static int
		DAMAGE_MORPHED_WITH_STRONG_ARMOR = 0,
		DAMAGE_WALKED_ON_LAVA = 1,
		DAMAGE_USING_ITEM = 2,
		DAMAGE_POISON = 3,
		DAMAGE_JINX = 4;

	public final static String 
		STATUS_STUN = "STUN",
		STATUS_POISON = "POISON",
		STATUS_PETRIFY = "PETRIFY",
		STATUS_FAINTED = "FAINTED";

	public Item getWeapon() {
		return weapon;
	}

	public void setWeapon(Item value) {
		weapon = value;
	}
	
	public Item getSecondaryWeapon() {
		return secondaryWeapon;
	}

	public void setSecondaryWeapon(Item value) {
		secondaryWeapon = value;
	}

	public Item getArmor() {
		return armor;
	}

	public void setArmor(Item value) {
		armor = value;
	}

	public Item getShield() {
		return shield;
	}

	public void setShield(Item value) {
		shield = value;
	}

	public String getStatusString(){
		String status = "";
	   	if (isInvisible())
			status +="Invisible ";
		if (hasEnergyField())
			status +="EnergyField ";
    	if (hasIncreasedDefense())
    		status +="Protected ";
    	if (hasIncreasedJumping())
    		status +="Spring ";
    	if (isInvincible())
    		status +="Invincible ";
    	if (hasCounter(Consts.C_BLOOD_THIRST))
    		status +="Vampiric ";
    	if (hasCounter(Consts.C_BATMORPH))
    		status +="Bat ";
    	if (hasCounter(Consts.C_BATMORPH2))
        	status +="HBat ";
    	if (hasCounter(Consts.C_MYSTMORPH))
        	status +="Myst ";
    	if (hasCounter(Consts.C_MYSTMORPH2))
        	status +="HMyst ";
    	if (hasCounter(Consts.C_WOLFMORPH))
        	status +="Wolf ";
    	if (hasCounter(Consts.C_WOLFMORPH2))
        	status +="HWolf ";
		if (hasCounter(Consts.C_LUPINEMORPH))
			status +="Wolvish ";
		if (hasCounter(Consts.C_BEARMORPH))
			status +="Bear ";
		if (hasCounter(Consts.C_BEASTMORPH))
			status +="Beast ";
		if (hasCounter(Consts.C_DEMONMORPH))
			status +="Demon ";
		if (hasCounter(Consts.C_WEREWOLFMORPH))
			status +="WereWolf ";
		if (hasCounter(Consts.C_TURTLESHELL)){
			status +="TurtleSoul ";
		}
		
		if (hasCounter("SHIELD_GUARD"))
			status +="Guarding ";

		if (isPoisoned())
    		status +="Poison ";
    	if (isStunned())
    		status +="Stun ";
    	if (isPetrified())
    		status +="Stone ";
    	if (getHoverHeight() > 0){
    		status +="Fly("+getHoverHeight()+")";
    	}
    	if (hasCounter(Consts.C_FIREBALL_WHIP))
    		status +="EnchantWhip ";
    	if (hasCounter(Consts.C_WEAPON_ENCHANTMENT))
    		status +="EnchantWeapon ";
    	if (hasCounter(Consts.C_ENERGYSHIELD))
    		status +="EnergyShield ";
    	if (hasCounter(Consts.C_MAGICLIGHT))
    		status +="MagicLight ";
    	if (getFlag("PLAYER_SWIMMING"))
    		status +="Swimming (O2="+getCounter("OXYGEN")+") ";
    	return status;
    }

	public int getDaggerLevel(){
		return daggerLevel;
	}

	public final static int
		DAGGER = 0,
		AXE = 1,
		HOLY = 2,
		STOPWATCH = 3,
		CROSS = 4,
		BIBLE = 5,
		SACRED_CRYSTAL = 6,
		SACRED_FIST = 7 ,
		SACRED_REBOUND = 8;

	private static Action [] MYSTIC_ACTIONS;

	public Action getMysticAction(){
		if (getMysticWeapon() == -1)
			return null;
		else
			return MYSTIC_ACTIONS[getMysticWeapon()];
	}

	/*public boolean deservesHighMystics(){
		return score > 20000 && playerLevel > 6;
	}*/
	
		public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlot() {
		return plot;
	}
	
	public String getPlot2() {
		return plot2;
	}

	public void setPlot(String plot, String plot2) {
		this.plot = plot;
		this.plot2 = plot2;
	}

	public int getHitsMax() {
		return hitsMax;
	}

	public void setHitsMax(int hitsMax) {
		this.hitsMax = hitsMax;
	}
	
	public void increaseHeartMax(int how){
		heartMax+=how;
	}
	
	public void increaseHitsMax(int how){
		hitsMax+=how;
		if (hitsMax > HITMAX)
			hitsMax = HITMAX;
	}
	
	public void increaseMUpgradeCount(){
		mUpgradeCount++;
	}
	

	public int getHeartsMax(){return heartMax;}
	
	public int getSightRange(){
		return baseSightRange + 
			(level.isDay()?3:0)+
			(level.getMapCell(getPosition()) != null ? level.getMapCell(getPosition()).getHeight()>0?1:0:0)+
			(hasCounter(Consts.C_MAGICLIGHT) ? 3 : 0) +
			(hasCounter("LIGHT") ? 3 : 0);
	}
	
	public int getDarkSightRange(){
		return baseSightRange + 7 +(level.getMapCell(getPosition()) != null && level.getMapCell(getPosition()).getHeight()>0?1:0);
	}

	public int getBaseSightRange() {
		return baseSightRange;
	}

	public void setBaseSightRange(int baseSightRange) {
		this.baseSightRange = baseSightRange;
	}


	public void setFOV(FOV fov){
		this.fov = fov;
	}
	
	private transient FOV fov;
	
	public void see(){
		//fov.startCircle(getLevel(), getPosition().x, getPosition().y, getSightRange());
		fov.startCircle(getLevel(), getPosition().x, getPosition().y, getDarkSightRange());
	}
	
	public void darken(){
		level.darken();
	}
	
	public Position getNearestMonsterPosition(){
		Monster nearMonster = getNearestMonster();
		if (nearMonster != null)
			return nearMonster.getPosition();
		else
			return null;
	}
	
	public Monster getNearestMonster(){
		VMonster monsters = level.getMonsters();
		Monster nearMonster = null;
		int minDist = 150;
		for (int i = 0; i < monsters.size(); i++){
			Monster monster = (Monster) monsters.elementAt(i);
			if (monster instanceof NPC)
				continue;
			if (monster.getPosition().z != getPosition().z)
				continue;
			int distance = Position.flatDistance(level.getPlayer().getPosition(), monster.getPosition());
			if (distance < minDist){
				minDist = distance;
				nearMonster = monster;
			}
		}
		return nearMonster;
	}

	public int getAttackCost() {
		return attackCost;
	}

	public void setAttackCost(int attackCost) {
		this.attackCost = attackCost;
	}

	public int getCastCost() {
		return castCost;
	}

	public void setCastCost(int castCost) {
		this.castCost = castCost;
	}

	public int getWalkCost() {
		int walkCostBonus = 0;
		if (hasCounter(Consts.C_WOLFMORPH2))
			walkCostBonus = -25;
		else if (hasCounter(Consts.C_WOLFMORPH))
			walkCostBonus = -20;
		if (hasCounter(Consts.C_BATMORPH2))
			walkCostBonus = -25;
		else if (hasCounter(Consts.C_BATMORPH))
			walkCostBonus = -20;
		
		return walkCost + walkCostBonus > 0 ? walkCost + walkCostBonus : 1;
	}

	public void setWalkCost(int walkCost) {
		this.walkCost = walkCost;
	}
	
	public int getPlayerLevel(){
		return playerLevel;
	}
	
	public void reduceCastCost(int param){
		this.castCost -= param;
	}

	public void increaseSoulPower(int param){
		this.soulPower += param;
	}
	
	public void reduceWalkCost(int param){
		this.walkCost -= param;
	}
	
	public void increaseCarryMax(int param){
		this.carryMax += param;
	}
	
	public void increaseEvadeChance(int param){
		this.evadeChance += param;
	}
	
	public void reduceAttackCost(int param){
		this.attackCost -= param;
	}
	
	public void increaseAttack(int param){
		this.attack += param;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getEvadeChance() {
		return evadeChance;
	}

	public void setEvadeChance(int evadeChance) {
		this.evadeChance = evadeChance;
	}

	public int getCarryMax() {
		return carryMax;
	}
	
	public int getSoulPower() {
		return soulPower;
	}
	
	public int getUnarmedAttack(){
		return weaponSkill(ItemDefinition.CAT_UNARMED)+1;
	}
	
	 public int getXp(){
		 return xp;
	 }
	 
	 public int getNextXP(){
		 return nextLevelXP;
	 }
	 
	 public boolean sees(Position p){
		 return level.isVisible(p.x, p.y);
	 }
	 
	 public boolean sees(Monster m){
		 return sees(m.getPosition());
	 }
	 
	 public void setSoulPower(int sp){
		 this.soulPower = sp;
	 }

	public boolean isDoNotRecordScore() {
		return doNotRecordScore;
	}

	public void setDoNotRecordScore(boolean doNotRecordScore) {
		this.doNotRecordScore = doNotRecordScore;
	}
	
	public void setPlayerLevel(int level){
		playerLevel = level;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	public static String getFeatureNameForMystic(int mysticID){
		switch (mysticID){
		case AXE:
			return "AXEWP";
		case DAGGER:
			return "DAGGERWP";
		case HOLY:
			return "HOLYWP";
		case CROSS:
			return "CROSSWP";
		case STOPWATCH:
			return "STOPWATCHWP";
		case BIBLE:
			return "BIBLEWP";
		case SACRED_FIST:
			return "FISTWP";
		case SACRED_CRYSTAL:
			return "CRYSTALWP";
		case SACRED_REBOUND:
			return "REBOUNDWP";
		}
		return "DAGGERWP";
	}
	
	private Hashtable hashFlags = new Hashtable();
	public void setFlag(String flagID, boolean value){
		hashFlags.put(flagID, new Boolean(value));
	}
	
	public boolean getFlag(String flagID){
		Boolean val =(Boolean)hashFlags.get(flagID); 
		return val != null && val.booleanValue();
	}
	
	public static Advancement[][] STATADVANCEMENTS ;
	
	public static Advancement[][] ADVANCEMENTS;
	
	
	private Vector tmpAvailableAdvancements = new Vector();
	public Vector getAvailableAdvancements(){
		tmpAvailableAdvancements.clear();
		out: for (int i = 0; i < ADVANCEMENTS[getPlayerClass()].length; i++){
			if (getFlag(ADVANCEMENTS[getPlayerClass()][i].getID())){
				//Already has the advancement
				continue;
			}
			String[] requirements = ADVANCEMENTS[getPlayerClass()][i].getRequirements();
			for (int j = 0; j < requirements.length; j++){
				if (!getFlag(requirements[j])){
					//Misses a requirement
					continue out;
				}
			}
			String[] bans = ADVANCEMENTS[getPlayerClass()][i].getBans();
			for (int j = 0; j < bans.length; j++){
				if (getFlag(bans[j])){
					//Has a ban
					continue out;
				}
			}
			tmpAvailableAdvancements.add(ADVANCEMENTS[getPlayerClass()][i]);
		}
		return tmpAvailableAdvancements;
	}
	
	
	public Vector getAvailableStatAdvancements(){
		return tmpAvailableAdvancements;
	}
	
	public boolean canAttack(){
		return !(isSwimming() ||
				hasCounter(Consts.C_BATMORPH) ||
				hasCounter(Consts.C_BATMORPH2) ||
				hasCounter(Consts.C_MYSTMORPH) ||
				hasCounter(Consts.C_MYSTMORPH2));
	}
	
	public void cure(){
		if (isPoisoned()){
			level.addMessage("The poison leaves your veins");
			setPoison(0);
		} else {
			level.addMessage("Nothing happens");
		}
	}
	
	public void deMorph(){
		if (hasCounter(Consts.C_BATMORPH))
			setCounter(Consts.C_BATMORPH, 0);
		if (hasCounter(Consts.C_BATMORPH2))
			setCounter(Consts.C_BATMORPH2, 0);
		if (hasCounter(Consts.C_MYSTMORPH))
			setCounter(Consts.C_MYSTMORPH, 0);
		if (hasCounter(Consts.C_MYSTMORPH2))
			setCounter(Consts.C_MYSTMORPH2, 0);
		if (hasCounter(Consts.C_WOLFMORPH))
			setCounter(Consts.C_WOLFMORPH, 0);
		if (hasCounter(Consts.C_WOLFMORPH2))
			setCounter(Consts.C_WOLFMORPH2, 0);
	}
	
	private ActionSelector originalSelector;
	public void morph(String morphID, int count, boolean smallMorph, boolean bigMorph, int morphStrength, int loseMindChance){
		deMorph();
		if (getFlag(Consts.F_SELFCONTROL))
			loseMindChance = (int) Math.floor(loseMindChance / 2.0D);
		if (getFlag(Consts.F_COMPLETECONTROL))
			loseMindChance = 0;
		if (Util.chance(loseMindChance)){
			level.addMessage("You lose your mind!");
			setFlag("KEEPMESSAGES", true);
			originalSelector = getSelector();
			setCounter("REGAIN_SHAPE", count);
			setSelector(SelectorFactory.getSelectorFactory().getSelector("WILD_MORPH_AI"));
		}
		
		Item weapon = getWeapon();
		if (weapon != null){
			level.addMessage("You drop your "+weapon.getDescription());
			level.addItem(getPosition(), weapon);
			setWeapon(null);
		}
		
		if (getSecondaryWeapon() != null){
			level.addMessage("You drop your "+getSecondaryWeapon().getDescription());
			level.addItem(getPosition(), getSecondaryWeapon());
			setSecondaryWeapon(null);
		}
		
		if (getShield() != null){
			level.addMessage("You drop your "+getShield().getDescription());
			level.addItem(getPosition(), getShield());
			setShield(null);
		}
		
		Item armor = getArmor();
		if (armor != null){
			if (bigMorph){
				if (armor.getDefense() > morphStrength){
					level.addMessage("Your armor is too strong! You feel trapped! You are injured!");
					selfDamage(Player.DAMAGE_MORPHED_WITH_STRONG_ARMOR, 10);
					return;
				}
				level.addMessage("You destroy your "+armor.getDescription()+"!");
				setArmor(null);
			} else if (smallMorph){
				level.addMessage("Your "+armor.getDescription()+" falls.");
				level.addItem(getPosition(), armor);
				setArmor(null);
			}
		}
		
		
		setCounter(morphID, count);
	}
	
	public boolean canWield(){
		return !isMorphed();
	}
	
	public boolean isMorphed(){
		return 
			hasCounter(Consts.C_LUPINEMORPH) ||
			hasCounter(Consts.C_BEARMORPH) ||
			hasCounter(Consts.C_BEASTMORPH)||
			hasCounter(Consts.C_DEMONMORPH) ||
			hasCounter(Consts.C_WEREWOLFMORPH) ||
			hasCounter(Consts.C_BATMORPH) ||
			hasCounter(Consts.C_BATMORPH2) ||
			hasCounter(Consts.C_WOLFMORPH) ||
			hasCounter(Consts.C_WOLFMORPH2) ||
			hasCounter(Consts.C_MYSTMORPH) ||
			hasCounter(Consts.C_MYSTMORPH2);
	}
	
	private boolean standsOnPlace(){
		return 
			hasCounter(Consts.C_LUPINEMORPH) ||
			hasCounter(Consts.C_BEARMORPH) ||
			hasCounter(Consts.C_BEASTMORPH)||
			hasCounter(Consts.C_DEMONMORPH) ||
			hasCounter(Consts.C_WEREWOLFMORPH) ||
			hasCounter(Consts.C_MYSTMORPH) ||
			hasCounter(Consts.C_MYSTMORPH2);
	}
	
	public int stareMonster(Monster who){
		if (who.getPosition().z != getPosition().z)
			return -1;
		if (who.wasSeen()){
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
	
	public int stareMonster(){
		Monster nearest = getNearestMonster();
		if (nearest == null)
			return -1;
		else
			return stareMonster(nearest);
	}
	
	private String[] bannedArmors;

	public String[] getBannedArmors() {
		return bannedArmors;
	}

	public void setBannedArmors(String[] bannedArmors) {
		this.bannedArmors = bannedArmors;
	}

	private Hashtable lastIncrements = new Hashtable();
	
	public void addLastIncrement(String key, int value){
		Integer current = (Integer) lastIncrements.get(key);
		if (current == null){
			current = new Integer(value);
		} else {
			current = new Integer(current.intValue()+value);
		}
		lastIncrements.put(key, current);
	}
	
	private int  getLastIncrement(String key){
		Integer current = (Integer) lastIncrements.get(key);
		if (current == null){
			return 0;
		} else {
			return current.intValue();
		}
	}
	
	public void resetLastIncrements(){
		lastIncrements.clear();
	}

	public int getBreathing() {
		return breathing;
	}

	public void setBreathing(int breathing) {
		this.breathing = breathing;
	}
	
	private Vector counteredItems = new Vector();
	public void addCounteredItem(Item i){
		counteredItems.add(i);
	}
	
	public int getPunchDamage(){
		int punchDamage = weaponSkill(ItemDefinition.CAT_UNARMED)+1;
		if (hasCounter(Consts.C_BEASTMORPH)){
			punchDamage = 2*(weaponSkill(ItemDefinition.CAT_UNARMED)+1)+getAttack();
		} else if (hasCounter(Consts.C_BEARMORPH)){
			punchDamage = (int)Math.ceil(1.7*(weaponSkill(ItemDefinition.CAT_UNARMED)+1));
		} else if (hasCounter(Consts.C_DEMONMORPH)){
			punchDamage = (int)Math.ceil(2*(weaponSkill(ItemDefinition.CAT_UNARMED)+1));
		} else if (hasCounter(Consts.C_LUPINEMORPH)){
			punchDamage = (int)Math.ceil(1.5d*(weaponSkill(ItemDefinition.CAT_UNARMED)+1));
		} else if (hasCounter(Consts.C_WEREWOLFMORPH)){
			punchDamage = (int)Math.ceil(3*(weaponSkill(ItemDefinition.CAT_UNARMED)+1));
		} else if (hasCounter(Consts.C_WOLFMORPH) || hasCounter(Consts.C_WOLFMORPH2)){
			punchDamage = getAttack()+1+Util.rand(0,2);
		} else if (hasCounter(Consts.C_POWERBLOW)){
			punchDamage = 10+2*getAttack()+(int)Math.ceil(1.3d*(weaponSkill(ItemDefinition.CAT_UNARMED)+1));
		} else if (hasCounter(Consts.C_POWERBLOW2)){ 
			punchDamage = 16+2*getAttack()+(int)Math.ceil(1.5d*(weaponSkill(ItemDefinition.CAT_UNARMED)+1));
		} else if (hasCounter(Consts.C_POWERBLOW3)){ 
			punchDamage = 25+3*getAttack()+(int)Math.ceil(1.7d*(weaponSkill(ItemDefinition.CAT_UNARMED)+1));
		} else if (getPlayerClass() == Player.CLASS_MANBEAST){
			punchDamage = (int)Math.ceil(1.3d*(weaponSkill(ItemDefinition.CAT_UNARMED)+1));
		}
		return getAttack() + punchDamage;
	}
	
	public int getPunchPush(){
		int push = 0;
		if (hasCounter(Consts.C_BEASTMORPH)){
			push = 3;
		} else if (hasCounter(Consts.C_BEARMORPH)){
			push = 4;
		} else if (hasCounter(Consts.C_LUPINEMORPH)){
			push = 2;
		} else if (hasCounter(Consts.C_WEREWOLFMORPH)){
			push = 3;
		} else if (hasCounter(Consts.C_POWERBLOW)){
			push = 2;
		} else if (hasCounter(Consts.C_POWERBLOW2)){ 
			push = 3;
		} else if (hasCounter(Consts.C_POWERBLOW3)){ 
			push = 4;
		}
		return push;
	}
	
	public String getPunchDescription(){
		String attackDescription = "punch";
		if (hasCounter(Consts.C_BEASTMORPH)){
			attackDescription = "claw";
		} else if (hasCounter(Consts.C_BEARMORPH)){
			attackDescription = "bash";
		} else if (hasCounter(Consts.C_DEMONMORPH)){
			attackDescription = "claw";
		} else if (hasCounter(Consts.C_LUPINEMORPH)){
			attackDescription = "slash at";
		} else if (hasCounter(Consts.C_WEREWOLFMORPH)){
			attackDescription = "slash through";
		} else if (hasCounter(Consts.C_WOLFMORPH) || hasCounter(Consts.C_WOLFMORPH2)){
			attackDescription = "bite";
		} else if (hasCounter(Consts.C_POWERBLOW)){
			attackDescription = "charge against";
		} else if (hasCounter(Consts.C_POWERBLOW2)){ 
			attackDescription = "charge against";
		} else if (hasCounter(Consts.C_POWERBLOW3)){ 
			attackDescription = "charge against";
		} else if (getPlayerClass() == Player.CLASS_MANBEAST){
			attackDescription = "slash at";
		}
		return attackDescription;
	}
	
	public int getWeaponAttack(){
		if (weapon != null) 
			if (getPlayerClass() == Player.CLASS_VAMPIREKILLER){
				return weaponSkill(weapon.getDefinition().getWeaponCategory()) + (int)Math.round(getAttack() * (weapon.getAttack()/2.0D));
			} else
				return weapon.getAttack() + 
					weaponSkill(weapon.getDefinition().getWeaponCategory()) + 
					getAttack() +
					(weapon.hasCounter(Consts.C_WEAPON_ENCHANTMENT)?2:0);
		else
			return getPunchDamage();
	}
	
	
	
	public int getArmorDefense(){
		if (getArmor() != null){
			if (getPlayerClass() == CLASS_VAMPIREKILLER){
				return (int)(getArmor().getDefense() + Math.ceil(getPlayerLevel()/5.0D));
			} else {
				return getArmor().getDefense();
			}
		} else
			return 0;
	}
	
	
	public int getDefenseBonus(){
		int ret = 0;
		if (hasIncreasedDefense())
			ret++;
		ret += getMorphDefense();
		return ret;
	}
	
	public int getShieldBlockChance(){
		int blockChance = 0;
		if (getShield() != null &&
			(getWeapon() == null || (getWeapon()!= null && !getWeapon().isTwoHanded()))) {
			if (getPlayerClass() == CLASS_KNIGHT){
				blockChance = getShield().getCoverage();
			} else {
				blockChance = (int)(getShield().getCoverage()/2.0d);
			}
			blockChance += 2 * weaponSkill(ItemDefinition.CAT_SHIELD);
			if (blockChance > 70)
				return 70;
			else
				return blockChance;
		} else {
			return 0;
		}
	}

	
	public int getShieldCoverageChance(){
		if (getShield() != null &&
			(getWeapon() == null || (getWeapon()!= null && !getWeapon().isTwoHanded()))) {
			int coverageChance = 0;
			if (getPlayerClass() == CLASS_KNIGHT){
				coverageChance = 70;
			} else {
				coverageChance = getShield().getCoverage();
			}
			return coverageChance;
		} else{
			return 0;
		}
	}
	
	public void increaseCoolness(int x){
		coolness += x;
	}
	
	public void reduceCoolness(int x){
		coolness -= x;
	}
	
	public int getCoolness(){
		return coolness;
	}

	public void abandonHostage(){
		getHostage().setPosition(getPosition());
		level.addMonster(getHostage());
		addHistoricEvent("abandoned "+getHostage().getDescription()+" at the "+getLevel().getDescription());
		setHostage(null);
	}
	
	public void putCustomMessage(String messageID, String text){
		customMessages.put(messageID, text);
	}
	
	public String getCustomMessage(String messageID){
		return (String) customMessages.get(messageID);
	}
	
	public int getLastFacing(){
		return lastFacing;
	}
	
	public void setLastFacing(int facing){
		lastFacing = facing;
	}
	private int lastFacing = FACING_RIGHT;
	
	public final static int FACING_LEFT = 0;
	public final static int FACING_RIGHT = 1;
	
	public void explode(){
		level.addMessage("You are destroyed by the spikes!");
		getGameSessionInfo().setDeathCause(GameSessionInfo.SPIKES);
		SFXManager.play("wav/lazrshot.wav");
		level.addEffect(EffectFactory.getSingleton().createLocatedEffect(getPosition(), "SFX_MEGAMAN_BLAST"));
		hits = -1;
	}
	
	public void fallOnPit(){
		level.addMessage("You fall into an endless pit...");
		getGameSessionInfo().setDeathCause(GameSessionInfo.ENDLESS_PIT);
		SFXManager.play("wav/lazrshot.wav");
		hits = -1;
	}
	
	public boolean reload(int n){
		if (weapon != null){
			if (weapon.getDefinition().isSingleUse()){
				return false;
			} else {
				weapon.reload(n);
				getLevel().addMessage("Your " + weapon.getDescription()+" recharges");
				return true;
			}
		} else {
		}
		return false;
	}
}