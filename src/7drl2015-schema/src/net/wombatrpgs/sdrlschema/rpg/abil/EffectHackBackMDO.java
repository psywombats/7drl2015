/**
 *  EffectHackBack.java
 *  Created on Mar 13, 2015 11:12:10 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.abil;

import net.wombatrpgs.mgns.core.Annotations.Desc;

/**
 * Hacks at an enemy then backs up.
 */
public class EffectHackBackMDO extends AbilityEffectMDO {
	
	@Desc("Retreat range - will jump back this many squares")
	public Integer retreatRange;
	
	@Desc("Damage ratio - 1.0 for normal melee damage, 2.0 for double etc")
	public Float damageRatio;

}
