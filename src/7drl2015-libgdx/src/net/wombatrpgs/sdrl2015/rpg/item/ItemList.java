/**
 *  ItemList.java
 *  Created on Mar 14, 2015 2:53:25 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.item;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrlschema.rpg.ItemListMDO;
import net.wombatrpgs.sdrlschema.rpg.ItemMDO;

/**
 * A bunch of items that can be dropped or spawned as a loot table.
 */
public class ItemList {
	
	protected List<ItemMDO> itemMDOs;
	
	/**
	 * Creates a new item list from data.
	 * @param	mdo				The data to create from
	 */
	public ItemList(ItemListMDO mdo) {
		itemMDOs = new ArrayList<ItemMDO>();
		for (String key : mdo.items) {
			itemMDOs.add(MGlobal.data.getEntryFor(key, ItemMDO.class));
		}
	}
	
	/**
	 * Creates a new item list from a data key.
	 * @param	key				The key to a ItemListMDO
	 */
	public ItemList(String key) {
		this(MGlobal.data.getEntryFor(key, ItemListMDO.class));
	}
	
	/**
	 * Generate and return a ready-to-use item.
	 * @return					A ready to go item
	 */
	public Item generateItem() {
		ItemMDO selected = itemMDOs.get(MGlobal.rand.nextInt(itemMDOs.size()));
		Item item = new Item(selected);
		MGlobal.assetManager.loadAsset(item, "new item");
		return item;
	}

}
