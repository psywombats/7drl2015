/**
 *  UIElement.java
 *  Created on Oct 21, 2013 1:27:45 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.graphics.Graphic;
import net.wombatrpgs.sdrl2015.screen.ScreenObject;

/**
 * Superclass to wrap some common UI functionality. It really shouldn't do much
 * other than manage assets.
 */
public abstract class UIElement extends ScreenObject {
	
	/**
	 * Creates a new UI element on the first z layer above the map.
	 */
	public UIElement() {
		super(1);
	}

	/**
	 * Initializes a graphic from file name and then adds it to assets.
	 * @param 	fileName		The name of the file to load
	 * @return					The created graphic
	 */
	protected Graphic startGraphic(String fileName) {
		Graphic graphic = new Graphic(fileName);
		assets.add(graphic);
		return graphic;
	}

	/**
	 * Gets the batch we should be using to render.
	 * @return					The UI batch to render with
	 */
	protected SpriteBatch getBatch() {
		return MGlobal.screens.peek().getUIBatch();
	}

}
