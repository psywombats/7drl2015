/**
 *  Effect.java
 *  Created on Apr 18, 2013 11:13:08 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.graphics.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.wombatrpgs.mrogueschema.graphics.effects.data.EffectMDO;
import net.wombatrpgs.sdrl2015.maps.Level;
import net.wombatrpgs.sdrl2015.screen.ScreenObject;

/**
 * A thing that represents a graphical effect on a map such as weather, fog,
 * etc.
 */
public abstract class Effect extends ScreenObject {
	
	protected EffectMDO mdo;
	protected Level parent;
	protected SpriteBatch batch;
	
	/**
	 * Constructs an effect from data.
	 * @param	parent			The parent level
	 * @param	mdo				The data to construct from
	 */
	public Effect(Level parent, EffectMDO mdo) {
		this.mdo = mdo;
		this.parent = parent;
		batch = new SpriteBatch();
	}

}
