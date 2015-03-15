/**
 *  HoardEntryMDO.java
 *  Created on Mar 15, 2015 1:57:10 AM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.data;

import net.wombatrpgs.mgns.core.Annotations.DefaultValue;
import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.HeadlessSchema;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.sdrlschema.rpg.EnemyMDO;

/**
 * An entry for a hoard.
 */
public class HoardEntryMDO extends HeadlessSchema {
	
	@SchemaLink(EnemyMDO.class)
	public String enemy;
	
	@Desc("Quantity - in the form min-max or just num, eg, 1-3, 2... sorry, no dice notation")
	@DefaultValue("1")
	public String quantity;

}
