package crl.levelgen.patterns;

import java.util.Hashtable;

public class FireManStage {
	public static Hashtable<String, String> defaultCharMap = new Hashtable<String, String>();
	static {
		defaultCharMap.put(":", "BLACKNESS");
		defaultCharMap.put(" ", "BLACKNESS");
		defaultCharMap.put(".", "BLACK_BGROUND");
		defaultCharMap.put("-", "ORANGE_BGROUND");
		defaultCharMap.put("#", "METAL_BRICK");
		defaultCharMap.put("w", "METAL_BRICK");
		defaultCharMap.put("=", "CONCRETESTAIRS");
		defaultCharMap.put("s", "CONCRETESTAIRS");
		defaultCharMap.put("f", "LAVA");
		defaultCharMap.put("T", "BUNKER");
		defaultCharMap.put("P", "CONESPIKE");
		defaultCharMap.put("0", "BOSSDOOR");
		defaultCharMap.put("S", "ORANGE_BGROUND EXIT _START");
		defaultCharMap.put("W", "WILYLOGO");
		defaultCharMap.put("*", "GRAY_METAL_BGROUND");
		
		defaultCharMap.put("1", "BLACK_BGROUND ENEMYGEN KAMADOMA_RED 10");
		defaultCharMap.put("2", "BLACK_BGROUND ENEMYGEN BOMBOMBOMB_BLUE 10");
		defaultCharMap.put("3", "BLACK_BGROUND ENEMYGEN SUZYBOT 10");
		defaultCharMap.put("4", "BLACK_BGROUND ENEMYGEN BLASTER 10");
		defaultCharMap.put("5", "BLACK_BGROUND ENEMYGEN GABYOALL 10");
		defaultCharMap.put("6", "BLACK_BGROUND ENEMYGEN KILLERBOMB 10");
		defaultCharMap.put("7", "BLACK_BGROUND ENEMYGEN MAMBU 25");
	}
}