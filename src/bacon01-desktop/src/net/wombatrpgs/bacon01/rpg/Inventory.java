/**
 *  Inventory.java
 *  Created on Jan 26, 2015 9:36:27 PM for project bacon01-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.bacon01.rpg;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.baconschema.rpg.ItemMDO;
import net.wombatrpgs.baconschema.rpg.data.ItemType;
import net.wombatrpgs.mgne.core.AssetQueuer;
import net.wombatrpgs.mgne.core.MGlobal;

/**
 * Contains items for the hero!
 */
public class Inventory extends AssetQueuer {
	
	protected List<InventoryItem> items;
	
	/**
	 * Creates a new emtpy inventory.
	 */
	public Inventory() {
		this.items = new ArrayList<InventoryItem>();
	}
	
	/**
	 * Create from serialized
	 * @param memories
	 */
	public Inventory(List<ItemMemory> memories) {
		this();
		for (ItemMemory memory : memories) {
			items.add(new InventoryItem(memory));
		}
		assets.addAll(items);
	}
	
	/**
	 * Picks up an item with the given mdo.
	 * @param	mdoKey		The key of the item to pick up.
	 */
	public void pickUp(String mdoKey) {
		for (InventoryItem item : items) {
			if (item.getKey().equals(mdoKey)) {
				item.changeQuantity(1);
				return;
			}
		}
		InventoryItem item = new InventoryItem(mdoKey);
		MGlobal.assets.loadAsset(item, mdoKey);
		items.add(item);
	}
	
	public void pickUp(ItemMDO mdo) {
		for (InventoryItem item : items) {
			if (item.getKey().equals(mdo.key)) {
				item.changeQuantity(1);
				return;
			}
		}
		InventoryItem item = new InventoryItem(mdo);
		MGlobal.assets.loadAsset(item, mdo.key);
		items.add(item);
	}
	
	public int size() {
		return items.size();
	}
	
	public int count() {
		int count = 0;
		for (InventoryItem item : items) {
			count += item.getQuantity();
		}
		return count;
	}
	
	public InventoryItem at(int n) {
		return items.get(n);
	}
	
	public List<ItemMemory> toMemory() {
		List<ItemMemory> memories = new ArrayList<ItemMemory>();
		for (InventoryItem item : items) {
			memories.add(new ItemMemory(item));
		}
		return memories;
	}
	
	public boolean contains(String itemKey) {
		for (InventoryItem item : items) {
			if (item.getKey().equals(itemKey)) {
				return true;
			}
		}
		return false;
	}
	
	public int countPages() {
		int count = 0;
		for (InventoryItem item : items) {
			if (item.mdo.itemType == ItemType.GRAPHIC) {
				count += item.getQuantity();
			}
		}
		return count;
	}

}
