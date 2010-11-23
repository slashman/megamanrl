package crl.cuts.intro;

import sz.util.Position;
import sz.util.Util;
import crl.cuts.Unleasher;
import crl.game.Game;
import crl.game.STMusicManagerNew;
import crl.level.Level;
import crl.monster.Monster;
import crl.monster.MonsterFactory;
import crl.ui.UserInterface;

public class Intro3 extends Unleasher {

	public void unleash(Level level, Game game) {
		if (level.getFlag("INTRO2")&& level.getCounter("COUNTBACK_INTRO_2").isOver()){
			level.addMessage("A pack of Wargs appears from nowhere!");
			Position playerFloor = new Position(level.getPlayer().getPosition());
			playerFloor.z = 2;
			int wargs = Util.rand(1,3);
			for (int i = 0; i < wargs; i++){
				int xpos = Util.rand(-8,8);
				int ypos = Util.rand(-8,8);
				Position wargPosition = Position.add(playerFloor, new Position(xpos, ypos));
				if (level.isWalkable(wargPosition)){
					Monster warg = MonsterFactory.getFactory().buildMonster("WARG");
					warg.setPosition(wargPosition);
					level.addMonster(warg);
				} else {
					i--;
				}
			}
			STMusicManagerNew.thus.playKey("WRECKAGE");
			level.setMusicKeyMorning("WRECKAGE");
			if (level.getNPCByID("MELDUCK") != null)
				level.getNPCByID("MELDUCK").setTalkMessage("On your way! I will take care of anything you leave here");
			enabled = false;
			level.removeCounter("COUNTBACK_INTRO_2");
			level.getPlayer().see();
			UserInterface.getUI().refresh();
		}
	}

}