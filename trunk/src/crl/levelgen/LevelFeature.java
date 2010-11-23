package crl.levelgen;

import sz.util.*;

import java.util.*;
import crl.*;

public class LevelFeature {
	//private Vector subFeatures; //Class AssignedFeature
	private Dimension size;

	private Vector<String[][]> layouts = new Vector<String[][]>();

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension value) {
		size = value;
	}

	public void addLayout(String[][] value) {
		layouts.add(value);
	}
	
	public void addLayout(ArrayList<String> value) {
		addLayout(value.toArray(new String[value.size()]));
	}

	public void addLayout(String[] value){
		addLayout(new String[][] {value});
	}

	public final static String [] descriptions = new String [] {
    	"COURTYARDGRASS",
		"COURTYARDDIRT",
		"BRICKWALKWAY",
		"COURTYARDDOOR",
		"COURTYARDWALL",
		"CASTLEWALL",
		"GARGOYLESTATUE",
		"HUMANSTATUE",
		"DARKTREE",
		"DEADSTUMP",
		"TORCH",
		"FENCE",
		"CASTLEDOOR",
		"FOUNTAINCENTER",
		"FOUNTAINAROUND",
		"FOUNTAINPOOL",
		"STREAM"
	};

	public String [][] getALayout(){
		int index = Util.rand(0, layouts.size()-1);
		return (String[][])layouts.elementAt(index);
	}
}