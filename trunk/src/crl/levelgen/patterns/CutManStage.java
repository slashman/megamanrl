package crl.levelgen.patterns;

import java.util.Hashtable;

public class CutManStage {
	public static Hashtable<String, String> defaultCharMap = new Hashtable<String, String>();
	static {
		defaultCharMap.put(":", "BLACKNESS");
		defaultCharMap.put(" ", "BLACKNESS");
		defaultCharMap.put(".", "CYANSKY_BGROUND");
		defaultCharMap.put("-", "GRAY_BGROUND");
		defaultCharMap.put("#", "GREEN_BRICK");
		defaultCharMap.put("=", "CONCRETESTAIRS");
		defaultCharMap.put("T", "BUNKER");
		defaultCharMap.put("P", "CONESPIKE");
		defaultCharMap.put("0", "BOSSDOOR");
		defaultCharMap.put("S", "GRAY_BGROUND EXIT _START");
		defaultCharMap.put("W", "WILYLOGO");
		defaultCharMap.put("*", "GRAY_METAL_BGROUND");
		defaultCharMap.put("C", "GRAY_METAL_BGROUND MONSTER CUTMAN");
		
		defaultCharMap.put("1", "CYANSKY_BGROUND ENEMYGEN KAMADOMA_RED 10");
		defaultCharMap.put("2", "CYANSKY_BGROUND ENEMYGEN BOMBOMBOMB_BLUE 10");
		defaultCharMap.put("3", "CYANSKY_BGROUND ENEMYGEN SUZYBOT 10");
		defaultCharMap.put("4", "CYANSKY_BGROUND ENEMYGEN BLASTER 10");
		defaultCharMap.put("5", "CYANSKY_BGROUND ENEMYGEN GABYOALL 10");
		defaultCharMap.put("6", "CYANSKY_BGROUND ENEMYGEN KILLERBOMB 10");
		defaultCharMap.put("7", "CYANSKY_BGROUND ENEMYGEN MAMBU 25");
	}
}