/**
 *  Decorator3x3.java
 *  Created on Oct 13, 2013 6:28:32 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.maps.gen.dec;

import com.badlogic.gdx.assets.AssetManager;

import net.wombatrpgs.mrogueschema.maps.decorators.Decorator3x3MDO;
import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.maps.Tile;
import net.wombatrpgs.sdrl2015.maps.gen.MapGenerator;

/**
 * Generates a carpet thing.
 */
public class Decorator3x3 extends DecoratorSingle {
	
	protected Decorator3x3MDO mdo;

	/**
	 * Generates a decorator from data.
	 * @param	mdo				The data to generate from
	 * @param	gen				The generator to generate for
	 */
	public Decorator3x3(Decorator3x3MDO mdo, MapGenerator gen) {
		super(mdo, gen);
		this.mdo = mdo;
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.gen.dec.Decorator#apply
	 * (net.wombatrpgs.mrogue.maps.Tile[][])
	 */
	@Override
	public void apply(Tile[][] tilesOld, Tile[][] tilesNew) {
		this.tilesNew = tilesNew;
		this.tilesNew = tilesNew;
		for (int x = 0; x < gen.getWidth(); x += 1) {
			for (int y = 0; y < gen.getHeight(); y += 1) {
				if (mdo.chance < gen.rand().nextFloat()) continue;
				if (!legal(tilesOld, x+1, y+1)) continue;
				if (!legal(tilesOld, x+1, y)) continue;
				if (!legal(tilesOld, x+1, y-1)) continue;
				if (!legal(tilesOld, x, y-1)) continue;
				if (!legal(tilesOld, x, y)) continue;
				if (!legal(tilesOld, x, y+1)) continue;
				if (!legal(tilesOld, x-1, y+1)) continue;
				if (!legal(tilesOld, x-1, y)) continue;
				if (!legal(tilesOld, x-1, y-1)) continue;
				tilesNew[y+1][x-1] = MGlobal.tiles.getTile(mdo.ul);
				tilesNew[y+1][x] = MGlobal.tiles.getTile(mdo.u);
				tilesNew[y+1][x+1] = MGlobal.tiles.getTile(mdo.ur);
				tilesNew[y][x-1] = MGlobal.tiles.getTile(mdo.l);
				tilesNew[y][x] = MGlobal.tiles.getTile(mdo.c);
				tilesNew[y][x+1] = MGlobal.tiles.getTile(mdo.r);
				tilesNew[y-1][x-1] = MGlobal.tiles.getTile(mdo.bl);
				tilesNew[y-1][x] = MGlobal.tiles.getTile(mdo.b);
				tilesNew[y-1][x+1] = MGlobal.tiles.getTile(mdo.br);
			}
		}
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.maps.gen.dec.Decorator#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		super.queueRequiredAssets(manager);
		MGlobal.tiles.requestTile(manager, mdo.ul, replace);
		MGlobal.tiles.requestTile(manager, mdo.u, replace);
		MGlobal.tiles.requestTile(manager, mdo.ur, replace);
		MGlobal.tiles.requestTile(manager, mdo.l, replace);
		MGlobal.tiles.requestTile(manager, mdo.c, replace);
		MGlobal.tiles.requestTile(manager, mdo.r, replace);
		MGlobal.tiles.requestTile(manager, mdo.bl, replace);
		MGlobal.tiles.requestTile(manager, mdo.b, replace);
		MGlobal.tiles.requestTile(manager, mdo.br, replace);
	}

}
