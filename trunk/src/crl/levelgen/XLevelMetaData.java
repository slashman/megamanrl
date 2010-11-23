package crl.levelgen;

import java.util.ArrayList;
import java.util.Hashtable;

import sz.util.Dimension;

public class XLevelMetaData extends LevelMetaData{
	private ArrayList<LevelSectionInfo> sections = new ArrayList<LevelSectionInfo>();
	private LevelFeature base;
	private Hashtable<String, String> charmap;

	public void addSectionInfo(LevelSectionInfo i){
		sections.add(i);
	}
	
	public ArrayList<LevelSectionInfo> getSections() {
		return sections;
	}

	public void setSections(ArrayList<LevelSectionInfo> sections) {
		this.sections = sections;
	}

	public LevelFeature getBase() {
		return base;
	}

	public void setBase(LevelFeature base) {
		this.base = base;
	}

	public Hashtable<String, String> getCharmap() {
		return charmap;
	}

	public void setCharmap(Hashtable<String, String> charmap) {
		this.charmap = charmap;
	}
	
	public LevelFeature normalize(int width, int height){
		int lessX = sections.get(0).getPosition().x;
		int lessY = sections.get(0).getPosition().y;
		int bigX = sections.get(0).getPosition().x;
		int bigY = sections.get(0).getPosition().y;
		for (LevelSectionInfo info: sections){
			if (info.getPosition().x < lessX){
				lessX = info.getPosition().x; 
			}
			if (info.getPosition().y < lessY){
				lessY = info.getPosition().y; 
			}
			
			if (info.getPosition().x > bigX){
				bigX = info.getPosition().x; 
			}
			if (info.getPosition().y > bigY){
				bigY = info.getPosition().y; 
			}
		}
		int columns = bigX - lessX;
		int rows = bigY - lessY;
		for (LevelSectionInfo info: sections){
			info.getPosition().x = (info.getPosition().x - lessX)*width;
			info.getPosition().y = (info.getPosition().y - lessY)*height;
		}
		LevelFeature ret = new LevelFeature();
		Dimension size = new Dimension();
		size.x = (columns+1) * width;
		size.y = (rows+1) * height;
		String[] featureArray = new String[size.y];
		StringBuilder builder = new StringBuilder();
		for (int x = 0; x < size.x; x++){
			builder.append(':');
		}
		String emptyRow = builder.toString();
		for (int y = 0; y < size.y; y++){
			featureArray[y] = emptyRow;
		}
		ret.addLayout(featureArray);
		return ret;
	}

}
