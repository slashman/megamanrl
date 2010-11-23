package crl.level;

import crl.action.*;
import sz.util.*;
import crl.ai.*;
import crl.actor.*;

public class SimpleCounterAI implements ActionSelector{
	private int counter;
	private Action action;
	
	public String getID(){
		return "Simple Counter";
	}
	
	public SimpleCounterAI(Action action){
		this.action = action;
	}

	public Action selectAction(Actor who) {
		Debug.enterMethod(this, "selectAction", who);
		Counterable x = (Counterable) who;
		counter++;
		if (x.getCounter() < counter){
			counter = 0;
			Action ret = action;
			Debug.exitMethod(ret);
			return ret;
    	}
    	Debug.exitMethod("null");
	 	return null;
	}

	public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}
}