package crl.level;

import crl.actor.*;

public class EnemyGenerator extends Actor{
	private String monsterId;
	private int counter;

	public EnemyGenerator(String pMonsterId, int counter){
		monsterId = pMonsterId;
		this.counter = counter;
		setSelector(new EnemyGeneratorAI());
 	}

	public String getDescription(){
		return "EnemyGenerator";
	}
	
	public int getCounter() {
		return counter;
	}
	
	public void resetCounter(){
		counter = 0;
	}

	public String getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(String monsterId) {
		this.monsterId = monsterId;
	}
}