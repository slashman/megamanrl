package crl.levelgen.patterns;

import java.util.Hashtable;

public class BombManStage {
	public static Hashtable<String, String> defaultCharMap = new Hashtable<String, String>();
	static {
		defaultCharMap.put(":", "BLACKNESS");
		defaultCharMap.put(".", "CYANSKY_BGROUND");
		defaultCharMap.put("-", "CONCRETEFLOOR");
		defaultCharMap.put("T", "CONCRETETOWER");
		defaultCharMap.put("t", "CONCRETETOWER2");
		defaultCharMap.put("s", "CONCRETESTAIRS");
		defaultCharMap.put("S", "CYANSKY_BGROUND EXIT _START");
		defaultCharMap.put("P", "CONESPIKE");
		defaultCharMap.put("X", "PIPESWALL");
		defaultCharMap.put("W", "WILYLOGO");
		defaultCharMap.put("D", "BOSSDOOR");
		defaultCharMap.put("K", "CYANSKY_BGROUND MONSTER KAMADOMA_RED 50");
		defaultCharMap.put("*", "GREENBRICK_BGROUND");
		defaultCharMap.put("B", "GREENBRICK_BGROUND MONSTER BOMBMAN");
		defaultCharMap.put("b", "CYANSKY_BGROUND MONSTER BOMBOMBOMB_BLUE");
		
		defaultCharMap.put("1", "CYANSKY_BGROUND ENEMYGEN KAMADOMA_RED 10");
		defaultCharMap.put("2", "CYANSKY_BGROUND ENEMYGEN BOMBOMBOMB_BLUE 10");
		defaultCharMap.put("3", "CYANSKY_BGROUND ENEMYGEN SUZYBOT 10");
		defaultCharMap.put("4", "CYANSKY_BGROUND ENEMYGEN BLASTER 10");
		defaultCharMap.put("5", "CYANSKY_BGROUND ENEMYGEN GABYOALL 10");
		defaultCharMap.put("6", "CYANSKY_BGROUND ENEMYGEN KILLERBOMB 10");
		defaultCharMap.put("7", "CYANSKY_BGROUND ENEMYGEN MAMBU 25");
		
		defaultCharMap.put("w", "CYANSKY_BGROUND MONSTER MAMBU");
		
	}
}