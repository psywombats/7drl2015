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
import net.wombatrpgs.sdrl2015.core.Queueable;
import net.wombatrpgs.sdrl2015.graphics.Graphic;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrl2015.rpg.act.Action;
import net.wombatrpgs.sdrl2015.rpg.stats.SdrlStats;
import net.wombatrpgs.sdrlschema.rpg.ItemMDO;
import net.wombatrpgs.sdrlschema.rpg.data.EquipmentSlot;

/**
 * Completely and totally gutted for 7DRL. It probably shouldn't extend Action.
 */
public abstract class Item extends Action implements Queueable {
	
	protected ItemMDO mdo;
	
	protected GameUnit owner;
	protected ItemEvent parent;
	protected Graphic icon;
	protected SdrlStats stats;
	
	protected List<Queueable> assets;
	
	/**
	 * Creates a new item from data.
	 * @param	mdo				The data to generate from
	 */
	public Item(ItemMDO mdo) {
		assets = new ArrayList<Queueable>();
		icon = new Graphic(Constants.ITEMS_DIR, mdo.icon);
		assets.add(icon);
		stats = new SdrlStats(mdo.stats);
	}
	
	/** @return The chara that has this item in their inventory */
	public GameUnit getOwner() { return owner; }
	
	/** @return The in-map representation of this item */
	public ItemEvent getEvent() { return parent; }
	
	/** @return The visual for this item */
	public Graphic getIcon() { return icon; }
	
	/** @param event The new in-map representation of this item */
	public void setParent(ItemEvent event) { this.parent = event; }
	
	/** @return The display name of the item */
	public String getName() { return mdo.itemName; }
	
	/** @return The in-game description of the item */
	public String getDescription() { return mdo.itemDescription; }
	
	// TODO
	public boolean isUseable() { return false; }
	
	/** @return The equipment slot of this item, or null for not equippable */
	public EquipmentSlot getSlot() { return mdo.slot; }
	
	/** @return The stats gained by equipping this item */
	public SdrlStats getStats() { return stats; }
	
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
	 * @see net.wombatrpgs.mrogue.rpg.act.Action#act()
	 */
	@Override
	public void act() {
		use();
	}

	
	// TODO: 7DRL: this probably doesn't work right
	/**
	 * This should be called to simulate a character picking up this item. Is
	 * called exlusively from its ItemEvent parent.
	 * @param	unit			The chara picking us up
	 */
	public void onPickup(GameUnit unit) {
		this.owner = unit;
		parent = null;
		setActor(unit.getParent());
		unit.pickUp(this);
	}
	
	/**
	 * Call this when the character opts to spend their turn and use the item.
	 */
	public void use() {
		owner.getInventory().removeItem(this);
		internalUse();
		owner = null;
		parent = null;
		assets.clear();
	}
	
	/**
	 * Called by the item when it's time to apply whatever this item's effect
	 * is. So do your main thing. Inventory is taken care of elsewhere.
	 */
	protected abstract void internalUse();

}
