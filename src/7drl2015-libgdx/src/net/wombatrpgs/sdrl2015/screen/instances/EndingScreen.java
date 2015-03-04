package net.wombatrpgs.sdrl2015.screen.instances;

import com.badlogic.gdx.Gdx;

import net.wombatrpgs.mrogueschema.io.data.InputCommand;
import net.wombatrpgs.mrogueschema.settings.EndSettingsMDO;
import net.wombatrpgs.mrogueschema.settings.IntroSettingsMDO;
import net.wombatrpgs.sdrl2015.core.Constants;
import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.io.command.CMapSplash;
import net.wombatrpgs.sdrl2015.maps.objects.Picture;
import net.wombatrpgs.sdrl2015.scenes.SceneParser;
import net.wombatrpgs.sdrl2015.screen.Screen;

/**
 * FALLed INTO MADNESS.
 */
public class EndingScreen extends Screen {
	
	protected static final String KEY_ENDING = "ending_default";
	
	protected EndSettingsMDO mdo;
	protected Picture screen;
	protected SceneParser outroParser, inParser, outParser;

	/**
	 * Creates the title screen by looking up default title screen settings.
	 */
	public EndingScreen() {
		super();
		mdo = MGlobal.data.getEntryFor(KEY_ENDING, EndSettingsMDO.class);
		screen = new Picture(mdo.bg, 0, 0, 0);
		assets.add(screen);
		addObject(screen);
		pushCommandContext(new CMapSplash());
		
		IntroSettingsMDO introMDO=MGlobal.data.getEntryFor(Constants.KEY_INTRO, IntroSettingsMDO.class);
		outroParser = MGlobal.levelManager.getCutscene(mdo.ending, this);
		inParser = MGlobal.levelManager.getCutscene(introMDO.immScene, this);
		outParser = MGlobal.levelManager.getCutscene(introMDO.outScene, this);
		assets.add(outroParser);
		assets.add(inParser);
		assets.add(outParser);
		
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
		if (!inParser.isRunning() && !inParser.hasExecuted()) {
			inParser.run();
		} else if (outroParser.hasExecuted()) {
			if (outParser.hasExecuted()) {
				Gdx.app.exit();
			} else if (!outParser.isRunning()) {
				outParser.run();
			}
		} else if (!outroParser.isRunning()) {
			outroParser.run();
		}
	}

}