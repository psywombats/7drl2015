/**
 *  RGlobal.java
 *  Created on Nov 11, 2012 3:08:03 AM for project rainfall-libgdx-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import net.wombatrpgs.sdrl2015.core.reporters.*;
import net.wombatrpgs.sdrl2015.graphics.GraphicsSettings;
import net.wombatrpgs.sdrl2015.io.DefaultKeymap;
import net.wombatrpgs.sdrl2015.io.FileLoader;
import net.wombatrpgs.sdrl2015.io.Keymap;
import net.wombatrpgs.sdrl2015.io.loaders.DataLoader;
import net.wombatrpgs.sdrl2015.io.loaders.SceneLoader;
import net.wombatrpgs.sdrl2015.maps.LevelManager;
import net.wombatrpgs.sdrl2015.maps.gen.TileManager;
import net.wombatrpgs.sdrl2015.rpg.Hero;
import net.wombatrpgs.sdrl2015.scenes.SceneData;
import net.wombatrpgs.sdrl2015.screen.ScreenStack;
import net.wombatrpgs.sdrl2015.screen.WindowSettings;
import net.wombatrpgs.sdrl2015.screen.instances.TitleScreen;
import net.wombatrpgs.sdrl2015.ui.UISettings;
import net.wombatrpgs.sdrlschema.settings.GraphicsSettingsMDO;
import net.wombatrpgs.sdrlschema.settings.UISettingsMDO;
import net.wombatrpgs.sdrlschema.settings.WindowSettingsMDO;

/**
 * Rainfall's version of the MGNDB global.
 */
public class MGlobal {
	
	/** Error-reporting dispatcher */
	public static Reporter reporter;
	/** Storage container for data entries */
	public static Database data;
	/** Manages all in-game assets */
	public static MAssets assetManager;
	/** The stack of screeeeeeens */
	public static ScreenStack screens;
	/** Current mapper of the keyys */
	public static Keymap keymap;
	/** All magic numbers and stuff */
	public static Constants constants;
	/** My hero~~~~ <3 <3 <3 (the player's physical representation */
	public static Hero hero;
	/** Stores all of our levels */
	public static LevelManager levelManager;
	/** A single-source RNG */
	public static Random rand;
	/** The UI settings currently in use by the game */
	public static UISettings ui;
	/** Are we done loading yet? */
	public static boolean initialized = false;
	/** Our current window settings */
	public static WindowSettings window;
	/** Our current graphics settings */
	public static GraphicsSettings graphics;
	/** Loader for simple text files */
	public static FileLoader loader;
	/** Manages all tile types and graphics etc */
	public static TileManager tiles;
	/** Desktop mode */
	public static Platform platform;
	/** Loaded from config file */
	public static Map<String, Boolean> config;
	
	private static List<Queueable> toLoad;
	
	/**
	 * Called as part of the map game.
	 */
	public static void globalInit() {
		
		// debugging is needed first
		MGlobal.reporter = new PrintReporter();
		try {
			long startTime = System.currentTimeMillis();
			MGlobal.reporter.inform("Initialized error reporting");
			MGlobal.assetManager = new MAssets();
			MGlobal.reporter.inform("Initializing primary globals");
			long seed = System.currentTimeMillis();
			MGlobal.rand = new Random(seed);
			MGlobal.reporter.inform("Using global seed " + seed);
			MGlobal.data = new Database();
			MGlobal.loader = new FileLoader();
			
			// this is terrible
			config = new HashMap<String, Boolean>();
			String result = loader.getText(Constants.CONFIG_FILE);
			Scanner scanner = new Scanner(result);
			while (scanner.hasNext()) {
				String configString = scanner.next();
				String[] tokens = configString.split("=");
				config.put(tokens[0], Boolean.valueOf(tokens[1]));
			}
			scanner.close();
			
			// load up data marked essential, this will always be ugly
			MGlobal.reporter.inform("Loading essential data");
			MGlobal.keymap = new DefaultKeymap();
			setHandlers();
			MGlobal.data.queueData(assetManager, Constants.PRELOAD_SCHEMA);
			long assetStart = System.currentTimeMillis();
			assetManager.finishLoading();
			long assetEnd = System.currentTimeMillis();
			float assetElapsed = (assetEnd - assetStart) / 1000f;
			MGlobal.reporter.inform("Finished loading essential data, " +
					"elapsed time: " + assetElapsed + "seconds");
			
			// here on out, these may require essential data
			toLoad = new ArrayList<Queueable>();
			MGlobal.reporter.inform("Intializing secondary globals");
			MGlobal.constants = new Constants();
			MGlobal.screens = new ScreenStack();
			MGlobal.tiles = new TileManager();
			MGlobal.levelManager = new LevelManager();
			
			// load secondary data
			// TODO: load with a loading bar
			MGlobal.reporter.inform("Loading secondary data");
			MGlobal.data.queueFilesInDir(assetManager, Gdx.files.internal(Constants.DATA_DIR));
			assetEnd = System.currentTimeMillis();
			assetManager.finishLoading();
			assetEnd = System.currentTimeMillis();
			MGlobal.reporter.inform("Finished loading secondary data, " +
					"elapsed time: " + assetElapsed + "seconds");
	
			// initialize everything that needed data
			MGlobal.reporter.inform("Initializing data-dependant resources");
			MGlobal.window = new WindowSettings(
					MGlobal.data.getEntryFor(Constants.KEY_WINDOW, WindowSettingsMDO.class));
			MGlobal.graphics = new GraphicsSettings(
					MGlobal.data.getEntryFor(Constants.KEY_GRAPHICS, GraphicsSettingsMDO.class));
			MGlobal.ui = new UISettings(MGlobal.data.getEntryFor(
					UISettings.DEFAULT_MDO_KEY, UISettingsMDO.class));
			toLoad.add(ui);
			toLoad.add(graphics);
			for (Queueable q : toLoad) q.queueRequiredAssets(assetManager);
			for (int pass = 0; MGlobal.assetManager.getProgress() < 1; pass++) {
				assetStart = System.currentTimeMillis();
				MGlobal.assetManager.finishLoading();
				assetEnd = System.currentTimeMillis();
				assetElapsed = (assetEnd - assetStart) / 1000f;
				MGlobal.reporter.inform("Loading pass " + pass + ", took " + assetElapsed);
				for (Queueable q : toLoad) q.postProcessing(MGlobal.assetManager, pass);
			}
			
			// initializing graphics
			MGlobal.reporter.inform("Creating level-dependant data");
			toLoad.clear();
			
			boolean fullscreen = config.get("fullscreen");
			Gdx.graphics.setDisplayMode(
					MGlobal.window.getResolutionWidth(),
					MGlobal.window.getResolutionHeight(), 
					fullscreen);
			MGlobal.screens.push(new TitleScreen());
			Gdx.graphics.setTitle(MGlobal.window.getTitle());
			//Gdx.graphics.setVSync(true);
			Gdx.input.setInputProcessor(MGlobal.keymap);
			
			MGlobal.reporter.inform("Loading level assets");
			for (Queueable q : toLoad) q.queueRequiredAssets(assetManager);
			for (int pass = 0; MGlobal.assetManager.getProgress() < 1; pass++) {
				MGlobal.reporter.inform("Loading pass " + pass + ", took " + assetElapsed);
				MGlobal.assetManager.finishLoading();
				for (Queueable q : toLoad) q.postProcessing(MGlobal.assetManager, pass);
				assetEnd = System.currentTimeMillis();
				assetElapsed = (assetEnd - assetStart) / 1000f;
			}
			
			initialized = true;
			long endTime = System.currentTimeMillis();
			float elapsed = (endTime - startTime) / 1000f;
			MGlobal.reporter.inform("Done loading, elasped time: " + elapsed + 
					" seconds");
			
		} catch (Exception e) {
			// TODO: proper init error handling
			MGlobal.reporter.err("Exception during initialization: ", e);
			Gdx.app.exit();
		}
	}
	
	/**
	 * Dumps the old level manager and associated state and then starts all
	 * over. This is separate from the global init for some reason.
	 */
	public static void newGame() {
		MGlobal.screens.reset();
		MGlobal.levelManager.reset();
		MGlobal.hero = null;
		MGlobal.screens.push(new TitleScreen());
	}
	
	/**
	 * Sets all the file handlers used by the asset manager.
	 */
	public static void setHandlers() {
		assetManager.setLoader(SceneData.class, new SceneLoader(new InternalFileHandleResolver()));
		assetManager.setLoader(DataEntry.class, new DataLoader(new InternalFileHandleResolver()));
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
	}
	
	/**
	 * Version info, hacked backwards from SaGa's better system. This will be
	 * axed in the next iteration of the engine.
	 * @return					Versioning information for 7DRL
	 */
	public static VersionInfo getVersionInfo() {
		VersionInfo info = new VersionInfo("0.1", 1, "7drl2015");
		return info;
	}

}
