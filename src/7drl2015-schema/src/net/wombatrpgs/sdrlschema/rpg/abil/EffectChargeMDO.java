/**
 *  EffectCharge.java
 *  Created on Mar 13, 2015 6:12:32 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.abil;

import net.wombatrpgs.mgns.core.Annotations.Desc;

/**
 * TOME's Rush.
 */
public class EffectChargeMDO extends AbilityEffectMDO {
	
	@Desc("Damage ratio - 1.0 for normal melee damage, 2.0 for double etc")
	public Float damageRatio;

}
