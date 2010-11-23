package crl.level;

import crl.actor.*;

public class GravityActor extends Actor implements Counterable{
	private int freq;

	public GravityActor (int freq){
		this.freq = freq;
		setSelector(new SimpleCounterAI(new GravityAction()));
	}

	public int getFreq() {
		return freq;
	}
	
	public String getDescription(){
		return "Gravity Actor";
	}
	
	public int getCounter() {
		return freq;
	}
}