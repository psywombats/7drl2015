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

	
	// TODO: 7DRL: this probably doesn't work right
	/**
	 * This should be called to simulate a character picking up this item. Is
	 * called exlusively from its ItemEvent parent.
	 * @param	unit			The chara picking us up
	 */
	public void onPickup(GameUnit unit) {
		parent = null;
		unit.pickUp(this);
	}
	
	/**
	 * Call this when the character opts to spend their turn and use the item.
	 */
	public void use() {
		// TODO: use the item
	}

}
