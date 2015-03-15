/**
 *  Item.java
 *  Created on Oct 20, 2013 6:41:32 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.item;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import net.wombatrpgs.sdrl2015.core.Constants;
import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.core.Queueable;
import net.wombatrpgs.sdrl2015.graphics.Graphic;
import net.wombatrpgs.sdrl2015.maps.Level;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrl2015.rpg.stats.SdrlStats;
import net.wombatrpgs.sdrlschema.rpg.ItemMDO;
import net.wombatrpgs.sdrlschema.rpg.data.EquipmentSlot;

/**
 * Completely and totally gutted for 7DRL. It probably shouldn't extend Action.
 */
public class Item implements Queueable {
	
	protected ItemMDO mdo;
	
	protected ItemEvent parent;
	protected Graphic icon;
	protected SdrlStats stats;
	
	protected List<Queueable> assets;
	
	/**
	 * Creates a new item from data.
	 * @param	mdo				The data to generate from
	 */
	public Item(ItemMDO mdo) {
		this.mdo = mdo;
		assets = new ArrayList<Queueable>();
		icon = new Graphic(Constants.ITEMS_DIR, mdo.icon);
		assets.add(icon);
		stats = new SdrlStats(mdo.stats);
	}
	
	/** @return The visual for this item */
	public Graphic getIcon() { return icon; }
	
	/** @param event The new in-map representation of this item */
	public void setParent(ItemEvent event) { this.parent = event; }
	
	/** @return The display name of the item */
	public String getName() { return mdo.itemName; }
	
	/** @return The in-game description of the item */
	public String getDescription() { return mdo.itemDescription; }
	
	/** @return The number of times this item's ability can be used, 0 for no limit */
	public int getUses() { return mdo.uses != null ? mdo.uses : 0; }
	
	/** @return True if the item can be equipped */
	public boolean isEquippable() { return getSlot() != null; }
	
	/** @return The equipment slot of this item, or null for not equippable */
	public EquipmentSlot getSlot() { return mdo.slot; }
	
	/** @return The stats gained by equipping this item */
	public SdrlStats getStats() { return stats; }
	
	/** @return The key of the ability granted by equipping this item */
	public String getEquipAbilityKey() { return mdo.equippedAbility; }
	
	/** @return The key of the ability granted by carrying this item */
	public String getCarryAbilityKey() { return mdo.carriedAbility; }
	
	/**
	 * @see net.wombatrpgs.mrogue.core.Queueable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		for (Queueable asset : assets) {
			asset.queueRequiredAssets(manager);
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.core.Queueable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		for (Queueable asset : assets) {
			asset.postProcessing(manager, pass);
		}
	}
	
	/**
	 * This should be called to simulate a character picking up this item. Is
	 * called exlusively from its ItemEvent parent.
	 * @param	unit			The chara picking us up
	 */
	public void onPickup(GameUnit unit) {
		unit.pickUp(this);
	}
	
	/**
	 * Called when a character unit drops this item.
	 * @param	unit			The unit dropping the item
	 */
	public void onDrop(GameUnit unit) {
		if (parent == null) {
			parent = new ItemEvent(unit.getParent().getParent(),
					this,
					unit.getParent().getTileX(),
					unit.getParent().getTileY());
			MGlobal.assetManager.loadAsset(parent, this.toString());
		}
		unit.getParent().getParent().addEvent(parent,
				unit.getParent().getTileX(),
				unit.getParent().getTileY());
	}
	
	/**
	 * Spawns this item unseen on the map.
	 * @param	map				The map to spawn on
	 */
	public void spawnUnseen(Level map) {
		if (parent == null) {
			parent = new ItemEvent(null, this, 0, 0);
			MGlobal.assetManager.loadAsset(parent, "new item parent");
		}
		if (map != parent.getParent()) {
			if (parent.getParent() != null) {
				parent.getParent().removeEvent(parent);
			}
		}
		parent.spawnUnseen(map);
	}
	
	/**
	 * Spawns this item on the map in the vicinity of the given location.
	 * @param	map				The parent map to spawn on
	 * @param	tileX			The x-coord to spawn nearby
	 * @param	tileY			The y-coord to spawn nearby
	 * @param	radius			The radius to deviate from the given location
	 */
	public void spawnNear(Level map, int tileX, int tileY, int radius) {
		if (parent == null) {
			parent = new ItemEvent(null, this, 0, 0);
			MGlobal.assetManager.loadAsset(parent, "new item parent");
		}
		if (map != parent.getParent()) {
			if (parent.getParent() != null) {
				parent.getParent().removeEvent(parent);
			}
			map.addEvent(parent);
		}
		parent.setTileX(0);
		parent.setTileY(0);
		while(!map.isTilePassable(parent, parent.getTileX(), parent.getTileY())) {
			parent.setTileX(tileX + MGlobal.rand.nextInt(radius*2) - radius);
			parent.setTileY(tileY + MGlobal.rand.nextInt(radius*2) - radius);
		}
		parent.setX(parent.getTileX() * map.getTileWidth());
		parent.setY(parent.getTileY() * map.getTileHeight());
	}

}
