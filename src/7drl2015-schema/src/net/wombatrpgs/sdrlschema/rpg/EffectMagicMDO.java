/**
 *  EffectMagicMDO.java
 *  Created on Mar 13, 2015 11:59:38 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.Nullable;
import net.wombatrpgs.sdrlschema.rpg.abil.AbilityEffectMDO;
import net.wombatrpgs.sdrlschema.rpg.data.MagicElement;

/**
 * it's maaagic
 */
public class EffectMagicMDO extends AbilityEffectMDO {
	
	@Desc("Damage ratio - 1.0 for normal magic damage, 2.0 for double etc")
	public Float damageRatio;
	
	@Desc("Element - determines resistances")
	@Nullable
	public MagicElement element;

}
