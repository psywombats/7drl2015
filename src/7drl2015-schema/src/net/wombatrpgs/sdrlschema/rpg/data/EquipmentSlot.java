/**
 *  InventorySlot.java
 *  Created on Mar 8, 2015 3:08:24 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.data;

/**
 * Places where items can be equipped.
 */
public enum EquipmentSlot {
	
	WEAPON		("Weapon"),
	ARMOR		("Armor"),
	RING		("Ring");
	
	private String displayName;
	
	/**
	 * Private enum constructor.
	 */
	private EquipmentSlot(String displayName) {
		this.displayName = displayName;
	}
	
	/** @return The display name of this inventory slot, for use in UI */
	public String getDisplayName() { return displayName; }
	
	/**
	 * Determines the label char of this slot by converting its ordinal.
	 * @return					The label char, starting from a for top slot
	 */
	public char getLabelChar() {
		return (char) (ordinal() + 97);
	}

}
