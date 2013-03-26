/**
 *  MovesetAct.java
 *  Created on Jan 20, 2013 11:55:42 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.characters.moveset;

import com.badlogic.gdx.assets.AssetManager;

import net.wombatrpgs.rainfall.characters.CharacterEvent;
import net.wombatrpgs.rainfall.core.Queueable;
import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.graphics.FacesAnimation;
import net.wombatrpgs.rainfall.graphics.FourDir;
import net.wombatrpgs.rainfall.graphics.Graphic;
import net.wombatrpgs.rainfall.io.audio.SoundObject;
import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfallschema.audio.SoundMDO;
import net.wombatrpgs.rainfallschema.characters.hero.moveset.data.MoveMDO;
import net.wombatrpgs.rainfallschema.graphics.FourDirMDO;
import net.wombatrpgs.rainfallschema.graphics.GraphicMDO;

/**
 * A superclass for all moves from the movesets that involve character animation
 * swapping and other stuff. A MovesetAct should really only be used by a single
 * actor.
 */
public abstract class MovesetAct implements Actionable, 
											Queueable {
	
	protected CharacterEvent actor;
	protected Level map;
	protected FacesAnimation appearance;
	protected Graphic icon;
	protected SoundObject sfx;
	
	/**
	 * Constructs a moveset act from data.
	 * @param 	mdo				The MDO with data to construct from
	 */
	public MovesetAct(CharacterEvent actor, MoveMDO mdo) {
		if (mdo.animation != null && !"".equals(mdo.animation)) {
			FourDirMDO animMDO = RGlobal.data.getEntryFor(mdo.animation, FourDirMDO.class);
			this.appearance = new FourDir(animMDO, actor);
		}
		if (mdo.graphic != null) {
			GraphicMDO iconMDO= RGlobal.data.getEntryFor(mdo.graphic, GraphicMDO.class);
			this.icon = new Graphic(iconMDO);
		}
		if (mdo.sound != null) {
			SoundMDO soundMDO = RGlobal.data.getEntryFor(mdo.sound, SoundMDO.class);
			this.sfx = new SoundObject(soundMDO, actor);
		}
		this.actor = actor;
	}
	
	/** @return The animation associated with this move */
	public FacesAnimation getAppearance() { return appearance; }

	/**
	 * @see net.wombatrpgs.rainfall.characters.moveset.Actionable#act
	 * (net.wombatrpgs.rainfall.maps.Level, net.wombatrpgs.rainfall.characters.CharacterEvent)
	 */
	@Override
	public final void act(Level map, CharacterEvent actor) {
		if (!actor.canAct()) return;
		coreAct(map, actor);
		this.map = map;
	}
	
	/**
	 * @see net.wombatrpgs.rainfall.core.Queueable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		if (appearance != null) {
			appearance.queueRequiredAssets(manager);
		}
		if (icon != null) {
			icon.queueRequiredAssets(manager);
		}
		if (sfx != null) {
			sfx.queueRequiredAssets(manager);
		}
	}

	/**
	 * @see net.wombatrpgs.rainfall.core.Queueable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		if (appearance != null) {
			appearance.postProcessing(manager, pass);
		}
		if (icon != null) {
			icon.postProcessing(manager, pass);
		}
		if (sfx != null) {
			sfx.postProcessing(manager, pass);
		}
	}

	/**
	 * @see net.wombatrpgs.rainfall.characters.moveset.Actionable#act
	 * (net.wombatrpgs.rainfall.maps.Level, net.wombatrpgs.rainfall.characters.CharacterEvent)
	 * Same as that thing 
	 */
	public abstract void coreAct(Level map, CharacterEvent actor);
	
	/**
	 * Called when the event is pre-emptively interrupted by the hero. This only
	 * really applies to events that are active for a period of time.
	 */
	public void cancel() {
		actor.stopAction(this);
	}
	
	/**
	 * Gets the icon displayed for this icon. To be displayed when this move is
	 * bound to a command. Can be null
	 * @return					The icon of this move, or null if none
	 */
	public Graphic getIcon() {
		return icon;
	}
	
	/**
	 * Shortcut for subclasses to act themselves.
	 * @param 	map				The map the action takes place on
	 */
	protected final void act(Level map) {
		act(map, this.actor);
	}

}
