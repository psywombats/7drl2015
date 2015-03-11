/**
 *  LevelManager.java
 *  Created on Dec 25, 2012 5:54:15 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.maps;

import java.util.HashMap;
import java.util.Map;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.rpg.enemy.EnemyGenerator;
import net.wombatrpgs.sdrl2015.scenes.TeleportManager;
import net.wombatrpgs.sdrl2015.screen.Screen;
import net.wombatrpgs.sdrlschema.maps.MapMDO;
import net.wombatrpgs.sdrlschema.settings.TeleportSettingsMDO;

/**
 * Manages levels so that they don't have to be created multiple times. It
 * should probably support asynchronous loading at some point. This... This is
 * probably a memory hog.
 */
public class LevelManager {
	
	/** Goes from map IDs to their level manifestation */
	protected Map<String, Level> levels;
	protected Screen screen;
	protected Level active;
	protected TeleportManager teleport;
	protected EnemyGenerator enemyGen;
	
	/**
	 * Creates and initializes a new level manager.
	 */
	public LevelManager() {
		levels = new HashMap<String, Level>();
		enemyGen = new EnemyGenerator();
	}
	
	/** @param screen The screen that will be showing levels */
	public void setScreen(Screen screen) { this.screen = screen; }
	
	/** @return The screen levels use */
	public Screen getScreen() { return screen; }
	
	/** @return The currently active level */
	public Level getActive() { return active; }
	
	/** @param active The new active level */
	public void setActive(Level active) { this.active = active; }
	
	/** @return The teleport processor for these levels */
	public TeleportManager getTele() { return this.teleport; }
	
	/** @return The enemy generator for these levels */
	public EnemyGenerator getEnemyGen() { return this.enemyGen; }
	
	/**
	 * Resets like it's a new game.
	 */
	public void reset() {
		screen = null;
		active = null;
		teleport = null;
		levels.clear();
		levels = new HashMap<String, Level>();
	}
	
	/**
	 * Converts a string id into a level, either by fetching it or loading it
	 * up. WARNING: right now it loads the entire goddamn level if it hasn't
	 * been loaded yet.
	 * @param 	mapID		The map id to load up
	 * @return				The map, either gen'd or stored
	 */
	public Level getLevel(String mapID) {
		if (teleport == null) {
			teleport = new TeleportManager(MGlobal.data.getEntryFor(
					TeleportManager.MD0_KEY, TeleportSettingsMDO.class));
			teleport.queueRequiredAssets(MGlobal.assetManager);
		}
		if (!levels.containsKey(mapID)) {
			// TODO: figure out this tint bullshit and why it's needed
			// it's buggy, this shouldn't be necessary
			float oldR = 0, oldG = 0, oldB = 0;
			if (MGlobal.screens.size() > 0) {
				oldR = MGlobal.screens.peek().getTint().r;
				oldG = MGlobal.screens.peek().getTint().g;
				oldB = MGlobal.screens.peek().getTint().b;
				MGlobal.screens.peek().getTint().r = 1;
				MGlobal.screens.peek().getTint().g = 1;
				MGlobal.screens.peek().getTint().b = 1;
			}
			MapMDO mapMDO = MGlobal.data.getEntryFor(mapID, MapMDO.class);
			Level map = new Level(mapMDO, screen);
			long startTime = System.currentTimeMillis();
			map.queueRequiredAssets(MGlobal.assetManager);
			for (int pass = 0; MGlobal.assetManager.getProgress() < 1; pass++) {
				MGlobal.assetManager.finishLoading();
				map.postProcessing(MGlobal.assetManager, pass);
				teleport.postProcessing(MGlobal.assetManager, pass);
			}
			long endTime = System.currentTimeMillis();
			float elapsed  = (endTime - startTime) / 1000f;
			MGlobal.reporter.inform("Loaded level " + mapID + ", elapsed " +
						"time: " + elapsed + " seconds");
			levels.put(mapID, map);
			if (MGlobal.screens.size() > 0) {
				MGlobal.screens.peek().getTint().r = oldR;
				MGlobal.screens.peek().getTint().g = oldG;
				MGlobal.screens.peek().getTint().b = oldB;
			}
		}
		return levels.get(mapID);
	}
	
}
