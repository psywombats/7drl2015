/**
 *  SpecialAttack.java
 *  Created on Oct 18, 2013 3:49:41 AM for project MRogueSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.abil;

import net.wombatrpgs.mgns.core.Annotations.DefaultValue;
import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.FileLink;
import net.wombatrpgs.mgns.core.Annotations.InlinePolymorphic;
import net.wombatrpgs.mgns.core.Annotations.Nullable;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.mgns.core.PolymorphicLink;
import net.wombatrpgs.sdrlschema.graphics.effects.data.AbilFxMDO;

/**
 * Something a little more subtle than walking into a character.
 */
@Path("rpg/")
public class AbilityMDO extends MainSchema {
	
	@Desc("Name - displayed in-game")
	public String name;
	
	@Desc("Description - displayed in-game only at the class select screen")
	public String abilDesc;
	
	@Desc("Leveling desc - displayed in-game when skill is about to be leveled maybe")
	public String levelDesc;
	
	@Desc("Target - What or where this ability targets")
	public AbilityTargetType target;
	
	@Desc("Range - In tiles, could be used for some targeting types")
	public Float range;
	
	@Desc("Effect - what happens when this applies")
	@InlinePolymorphic(AbilityEffectMDO.class)
	@Nullable
	public PolymorphicLink warhead;
	
	@Desc("Icon - file used for this ability in the UI")
	@FileLink("items")
	@Nullable
	public String icon;
	
	@Desc("Energy cost - How long it takes to use this action (1000 default, 2000 is twice as long, etc")
	@DefaultValue("1000")
	public Integer energyCost;
	
	@Desc("Mana cost - How much mp this takes")
	@DefaultValue("0")
	public Integer mpCost;
	
	@Desc("Stamina cost - How much sp this takes")
	@DefaultValue("0")
	public Integer spCost;
	
	@Desc("Graphical fx - Special animations for this ability!!")
	@SchemaLink(AbilFxMDO.class)
	@Nullable
	public String fx;

}
