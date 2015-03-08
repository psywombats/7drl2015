/**
 *  SpeciesMDO.java
 *  Created on Mar 7, 2015 6:11:36 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg;

import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.mgns.core.Annotations.DefaultValue;
import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.InlineSchema;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.sdrlschema.graphics.FourDirMDO;
import net.wombatrpgs.sdrlschema.rpg.stats.StatSetMDO;

/**
 * Top-level monster type for 7DRL.
 */
@Path("rpg/")
public class SpeciesMDO extends MainSchema {
	
	@Desc("Name - name of the species, sometimes blank for varied species")
	@DefaultValue("")
	public String raceName;
	
	@Desc("Appearance - can be overridden by unit or race, but used as default")
	@SchemaLink(FourDirMDO.class)
	public String appearance;
	
	@Desc("Stats modifier - applied to the default statset for enemies")
	@InlineSchema(StatSetMDO.class)
	public StatSetMDO statsMod;
	
	@Desc("Skills - innate, non-item-granted skills")
	@SchemaLink(AbilityMDO.class)
	public String[] abilities;
	
	@Desc("Races - available races for this species")
	@SchemaLink(RaceMDO.class)
	public String[] races;
	
	@Desc("Units - available units for this species")
	@SchemaLink(UnitMDO.class)
	public String[] units;

}
