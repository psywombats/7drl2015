/**
 *  EquippedItems.java
 *  Created on Mar 8, 2015 3:28:04 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrlschema.rpg.data.EquipmentSlot;

/**
 * Represents a character's equipped items, alongside its inventory.
 */
public class EquippedItems {
	
	protected GameUnit owner;
	
	protected Map<EquipmentSlot, Item> equipment;
	
	/**
	 * Creates a new set of equipment slots for the given unit.
	 * @param	owner			The owner to make an inventory for
	 */
	public EquippedItems(GameUnit owner) {
		this.owner = owner;
		equipment = new HashMap<EquipmentSlot, Item>();
	}
	
	/**
	 * Returns the equipped item at the given slot.
	 * @param	slot			The slot to check at
	 * @return					The item in that slot, or null if none
	 */
	public Item at(EquipmentSlot slot) {
		return equipment.get(slot);
	}
	
	/**
	 * Equips the supplied item. Assumes the item is equippable. If something
	 * else is already in its equipment slot, that item is removed first. Also
	 * assumes the equipped item exists in the owner's inventory. Applies the
	 * equipped item's stats to the owner.
	 * @param	item			The piece of equipment to equip
	 */
	public void equip(Item item) {
		EquipmentSlot slot = item.getSlot();
		if (slot == null) {
			MGlobal.reporter.err("Equipping non-equipment: " + item);
		}
		owner.grantAbility(item.getEquipAbilityKey(), item);
		owner.getInventory().removeItem(item);
		Item previous = equipment.get(slot);
		if (previous != null) {
			unequip(slot);
		}
		equipment.put(slot, item);
		MGlobal.reporter.inform("APPLYING " + item.getName());
		owner.applyStatset(item.getStats(), false);
	}
	
	/**
	 * Unequips an item from the given inventory slot, if possible. If the owner
	 * has no inventory space, does nothing. If nothing is in that slot, does
	 * nothing.
	 * @param	slot			The slot to unequip from
	 * @return					True if any unequipment actually happened
	 */
	public boolean unequip(EquipmentSlot slot) {
		Item equipped = equipment.get(slot);
		if (equipped != null && !owner.getInventory().isFull()) {
			equipment.put(slot, null);
			owner.getInventory().addItem(equipped);
			MGlobal.reporter.inform("UNAPPLYING " + equipped.getName());
			owner.applyStatset(equipped.getStats(), true);
			owner.revokeAbility(equipped.getEquipAbilityKey());
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns a list of all equipped items. Useful to check if an item exists
	 * or to drop them on the ground or something.
	 * @return					A list of all equipped items
	 */
	public List<Item> getItems() { 
		List<Item> items = new ArrayList<Item>();
		for (Item item : equipment.values()) {
			items.add(item);
		}
		return items;
	}
	
	/**
	 * Checks if a given item is equipped.
	 * @param	item			True if this item is equipped
	 * @return
	 */
	public boolean isEquipped(Item item) {
		if (item.getSlot() == null) return false;
		return at(item.getSlot()) == item;
	}

}
