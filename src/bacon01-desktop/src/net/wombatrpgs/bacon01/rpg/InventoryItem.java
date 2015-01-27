/**
 *  InventoryItem.java
 *  Created on Jan 26, 2015 9:36:51 PM for project bacon01-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.bacon01.rpg;

import net.wombatrpgs.bacon01.graphics.GraphicItem;
import net.wombatrpgs.baconschema.rpg.ItemMDO;
import net.wombatrpgs.baconschema.rpg.data.ItemType;
import net.wombatrpgs.mgne.core.AssetQueuer;
import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.ui.Graphic;

/**
 * Stick one in your inventory.
 */
public class InventoryItem extends AssetQueuer {
	
	protected ItemMDO mdo;
	protected Graphic icon;
	protected GraphicItem graphic;
	protected int quantity;

	/**
	 * Creates a new inventory item from data. Has 1 quantity.
	 * @param	mdo				The data to create from
	 */
	public InventoryItem(ItemMDO mdo) {
		this.mdo = mdo;
		this.icon = new Graphic("res/sprites/", mdo.icon);
		assets.add(icon);
		quantity = 1;
		
		if (mdo.type == ItemType.GRAPHIC) {
			graphic = new GraphicItem(mdo);
			assets.add(graphic);
		}
	}
	
	/**
	 * Create from serialized
	 * @param memory
	 */
	public InventoryItem(ItemMemory memory) {
		this(memory.itemKey);
		this.quantity = memory.quantity;
	}
	
	/**
	 * Creates a new inventory item from data key.
	 * @param	mdoKey			The key of the data to create from
	 */
	public InventoryItem(String mdoKey) {
		this(MGlobal.data.getEntryFor(mdoKey, ItemMDO.class));
	}
	
	/** @return The display name of the item */
	public String getName() { return mdo.name; }
	
	/** @return The internal UID of the item */
	public String getKey() { return mdo.key; }
	
	/** @param delta The amt to change quantity by */
	public void changeQuantity(int delta) { this.quantity += delta; }
	
	/** @return The number of items of this type in the inventory */
	public int getQuantity() { return quantity; }
	
	/** @return The visual representation of this icon */
	public Graphic getIcon() { return icon; }
	
	/** @return The item description, if we're going to be using one */
	public String getDescription() { return mdo.ingameDescription; }
	
	/**
	 * This method is going to be a mess... could it be a giant switch statement
	 * based on type? That would be horrible.
	 */
	public void onUse() {
		MGlobal.reporter.inform("Used the " + getName());
		switch(mdo.type) {
		case GRAPHIC:
			graphic.show();
			return;
		case RADIO:
			MGlobal.ui.getBlockingBox().blockText(MGlobal.screens.peek(), "Stop calling me on the radio dimwit");
			return;
		}
	}
}