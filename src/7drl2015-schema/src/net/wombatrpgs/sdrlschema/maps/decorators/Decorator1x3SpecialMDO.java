/**
 *  Decorator1x1.java
 *  Created on Oct 13, 2013 6:13:29 AM for project MRogueSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.maps.decorators;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.InlineSchema;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.sdrlschema.maps.data.TileMDO;
import net.wombatrpgs.sdrlschema.maps.data.TileType;
import net.wombatrpgs.sdrlschema.maps.decorators.data.SingleDecoratorMDO;

/**
 * A decorator 1x3
 */
@Path("maps/decorators/")
public class Decorator1x3SpecialMDO extends SingleDecoratorMDO {
	
	@Desc("The top tile to replace with")
	@InlineSchema(TileMDO.class)
	public TileMDO t;

	@Desc("The middle tile to replace with")
	@InlineSchema(TileMDO.class)
	public TileMDO m;
	
	@Desc("The bottom tile to replace with")
	@InlineSchema(TileMDO.class)
	public TileMDO b;
	
	@Desc("The tile type of the bottom tile only, overrides other")
	public TileType bottomType;

}
