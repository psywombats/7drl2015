/**
 *  AbilArmorPierceMDO.java
 *  Created on Oct 18, 2013 4:18:01 PM for project MRogueSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.characters.effects;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.sdrlschema.characters.data.AbilityEffectMDO;

/**
 * Offensive magic
 */
@Path("characters/effects/")
public class AbilMagicDamageMDO extends AbilityEffectMDO {
	
	@Desc("Multiplier to deal of normal magic damage")
	public Float mult;
	
	@Desc("Base damage to always deal")
	public Integer base;

}
