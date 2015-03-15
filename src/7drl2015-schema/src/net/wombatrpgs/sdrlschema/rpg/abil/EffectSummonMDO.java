/**
 *  EffectSummonMDO.java
 *  Created on Mar 14, 2015 10:16:47 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.abil;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.sdrlschema.rpg.RaceMDO;
import net.wombatrpgs.sdrlschema.rpg.UnitMDO;

/**
 * Summon minions.
 */
public class EffectSummonMDO extends AbilityEffectMDO {
	
	@Desc("Races - races to summon, if empty will use own")
	@SchemaLink(RaceMDO.class)
	public String[] race;
	
	@Desc("Units - units to summon, if empty will use own")
	@SchemaLink(UnitMDO.class)
	public String[] unit;
	
	@Desc("Min - minimum amount to summon")
	public Integer summonMin;
	
	@Desc("Max - maximum amount to summon")
	public Integer summonMax;

}
