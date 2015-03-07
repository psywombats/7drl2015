/**
 *  EnemyEventMDO.java
 *  Created on Jan 23, 2013 9:37:59 PM for project RainfallSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.sdrlschema.rpg.ai.data.IntelligenceMDO;
import net.wombatrpgs.sdrlschema.rpg.data.CharacterMDO;

/**
 * A thing on the ground that attempts to killlll youuuu.
 * 
 * 7DRL note: gutted
 */
@Path("characters/")
public class EnemyMDO extends CharacterMDO {
	
	@Desc("Intelligence - the set of behaviors that control the enemy")
	@SchemaLink(IntelligenceMDO.class)
	public String intelligence;
	
	@Desc("Danger level - minimum danger on which this enemy spawns")
	public Integer danger;

}
