package crl.levelgen;

import java.util.Hashtable;

public class Mosaic {
	private String feature, featureSet, compass;
	private int segments;
	private Hashtable<String, Integer> chances;
	
	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getFeatureSet() {
		return featureSet;
	}

	public void setFeatureSet(String featureSet) {
		this.featureSet = featureSet;
	}

	public String getCompass() {
		return compass;
	}

	public void setCompass(String compass) {
		this.compass = compass;
	}

	public int getSegments() {
		return segments;
	}

	public void setSegments(int segments) {
		this.segments = segments;
	}

	public Hashtable<String, Integer> getChances() {
		return chances;
	}

	public void setChances(Hashtable<String, Integer> chances) {
		this.chances = chances;
	}
}
