package crl.ui.consoleUI;

import sz.csi.CharKey;
import sz.csi.ConsoleSystemInterface;
import sz.util.Debug;
import sz.util.Position;
import crl.action.Action;
import crl.actor.Actor;
import crl.actor.Message;
import crl.ai.ActionSelector;
import crl.monster.Monster;
import crl.npc.NPC;
import crl.player.Player;
import crl.ui.ActionCancelException;
import crl.ui.CommandListener;
import crl.ui.UISelector;
import crl.ui.UserAction;

public class ConsoleUISelector extends UISelector {
	private ConsoleSystemInterface si;
	public ConsoleUserInterface ui(){
		return (ConsoleUserInterface) getUI();
	}
	
	public void init(ConsoleSystemInterface csi, UserAction[] gameActions, Action advance, ConsoleUserInterface ui){
		super.init(gameActions, advance, ui);
		this.si = csi;
	}
	
	/** 
	 * Returns the Action that the player wants to perform.
     * It may also forward a command instead
     * 
     */
	public Action selectAction(Actor who){
    	Debug.enterMethod(this, "selectAction", who);
	    CharKey input = null;
	    Action ret = null;
	    while (ret == null){
	    	if (ui().gameOver())
	    		return null;
			input = si.inkey();
			ret = ui().selectCommand(input);
			if (ret != null)
				return ret;
			if (input.code == CharKey.DOT) {
				Debug.exitMethod("null");
				return null;
			}
			if (input.code == CharKey.DELETE) {
				Debug.exitMethod("null");
				return null;
			}
			/*if (cheatConsole(input)){
				continue;
			}*/
			if (input.isArrow()){
				int direction = Action.toIntDirection(input);
				advance.setDirection(direction);
				Debug.exitMethod(advance);
				return advance;
				
			} else
			if (input.code == WEAPONCODE){
				if (player.getPlayerClass() == Player.CLASS_VAMPIREKILLER) {
					ret = player.getMysticAction();
					try {
		            	if (ret != null){
		                	ret.setPerformer(player);
		                	if (ret.canPerform(player))
		                		ui().setTargets(ret);
		                	else {
		                		level.addMessage(ret.getInvalidationMessage());
		                		throw new ActionCancelException();
		                	}
        	                Debug.exitMethod(ret);
            	        	return ret;
						}
					}
					catch (ActionCancelException ace){
						ret = null;
					}
				} 
			}else{
            	ret = getRelatedAction(input.code);
            	try {
	            	if (ret != null){
	            		ret.setPerformer(player);
	            		if (ret.canPerform(player))
	            			ui().setTargets(ret);
	            		else {
	            			level.addMessage(ret.getInvalidationMessage());
	            			throw new ActionCancelException();
	            		}
	            		
                     	Debug.exitMethod(ret);
                    	return ret;
					}

				}
				catch (ActionCancelException ace){
					//si.cls();
	 				//refresh();
					ui().addMessage(new Message("Cancelled", player.getPosition()));
					ret = null;
				}
				//refresh();
			}
		}
		Debug.exitMethod("null");
		return null;
	}
	
	public String getID(){
		return "UI";
	}
    
	public ActionSelector derive(){
 		return null;
 	}
	
	private static final int WEAPONCODE = CharKey.SPACE;
	
}
