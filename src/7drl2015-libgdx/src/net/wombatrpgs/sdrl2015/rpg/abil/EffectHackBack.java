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
import net.wombatrpgs.sdrl2015.rpg.travel.StepMove;
import net.wombatrpgs.sdrlschema.maps.data.EightDir;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectHackBackMDO;
import net.wombatrpgs.sdrlschema.rpg.data.LevelingAttribute;

/**
 * Hacks then backs
 */
public class EffectHackBack extends AbilEffect {
	
	protected EffectHackBackMDO mdo;

	/**
	 * Creates an effect given data, ability.
	 * @param	mdo				The data to generate from
	 * @param	abil			The ability to generate for
	 */
	public EffectHackBack(EffectHackBackMDO mdo, Ability abil) {
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
			if (target.calcDodgeChance(0) < MGlobal.rand.nextFloat()) {
				int dmg = (int) (actor.getUnit().calcMeleeDamage() * (float) mdo.damageRatio);
				if (abil.isLeveled(LevelingAttribute.INCREASE_DAMAGE)) {
					dmg *= 1f + .2f * (float) getLevel();
				}
				int dealt = target.takePhysicalDamage(dmg);
				if (MGlobal.hero.inLoS(target.getParent())) {
					GameUnit.out().msg(target.getName() + " took " + dealt + " damage.");
				}
				int tileX = actor.getTileX();
				int tileY = actor.getTileY();
				EightDir dir = target.getParent().directionTo(actor);
				int retreat = mdo.retreatRange;
				if (abil.isLeveled(LevelingAttribute.INCREASE_SECONDARY)) {
					retreat += getLevel();
				}
				for (int i = 0; i < retreat; i += 1) {
					tileX += dir.getVector().x;
					tileY += dir.getVector().y;
					actor.addStep(new StepMove(actor, tileX, tileY));
				}
				actor.setTileX(tileX);
				actor.setTileY(tileY);
				target.ensureAlive();
			} else {
				GameUnit.out().msg(actor.getName() + " misses.");
			}
			actor.faceToward(target.getParent());
			target.onAttackBy(actor.getUnit());
		}
	}

}
