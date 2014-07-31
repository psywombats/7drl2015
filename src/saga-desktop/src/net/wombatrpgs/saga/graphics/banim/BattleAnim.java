/**
 *  BattleAnimation.java
 *  Created on May 23, 2014 8:40:10 AM for project saga-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.graphics.banim;

import net.wombatrpgs.mgne.core.AssetQueuer;
import net.wombatrpgs.mgne.core.interfaces.Updateable;
import net.wombatrpgs.mgne.graphics.interfaces.Disposable;
import net.wombatrpgs.mgne.graphics.interfaces.PosRenderable;
import net.wombatrpgs.saga.screen.ScreenBattle;
import net.wombatrpgs.sagaschema.graphics.banim.BattleAnimMDO;

/**
 * A thing that can be played back for an attack during battle.
 */
public abstract class BattleAnim extends AssetQueuer implements	Disposable,
																PosRenderable,
																Updateable {
	
	protected BattleAnimMDO mdo;
	
	protected float sinceStart;
	protected boolean started, playing;
	
	/**
	 * Creates a new battle anim from data.
	 * @param	mdo				The data to create from.
	 */
	public BattleAnim(BattleAnimMDO mdo) {
		this.mdo = mdo;
	}

	/** @see net.wombatrpgs.mgne.graphics.interfaces.Boundable#getWidth() */
	@Override public int getWidth() { return 0; }

	/** @see net.wombatrpgs.mgne.graphics.interfaces.Boundable#getHeight() */
	@Override public int getHeight() { return 0; }
	
	/** @return True if animation has started yet, false otherwise */
	public final boolean isPlaying() { return playing; }
	
	/** @return True if animation has at any point been playing */
	public final boolean isStarted() { return started; }

	/**
	 * @see net.wombatrpgs.mgne.core.interfaces.Updateable#update(float)
	 */
	@Override
	public void update(float elapsed) {
		if (playing) {
			sinceStart += elapsed;
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		// default is nothing
	}
	
	/**
	 * Check if this animation is done or not.
	 * @return					True if animation has finished, false if ongoing
	 */
	public abstract boolean isDone();
	
	/**
	 * Start and reset this animation.
	 * @param screen TODO
	 */
	public void start(ScreenBattle screen) {
		sinceStart = 0;
		started = true;
		playing = true;
	}
	
	/**
	 * Sets this animation back to its initial state.
	 */
	public void reset() {
		sinceStart = 0;
		playing = false;
	}
	
	/**
	 * Stops this animation from gaining time.
	 */
	public final void pause() {
		playing = false;
	}
	
	/**
	 * Pauses this animation at a certain point in time.
	 * @param time
	 */
	public final void pauseAt(float time) {
		pause();
		sinceStart = time;
	}
	
	/**
	 * Called by the screen when this animation is terminated.
	 * @param screen TODO
	 */
	public void finish(ScreenBattle screen) {
		playing = false;
	}

}
