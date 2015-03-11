/**
 *  EffectArmorPierceMDO.java
 *  Created on Mar 10, 2015 10:57:47 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.abil;

import net.wombatrpgs.mgns.core.Annotations.DefaultValue;
import net.wombatrpgs.mgns.core.Annotations.Desc;

/**
 * Armor-piercing effect MDO.
 */
public class EffectArmorPierceMDO extends AbilityEffectMDO {
	
	@Desc("Pierce - percent of armor to pierce, from 0 to 1")
	@DefaultValue("1.0")
	public Float pierce;

}
