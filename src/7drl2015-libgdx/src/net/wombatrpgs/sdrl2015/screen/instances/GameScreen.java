/**
 *  GameScreen.java
 *  Created on Feb 22, 2013 4:13:09 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.screen.instances;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.io.command.CMapGame;
import net.wombatrpgs.sdrl2015.maps.Level;
import net.wombatrpgs.sdrl2015.maps.Loc;
import net.wombatrpgs.sdrl2015.maps.events.Cursor;
import net.wombatrpgs.sdrl2015.screen.Screen;
import net.wombatrpgs.sdrlschema.io.data.InputCommand;
import net.wombatrpgs.sdrlschema.settings.IntroSettingsMDO;

/**
 * This is the default screen that appears when the game is first loaded. Once
 * on this screen, the opening scene is played.
 */
public class GameScreen extends Screen {
	
	protected Level map;
	protected Cursor cursor;
	
	/**
	 * Constructs the introduction scene. This consists of simply setting up the
	 * parser and map.
	 */
	public GameScreen() {
		super();
		MGlobal.levelManager.setScreen(this);
		
		IntroSettingsMDO introMDO=MGlobal.data.getEntryFor("default_intro", IntroSettingsMDO.class);
		map = MGlobal.levelManager.getLevel(introMDO.map);
		MGlobal.levelManager.setActive(map);
		if (map.getBGM() != null) {
			MGlobal.screens.playMusic(map.getBGM(), false);
		}
		
		addObject(map);
		pushCommandContext(new CMapGame());
		
		cursor = new Cursor();
		assets.add(cursor);
		
		addObject(MGlobal.ui.getNarrator());
		addObject(MGlobal.ui.getHud());
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.io.CommandListener#onCommand
	 * (net.wombatrpgs.mrogueschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		if (super.onCommand(command)) return true;
		switch (command) {
		case INTENT_INVENTORY:
			MGlobal.ui.getInventory().show();
			return true;
		case INTENT_LOOK:
			cursor.activate(false);
			return true;
		default:
			return MGlobal.hero.onCommand(command);
		}
		
	}

	/**
	 * @see net.wombatrpgs.mrogue.screen.Screen#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		super.postProcessing(manager, pass);
		
		if (pass == 0) {
			map.addEvent(MGlobal.hero);
			while (!MGlobal.hero.isEligibleForCamp(true)) {
				MGlobal.hero.setTileX(MGlobal.rand.nextInt(map.getWidth()));
				MGlobal.hero.setTileY(MGlobal.rand.nextInt(map.getHeight()));
			}
			MGlobal.hero.setUpCamp();
			map.setTeleInLoc("hero", new Loc(MGlobal.hero.getTileX(), MGlobal.hero.getTileY()));
			MGlobal.hero.setX(MGlobal.hero.getTileX()*map.getTileWidth());
			MGlobal.hero.setY(MGlobal.hero.getTileY()*map.getTileHeight());
			getCamera().track(MGlobal.hero);
			getCamera().update(0);
			MGlobal.hero.refreshVisibilityMap();
		}
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.screen.Screen#onFocusGained()
	 */
	@Override
	public void onFocusGained() {
		super.onFocusGained();
		tintTo(new Color(1, 1, 1, 1));
	}

}
