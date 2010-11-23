package crl.actor;

import java.util.Enumeration;
import java.util.Hashtable;

import sz.util.Debug;
import sz.util.Position;
import sz.util.PriorityEnqueable;
import crl.action.Action;
import crl.ai.ActionSelector;
import crl.game.SFXManager;
import crl.level.Cell;
import crl.level.Level;
import crl.monster.Monster;
import crl.player.GameSessionInfo;
import crl.player.Player;
import crl.ui.Appearance;
import crl.ui.consoleUI.ConsoleUserInterface;
import crl.ui.effects.EffectFactory;

public class Actor implements Cloneable, java.io.Serializable, PriorityEnqueable{
	protected /*transient*/ int positionx, positiony, positionz;
	protected transient Appearance appearance;
	
	protected ActionSelector selector;
	private /*transient*/ Position position = new Position(0,0,0);
	private int hoverHeight;
	private /*transient*/ int nextTime=10;
	
	public int getCost(){
		//Debug.say("Cost of "+getDescription()+" "+ nextTime);
		return nextTime;
	}
	
	public void reduceCost(int value){
		//Debug.say("Reducing cost of "+getDescription()+"by"+value+" (from "+nextTime+")");
		nextTime = nextTime - value;
	}
	
	public void setNextTime(int value){
		//Debug.say("Next time for "+getDescription()+" "+ value);
		nextTime = value;
	}

	protected Level level;

	public void updateStatus(){
		Enumeration countersKeys = hashCounters.keys();
		while (countersKeys.hasMoreElements()){
			String key = (String) countersKeys.nextElement();
			Integer counter = (Integer)hashCounters.get(key);
			if (counter.intValue() == 0){
				hashCounters.remove(key);
			} else {
				hashCounters.put(key, new Integer(counter.intValue()-1));
			}
		}
	}

	public String getDescription(){
		return "";
	}

	public void execute(Action x){
		if (x != null){
        	x.setPerformer(this);
        	if (x.canPerform(this)){
	        	if (x.getSFX() != null)
	        		SFXManager.play(x.getSFX());
				x.execute();
				//Debug.say("("+x.getCost()+")");
				setNextTime(x.getCost());
        	}
		} else {
			//setNextTime(50);
			setNextTime(getBaseCost());
		}
		updateStatus();
	}
	public void act(){
		Action x = getSelector().selectAction(this);
		execute(x);
		if (hasInertia()){
			doInertia();
		}
		if (isPlayer())
			ConsoleUserInterface.correctY(0);
		if (hasCounter("JUMPING")){
			performJump();
		} else {
			 
			//jumpHeight = 0;
		}
		
	}

	public void setPosition (int x, int y, int z){
		position.x = x;
		position.y = y;
		position.z = z;
	}

	public void die(){
		/** Request to be removed from any dispatcher or structure */
		aWannaDie = true;
	}

	public boolean wannaDie(){
		return aWannaDie;
	}

	private boolean aWannaDie;


	public void setPosition (Position p){
		position = p;
	}

	public Position getPosition(){
		return position;
	}

	public void setLevel(Level what){
		level = what;
	}

	public Level getLevel(){
		return level;
	}

	public ActionSelector getSelector() {
		return selector;
	}

	public void setSelector(ActionSelector value) {
		selector = value;
	}

	public Appearance getAppearance() {
		return appearance;
	}

	public void setAppearance(Appearance value) {
		appearance = value;
	}

	public Object clone(){
		try {
			Actor x = (Actor) super.clone();
			if (position != null)
				x.setPosition(new Position(position.x, position.y, position.z));
			return x;
		} catch (CloneNotSupportedException cnse){
			Debug.doAssert(false, "failed class cast, Feature.clone()");
		}
		return null;
	}


	public void message(String mess){
	}
	
	protected Hashtable hashCounters = new Hashtable();
	public void setCounter(String counterID, int turns){
		hashCounters.put(counterID, new Integer(turns));
	}
	
	public int getCounter(String counterID){
		Integer val = (Integer)hashCounters.get(counterID);
		if (val == null)
			return -1;
		else
			return val.intValue();
	}
	
	public boolean hasCounter(String counterID){
		return getCounter(counterID) > 0;
	}

	public int getHoverHeight() {
		return hoverHeight;
	}

	public void setHoverHeight(int hoverHeight) {
		if (hoverHeight > 0)
			this.hoverHeight = hoverHeight;
		else
			this.hoverHeight = 0;
	}
	
	public int getStandingHeight(){
		if (isJumping){
			return startingJumpingHeight+2;
		}
		if (level.getMapCell(getPosition()) != null)
			return level.getMapCell(getPosition()).getHeight()+getHoverHeight();
		else
			return getHoverHeight();
	}
	
	private boolean isJumping;
	
	private int startingJumpingHeight;

	public boolean isJumping() {
		return hasCounter("JUMPING");
	}

	public void doJump(int startingJumpingHeight) {
		this.isJumping = true;
		this.startingJumpingHeight = startingJumpingHeight;
	}
	
	public void stopJump(){
		this.isJumping = false;
	}
	
	private int fallSpeed;
	private static final Position FALL = new Position(0,1,0);
	private static final Position JUMP = new Position(0,-1,0);
	public void fall(){
		if (isFlying()){
			return;
		}
		//ConsoleUserInterface.correctY(0);
		Cell standingCell = getStandingCell();
		if (standingCell == null || standingCell.getID().equals("BLACKNESS")){
			fallOnPit();
		}
		if (standingCell != null && (standingCell.isSolid() || standingCell.isStair() || standingCell.isSpike())){
			if (standingCell.isSpike()){
				if (this instanceof Player)
					explode();
			}
			jumpHeight = 0;
			if (isPlayer())
				ConsoleUserInterface.resetYPos();
			fallSpeed = 1;
			if (isDestroyOnImpact())
				die();
		} else {
			if (!isJumping()){
				if (jumpHeight <= 0)
					fallSpeed++;
				else
					fallSpeed = 1;
				for (int i = 0; i < fallSpeed; i++){
					Position destination = Position.add(getPosition(), FALL);
					Cell destinationCell = level.getMapCell(destination);
					if (destinationCell == null){
						if (isPlayer())
							ConsoleUserInterface.resetYPos();
						jumpHeight = 0;
						break;
					}
					if (destinationCell.isSolid()){
						jumpHeight = 0;
						break;
					}
						
					if (jumpHeight > 0){
						if (isPlayer())
							ConsoleUserInterface.changeYPos(1);
					}
					changePositionTo(destination);
					if (isPlayer()){
						standingCell = getStandingCell();
						if (standingCell != null && standingCell.isSolid())
							ConsoleUserInterface.resetYPos();
					}
				}
			}
		}
	}
	
	public void changePositionTo(Position to){
		if (this instanceof Monster){
			Position var = Action.directionToVariation(Action.getGeneralDirection(getPosition(), to));
			Monster aMonster = (Monster) this;
			if (aMonster.damagesEnemies()){
				Monster m = level.getMonsterAt(to);
				if (m != null){
					m.damage(aMonster.getAttack()+aMonster.getDamageMod());
					if (isDestroyOnImpact())
						die();
				} else {
					setPosition(to);
				}
			} else {
				if (level.getPlayer().getPosition().equals(to)){
					if (level.getPlayer().damage(aMonster, aMonster.getAttack())) {
						level.getPlayer().bounceBack(var,2);
						if (isDestroyOnImpact())
							die();
						setPosition(to);
					}
				} else {
					setPosition(to);
				}
			}
		} else {
			setPosition(to);
		}
		
	}
	
	public void doInertia(){
		int absInertia = Math.abs(inertiaX);
		int sign = inertiaX > 0? 1:-1;
		Position initial = new Position(getPosition());	
		for (int i = 0; i < absInertia; i++){
			initial.x += sign;
			Cell destinationCell = level.getMapCell(initial);
			if (destinationCell == null || (destinationCell.isSolid() && !isEthereal()) ){
				if (isDestroyOnImpact()){
					//getLevel().removeMonster((Monster)this);
					die();
					return;
				}
				break;
			}
			changePositionTo(initial);
			
		}
		
		absInertia = Math.abs(inertiaY);
		sign = inertiaY > 0? 1:-1;
		initial = new Position(getPosition());	
		for (int i = 0; i < absInertia; i++){
			initial.y += sign;
			Cell destinationCell = level.getMapCell(initial);
			if (destinationCell == null || (destinationCell.isSolid() && !isEthereal()) ){
				if (isDestroyOnImpact()){
					//getLevel().removeMonster((Monster)this);
					die();
					return;
				}
				break;
			}
			changePositionTo(initial);
		}
	}
	
	public void performJump(){
		
		Position destination = Position.add(getPosition(), JUMP);
		Cell destinationCell = level.getMapCell(destination);
		if (destinationCell != null && (!destinationCell.isSolid() || isEthereal())){
			//Actor destinationActor = level.getActorAt(destination);
			jumpHeight++;
			if (isPlayer()){
				ConsoleUserInterface.changeYPos(-1);
				if (getCounter("JUMPING")==1)
					ConsoleUserInterface.correctY(-1);
					
			}
			changePositionTo(destination);
			
		} else {
			/*jumpHeight--;
			if (isPlayer())
				ConsoleUserInterface.changeYPos(1);*/
		}
	}
	
	private int jumpHeight;
	
	public int getJumpHeight(){
		return jumpHeight;
	}
	
	public Cell getStandingCell(){
		return level.getMapCell(Position.add(getPosition(), FALL));
	}
	
	public void explode(){
		
	}
	
	public void fallOnPit(){
		die();
	}
	
	private int inertiaX = 0;
	private int inertiaY = 0;
	
	public int getInertiaX(){
		return inertiaX;
	}
	
	public int getInertiaY(){
		return inertiaY;
	}
	
	public boolean hasInertia(){
		return inertiaX != 0 || inertiaY != 0; 
	}
	
	public void setInertiaX(int x){
		inertiaX = x;
		
	}
	
	public void setInertiaY(int y){
		inertiaY = y;
	}
	
	public boolean isFlying(){
		return false;
	}
	
	private boolean isPlayer(){
		return this == ConsoleUserInterface.getUI().getPlayer();
	}
	
	public boolean isDestroyOnImpact() {
		return false;
	}

	public boolean isEthereal(){
		return false;
	}
	
	public int getBaseCost(){
		return 50;
	}

}