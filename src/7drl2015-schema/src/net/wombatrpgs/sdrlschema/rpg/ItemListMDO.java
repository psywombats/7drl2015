/**
 *  ItemListMDO.java
 *  Created on Mar 14, 2015 3:29:03 AM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg;

import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;

/**
 * Serialized list of items.
 */
@Path("rpg/")
public class ItemListMDO extends MainSchema {
	
	@SchemaLink(ItemMDO.class)
	public String[] items;

}
