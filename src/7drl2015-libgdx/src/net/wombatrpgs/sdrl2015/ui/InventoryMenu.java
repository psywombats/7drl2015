/**
 *  InventoryMenu.java
 *  Created on Oct 21, 2013 1:35:16 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;

import net.wombatrpgs.sdrlschema.maps.data.OrthoDir;

/**
 * 7DRL overhaul of MGNE's tab menu. Expect hacks all over the UI.
 */
public class InventoryMenu extends Popup {
	
	/**
	 * Creates a new inventory menu. Hardcoded to hell and back.
	 */
	public InventoryMenu() {

	}
	
	/** @return True if this inventory menu is up on the screen */
	public boolean isDisplaying() { return active; }

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#show()
	 */
	@Override
	public void show() {
		super.show();
	}

	/**
	 * @see net.wombatrpgs.mrogue.graphics.Renderable#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		
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

	/**
	 * @see net.wombatrpgs.sdrl2015.ui.Popup#cancel()
	 */
	@Override
	protected boolean cancel() {
		// TODO Auto-generated method stub
		return super.cancel();
	}

}
