package net.wombatrpgs.sdrl2015.screen.instances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import net.wombatrpgs.sdrl2015.core.Constants;
import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.io.command.CMapSplash;
import net.wombatrpgs.sdrl2015.maps.objects.Picture;
import net.wombatrpgs.sdrl2015.screen.Screen;
import net.wombatrpgs.sdrlschema.io.data.InputCommand;
import net.wombatrpgs.sdrlschema.settings.DeathSettingsMDO;

/**
 * FALL INTO DEATH.
 */
public class GameOverScreen extends Screen {
	
	protected DeathSettingsMDO mdo;
	
	protected Picture screen;
	protected boolean shouldIntroduce;

	/**
	 * Creates the title screen by looking up default title screen settings.
	 */
	public GameOverScreen() {
		super();
		tint = new Color(1, 1, 1, 1);
		mdo = MGlobal.data.getEntryFor(Constants.KEY_DEATH, DeathSettingsMDO.class);
		screen = new Picture(mdo.bg, 0, 0, 0);
		assets.add(screen);
		addObject(screen);
		pushCommandContext(new CMapSplash());
		shouldIntroduce = false;
		
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
		if (shouldIntroduce) {
			MGlobal.newGame();
		}
	}

}