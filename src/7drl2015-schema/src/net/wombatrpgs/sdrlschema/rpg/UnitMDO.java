/**
 *  UnitMDO.java
 *  Created on Mar 7, 2015 6:29:06 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg;

import net.wombatrpgs.mgns.core.Annotations.DefaultValue;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.InlineSchema;
import net.wombatrpgs.mgns.core.Annotations.Nullable;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.sdrlschema.graphics.FourDirMDO;
import net.wombatrpgs.sdrlschema.rpg.data.RivalryRequirementType;
import net.wombatrpgs.sdrlschema.rpg.stats.StatSetMDO;

/**
 * A further specialization of enemies. May be shared between multiple races.
 */
@Path("rpg/")
public class UnitMDO extends MainSchema {
	
	@Desc("Name - the name of the unit, appended to any names of race and species")
	@DefaultValue("")
	public String suffix;
	
	@Desc("Appearance - can be used to override appearance from race")
	@SchemaLink(FourDirMDO.class)
	@Nullable
	public String appearance;
	
	@Desc("Stats modifier - applied to the default statset for enemies")
	@InlineSchema(StatSetMDO.class)
	public StatSetMDO statsMod;
	
	@Desc("Skills - innate, non-item-granted skills")
	@SchemaLink(AbilityMDO.class)
	public String[] abilities;
	
	@Desc("Rivalry requirement - does this unit require rivalry to be generated?")
	@DefaultValue("NO_REQUIREMENT")
	public RivalryRequirementType rivalry;
}
