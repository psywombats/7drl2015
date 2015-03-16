/**
 *  InventoryMenu.java
 *  Created on Oct 21, 2013 1:35:16 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.graphics.Graphic;
import net.wombatrpgs.sdrl2015.rpg.abil.Ability;
import net.wombatrpgs.sdrl2015.rpg.item.EquippedItems;
import net.wombatrpgs.sdrl2015.rpg.item.Inventory;
import net.wombatrpgs.sdrl2015.rpg.item.Item;
import net.wombatrpgs.sdrl2015.screen.WindowSettings;
import net.wombatrpgs.sdrl2015.ui.text.FontHolder;
import net.wombatrpgs.sdrl2015.ui.text.TextBoxFormat;
import net.wombatrpgs.sdrlschema.io.data.InputCommand;
import net.wombatrpgs.sdrlschema.maps.data.OrthoDir;
import net.wombatrpgs.sdrlschema.rpg.data.EquipmentSlot;

/**
 * 7DRL overhaul of MGNE's tab menu. Expect hacks all over the UI.
 */
public class InventoryMenu extends Popup {
	
	protected static final String BACKER_FILE = "black.png";
	protected static final String CURSOR_FILE = "finger.png";
	protected static final String SELECTION_DIALOG_FILE = "dialog_2.png";
	
	protected static final String BLANK_STRING = "--";
	protected static final String HINT_STRING = "TAB for skills";
	protected static final String EQUIP_DESC = "EQUIPMENT";
	protected static final String INVENTORY_DESC = "INVENTORY";
	
	protected static final String OPTION_EQUIP = "Equip";
	protected static final String OPTION_USE = "Use";
	protected static final String OPTION_DROP = "Drop";
	
	protected static final int EQUIP_WIDTH = 200;
	protected static final int SLOT_NAME_WIDTH = 120;
	protected static final int INVENTORY_HORIZ_MARGIN = 20;
	protected static final int LINE_HEIGHT = 16;
	
	protected Graphic backer, cursor;
	protected TextBoxFormat slotFormat, descFormat, equipFormat,
			inventoryFormat, tabHintFormat;
	protected List<String> optionStrings;
	protected int selected;
	protected boolean equipped;
	protected boolean equippedSide;
	protected boolean asking;
	
	/**
	 * Creates a new inventory menu. Hardcoded to hell and back.
	 */
	public InventoryMenu() {
		
		backer = new Graphic(BACKER_FILE);
		assets.add(backer);
		
		cursor = new Graphic(CURSOR_FILE);
		assets.add(cursor);
		
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
		
		tabHintFormat = new TextBoxFormat();
		tabHintFormat.align = HAlignment.CENTER;
		tabHintFormat.width = 100;
		tabHintFormat.height = 50;
		tabHintFormat.x = MGlobal.window.getWidth() - tabHintFormat.width;
		tabHintFormat.y = tabHintFormat.height;
	}
	
	/** @return True if this inventory menu is up on the screen */
	public boolean isDisplaying() { return active; }
	
	/** @return True if an item was equipped this showing */
	public boolean wasEquipped() { return equipped; }
	
	/** acknowledge the equip state */
	public void acknowledge() { equipped = false; }

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#show()
	 */
	@Override
	public void show() {
		super.show();
		equipped = false;
		MGlobal.screens.peek().addObject(this);
		equippedSide = false;
		selected = 2;
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.ui.Popup#hide()
	 */
	@Override
	public void hide() {
		super.hide();
		MGlobal.screens.peek().removeObject(this);
	}

	/**
	 * @see net.wombatrpgs.mrogue.graphics.Renderable#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		backer.renderAt(getBatch(), 0, 0);
		
		FontHolder font = MGlobal.ui.getFont();
		
		Item selectedItem;
		int index = selected-2;
		if (equippedSide) {
			EquipmentSlot slot = EquipmentSlot.values()[index];
			selectedItem = MGlobal.hero.getUnit().getEquipment().at(slot);
		} else {
			selectedItem = MGlobal.hero.getUnit().getInventory().at(index);
		}
		String desc = (selectedItem == null) ? "" : selectedItem.getDescription();
		font.draw(getBatch(), descFormat, desc, 0);
		
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
		
		if (equippedSide) {
			cursor.renderAt(getBatch(),
					slotFormat.x + 12,
					slotFormat.y - selected * LINE_HEIGHT - LINE_HEIGHT/2 - cursor.getHeight()/2);
		} else {
			cursor.renderAt(getBatch(),
					inventoryFormat.x - 12,
					inventoryFormat.y - selected * LINE_HEIGHT - LINE_HEIGHT/2 -
					cursor.getHeight()/2);
		}
		
		font.draw(getBatch(), tabHintFormat, HINT_STRING, 0);
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.ui.Popup#onCommand
	 * (net.wombatrpgs.sdrlschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		switch (command) {
		case INTENT_TAB:
			tabToAbils();
			return true;
		default:
			return super.onCommand(command);
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#onCursorMove
	 * (net.wombatrpgs.mrogueschema.maps.data.OrthoDir)
	 */
	@Override
	protected boolean onCursorMove(OrthoDir dir) {
		switch (dir) {
		case EAST: case WEST:
			equippedSide = !equippedSide;
			break;
		case NORTH:
			selected -= 1;
			break;
		case SOUTH:
			selected += 1;
			break;
		}
		if (selected < 2) selected = 2;
		if (equippedSide) {
			if (selected >= EquipmentSlot.values().length+1) {
				selected = EquipmentSlot.values().length+1;
			}
		} else {
			if (selected >= MGlobal.hero.getUnit().getInventory().getCapacity()+1) {
				selected = MGlobal.hero.getUnit().getInventory().getCapacity()+1;
			}
		}
		return true;
	}

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#confirm()
	 */
	@Override
	protected boolean confirm() {
		if (asking) return true;
		final Inventory inventory = MGlobal.hero.getUnit().getInventory();
		final EquippedItems equipment = MGlobal.hero.getUnit().getEquipment();
		if (equippedSide) {
			EquipmentSlot slot = EquipmentSlot.values()[selected-2];
			if (equipment.at(slot) != null) {
				equipment.unequip(slot);
			}
		} else {
			final Item item = inventory.at(selected-2);
			if (item != null) {
				int selectX = inventoryFormat.x;
				int selectY = inventoryFormat.y + LINE_HEIGHT * -selected;
				asking = true;
				
				optionStrings = new ArrayList<String>();
				if (item.isEquippable()) optionStrings.add(OPTION_EQUIP);
				if (item.hasAbility()) optionStrings.add(OPTION_USE);
				optionStrings.add(OPTION_DROP);
				SelectionDialog dialog = new SelectionDialog(SELECTION_DIALOG_FILE,
						optionStrings.toArray(new String[optionStrings.size()]));
				MGlobal.assetManager.loadAsset(dialog, "selection dialog");
				
				dialog.ask(new SelectionListener() {
					@Override public void onResult(int selection) {
						String option = optionStrings.get(selection);
						switch (option) {
						case OPTION_USE:
							String carryKey = item.getCarryAbilityKey();
							for (Ability abil : MGlobal.hero.getUnit().getAbilities()) {
								if (abil.getKey().equals(carryKey)) {
									hide();
									MGlobal.hero.useAbility(abil);
									asking = false;
									return;
								}
							}
							String equipKey = item.getEquipAbilityKey();
							for (Ability abil : MGlobal.hero.getUnit().getAbilities()) {
								if (abil.getKey().equals(equipKey)) {
									hide();
									MGlobal.hero.useAbility(abil);
									asking = false;
									return;
								}
							}
							MGlobal.reporter.warn("Hero doesn't know abil from "
									+ "abil-granting item " + item.getName());
							break;
						case OPTION_DROP:
							inventory.removeItem(item);
							item.onDrop(MGlobal.hero.getUnit());
							break;
						case OPTION_EQUIP:
							if (item.isEquippable()) {
								equipment.equip(item);
								equipped = true;
							}
							break;
						}
						asking = false;
					}
					@Override public void onCancel() {
						// nothing happens
						asking = false;
					}
				}, selectX, selectY);
			}
		}
		return true;
	}
	
	/**
	 * Tab from the inventory menu to the ability menu.
	 */
	protected void tabToAbils() {
		AbilityMenu abils = new AbilityMenu(false);
		MGlobal.assetManager.loadAsset(abils, "abil screen");
		abils.show();
		hide();
	}

}
