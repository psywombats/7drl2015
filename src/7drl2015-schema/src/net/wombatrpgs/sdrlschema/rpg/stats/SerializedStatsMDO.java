/**
 *  SerializedStatsMDO.java
 *  Created on Mar 7, 2015 9:52:34 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.stats;

import net.wombatrpgs.mgns.core.Annotations.InlineSchema;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.mgns.core.Annotations.Path;

/**
 * A head for StatSetMDO.
 */
@Path("rpg/")
public class SerializedStatsMDO extends MainSchema {

	@InlineSchema(StatSetMDO.class)
	public StatSetMDO stats;
}
