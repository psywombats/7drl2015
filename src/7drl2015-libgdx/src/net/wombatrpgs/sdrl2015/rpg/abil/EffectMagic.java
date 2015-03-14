/**
 *  EffectMagic.java
 *  Created on Mar 13, 2015 11:58:42 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrlschema.rpg.EffectMagicMDO;
import net.wombatrpgs.sdrlschema.rpg.data.LevelingAttribute;

/**
 * Offensive magic.
 */
public class EffectMagic extends AbilEffect {
	
	protected EffectMagicMDO mdo;

	/**
	 * Creates an effect given data, ability.
	 * @param	mdo				The data to generate from
	 * @param	abil			The ability to generate for
	 */
	public EffectMagic(EffectMagicMDO mdo, Ability abil) {
		super(mdo, abil);
		this.mdo = mdo;
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#internalAct(java.util.List)
	 */
	@Override
	protected void internalAct(List<GameUnit> targets) {
		for (GameUnit target : targets) {
			if (target.calcDodgeChance(0) > MGlobal.rand.nextFloat()) {
				GameUnit.out().msg(target.getName() + " dodges.");
			} else if (target.resists(mdo.element)) {
				GameUnit.out().msg(target.getName() + " resists.");
			} else {
				int dmg = (int) (actor.getUnit().calcMagicDamage(mdo.element) *
						((float) mdo.damageRatio + (float) getLevel() / 5f));
				if (abil.isLeveled(LevelingAttribute.INCREASE_DAMAGE)) {
					dmg *= .2f * (float) getLevel();
				}
				int dealt = target.takeMagicDamage(dmg);
				if (MGlobal.hero.inLoS(target.getParent())) {
					GameUnit.out().msg(target.getName() + " took " + dealt + " damage.");
				}
				target.ensureAlive();
			}
			target.onAttackBy(actor.getUnit());
		}
	}

}
