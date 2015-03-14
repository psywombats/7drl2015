/**
 *  EffectTeleportMDO.java
 *  Created on Mar 14, 2015 2:30:51 AM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.abil;

import net.wombatrpgs.mgns.core.Annotations.Desc;

/**
 * BLINK
 */
public class EffectTeleportMDO extends AbilityEffectMDO {
	
	@Desc("Min radius - will teleport at least this many tiles away")
	public Integer minRadius;
	
	@Desc("Max radius - will teleport no more than this many tiles away")
	public Integer maxRadius;

}
