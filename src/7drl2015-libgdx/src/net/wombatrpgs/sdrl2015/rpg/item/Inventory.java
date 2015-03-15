/**
 *  Inventory.java
 *  Created on Oct 21, 2013 12:48:05 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.item;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;

/**
 * 7DRL: overhauled from the old MRogue version, this one has limited capacity.
 */
public class Inventory {
	
	protected static final int CAPACITY = 9;
	
	protected GameUnit parent;
	protected List<Item> items;
	
	/**
	 * Creates a new inventory for the specified game unit.
	 * @param	parent			The unit to create for
	 */
	public Inventory(GameUnit parent) {
		this.parent = parent;
		items = new ArrayList<Item>();
	}
	
	/** @return The raw item data of this inventory */
	public List<Item> getItems() { return items; }
	
	/** @return The number of items this inventory can hold */
	public int getCapacity()  { return CAPACITY; }
	
	/**
	 * Returns the nth item in the inventory, or null the inventory doesn't have
	 * that many items.
	 * @param	n				The slot in the inventory to check
	 * @return					The item in that slot, or null
	 */
	public Item at(int n) {
		if (n >= items.size()) {
			return null;
		} else {
			return items.get(n);
		}
	}
	
	/**
	 * Throws an item into the inventory. Always check to make sure the
	 * inventory has room first!
	 * @param	item			The item to add
	 */
	public void addItem(Item item) {
		if (isFull()) {
			MGlobal.reporter.warn("Adding to an inventory past capacity: " + item);
		}
		items.add(item);
		parent.grantAbility(item.getCarryAbilityKey(), item);
		parent.grantAbility(item.getEquipAbilityKey(), item);
	}
	
	/**
	 * Removes an item by reference. Does not drop it on the floor or other
	 * clever handling.
	 * @param	item			The item to remove
	 */
	public void removeItem(Item item) {
		if (!items.contains(item)) {
			MGlobal.reporter.warn("Removing non-contained item: " + item);
		}
		items.remove(item);
		parent.revokeAbility(item.getCarryAbilityKey());
	}
	
	/**
	 * Checks if this inventory has room for further items.
	 * @return					True if the inventory is full
	 */
	public boolean isFull() {
		return items.size() >= CAPACITY;
	}

}
