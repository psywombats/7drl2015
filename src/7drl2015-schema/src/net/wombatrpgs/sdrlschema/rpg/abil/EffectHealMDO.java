/**
 *  EffectHealMDO.java
 *  Created on Mar 14, 2015 1:47:42 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.abil;

import net.wombatrpgs.mgns.core.Annotations.Desc;

/**
 * Heals?
 */
public class EffectHealMDO extends AbilityEffectMDO {
	
	@Desc("Magic power - will add this ratio of magic power to the heal, ie "
			+ "1.0 heals the same as a magic attack")
	public Float magicPower;
	
	@Desc("Heal min - minimum HP to heal, not including magic component")
	public Integer healMin;
	
	@Desc("Heal max - maximum HP to heal, not including magic component")
	public Integer healMax;

}
