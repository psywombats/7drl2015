/**
 *  InventoryMenu.java
 *  Created on Oct 21, 2013 1:35:16 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.graphics.Graphic;
import net.wombatrpgs.sdrl2015.rpg.item.Inventory;
import net.wombatrpgs.sdrl2015.rpg.item.Item;
import net.wombatrpgs.sdrl2015.screen.WindowSettings;
import net.wombatrpgs.sdrl2015.ui.text.FontHolder;
import net.wombatrpgs.sdrl2015.ui.text.TextBoxFormat;
import net.wombatrpgs.sdrlschema.maps.data.OrthoDir;
import net.wombatrpgs.sdrlschema.rpg.data.EquipmentSlot;

/**
 * 7DRL overhaul of MGNE's tab menu. Expect hacks all over the UI.
 */
public class InventoryMenu extends Popup {
	
	protected static final String BACKER_FILE = "black.png";
	
	protected static final String BLANK_STRING = "--";
	protected static final String EQUIP_DESC = "EQUIPMENT";
	protected static final String INVENTORY_DESC = "INVENTORY";
	
	protected static final int EQUIP_WIDTH = 300;
	protected static final int SLOT_NAME_WIDTH = 120;
	protected static final int INVENTORY_HORIZ_MARGIN = 20;
	protected static final int LINE_HEIGHT = 16;
	
	protected Graphic backer;
	protected TextBoxFormat slotFormat, descFormat, equipFormat, inventoryFormat;
	
	/**
	 * Creates a new inventory menu. Hardcoded to hell and back.
	 */
	public InventoryMenu() {
		backer = new Graphic(BACKER_FILE);
		assets.add(backer);
		
		WindowSettings win = MGlobal.window;
		
		descFormat = new TextBoxFormat();
		descFormat.align = HAlignment.CENTER;
		descFormat.width = win.getWidth() / 2;
		descFormat.height = 100;
		descFormat.x = win.getWidth() / 2 - descFormat.width / 2;
		descFormat.y = win.getHeight() * 3 / 4;
		
		slotFormat = new TextBoxFormat();
		slotFormat.align = HAlignment.RIGHT;
		slotFormat.width = 100;
		slotFormat.height = 50;
		slotFormat.x = win.getWidth() / 2 - SLOT_NAME_WIDTH - EQUIP_WIDTH;
		slotFormat.y = win.getHeight() * 2 / 3;
		
		equipFormat = new TextBoxFormat();
		equipFormat.align = HAlignment.LEFT;
		equipFormat.width = EQUIP_WIDTH;
		equipFormat.height = 50;
		equipFormat.x = win.getWidth() / 2 - EQUIP_WIDTH;
		equipFormat.y = win.getHeight() * 2 / 3;
		
		inventoryFormat = new TextBoxFormat();
		inventoryFormat.align = HAlignment.LEFT;
		inventoryFormat.width = 500;
		inventoryFormat.height = 50;
		inventoryFormat.x = win.getWidth() / 2 + INVENTORY_HORIZ_MARGIN;
		inventoryFormat.y = win.getHeight() * 2 / 3;
	}
	
	/** @return True if this inventory menu is up on the screen */
	public boolean isDisplaying() { return active; }

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#show()
	 */
	@Override
	public void show() {
		super.show();
		MGlobal.screens.peek().addObject(this);
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.ui.Popup#hide()
	 */
	@Override
	public void hide() {
		super.hide();
		MGlobal.screens.peek().addObject(this);
	}

	/**
	 * @see net.wombatrpgs.mrogue.graphics.Renderable#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		backer.renderAt(getBatch(), 0, 0);
		
		FontHolder font = MGlobal.ui.getFont();
		
		font.draw(getBatch(), descFormat, "TEST DESC", 0);
		
		for (int i = 0; i < EquipmentSlot.values().length + 2; i += 1) {
			int offY = LINE_HEIGHT * -i;
			if (i == 0) {
				font.draw(getBatch(), equipFormat, EQUIP_DESC, offY);
			} else if (i >= 2) {
				int slotNo = i - 2;
				EquipmentSlot slot = EquipmentSlot.values()[slotNo];
				font.draw(getBatch(), slotFormat, slot.getDisplayName()+":", offY);
				Item equipped = MGlobal.hero.getUnit().getEquipment().at(slot);
				String equipName = (equipped == null) ? BLANK_STRING : equipped.getName();
				font.draw(getBatch(), equipFormat, equipName, offY);
			}
		}
		
		Inventory inventory = MGlobal.hero.getUnit().getInventory();
		for (int i = 0; i < inventory.getCapacity() + 2; i += 1) {
			int offY = LINE_HEIGHT * -i;
			if (i == 0) {
				font.draw(getBatch(), inventoryFormat, INVENTORY_DESC, offY);
			} else if (i >= 2) {
				int slotNo = i - 2;
				Item item = inventory.at(slotNo);
				String itemText = (item == null) ? BLANK_STRING : item.getName();
				font.draw(getBatch(), inventoryFormat, itemText, offY);
			}
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#onCursorMove
	 * (net.wombatrpgs.mrogueschema.maps.data.OrthoDir)
	 */
	@Override
	protected boolean onCursorMove(OrthoDir dir) {
		return true;
	}

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#confirm()
	 */
	@Override
	protected boolean confirm() {
		return true;
	}

}
