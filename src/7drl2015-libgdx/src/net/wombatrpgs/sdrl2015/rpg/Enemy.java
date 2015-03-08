/**
 *  Enemy.java
 *  Created on Jan 23, 2013 9:14:38 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg;

import net.wombatrpgs.sdrl2015.maps.Level;
import net.wombatrpgs.sdrl2015.rpg.ai.BTNode;
import net.wombatrpgs.sdrl2015.rpg.ai.IntelligenceFactory;
import net.wombatrpgs.sdrlschema.rpg.stats.Stat;

/**
 * The one and only class for those pesky badniks that hunt down the valiant
 * hero and hinder his quest to save the earth.
 */
public class Enemy extends CharacterEvent {
	
	protected static final String KEY_DEFAULT_BEHAVIOR = "behavior_default";
	
	protected BTNode intelligence;
	
	/**
	 * Creates a new enemy on a map from a database entry.
	 * @param 	mdo				The MDO with data to create from
	 * @param 	parent			The parent map of the object
	 */
	public Enemy(Level parent) {
		// TODO: call the appropriate superconstructor
		this.intelligence = IntelligenceFactory.createIntelligence(KEY_DEFAULT_BEHAVIOR, this);
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.rpg.CharacterEvent#act()
	 */
	@Override
	public void act() {
		if (unit.get(Stat.HP) <= 0) {
			this.ticksRemaining += 100000;
			return;
		} else {
			intelligence.getStatusAndAct();
		}
	}

}
