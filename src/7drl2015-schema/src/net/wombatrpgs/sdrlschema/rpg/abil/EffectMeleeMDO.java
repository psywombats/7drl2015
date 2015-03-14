/**
 *  EffectArmorPierceMDO.java
 *  Created on Mar 10, 2015 10:57:47 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.abil;

import net.wombatrpgs.mgns.core.Annotations.Desc;

/**
 * Average melee attack.
 */
public class EffectMeleeMDO extends AbilityEffectMDO {
	
	@Desc("Damage ratio - 1.0 for normal melee damage, 2.0 for double etc")
	public Float damageRatio;
	
	@Desc("Accuracy - 0-based, this is subtracted from enemy's DV, 100 never misses, -100 always hits")
	public Integer accuracy;

}
