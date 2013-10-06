/**
 *  TileLayer.java
 *  Created on Nov 29, 2012 3:51:55 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mrogue.maps.layers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.wombatrpgs.mrogue.maps.Level;
import net.wombatrpgs.mrogue.maps.Tile;
import net.wombatrpgs.mrogue.maps.events.MapEvent;

/**
 * A layer of tiles that is part of a level. It's named "grid" so as to not
 * conflict with the stubby libgdx idea of a TiledLayer which isn't a layer at
 * all, really.
 */
public class GridLayer extends Layer {
	
	protected Level parent;
	protected boolean isLower;
	protected Tile[][] tileData;
	protected float z;
	
	/**
	 * Creates a new object layer with a parent level and group of objects.
	 * Expects to be mutilated and pretty much bossed around by a map generator.
	 * @param 	parent			The parent level of the layer
	 * @param	tileData		The actual info about tiles on this layer
	 * @param	z				The z-depth of this layer
	 */
	public GridLayer(Level parent, Tile[][] tileData, float z) {
		this.z = z;
		this.parent = parent;
		this.tileData = tileData;
	}
	
	/**
	 * Gets the z-value of the layer. Layers with the same z-value share
	 * collisions and collision detection. 0 represents the floor, and each
	 * subsequent integer is another floor.
	 * @return					The z-value (depth) of this layer
	 */
	public float getZ() {
		return z;
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.layers.Layer#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		dumbRender(camera);
	}

	/**
	 * @see net.wombatrpgs.mrogue.graphics.Renderable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		// TODO: queueRequiredAssets
	}

	/**
	 * @see net.wombatrpgs.mrogue.graphics.Renderable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		// TODO: postProcessing
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.maps.layers.Layer#isLowerChip()
	 */
	@Override
	public boolean isLowerChip() {
		return isLower;
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.layers.Layer#isPassable(MapEvent, int, int)
	 */
	@Override
	public boolean isPassable(MapEvent actor, final int x, final int y) {
		return	(x >= 0 && x < parent.getWidth()) &&
				(y >= 0 && y < parent.getHeight()) &&
				tileData[y][x].isPassable();
	}
	
	/**
	 * Does an extremely inefficient rendering pass.
	 * @param	cam				The camera to render with
	 */
	protected void dumbRender(OrthographicCamera camera) {
		parent.getBatch().begin();
		for (int x = 0; x < parent.getWidth(); x += 1) {
			for (int y = 0; y < parent.getHeight(); y += 1) {
				float atX = parent.getTileWidth() * x;
				float atY = parent.getTileHeight() * y;
				tileData[y][x].renderLocal(camera, parent.getBatch(), atX, atY);
			}
		}
		parent.getBatch().end();
	}
}