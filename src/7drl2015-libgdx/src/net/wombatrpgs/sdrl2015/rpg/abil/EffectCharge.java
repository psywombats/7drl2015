/**
 *  EffectCharge.java
 *  Created on Mar 13, 2015 6:14:51 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.maps.events.MapEvent;
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
				if (abil.getActor().euclideanTileDistanceTo(MGlobal.hero) < 2) {
					return false;
				}
				int tileX = actor.getTileX();
				int tileY = actor.getTileY();
				EightDir dir = target.getParent().directionTo(tileX, tileY);
				EightDir oppDir = EightDir.getOpposite(dir);
				while (target.getParent().tileDistanceTo(tileX, tileY) > 1) {
					tileX += oppDir.getVector().x;
					tileY += oppDir.getVector().y;
					if (!parent.isTilePassable(actor, tileX, tileY)) {
						return false;
					}
					for (MapEvent event : parent.getEventsAt(tileX, tileY)) {
						if (!event.isPassable() && event != target.getParent()) {
							return false;
						}
					}
				}
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
		for (final GameUnit target : targets) {
			int tileX = actor.getTileX();
			int tileY = actor.getTileY();
			EightDir dir = target.getParent().directionTo(tileX, tileY);
			EightDir oppDir = EightDir.getOpposite(dir);
			while (target.getParent().tileDistanceTo(tileX, tileY) > 1) {
				tileX += oppDir.getVector().x;
				tileY += oppDir.getVector().y;
				if (!parent.isTilePassable(actor, tileX, tileY)) {
					break;
				}
				for (MapEvent event : parent.getEventsAt(tileX, tileY)) {
					if (!event.isPassable() && event != target.getParent()) {
						return;
					}
				}
				actor.addStep(new StepMove(actor, tileX, tileY));
			}
			actor.setTileX(tileX);
			actor.setTileY(tileY);
			actor.faceToward(target.getParent());
			float ratio = mdo.damageRatio;
			if (abil.isLeveled(LevelingAttribute.INCREASE_DAMAGE)) {
				ratio += .2f * (float) getLevel();
			}
			int dmg = (int) (actor.getUnit().calcMeleeDamage() * ratio);
			if (target.calcDodgeChance(0) > MGlobal.rand.nextFloat()) {
				if (MGlobal.hero.inLoS(target.getParent())) {
					GameUnit.out().msg(actor.getName() + " misses.");
				}
			} else {
				int dealt = target.takePhysicalDamage(dmg);
				if (MGlobal.hero.inLoS(target.getParent())) {
					GameUnit.out().msg(target.getName() + " takes " + dealt + " damage.");
				}
				target.ensureAlive();
			}
			target.onAttackBy(actor.getUnit());
		}
	}

}
