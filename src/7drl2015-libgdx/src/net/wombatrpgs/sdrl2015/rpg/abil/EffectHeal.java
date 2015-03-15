/**
 *  EffectHeal.java
 *  Created on Mar 14, 2015 1:55:59 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrl2015.rpg.ai.TacticType;
import net.wombatrpgs.sdrl2015.rpg.enemy.EnemyEvent;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectHealMDO;
import net.wombatrpgs.sdrlschema.rpg.data.LevelingAttribute;
import net.wombatrpgs.sdrlschema.rpg.stats.Stat;

/**
 * Heals?
 */
public class EffectHeal extends AbilEffect {
	
	protected EffectHealMDO mdo;

	/**
	 * Creates an effect given data, ability.
	 * @param	mdo				The data to generate from
	 * @param	abil			The ability to generate for
	 */
	public EffectHeal(EffectHealMDO mdo, Ability abil) {
		super(mdo, abil);
		this.mdo = mdo;
	}
	
	/** @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#getTactic() */
	@Override public TacticType getTactic() { return TacticType.SUPPORT; }

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#aiShouldUse()
	 */
	@Override
	public boolean aiShouldUse(EnemyEvent actor) {
		abil.acquireTargets();
		for (GameUnit target : abil.getTargets()) {
			// hack, we should actually just make sure healed targets are friendly
			if (target == MGlobal.hero.getUnit()) continue;
			if (target.get(Stat.HP) < target.get(Stat.MHP)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#internalAct(java.util.List)
	 */
	@Override
	protected void internalAct(List<GameUnit> targets) {
		float ratio = mdo.magicPower;
		if (abil.isLeveled(LevelingAttribute.INCREASE_DAMAGE)) {
			ratio += (.2f * (float) getLevel());
		}
		int healPower = (int) (ratio * actor.getUnit().calcMagicDamage(null));
		healPower += mdo.healMin;
		if (mdo.healMax - mdo.healMin > 0) healPower += MGlobal.rand.nextInt(mdo.healMax - mdo.healMin);
		
		for (GameUnit target : targets) {
			int healt = target.heal(healPower);
			GameUnit.out().msg(target.getName() + " healed " + healt + " HP.");
		}
	}

}
