/**
 *  EffectFactory.java
 *  Created on Oct 18, 2013 4:51:36 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrlschema.rpg.data.AbilityEffectMDO;

/**
 * Another one of these shitty instanceof/getclass constructions for MDOs.
 */
public class AbilEffectFactory {
	
	/**
	 * Creates an effect given data key, parent.
	 * @param	mdoKey			The key to use to get generation data
	 * @param	abil			The ability to generate for
	 * @return
	 */
	public static AbilEffect createEffect(String mdoKey, Ability abil) {
		AbilityEffectMDO mdo = MGlobal.data.getEntryFor(mdoKey, AbilityEffectMDO.class);
		return createEffect(mdo, abil);
	}
	
	/**
	 * Creates an effect given data, parent.
	 * @param	mdo				The data to generate from
	 * @param	abil			The ability to generate for
	 * @return
	 */
	public static AbilEffect createEffect(AbilityEffectMDO mdo, Ability abil) {
		MGlobal.reporter.err("Unknown ability type " + mdo);
		return null;
	}

}
