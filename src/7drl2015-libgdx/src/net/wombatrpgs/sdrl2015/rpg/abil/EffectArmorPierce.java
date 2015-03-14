/**
 *  EffectArmorPierce.java
 *  Created on Mar 10, 2015 10:52:29 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectArmorPierceMDO;
import net.wombatrpgs.sdrlschema.rpg.data.LevelingAttribute;
import net.wombatrpgs.sdrlschema.rpg.stats.Stat;

/**
 * Pierces armor.
 */
public class EffectArmorPierce extends AbilEffect {
	
	protected EffectArmorPierceMDO mdo;

	/**
	 * Creates an effect given data, ability.
	 * @param	mdo				The data to generate from
	 * @param	abil			The ability to generate for
	 */
	public EffectArmorPierce(EffectArmorPierceMDO mdo, Ability abil) {
		super(mdo, abil);
		this.mdo = mdo;
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#internalAct(java.util.List)
	 */
	@Override
	protected void internalAct(List<GameUnit> targets) {
		for (GameUnit target : targets) {
			actor.faceToward(target.getParent());
			int dmg = actor.getUnit().calcMeleeDamage();
			float pierce = (1f - mdo.pierce);
			dmg -= Math.floor((float) target.get(Stat.PV) * (1f-pierce));
			if (abil.isLeveled(LevelingAttribute.INCREASE_DAMAGE)) {
				dmg *= 1f + (.2f * (float) getLevel());
			}
			target.takeRawDamage(dmg);
			if (MGlobal.hero.inLoS(target.getParent())) {
				GameUnit.out().msg(target.getName() + " took " + dmg + " damage through armor.");
			}
			target.onAttackBy(actor.getUnit());
			target.ensureAlive();
		}
	}

}
