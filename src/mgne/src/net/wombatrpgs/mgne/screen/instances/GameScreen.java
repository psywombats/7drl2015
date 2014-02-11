/**
 *  GameScreen.java
 *  Created on Feb 22, 2013 4:13:09 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mgne.screen.instances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import net.wombatrpgs.mgne.core.Constants;
import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.io.command.CMapGame;
import net.wombatrpgs.mgne.maps.Level;
import net.wombatrpgs.mgne.rpg.Avatar;
import net.wombatrpgs.mgne.screen.Screen;
import net.wombatrpgs.mgne.ui.text.FontHolder;
import net.wombatrpgs.mgne.ui.text.TextBox;
import net.wombatrpgs.mgneschema.io.data.InputCommand;
import net.wombatrpgs.mgneschema.settings.IntroSettingsMDO;
import net.wombatrpgs.mgneschema.test.FramerateTestMDO;
import net.wombatrpgs.mgneschema.test.ShaderTestMDO;
import net.wombatrpgs.mgneschema.test.TextBoxTestMDO;
import net.wombatrpgs.mgneschema.test.data.TestState;
import net.wombatrpgs.mgneschema.ui.FontMDO;
import net.wombatrpgs.mgneschema.ui.TextBoxMDO;

/**
 * This is the default screen that appears when the game is first loaded. Once
 * on this screen, the opening scene is played.
 */
public class GameScreen extends Screen {
	
	protected Level map;
	protected Avatar hero;
	
	protected boolean stasisMode;
	
	// tests
	protected FramerateTestMDO fpsMDO;
	protected ShaderTestMDO shaderMDO;
	protected ShaderProgram testShader;
	
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
		
		fpsMDO = MGlobal.data.getEntryFor("test_fps", FramerateTestMDO.class);
		
		shaderMDO = MGlobal.data.getEntryFor("test_shader", ShaderTestMDO.class);
		if (shaderMDO.enabled == TestState.ENABLED) {
			testShader = new ShaderProgram(
					MGlobal.loader.getText(Constants.SHADERS_DIR + shaderMDO.vertexFile),
					MGlobal.loader.getText(Constants.SHADERS_DIR + shaderMDO.fragmentFile));
			batch.setShader(testShader);
			mapShader = (testShader);
		}
		
		// all this stuff is crap, not so much any more
		hero = new Avatar();
		assets.add(hero);
		
		TextBoxTestMDO testMDO = MGlobal.data.getEntryFor("test_textbox", TextBoxTestMDO.class);
		if (testMDO != null && testMDO.enabled == TestState.ENABLED) {
			FontMDO fontMDO = MGlobal.data.getEntryFor(testMDO.font, FontMDO.class);
			FontHolder font = new FontHolder(fontMDO);
			TextBoxMDO textMDO = MGlobal.data.getEntryFor(testMDO.box, TextBoxMDO.class);
			TextBox box = new TextBox(textMDO, font);
			box.setText(testMDO.text);
			assets.add(box);
			addObject(box);
		}
		
		// will be called later
		// normally
		init();
	}
	
	/**
	 * @see net.wombatrpgs.mgne.io.CommandListener#onCommand
	 * (net.wombatrpgs.mgneschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		if (super.onCommand(command)) return true;
		switch (command) {
			// TODO: saga: screen-based commands
		default:
			return hero.onCommand(command);
		}
		
	}

	/**
	 * @see net.wombatrpgs.mgne.screen.Screen#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		super.postProcessing(manager, pass);
		
		if (pass == 0) {
			hero.setTileX(2);
			hero.setTileY(2);
			while (!map.isTilePassable(hero, hero.getTileX(), hero.getTileY())) {
				hero.setTileX(MGlobal.rand.nextInt(map.getWidth()));
				hero.setTileY(MGlobal.rand.nextInt(map.getHeight()));
			}
			map.addEvent(hero);
			hero.setX(hero.getTileX()*map.getTileWidth());
			hero.setY(hero.getTileY()*map.getTileHeight());
			getCamera().track(hero);
			getCamera().update(0);
		}
	}
	
	/**
	 * @see net.wombatrpgs.mgne.screen.Screen#render()
	 */
	@Override
	public void render() {
		super.render();
		batch.begin();
		if (fpsMDO.enabled == TestState.ENABLED) {
			float wr = MGlobal.window.getZoom();
			float wh = MGlobal.window.getZoom();
			defaultFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
					cam.position.x - MGlobal.window.getWidth()/2*wh + 8,
					cam.position.y + MGlobal.window.getHeight()/2*wr - 8);
		}
		batch.end();
		
		
	}
	
	/**
	 * Fetches the player's avatar that's parading around on the map. This
	 * replaces one of those public static monstrosities that's been kicking
	 * around for forever.
	 * @return					The representation of the player on the map
	 */
	public Avatar getHero() {
		return hero;
	}

}