/**
 *  Encounter.java
 *  Created on Mar 15, 2015 1:45:38 AM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.enemy;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import net.wombatrpgs.sdrl2015.core.Queueable;
import net.wombatrpgs.sdrl2015.maps.Level;

/**
 * Group representation of hoards, patrols, and singletons.
 */
public abstract class Encounter implements Queueable {
	
	protected List<Queueable> assets;
	
	/**
	 * Creates an empty encounter.
	 */
	public Encounter() {
		assets = new ArrayList<Queueable>();
	}
	
	/**
	 * @see net.wombatrpgs.sdrl2015.core.Queueable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		for (Queueable asset : assets) {
			asset.queueRequiredAssets(manager);
		}
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.core.Queueable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		for (Queueable asset : assets) {
			asset.postProcessing(manager, pass);
		}
	}

	/**
	 * Spawn the encounter on the map.
	 * @param	map				The map to spawn on
	 */
	public abstract void spawn(Level map);

}
