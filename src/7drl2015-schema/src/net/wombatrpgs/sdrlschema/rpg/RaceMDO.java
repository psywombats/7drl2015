/**
 *  RaceMDO.java
 *  Created on Mar 7, 2015 6:12:25 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg;

import net.wombatrpgs.mgns.core.Annotations.DefaultValue;
import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.InlineSchema;
import net.wombatrpgs.mgns.core.Annotations.Nullable;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.sdrlschema.graphics.FourDirMDO;
import net.wombatrpgs.sdrlschema.rpg.abil.AbilityMDO;
import net.wombatrpgs.sdrlschema.rpg.stats.StatModMDO;

/**
 * Subtypes of species that mostly provide stat modifiers.
 */
@Path("rpg/")
public class RaceMDO extends MainSchema {
	
	@Desc("Name - name of the race, precedes all other names, usually blank")
	@DefaultValue("")
	public String prefix;
	
	@Desc("Appearance - can be used to override appearance from species")
	@SchemaLink(FourDirMDO.class)
	@Nullable
	public String appearance;
	
	@InlineSchema(DangerRequirementMDO.class)
	public DangerRequirementMDO dangerLevel;
	
	@Desc("Stats modifier - applied to the default statset for enemies")
	@InlineSchema(StatModMDO.class)
	public StatModMDO statsMod;
	
	@Desc("Skills - innate, non-item-granted skills")
	@SchemaLink(AbilityMDO.class)
	public String[] abilities;

}
