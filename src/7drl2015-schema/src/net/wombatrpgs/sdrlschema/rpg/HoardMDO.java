/**
 *  HoardMDO.java
 *  Created on Mar 15, 2015 1:56:44 AM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.InlineSchema;
import net.wombatrpgs.mgns.core.Annotations.Nullable;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.sdrlschema.rpg.data.HoardEntryMDO;

/**
 * A serialized grouping of enemies.
 */
@Path("rpg/")
public class HoardMDO extends MainSchema {
	
	@Desc("Members")
	@InlineSchema(HoardEntryMDO.class)
	public HoardEntryMDO[] entries;
	
	@InlineSchema(DangerRequirementMDO.class)
	public DangerRequirementMDO danger;
	
	@Desc("Rival species - will only spawn if this species is rival, or null for no requirement")
	@SchemaLink(SpeciesMDO.class)
	@Nullable
	public String rivalSpecies;

}
