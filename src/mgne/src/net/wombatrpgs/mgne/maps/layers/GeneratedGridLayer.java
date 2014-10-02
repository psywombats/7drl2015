/**
 *  TileLayer.java
 *  Created on Nov 29, 2012 3:51:55 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mgne.maps.layers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.maps.Level;
import net.wombatrpgs.mgne.maps.Tile;
import net.wombatrpgs.mgne.screen.TrackerCam;

/**
 * A grid layer that was generated by algorithm. Uses an internal array of tiles
 * to keep track of passability information.
 */
public class GeneratedGridLayer extends GridLayer {
	
	protected Tile[][] tileData;
	
	/**
	 * Creates a new grid layer with a parent level and z. This is expected for
	 * subclasses that will manipulate their own tile data.
	 */
	public GeneratedGridLayer(Level parent, float z) {
		super(parent, z);
	}
	
	/**
	 * Creates a new object layer with a parent level and group of objects.
	 * Expects to be mutilated and pretty much bossed around by a map generator.
	 * @param 	parent			The parent level of the layer
	 * @param	tileData		The actual info about tiles on this layer
	 * @param	z				The z-depth of this layer
	 */
	public GeneratedGridLayer(Level parent, Tile[][] tileData, float z) {
		this(parent, z);
		this.tileData = tileData;
	}

	
	
	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Renderable#render
	 * (com.badlogic.gdx.graphics.g2d.SpriteBatch)
	 */
	@Override
	public void render(SpriteBatch batch) {
		dumbRender(batch);
	}

	/**
	 * @see net.wombatrpgs.mgne.maps.layers.Layer#isTilePassable(int, int)
	 */
	@Override
	public boolean isTilePassable(int tileX, int tileY) {
		return	(tileX >= 0 && tileX < parent.getWidth()) &&
				(tileY >= 0 && tileY < parent.getHeight()) &&
				(tileData[tileY][tileX] == null || tileData[tileY][tileX].isPassable());
	}

	/**
	 * @see net.wombatrpgs.mgne.maps.layers.GridLayer#hasPropertyAt(int, int, java.lang.String)
	 */
	@Override
	public boolean hasPropertyAt(int tileX, int tileY, String property) {
		// TODO: generated maps: hasPropertyAt
		return false;
	}

	/**
	 * @see net.wombatrpgs.mgne.maps.layers.Layer#hasTileAt(int, int)
	 */
	@Override
	public boolean hasTileAt(int tileX, int tileY) {
		return tileData[tileY][tileX] == null;
	}

	/**
	 * @see net.wombatrpgs.mgne.maps.layers.GridLayer#getTerrainAt(int, int)
	 */
	@Override
	public int getTerrainAt(int tileX, int tileY) {
		return tileData[tileY][tileX].hashCode(); // exceedingly dumb
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		// noop for now?
	}

	/**
	 * Checks if a tile at the given location is see-through. Does not check
	 * for out of bounds.
	 * @param	tileX			The x-coord of the tile to check (in tiles)
	 * @param	tileY			The y-coord of the tile to check (in tiles)
	 * @return					True if tile is transparent, false otherwise
	 */
	public boolean isTransparentAt(int tileX, int tileY) {
		return tileData[tileY][tileX] == null ||
				tileData[tileY][tileX].isTransparent();
	}
	
	/**
	 * Does an extremely inefficient rendering pass.
	 */
	protected void dumbRender(SpriteBatch batch) {
		TrackerCam cam  = MGlobal.levelManager.getScreen().getCamera();
		int startX = (int) Math.floor((cam.position.x - MGlobal.window.getWidth()/2.f) / parent.getTileWidth());
		int startY = (int) Math.floor((cam.position.y - MGlobal.window.getHeight()/2.f) / parent.getTileHeight());
		int endX = (int) Math.ceil((cam.position.x + MGlobal.window.getWidth()/2.f) / parent.getTileWidth());
		int endY = (int) Math.ceil((cam.position.y + MGlobal.window.getHeight()/2.f) / parent.getTileHeight());
		if (startX < 0) startX = 0;
		if (startY < 0) startY = 0;
		if (endX > parent.getWidth()) endX = parent.getWidth();
		if (endY > parent.getHeight()) endY = parent.getHeight();
		Color old = batch.getColor().cpy();
		Color trans = batch.getColor().cpy();
		trans.a = .5f;
		parent.getBatch().begin();
		for (int x = startX; x < endX; x += 1) {
			for (int y = startY; y < endY; y += 1) {
				float atX = parent.getTileWidth() * x;
				float atY = parent.getTileHeight() * y;
				if (tileData[y][x] != null) {
					tileData[y][x].renderLocal(cam, batch, atX, atY);
				}
			}
		}
		parent.getBatch().end();
		parent.getBatch().setColor(old);
	}
}
