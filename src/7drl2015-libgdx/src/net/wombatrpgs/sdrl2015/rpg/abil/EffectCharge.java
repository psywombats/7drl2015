/**
 *  EffectCharge.java
 *  Created on Mar 13, 2015 6:14:51 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrl2015.rpg.ai.TacticType;
import net.wombatrpgs.sdrl2015.rpg.enemy.EnemyEvent;
import net.wombatrpgs.sdrl2015.rpg.travel.StepMove;
import net.wombatrpgs.sdrlschema.maps.data.EightDir;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectChargeMDO;
import net.wombatrpgs.sdrlschema.rpg.data.LevelingAttribute;

/**
 * TOME's Charge.
 */
public class EffectCharge extends AbilEffect {
	
	protected EffectChargeMDO mdo;

	/**
	 * Creates an effect given data, ability.
	 * @param	mdo				The data to generate from
	 * @param	abil			The ability to generate for
	 */
	public EffectCharge(EffectChargeMDO mdo, Ability abil) {
		super(mdo, abil);
		this.mdo = mdo;
	}
	
	/** @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#getTactic() */
	@Override public TacticType getTactic() { return TacticType.OFFENSE; }

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#aiShouldUse()
	 */
	@Override
	public boolean aiShouldUse(EnemyEvent actor) {
		parent = actor.getParent();
		if (!super.aiShouldUse(actor)) return false;
		abil.acquireTargets();
		for (GameUnit target : abil.getTargets()) {
			// hack, should only check hostiles
			if (target == MGlobal.hero.getUnit()) {
				int tileX = actor.getTileX();
				int tileY = actor.getTileY();
				while (target.getParent().tileDistanceTo(tileX, tileY) >=2) {
					EightDir oppDir = actor.directionTo(target.getParent());
					tileX += oppDir.getVector().x;
					tileY += oppDir.getVector().y;
					if (!parent.isTilePassable(actor, tileX, tileY)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#internalAct(java.util.List)
	 */
	@Override
	protected void internalAct(List<GameUnit> targets) {
		for (final GameUnit target : targets) {
			while (actor.tileDistanceTo(target.getParent()) >=2) {
				EightDir oppDir = actor.directionTo(target.getParent());
				int tileX = actor.getTileX();
				int tileY = actor.getTileY();
				tileX += oppDir.getVector().x;
				tileY += oppDir.getVector().y;
				if (!parent.isTilePassable(actor, tileX, tileY)) {
					break;
				}
				actor.setTileX(tileX);
				actor.setTileY(tileY);
				actor.addStep(new StepMove(actor, tileX, tileY));
			}
			actor.faceToward(target.getParent());
			float ratio = mdo.damageRatio;
			if (abil.isLeveled(LevelingAttribute.INCREASE_DAMAGE)) {
				ratio += .2f * (float) getLevel();
			}
			int dmg = (int) (actor.getUnit().calcMeleeDamage() * ratio);
			if (target.calcDodgeChance(0) > MGlobal.rand.nextFloat()) {
				if (MGlobal.hero.inLoS(target.getParent())) {
					GameUnit.out().msg(actor.getName() + " missed.");
				}
			} else {
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
