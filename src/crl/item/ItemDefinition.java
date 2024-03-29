package crl.item;

import java.util.Hashtable;

import sz.csi.textcomponents.BasicListItem;
import sz.csi.textcomponents.ListItem;
import sz.csi.textcomponents.MenuItem;
import crl.ui.*;

public class ItemDefinition {
	// Weapon Categories
	
	public final static String
		CAT_UNARMED = "HAND_TO_HAND",
		CAT_DAGGERS = "DAGGERS",
		CAT_SWORDS = "SWORDS",
		CAT_SPEARS = "SPEARS",
		CAT_WHIPS = "WHIPS",
		CAT_MACES = "MACES",
		CAT_STAVES = "POLE_WEAPONS",
		CAT_RINGS = "RINGS",
		CAT_PROJECTILES = "THROWN",
		CAT_BOWS = "BOWS",
		CAT_PISTOLS = "MISSILE_CRAFT",
		CAT_SHIELD = "SHIELD";
	
	public final static String[] CATS = new String[]{
		ItemDefinition.CAT_UNARMED,
		ItemDefinition.CAT_DAGGERS,
		ItemDefinition.CAT_SWORDS,
		ItemDefinition.CAT_SPEARS,
		ItemDefinition.CAT_WHIPS,
		ItemDefinition.CAT_MACES,
		ItemDefinition.CAT_STAVES,
		ItemDefinition.CAT_RINGS,
		ItemDefinition.CAT_PROJECTILES,
		ItemDefinition.CAT_BOWS,
		ItemDefinition.CAT_PISTOLS,
		ItemDefinition.CAT_SHIELD
	};
	
	public static int EQUIP_ARMOR = 1, EQUIP_WEAPON = 2, EQUIP_SHIELD = 3; 
	
	public int getShopChance() {
		return shopChance;
	}

	public int getCoolness() {
		return coolness;
	}

	public String getAttackSound() {
		return attackSound;
	}

	public static String getCategoryDescription(String catID){
		return (String) HASH_DESCRIPTIONS.get(catID);
	}
	
	public final static Hashtable HASH_DESCRIPTIONS = new Hashtable();
	static {
		HASH_DESCRIPTIONS.put(CAT_UNARMED,"hand to hand combat");
		HASH_DESCRIPTIONS.put(CAT_DAGGERS,"daggers");
		HASH_DESCRIPTIONS.put(CAT_SWORDS,"swords");
		HASH_DESCRIPTIONS.put(CAT_SPEARS,"spears");
		HASH_DESCRIPTIONS.put(CAT_WHIPS,"whips");
		HASH_DESCRIPTIONS.put(CAT_STAVES,"pole weapons");
		HASH_DESCRIPTIONS.put(CAT_RINGS,"rings");
		HASH_DESCRIPTIONS.put(CAT_PISTOLS,"missile craft");
		HASH_DESCRIPTIONS.put(CAT_PROJECTILES,"thrown weapons");
		HASH_DESCRIPTIONS.put(CAT_BOWS,"bows");
		HASH_DESCRIPTIONS.put(CAT_MACES,"maces");
		HASH_DESCRIPTIONS.put(CAT_SHIELD,"shields");
	};

	//Shop Categories
	public final static int
		SHOP_CRAFTS = 1,
		SHOP_MAGIC = 2,
		SHOP_WEAPONS = 3,
		SHOP_ARMOR = 4;

	//Attributes
	private String ID;
	private String description;
	private int goldPrice;
	
	private String effectOnAcquire;
	private boolean autoUse;
	private boolean singleUse;
	private boolean fixedMaterial;
	private int pinLevel;
	private int throwRange;
	private Appearance appearance;
	private String useMessage;
	//private String throwMessage;
	private int featureTurns;
	private String placedSmartFeature;
   	private int attack;
   	private String attackSound;
	private int range;
	private int reloadTurns;
	private boolean slicesThrough;
	//private MenuItem shopMenuItem;
	private String shopDescription;
	private int rarity;
	private int shopChance;
	private int defense;
	private int coverage;
	private int equipCategory;
	private String attackSFX;
	private String weaponCategory;
	private String action;
	private int shopCategory;
	private String menuDescription;
	private boolean twoHanded;
	private int coolness;
	/*private ListItem sightListItem;
	 * Debe ser calculado por la UI, y guardado en esta
	 */
	
	private int verticalRange;
	private int attackCost;
	
	private int reloadCostGold;
	
	private boolean unique;

	
	public boolean isUnique() {
		return unique;
	}

	public int getAttackCost() {
		return attackCost;
	}


	public ItemDefinition (String pID, String pDescription, String pAppearance, int pEquipCategory, 
			String pMenuDescription, int pinLevel,
			int pShopChance, String pShopDescription, int pGoldPrice, int pShopCategory,  
			int pAttack, int pRange, int pReloadTurns, String pWeaponCategory, String pAction, boolean pSlicesThrough,
			int pDefense,  int pCoverage, int pVerticalRange, int attackCost, int pReloadGoldCost, boolean pTwoHanded,
			boolean pAutoUse, String pEffectOnAcquire, int pThrowRange, String pPlacedSmartFeature, boolean pSingleUse,
			int pFeatureTurns, String pUseMessage, boolean pUnique, boolean fixedMaterial, String pAttackSFX, String pAttackSound, int pCoolness, int pRarity 
		 ){
		ID = pID;
		rarity = pRarity;
		coolness = pCoolness;
		description = pDescription;
		appearance = AppearanceFactory.getAppearanceFactory().getAppearance(pAppearance);
		goldPrice = pGoldPrice;
		autoUse = pAutoUse;
		effectOnAcquire = pEffectOnAcquire;
		throwRange = pThrowRange;
		placedSmartFeature = pPlacedSmartFeature;
		singleUse = pSingleUse;
		twoHanded = pTwoHanded;
		coverage = pCoverage;
		featureTurns = pFeatureTurns;
		attack = pAttack;
		range = pRange;
		attackSound = pAttackSound;
		reloadTurns = pReloadTurns;
		action = pAction;
		slicesThrough = pSlicesThrough;
		useMessage = pUseMessage;
		shopChance = pShopChance;
		shopDescription = pShopDescription;
		defense=pDefense;
		equipCategory = pEquipCategory;
		attackSFX = pAttackSFX;
		weaponCategory = pWeaponCategory;
		shopCategory = pShopCategory;
		verticalRange = pVerticalRange;
		this.attackCost = attackCost;
		menuDescription = pMenuDescription;
		//shopMenuItem = new ShopMenuItem(this);
		setReloadCostGold(pReloadGoldCost);
		unique = pUnique;
		this.fixedMaterial = fixedMaterial;
		this.pinLevel = pinLevel;
		//sightListItem = new BasicListItem(appearance.getChar(), appearance.getColor(), description);
	}


	/*public MenuItem getShopMenuItem(){
		return shopMenuItem;
    }*/

	public String getID() {
		return ID;
	}

	public String getDescription() {
		return description;
	}

	public int getGoldPrice() {
		return goldPrice;
	}

	public String getEffectOnUse() {
		return "";
	}

	public String getEffectOnAcquire() {
		return effectOnAcquire;
	}

	public boolean isSingleUse() {
		return singleUse;
	}

	public int getThrowRange() {
		return throwRange;
	}

	public Appearance getAppearance() {
		return appearance;
	}

	public String getUseMessage() {
		return useMessage;
	}

	/*public String getThrowMessage() {
		return throwMessage;
	}*/

	public int getFeatureTurns() {
		return featureTurns;
	}

	public String getPlacedSmartFeature() {
		return placedSmartFeature;
	}

	public int getAttack() {
		return attack;
	}

	public int getRange() {
		return range;
	}

	public int getReloadTurns() {
		return reloadTurns;
	}

	public boolean isHarmsUndead() {
		return false;
	}

	public boolean isSlicesThrough() {
		return slicesThrough;
	}

	/*public String getMenuDescription(){
		return getAttributesDescription();
	}
	
	public Appearance getMenuAppearance(){
	 	return getAppearance();
	}*/

	public int getRarity() {
		return rarity;
	}

	public int getEquipCategory() {
		return equipCategory;
	}

	public String getAttackSFX() {
		return attackSFX;
	}

	public int getDefense() {
		return defense;
	}


	public int getShopCategory() {
		return shopCategory;
	}


	public void setShopCategory(int shopCategory) {
		this.shopCategory = shopCategory;
	}


	public String getWeaponCategory() {
		return weaponCategory;
	}


	public void setWeaponCategory(String weaponCategory) {
		this.weaponCategory = weaponCategory;
	}

	/*public ListItem getSightListItem(){
		return sightListItem;
	}
	
	public void setSightListItem(ListItem sightListItem) {
		this.sightListItem = sightListItem;
	}
	*/
	public String getShopDescription(){
		return shopDescription;
	}

	
	public int getVerticalRange(){
		return verticalRange;
	}
	
	public String getAttributesDescription(){
		String base = getDescription();
		if (getAttack() > 0 || getDefense() > 0 || getRange() > 1 || getVerticalRange() > 0)
			base+= " (";
		if (getAttack() > 0)
			base+= "ATK:"+getAttack()+" ";
		if (getDefense() > 0)
			base+= "DEF:"+getDefense()+" ";
		if (getRange() > 1 || getVerticalRange() > 0)
			if (getVerticalRange() > 0)
				base+= "RNG:"+getRange()+","+getVerticalRange();
			else
				base+= "RNG:"+getRange();
		if (getReloadCostGold() > 0){
			base += " RLD:"+getReloadCostGold();
		}
		if (getAttack() > 0 || getDefense() > 0 || getRange() > 1 || getVerticalRange() > 0)
			base+= ")";
		return base;
	}


	public String getMenuDescription() {
		return menuDescription;
	}


	public void setMenuDescription(String menuDescription) {
		this.menuDescription = menuDescription;
	}


	public int getReloadCostGold() {
		return reloadCostGold;
	}


	public void setReloadCostGold(int reloadCostGold) {
		this.reloadCostGold = reloadCostGold;
	}

	public boolean isFixedMaterial() {
		return fixedMaterial;
	}

	public int getPinLevel() {
		return pinLevel;
	}

	public boolean isTwoHanded() {
		return twoHanded;
	}

	public int getCoverage() {
		return coverage;
	}
	
	public String getAction(){
		return action;
	}

	public boolean isAutoUse() {
		return autoUse;
	}
}