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
import net.wombatrpgs.sdrl2015.rpg.Hero;
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
	protected TimerObject timer;
	protected MusicObject music;
	protected boolean shouldIntroduce;

	/**
	 * Creates the title screen by looking up default title screen settings.
	 */
	public TitleScreen() {
		super();
		tint = new Color(1, 1, 1, 1);
		mdo = MGlobal.data.getEntryFor(Constants.KEY_TITLE, TitleSettingsMDO.class);
		screen = new Picture(mdo.bg, 0, 0, 0);
		assets.add(screen);
		addObject(screen);
		pushCommandContext(new CMapSplash());
		shouldIntroduce = false;
		
		IntroSettingsMDO introMDO=MGlobal.data.getEntryFor(Constants.KEY_INTRO, IntroSettingsMDO.class);
		
		if (MapThing.mdoHasProperty(introMDO.music)) {
			music = new MusicObject(MGlobal.data.getEntryFor(introMDO.music, MusicMDO.class));
			assets.add(music);
		}
		
		MGlobal.hero = new Hero(MGlobal.levelManager.getActive());
		assets.add(MGlobal.hero);
		
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
		if (shouldIntroduce) {
			MGlobal.screens.pop();
			Screen gameScreen = new GameScreen();
			MGlobal.screens.push(gameScreen);
			gameScreen.init();
		}
	}

}