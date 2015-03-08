/**
 *  HeroMDO.java
 *  Created on Jan 26, 2013 6:14:09 PM for project RainfallSchema
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
import net.wombatrpgs.sdrlschema.graphics.DirMDO;
import net.wombatrpgs.sdrlschema.rpg.stats.StatSetMDO;

/**
 * Defines stuff about the hero and how it moves/acts.
 * 
 * 7DRL: enemies and the hero no long share a common ancestor. Whether this is
 * a good idea or not remains to be seen.
 */
@Path("rpg/")
public class HeroMDO extends MainSchema {
	
	@Desc("Animation - what this event looks like")
	@SchemaLink(DirMDO.class)
	@Nullable
	public String appearance;
	
	@InlineSchema(StatSetMDO.class)
	public StatSetMDO stats;
	
	@Desc("Abilities - available to the player, maybe used by AI?")
	@SchemaLink(AbilityMDO.class)
	public String[] abilities;

}
