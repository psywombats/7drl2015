package net.wombatrpgs.sdrl2015.screen.instances;

import com.badlogic.gdx.Gdx;

import net.wombatrpgs.mrogueschema.io.data.InputCommand;
import net.wombatrpgs.mrogueschema.settings.DeathSettingsMDO;
import net.wombatrpgs.sdrl2015.core.Constants;
import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.io.command.CMapSplash;
import net.wombatrpgs.sdrl2015.maps.objects.Picture;
import net.wombatrpgs.sdrl2015.scenes.SceneParser;
import net.wombatrpgs.sdrl2015.screen.Screen;

/**
 * FALL INTO DEATH.
 */
public class GameOverScreen extends Screen {
	
	protected DeathSettingsMDO mdo;
	
	protected Picture screen;
	protected SceneParser immParser;
	protected boolean shouldIntroduce;

	/**
	 * Creates the title screen by looking up default title screen settings.
	 */
	public GameOverScreen() {
		super();
		mdo = MGlobal.data.getEntryFor(Constants.KEY_DEATH, DeathSettingsMDO.class);
		screen = new Picture(mdo.bg, 0, 0, 0);
		assets.add(screen);
		addObject(screen);
		pushCommandContext(new CMapSplash());
		shouldIntroduce = false;
		
		immParser = MGlobal.levelManager.getCutscene(mdo.immScene, this);
		assets.add(immParser);
		
		init();
	}

	/**
	 * @see net.wombatrpgs.mrogue.screen.Screen#onCommand
	 * (net.wombatrpgs.mrogueschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		if (super.onCommand(command)) {
			return true;
		}
		switch (command) {
		case INTENT_QUIT:
			Gdx.app.exit();
			return true;
		case INTENT_CONFIRM:
			shouldIntroduce = true;
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.screen.Screen#update(float)
	 */
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		if (!immParser.isRunning() && !immParser.hasExecuted()) {
			immParser.run();
		}
		if (shouldIntroduce) {
			if (immParser.hasExecuted()) {
				MGlobal.newGame();
			}
		}
	}

}