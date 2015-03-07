package net.wombatrpgs.sdrl2015.screen.instances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import net.wombatrpgs.sdrl2015.core.Constants;
import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.io.audio.MusicObject;
import net.wombatrpgs.sdrl2015.io.command.CMapSplash;
import net.wombatrpgs.sdrl2015.maps.MapThing;
import net.wombatrpgs.sdrl2015.maps.objects.Picture;
import net.wombatrpgs.sdrl2015.maps.objects.TimerListener;
import net.wombatrpgs.sdrl2015.maps.objects.TimerObject;
import net.wombatrpgs.sdrl2015.scenes.SceneParser;
import net.wombatrpgs.sdrl2015.screen.Screen;
import net.wombatrpgs.sdrlschema.audio.MusicMDO;
import net.wombatrpgs.sdrlschema.io.data.InputCommand;
import net.wombatrpgs.sdrlschema.settings.IntroSettingsMDO;
import net.wombatrpgs.sdrlschema.settings.TitleSettingsMDO;

/**
 * FALL INTO MADNESS.
 */
public class TitleScreen extends Screen {
	
	protected TitleSettingsMDO mdo;
	protected Picture screen, prompt;
	protected SceneParser introParser, inParser, outParser;
	protected TimerObject timer;
	protected MusicObject music;
	protected boolean shouldIntroduce;

	/**
	 * Creates the title screen by looking up default title screen settings.
	 */
	public TitleScreen() {
		super();
		mdo = MGlobal.data.getEntryFor(Constants.KEY_TITLE, TitleSettingsMDO.class);
		screen = new Picture(mdo.bg, 0, 0, 0);
		assets.add(screen);
		addObject(screen);
		pushCommandContext(new CMapSplash());
		shouldIntroduce = false;
		
		IntroSettingsMDO introMDO=MGlobal.data.getEntryFor(Constants.KEY_INTRO, IntroSettingsMDO.class);
		introParser = MGlobal.levelManager.getCutscene(introMDO.titleScene, this);
		inParser = MGlobal.levelManager.getCutscene(introMDO.immScene, this);
		outParser = MGlobal.levelManager.getCutscene(introMDO.outScene, this);
		assets.add(introParser);
		assets.add(inParser);
		assets.add(outParser);
		
		if (MapThing.mdoHasProperty(introMDO.music)) {
			music = new MusicObject(MGlobal.data.getEntryFor(introMDO.music, MusicMDO.class));
			assets.add(music);
		}
		
		prompt = new Picture(mdo.prompt, mdo.promptX, mdo.promptY, 1);
		prompt.setColor(new Color(1, 1, 1, 0));
		timer = new TimerObject(0f);
		final TitleScreen host = this;
		timer.addListener(new TimerListener() {
			boolean trans = false;
			@Override public void onTimerZero(TimerObject source) {
				if (trans) {
					prompt.tweenTo(new Color(1, 1, 1, 0), mdo.cycle);
				} else {
					prompt.tweenTo(new Color(1, 1, 1, 1), mdo.cycle);
				}
				trans = !trans;
				source.setTime(mdo.cycle);
				source.set(true);
				source.attach(host);
			}
		});
		timer.set(true);
		assets.add(prompt);
		addObject(prompt);
		timer.attach(this);
		
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
		if (shouldIntroduce) return true;
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
		if (!inParser.isRunning() && !inParser.hasExecuted()) {
			inParser.run();
			if (music != null) {
				MGlobal.screens.playMusic(music, false);
			}
		}
		if (shouldIntroduce) {
			if (introParser.hasExecuted()) {
				if (outParser.hasExecuted()) {
					MGlobal.screens.pop();
					Screen classScreen = new ClassScreen();
					MGlobal.screens.push(classScreen);
				} else if (!outParser.isRunning()) {
					outParser.run();
				}
			} else if (!introParser.isRunning()) {
				introParser.run();
				removeObject(prompt);
			}
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.screen.Screen#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
//		if (music != null) {
//			music.dispose();
//		}
	}

}