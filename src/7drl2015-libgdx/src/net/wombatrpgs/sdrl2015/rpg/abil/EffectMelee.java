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
import net.wombatrpgs.sdrl2015.rpg.ai.TacticType;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectMeleeMDO;
import net.wombatrpgs.sdrlschema.rpg.data.LevelingAttribute;

/**
 * Average melee attack.
 */
public class EffectMelee extends AbilEffect {
	
	protected EffectMeleeMDO mdo;

	/**
	 * Creates an effect given data, ability.
	 * @param	mdo				The data to generate from
	 * @param	abil			The ability to generate for
	 */
	public EffectMelee(EffectMeleeMDO mdo, Ability abil) {
		super(mdo, abil);
		this.mdo = mdo;
	}
	
	/** @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#getTactic() */
	@Override public TacticType getTactic() { return TacticType.OFFENSE; }

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#internalAct(java.util.List)
	 */
	@Override
	protected void internalAct(List<GameUnit> targets) {
		for (GameUnit target : targets) {
			actor.faceToward(target.getParent());
			if (target.calcDodgeChance(-1 * mdo.accuracy) > MGlobal.rand.nextFloat()) {
				GameUnit.out().msg(actor.getName() + " misses.");
			} else {
				int dmg = (int) (actor.getUnit().calcMeleeDamage() * (float) mdo.damageRatio);
				if (abil.isLeveled(LevelingAttribute.INCREASE_DAMAGE)) {
					dmg *= 1f + (.2f * (float) getLevel());
				}
				int dealt = target.takePhysicalDamage(dmg);
				if (MGlobal.hero.inLoS(target.getParent())) {
					GameUnit.out().msg(target.getName() + " took " + dealt + " damage.");
				}
				target.ensureAlive();
			}
			target.onAttackBy(actor.getUnit());
		}
	}

}
