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
import net.wombatrpgs.sdrl2015.rpg.travel.StepMove;
import net.wombatrpgs.sdrlschema.maps.data.EightDir;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectChargeMDO;

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

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#internalAct(java.util.List)
	 */
	@Override
	protected void internalAct(List<GameUnit> targets) {
		for (final GameUnit target : targets) {
			if (!actor.rayExistsTo(target.getParent(), actor.new RayCheck() {
				@Override public boolean bad(int tileX, int tileY) {
					if (!parent.isTilePassable(actor, tileX, tileY)) {
						return true;
					}
					for (MapEvent event : parent.getEventsAt(tileX, tileY)) {
						if (!event.isPassable() && event != target.getParent()) {
							return true;
						}
					}
					return false;
				}
			})) {
				return;
			}
			int tileX = actor.getTileX();
			int tileY = actor.getTileY();
			EightDir dir = target.getParent().directionTo(tileX, tileY);
			EightDir oppDir = EightDir.getOpposite(dir);
			while (target.getParent().tileDistanceTo(tileX, tileY) > 1) {
				tileX += oppDir.getVector().x;
				tileY += oppDir.getVector().y;
				actor.addStep(new StepMove(actor, tileX, tileY));
			}
			actor.setTileX(tileX);
			actor.setTileY(tileY);
			actor.faceToward(target.getParent());
			float ratio = mdo.damageRatio + .2f * (float) getLevel();
			int dmg = (int) (actor.getUnit().calcMeleeDamage() * ratio);
			if (target.calcDodgeChance(0) < MGlobal.rand.nextFloat()) {
				GameUnit.out().msg(actor.getName() + " misses.");
			} else {
				int dealt = target.takePhysicalDamage(dmg);
				GameUnit.out().msg(target.getName() + " takes " + dealt + " damage.");
				target.ensureAlive();
			}
			target.onAttackBy(actor.getUnit());
		}
	}

}
