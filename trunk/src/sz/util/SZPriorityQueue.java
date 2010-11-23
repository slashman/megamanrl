package sz.util;

import java.util.*;

import crl.actor.Actor;

public class SZPriorityQueue<T extends PriorityEnqueable> implements java.io.Serializable{
	private ArrayList<T> list;

	public SZPriorityQueue(){
		list = new ArrayList<T>(40);
 	}

 	public boolean contains (Object what){
 		return list.contains(what);
	}

 	public SZPriorityQueue (int ini){
 		list = new ArrayList<T>(ini);
	}

	public  ArrayList<T> getVector(){
		return list;
	}

 	public Object unqueue(){
 		if (list.size() > 0){
 			PriorityEnqueable x = list.get(0);
 			for (int i = 1; i < list.size(); i++){
 				list.get(i).reduceCost(x.getCost());
 			}
	 		
	 		list.remove(0);
	 		return x;
		} else
			return null;
	}

	public void enqueue(T what){
		//Debug.say("enqueuing "+what);
		if (list.size() == 0){
			list.add(what);
			//Debug.say("enqueued at 0");
			return;
		}
		int i = 0;
		while (i < list.size() && list.get(i).getCost() <= what.getCost()){
			i++;
		}
		list.add(i, what);
		/*if (i == list.size())
			list.insertElementAt(what, i);
		else
			list.insertElementAt(what, i+1);*/
		//Debug.say("enqueued at "+i);
    }

    public void forceToFront(T what){
        list.add(0, what);
    }

    public void forceToFront(T what, Object objClass){
    	/*Debug.say("forcing "+what+" to front");
    	Debug.say("forcing "+what.getClass()+" to front");
    	Debug.say("forcing "+objClass.getClass()+" to front");
    	Debug.say(what.getClass().equals(objClass.getClass())+"? ");
    	Debug.say(list.elementAt(0).getClass()+"  0? ");
    	Debug.say(list.elementAt(1).getClass()+" 1? ");
    	Debug.say(list.elementAt(2).getClass()+" 2? ");*/
		//RUN TO THE LAST OCURRENCE OF OBJCLASS
		for (int i = list.size() -1 ; i >= 0; i--)
			//if (! list.elementAt(i).getClass().toString().equals(objClass.getClass().toString())){
			if (list.get(i).getClass().equals(objClass.getClass())){
				if (i<list.size()-1){
					list.add(i+1, what);
					//Debug.say("inserted at " +(i+1));
				}
				else
					list.add(what);
				return;
			}


		//Debug.say("in the end, it doesnt even matter");
		list.add(what);

    }
    public void remove(Object what){
		list.remove(what);
	}

	public boolean isEmpty(){
		return list.isEmpty();
	}
	
	public void removeAll(){
		list.clear();
	}
	
	public void printStatus(){
		Debug.say("Status of SZPriorityQueue "+this);
		for (int i = 0; i < list.size(); i++){
			Debug.say(list.get(i)+" "+list.get(i).getCost());
		}
	}
}