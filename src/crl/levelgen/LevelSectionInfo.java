package crl.levelgen;

import sz.util.Position;

public class LevelSectionInfo {
	private LevelFeature feature;
	private Position position;
	public LevelFeature getFeature() {
		return feature;
	}
	public void setFeature(LevelFeature feature) {
		this.feature = feature;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
}
