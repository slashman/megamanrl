package crl.ui;

import java.util.Hashtable;
import sz.util.Debug;
import sz.util.Position;
import crl.action.Action;
import crl.ai.ActionSelector;
import crl.level.Level;
import crl.player.Player;

public abstract class UISelector implements ActionSelector  {
	protected Hashtable gameActions = new Hashtable();
	
	protected Action advance;
	/*protected Action attack;
	protected Action target;*/
	protected Position defaultTarget;
	protected Player player;
	protected Level level;

	public void setPlayer (Player p){
		player = p;
		level = player.getLevel();
	}
	protected Action getRelatedAction(int keyCode){
    	Debug.enterMethod(this, "getRelatedAction", keyCode+"");
    	UserAction ua = (UserAction) gameActions.get(keyCode+"");
    	if (ua == null){
    		Debug.exitMethod("null");
    		return null;
    	}
    	Action ret = ua.getAction();
		Debug.exitMethod(ret);
		return ret;
	}

	private UserInterface ui;
	
	public UserInterface getUI(){
		return ui;
	}
	
	protected void init(UserAction[] gameActions, Action advance, UserInterface ui){
		this.ui = ui;
		this.advance = advance;
		/*this.target = target;
		this.attack = attack;*/
		for (int i = 0; i < gameActions.length; i++){
			this.gameActions.put(gameActions[i].getKeyCode()+"", gameActions[i]);
		}
	}
	
}
