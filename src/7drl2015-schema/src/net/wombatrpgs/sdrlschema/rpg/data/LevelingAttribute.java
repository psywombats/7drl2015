/**
 *  LevelingAttribute.java
 *  Created on Mar 14, 2015 2:19:05 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.data;

/**
 * Common level up effects.
 */
public enum LevelingAttribute {
	
	INCREASE_USES,
	INCREASE_RANGE,
	DECREASE_ENERGY,
	INCREASE_DAMAGE,			// abil effects must implement this
	INCREASE_SECONDARY;			// abil effects must implement this

}
