/**
 *  EffectFactory.java
 *  Created on Oct 18, 2013 4:51:36 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrlschema.rpg.abil.AbilityEffectMDO;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectArmorPierceMDO;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectChargeMDO;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectHackBackMDO;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectMeleeMDO;

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
		if (EffectArmorPierceMDO.class.isAssignableFrom(mdo.getClass())) {
			return new EffectArmorPierce((EffectArmorPierceMDO) mdo, abil);
		} else if (EffectChargeMDO.class.isAssignableFrom(mdo.getClass())) {
			return new EffectCharge((EffectChargeMDO) mdo, abil);
		} else if (EffectMeleeMDO.class.isAssignableFrom(mdo.getClass())) {
			return new EffectMelee((EffectMeleeMDO) mdo, abil);
		} else if (EffectHackBackMDO.class.isAssignableFrom(mdo.getClass())) {
			return new EffectHackBack((EffectHackBackMDO) mdo, abil);
		} else {
			MGlobal.reporter.err("Unknown ability type " + mdo.key);
		}
		return null;
	}

}
