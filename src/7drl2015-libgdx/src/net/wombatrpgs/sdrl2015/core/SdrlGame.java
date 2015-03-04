package net.wombatrpgs.sdrl2015.core;

import net.wombatrpgs.sdrl2015.io.FocusListener;
import net.wombatrpgs.sdrl2015.io.FocusReporter;

import com.badlogic.gdx.ApplicationListener;

public class SdrlGame implements ApplicationListener, FocusListener {
	
	private boolean paused;
	
	/**
	 * Creates a new game. Requires a few setup tools that are platform
	 * dependant.
	 * @param 	focusReporter		The thing that tells if focus is lost
	 */
	public SdrlGame(Platform platform, FocusReporter focusReporter) {
		super();
		MGlobal.platform = platform;
		//focusReporter.registerListener(this);
		//this.focusReporter = focusReporter;
		paused = false;
		// Don't you dare do anything fancy in here
	}
	
	@Override
	public void create() {
		MGlobal.globalInit();
	}

	@Override
	public void dispose() {
		MGlobal.assetManager.dispose();
		MGlobal.screens.dispose();
	}

	@Override
	public void render() {		
		//focusReporter.update();
		if (!paused) {			
			MGlobal.screens.render();
		}
	}

	/**
	 * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		// TODO: handle game resizing
		
	}

	/**
	 * @see com.badlogic.gdx.ApplicationListener#pause()
	 */
	@Override
	public void pause() {
		paused = true;
		MGlobal.keymap.onPause();
	}

	/**
	 * @see com.badlogic.gdx.ApplicationListener#resume()
	 */
	@Override
	public void resume() {
		paused = false;
		MGlobal.keymap.onResume();
	}

	/**
	 * @see net.wombatrpgs.mrogue.io.FocusListener#onFocusLost()
	 */
	@Override
	public void onFocusLost() {
		pause();
	}

	/**
	 * @see net.wombatrpgs.mrogue.io.FocusListener#onFocusGained()
	 */
	@Override
	public void onFocusGained() {
		resume();
	}

}
