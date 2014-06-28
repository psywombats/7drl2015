/**
 *  Inventory.java
 *  Created on Apr 12, 2014 3:00:32 AM for project saga-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.rpg.items;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.saga.rpg.chara.Chara;
import net.wombatrpgs.sagaschema.rpg.chara.CharaMDO;
import net.wombatrpgs.sagaschema.rpg.chara.data.Race;

/**
 * A collection of combat items worn by a character.
 */
public class CharaInventory extends Inventory {
	
	protected static final int SLOT_COUNT = 8;
	
	protected Chara chara;
	
	/**
	 * Creates a new starter inventory suitable for the given character data.
	 * @param	mdo				The data to create from
	 */
	public CharaInventory(CharaMDO mdo, Chara chara) {
		super(SLOT_COUNT);
		this.chara = chara;
		for (int i = 0; i < mdo.equipped.length; i += 1) {
			String key = mdo.equipped[i];
			CombatItem item = new CombatItem(key);
			item.onAddedTo(this);
			set(i, item);
		}
	}

	/**
	 * @see net.wombatrpgs.saga.rpg.items.Inventory#set
	 * (int, net.wombatrpgs.saga.rpg.items.CombatItem)
	 */
	@Override
	public CombatItem set(int slot, CombatItem item) {
		if (item != null) {
			chara.onEquip(item);
		}
		CombatItem old = super.set(slot, item);
		if (old != null) {
			chara.onUnequip(old);
		}
		return old;
	}

	/**
	 * @see net.wombatrpgs.saga.rpg.items.Inventory#reservedAt(int)
	 */
	@Override
	public boolean reservedAt(int slot) {
		switch (chara.getRace()) {
		case HUMAN: case ROBOT: case MONSTER:
			return false;
		case MUTANT:
			return slot < 4;
		default:
			MGlobal.reporter.warn("Unknown race " + chara.getRace());
			return false;
		}
	}
	
	/**
	 * Restores any abilities in this inventory to top form.
	 */
	public void restoreAbilUses() {
		switch (chara.getRace()) {
		case HUMAN:
			// losers
			break;
		case MONSTER: case ROBOT:
			for (int i = 0; i < SLOT_COUNT; i += 1) {
				restoreAt(i);
			}
			break;
		case MUTANT:
			for (int i = 0; i < 4; i += 1) {
				restoreAt(i);
			}
			break;
		}
	}
	
	/**
	 * Checks if the given slot can have an item stored in it.
	 * @param	slot			The slot to check
	 * @return					True if that slot can have an item, else false
	 */
	public boolean equippableAt(int slot) {
		if (reservedAt(slot)) {
			return false;
		} else {
			return chara.getRace() != Race.MONSTER;
		}
	}
	
	/**
	 * Restores the ability at a given slot to max uses. Does nothing if no item
	 * at that location.
	 * @param	slot			The slot to restore at
	 */
	protected void restoreAt(int slot) {
		CombatItem item = get(slot);
		if (item == null) return;
		item.restoreUses();
	}

}