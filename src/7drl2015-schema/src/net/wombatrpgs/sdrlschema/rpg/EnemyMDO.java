/**
 *  EnemyMDO.java
 *  Created on Mar 15, 2015 1:53:48 AM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.Nullable;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;

/**
 * A serialized combination of race, species, etc.
 */
@Path("rpg/")
public class EnemyMDO extends MainSchema {
	
	@Desc("Species")
	@SchemaLink(SpeciesMDO.class)
	public String species;
	
	@Desc("Race")
	@SchemaLink(RaceMDO.class)
	@Nullable
	public String race;
	
	@Desc("Unit")
	@SchemaLink(UnitMDO.class)
	@Nullable
	public String unit;

}
