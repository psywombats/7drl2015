/**
 *  ItemMDO.java
 *  Created on Mar 8, 2015 3:58:06 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.FileLink;
import net.wombatrpgs.mgns.core.Annotations.InlineSchema;
import net.wombatrpgs.mgns.core.Annotations.Nullable;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.sdrlschema.rpg.abil.AbilityMDO;
import net.wombatrpgs.sdrlschema.rpg.data.EquipmentSlot;
import net.wombatrpgs.sdrlschema.rpg.stats.StatModMDO;

/**
 * All items and equipment.
 */
@Path("rpg/")
public class ItemMDO extends MainSchema {
	
	@Desc("Name - in-game name of the item")
	public String itemName;
	
	@Desc("Description - in-game description of the item")
	public String itemDescription;
	
	@Desc("Icon - icon representation of the item on the floor")
	@FileLink("items")
	public String icon;
	
	@Desc("Carry skill - skill granted by lugging this item around")
	@SchemaLink(AbilityMDO.class)
	@Nullable
	public String carriedAbility;
	
	@Desc("Equip skill - skill granted by equipping this item")
	@SchemaLink(AbilityMDO.class)
	@Nullable
	public String equippedAbility;
	
	@Desc("Equipment slot - where this item is equipped, it it's equipment")
	@Nullable
	public EquipmentSlot slot;
	
	@Desc("Equipment stats - stats applied to owner when equipped")
	@InlineSchema(StatModMDO.class)
	public StatModMDO stats;

}
