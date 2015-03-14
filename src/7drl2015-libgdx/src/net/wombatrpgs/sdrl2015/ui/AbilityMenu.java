/**
 *  AbilityMenu.java
 *  Created on Mar 12, 2015 1:28:08 AM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.ui;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.graphics.Graphic;
import net.wombatrpgs.sdrl2015.rpg.abil.Ability;
import net.wombatrpgs.sdrl2015.screen.WindowSettings;
import net.wombatrpgs.sdrl2015.ui.text.FontHolder;
import net.wombatrpgs.sdrl2015.ui.text.TextBoxFormat;
import net.wombatrpgs.sdrlschema.io.data.InputCommand;
import net.wombatrpgs.sdrlschema.maps.data.OrthoDir;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;

/**
 * Ability tab menu crap.
 */
public class AbilityMenu extends Popup {
	
	protected static final String BACKER_FILE = "black.png";
	protected static final String CURSOR_FILE = "finger.png";
	protected static final String SELECTION_DIALOG_FILE = "dialog_2.png";
	
	protected static final String BLANK_STRING = "--";
	protected static final String HINT_STRING = "TAB for items";
	protected static final String TITLE_STRING = "SKILL LIST";
	protected static final String LEVEL_STRING = "SELECT SKILL TO IMPROVE";
	
	protected static final int ABIL_WIDTH = 500;
	protected static final int ABIL_NAME_WIDTH = 210;
	protected static final int ABIL_ALIGN = 100;
	protected static final int ABIL_MARGIN = 80;
	protected static final int LINE_HEIGHT = 34;
	
	protected Graphic backer, cursor;
	protected TextBoxFormat descFormat, abilFormat, abilNameFormat, tabHintFormat;
	protected boolean levelMode;
	protected int selected;
	
	/**
	 * Creates a new inventory menu. Hardcoded to hell and back.
	 * @param	levelMode		True to ask player to level abil
	 */
	public AbilityMenu(boolean levelMode) {
		this.levelMode = levelMode;
		
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
		descFormat.y = win.getHeight() * 4 / 5;
		
		abilFormat = new TextBoxFormat();
		abilFormat.align = HAlignment.LEFT;
		abilFormat.width = ABIL_WIDTH;;
		abilFormat.height = 100;
		abilFormat.x = win.getWidth() / 2 - ABIL_ALIGN;
		abilFormat.y = win.getHeight() * 3 / 5;
		
		abilNameFormat = new TextBoxFormat();
		abilNameFormat.align = HAlignment.RIGHT;
		abilNameFormat.width = ABIL_NAME_WIDTH;
		abilNameFormat.height = 100;
		abilNameFormat.x = abilFormat.x - ABIL_NAME_WIDTH - ABIL_MARGIN;
		abilNameFormat.y = win.getHeight() * 3 / 5;
		
		tabHintFormat = new TextBoxFormat();
		tabHintFormat.align = HAlignment.CENTER;
		tabHintFormat.width = 100;
		tabHintFormat.height = 50;
		tabHintFormat.x = MGlobal.window.getWidth() - tabHintFormat.width;
		tabHintFormat.y = tabHintFormat.height;
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
		selected = 0;
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
		
		for (int i = 0; i < 10; i += 1) {
			int offY = i * -LINE_HEIGHT;
			Ability abil = MGlobal.hero.getUnit().abilityAt(i);
			if (abil != null) {
				String name = abil.getName();
				name = name + " (L" + MGlobal.hero.getUnit().getAbilityLevel(abil.getKey()) + ")";
				font.draw(getBatch(), abilNameFormat, name, offY);
				font.draw(getBatch(), abilFormat, abil.getDescription(), offY);
				abil.getIcon().renderAt(getBatch(),
						abilFormat.x - abil.getIcon().getWidth() - 8,
						abilFormat.y - abil.getIcon().getHeight()/2 - 
						cursor.getHeight()/2);
			} else {
				font.draw(getBatch(), abilNameFormat, BLANK_STRING, offY);
			}
		}
		
		cursor.renderAt(getBatch(),
				abilNameFormat.x,
				abilNameFormat.y - selected * LINE_HEIGHT - LINE_HEIGHT/2);
		
		Ability selectedAbil = MGlobal.hero.getUnit().abilityAt(selected);
		if (levelMode) {
			font.draw(getBatch(), descFormat, LEVEL_STRING, LINE_HEIGHT/2);
			font.draw(getBatch(), descFormat, selectedAbil.getLevelText(), -LINE_HEIGHT/2);
		} else {
			font.draw(getBatch(), descFormat, TITLE_STRING, LINE_HEIGHT/2);
			font.draw(getBatch(), tabHintFormat, HINT_STRING, 0);
		}
		String desc = (selectedAbil == null) ? "" : selectedAbil.getDescription();
		font.draw(getBatch(), descFormat, desc, 0);
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.ui.Popup#onCommand
	 * (net.wombatrpgs.sdrlschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		if (!levelMode) {
			switch (command) {
			case INTENT_TAB:
				tabToAbils();
				return true;
			default:
				return super.onCommand(command);
			}
		} else {
			switch (command) {
			case INTENT_CANCEL:
				return true;
			default:
				return super.onCommand(command);
			}
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#onCursorMove
	 * (net.wombatrpgs.mrogueschema.maps.data.OrthoDir)
	 */
	@Override
	protected boolean onCursorMove(OrthoDir dir) {
		switch (dir) {
		case NORTH:
			selected -= 1;
			break;
		case SOUTH:
			selected += 1;
			break;
		default:
			break;
		}
		int max = MGlobal.hero.getUnit().getAbilities().size() - 1;
		if (selected > max) selected = max;
		if (selected < 0) selected = 0;
		return true;
	}

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#confirm()
	 */
	@Override
	protected boolean confirm() {
		if (!levelMode) return true;
		Ability selectedAbil = MGlobal.hero.getUnit().abilityAt(selected);
		if (selectedAbil == null) return true;
		MGlobal.hero.getUnit().increaseAbilityLevel(selectedAbil.getKey());
		levelMode = false;
		return true;
	}
	
	/**
	 * Tab from the inventory menu to the ability menu.
	 */
	protected void tabToAbils() {
		InventoryMenu itemsMenu = new InventoryMenu();
		MGlobal.assetManager.loadAsset(itemsMenu, "items screen");
		itemsMenu.show();
		hide();
	}
}
