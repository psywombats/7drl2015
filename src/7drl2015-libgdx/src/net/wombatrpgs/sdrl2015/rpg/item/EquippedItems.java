/**
 *  EquippedItems.java
 *  Created on Mar 8, 2015 3:28:04 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.item;

import java.util.HashMap;
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
		Item previous = equipment.get(slot);
		equipment.put(slot, item);
		owner.applyStatset(item.getStats(), false);
		owner.getInventory().removeItem(item);
		if (previous != null) {
			owner.getInventory().addItem(previous);
		}
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
			owner.applyStatset(equipped.getStats(), true);
			return true;
		} else {
			return false;
		}
	}

}
