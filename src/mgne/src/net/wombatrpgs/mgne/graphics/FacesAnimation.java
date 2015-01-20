/**
 *  FacesAnimation.java
 *  Created on Jan 24, 2013 7:45:30 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mgne.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.wombatrpgs.mgne.core.MAssets;
import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.core.interfaces.Updateable;
import net.wombatrpgs.mgne.graphics.interfaces.Disposable;
import net.wombatrpgs.mgne.graphics.interfaces.PosRenderable;
import net.wombatrpgs.mgne.maps.Positionable;
import net.wombatrpgs.mgne.physics.Hitbox;
import net.wombatrpgs.mgneschema.graphics.DirMDO;
import net.wombatrpgs.mgneschema.maps.data.OrthoDir;

/**
 * Any animation used to represent a moving entity on the map. Generally it's
 * a congolomeration of faces that switch out depending on how the entity's
 * moving.
 */
public abstract class FacesAnimation implements	PosRenderable,
												Updateable,
												Disposable {
	
	protected static final float FLICKER_DURATION = .3f; // in s
	
	protected OrthoDir currentDir;
	protected AnimationStrip[] animations;
	protected DirMDO mdo;
	protected int rangeX, rangeY;
	protected int facings;
	protected boolean flickering;
	protected float time;
	
	/**
	 * Sets up an animation from data for a specific map.
	 * @param	mdo				The data used to create this anim
	 * @param	facings			How many facings make up this animation
	 */
	public FacesAnimation(DirMDO mdo, int facings) {
		this.mdo = mdo;
		this.facings = facings;
		setFacing(OrthoDir.SOUTH);
		animations = new AnimationStrip[facings];
		flickering = false;
		time = 0;
		rangeX = 0;
		rangeY = 0;
	}
	
	/** @return The mdo key used to create this animation */
	public String getKey() { return mdo.key; }
	
	/** @return The direction currently facing */
	public OrthoDir getFacing() { return currentDir; }
	
	/** @param dir The new current direction */
	public void setFacing(OrthoDir dir) { this.currentDir = dir; }

	/** @see net.wombatrpgs.mgne.graphics.ScreenGraphic#getWidth() */
	@Override public int getWidth() { return getStrip().getWidth(); }

	/** @see net.wombatrpgs.mgne.graphics.ScreenGraphic#getHeight() */
	@Override public int getHeight() { return getStrip().getHeight(); }
	
	/** @return The currently playing animation strip */
	public AnimationStrip getStrip() { return animations[currentDirOrdinal()]; }
	
	/** @return True if the anim is playing, false otherwise */
	public boolean isMoving() { return getStrip().isMoving(); }
	
	/** @return The currently playing hitbox, never null */
	public Hitbox getHitbox() { return getStrip().getHitbox(); }

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.PosRenderable#renderAt
	 * (com.badlogic.gdx.graphics.g2d.SpriteBatch, float, float)
	 */
	@Override
	public void renderAt(SpriteBatch batch, float x, float y) {
		if (shouldAppear()) {
			getStrip().renderAt(batch, x, y);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Renderable#queueRequiredAssets
	 * (MAssets)
	 */
	@Override
	public void queueRequiredAssets(MAssets manager) {
		for (int i = 0; i < facings; i++) {
			animations[i].queueRequiredAssets(manager);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Renderable#postProcessing
	 * (MAssets , int)
	 */
	@Override
	public void postProcessing(MAssets manager, int pass) {
		for (AnimationStrip strip : animations) {
			strip.postProcessing(manager, pass);
		}
		float bump;
		float frameLength = (1.f / (float) animations[0].getFPS());
		if (!MGlobal.game.synchronizeSprites()) {
			// this is so that the character starts moving right away
			bump = Math.max(0, frameLength - .05f);
			for (AnimationStrip strip : animations) {
				strip.setBump(bump);
				strip.update(0);
			}
		}
		for (AnimationStrip strip : animations) {
			strip.update(0);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.core.interfaces.Updateable#update(float)
	 */
	@Override
	public void update(float elapsed) {
		time += elapsed;
		for (int i = 0; i < facings; i++) {
			// we could just update the only one being displayed...
			animations[i].update(elapsed);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		for (AnimationStrip strip : animations) { 
			strip.dispose();
		}
	}

	/**
	 * Gets the duration to play the whole animation.
	 * @return					The time it takes to play this, in s
	 */
	public float getDuration() {
		return getStrip().getMaxTime();
	}
	
	/**
	 * Sets the position of all hitboxes of this animation.
	 * @param	parent			The parent to associate location with
	 */
	public void setParent(Positionable parent) {
		for (AnimationStrip strip : animations) {
			strip.getHitbox().setParent(parent);
		}
	}
	
	/**
	 * Fetches a specific frame of the animation. Wonky because it takes an
	 * ordinal, not a direction.
	 * @param	dirOrdinal		The ordinal of the direction to fetch
	 * @param	frame			The number of the frame to fetch
	 * @return					The texture region for that frame
	 */
	public TextureRegion getFrame(int dirOrdinal, int frame) {
		return getStrip().getFrame(frame);
	}
	
	/**
	 * Just starts moving.
	 */
	public void startMoving() {
		for (AnimationStrip strip : animations) {
			strip.startMoving();
		}
	}

	/**
	 * Halts all animation movement.
	 */
	public void stopMoving() {
		for (AnimationStrip strip : animations) {
			strip.stopMoving();
		}
	}
	
	/**
	 * Resets all the timings on this animation.
	 */
	public void reset() {
		for (AnimationStrip strip : animations) {
			strip.reset();
		}
		time = 0;
	}
	
	/**
	 * Turns on/off this animation's flickering.
	 * @param 	flicker			True if we should flicker, false otherwise
	 */
	public void setFlicker(boolean flicker) {
		this.flickering = flicker;
	}
	
	/**
	 * Flashes a certain color for a certain time.
	 * @param	c				The color to flash
	 * @param	duration		How long the flash should take in total
	 */
	public void flash(Color c, float duration) {
		for (AnimationStrip strip : animations) {
			strip.flash(c, duration);
		}
	}
	
	/**
	 * This is used to index into the animations array. Returns the index.
	 * @return					The index of the current anim in the anim array
	 */
	protected abstract int currentDirOrdinal();
	
	/**
	 * Populates the array of animations.
	 */
	protected abstract void sliceAnimations();
	
	/**
	 * Determines if this animation is visible at the moment. This isn't in the
	 * culling sense but more of the "did I drink an invisibility potion"
	 * sense.
	 * @return					True if should be rendered, false otherwise
	 */
	protected boolean shouldAppear() {
		if (flickering) {
			int ms = (int) (time * 1000);
			int d = (int) (FLICKER_DURATION * 1000);
			if (ms % d < d/2) {
				return false;
			}
		}
		return true;
	}

}
