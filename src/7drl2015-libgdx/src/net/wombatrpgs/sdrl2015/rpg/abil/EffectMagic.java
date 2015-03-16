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
import net.wombatrpgs.sdrl2015.rpg.ai.TacticType;
import net.wombatrpgs.sdrl2015.rpg.enemy.EnemyEvent;
import net.wombatrpgs.sdrlschema.rpg.EffectMagicMDO;
import net.wombatrpgs.sdrlschema.rpg.data.LevelingAttribute;
import net.wombatrpgs.sdrlschema.rpg.stats.Stat;

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
	
	/** @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#getTactic() */
	@Override public TacticType getTactic() { return TacticType.OFFENSE; }

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#aiShouldUse
	 * (net.wombatrpgs.sdrl2015.rpg.enemy.EnemyEvent)
	 */
	@Override
	public boolean aiShouldUse(EnemyEvent actor) {
		switch (mdo.element) {
		case EARTH:		return actor.getUnit().get(Stat.EARTH_DMG) > 0;
		case FIRE:		return actor.getUnit().get(Stat.FIRE_DMG) > 0;
		case ICE:		return actor.getUnit().get(Stat.ICE_DMG) > 0;
		default:		return true;
		}
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#internalAct(java.util.List)
	 */
	@Override
	protected void internalAct(List<GameUnit> targets) {
		for (GameUnit target : targets) {
			if (target.calcDodgeChance(target.get(Stat.DV) / -2) > MGlobal.rand.nextFloat()) {
				if (MGlobal.hero.inLoS(target.getParent())) {
					GameUnit.out().msg(target.getName() + " dodged.");
				}
				target.onAttackBy(actor.getUnit());
			} else if (target.resists(mdo.element)) {
				if (MGlobal.hero.inLoS(target.getParent())) {
					GameUnit.out().msg(target.getName() + " resisted.");
				}
			} else {
				int dmg = (int) (actor.getUnit().calcMagicDamage(mdo.element) *
						((float) mdo.damageRatio));
				if (abil.isLeveled(LevelingAttribute.INCREASE_DAMAGE)) {
					dmg *= 1f + (.2f * (float) getLevel());
				}
				if (target.isWeakTo(mdo.element)) {
					if (MGlobal.hero.inLoS(target.getParent())) {
						GameUnit.out().msg(target.getName() + " is weak to the atack.");
					}
					dmg *= 2;
				}
				int dealt = target.takeMagicDamage(dmg);
				if (MGlobal.hero.inLoS(target.getParent())) {
					GameUnit.out().msg(target.getName() + " took " + dealt + " damage.");
				}
				target.ensureAlive();
				target.onAttackBy(actor.getUnit());
			}
		}
	}

}
