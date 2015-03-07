/**
 *  Decorator3x3MDO.java
 *  Created on Oct 13, 2013 6:09:35 AM for project MRogueSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.maps.decorators;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.InlineSchema;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.sdrlschema.maps.data.TileMDO;
import net.wombatrpgs.sdrlschema.maps.decorators.data.SingleDecoratorMDO;

/**
 * A decorator
 */
@Path("maps/decorators/")
public class Decorator3x2MDO extends SingleDecoratorMDO {
	
	@Desc("Upper left tile")
	@InlineSchema(TileMDO.class)
	public TileMDO ul;
	
	@Desc("Upper middle tile")
	@InlineSchema(TileMDO.class)
	public TileMDO u;
	
	@Desc("Upper right tile")
	@InlineSchema(TileMDO.class)
	public TileMDO ur;
	
	@Desc("Bottom right tile")
	@InlineSchema(TileMDO.class)
	public TileMDO br;
	
	@Desc("Bottom middle tile")
	@InlineSchema(TileMDO.class)
	public TileMDO b;
	
	@Desc("Bottom left tile")
	@InlineSchema(TileMDO.class)
	public TileMDO bl;


}
