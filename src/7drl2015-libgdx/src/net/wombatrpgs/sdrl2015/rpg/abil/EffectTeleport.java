/**
 *  EffectTeleport.java
 *  Created on Mar 14, 2015 2:36:39 AM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrl2015.rpg.ai.TacticType;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectTeleportMDO;
import net.wombatrpgs.sdrlschema.rpg.data.LevelingAttribute;

/**
 * Random teleportation.
 */
public class EffectTeleport extends AbilEffect {
	
	protected EffectTeleportMDO mdo;

	/**
	 * Creates an effect given data, ability.
	 * @param	mdo				The data to generate from
	 * @param	abil			The ability to generate for
	 */
	public EffectTeleport(EffectTeleportMDO mdo, Ability abil) {
		super(mdo, abil);
		this.mdo = mdo;
	}
	
	/** @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#getTactic() */
	@Override public TacticType getTactic() { return TacticType.RANDOM; }

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#internalAct(java.util.List)
	 */
	@Override
	protected void internalAct(List<GameUnit> targets) {
		int minR = mdo.minRadius;
		int maxR = mdo.minRadius;
		if (abil.isLeveled(LevelingAttribute.INCREASE_SECONDARY)) {
			minR += getLevel() * 2;
			maxR += getLevel() * 2;
		}
		for (GameUnit target : targets) {
			if (MGlobal.hero.inLoS(target.getParent())) {
				GameUnit.out().msg(target.getName() + " vanishes.");
			}
			int origX = target.getParent().getTileX();
			int origY = target.getParent().getTileY();
			int tries = 0;
			do {
				int tx = origX + MGlobal.rand.nextInt(maxR*2) - maxR;
				int ty = origY + MGlobal.rand.nextInt(maxR*2) - maxR;
				if (tx < 0 || tx >= parent.getWidth()) continue;
				if (ty < 0 || ty >= parent.getHeight()) continue;
				target.getParent().setTileX(tx);
				target.getParent().setTileY(ty);
				tries += 1;
			} while ((!target.getParent().getParent().isTilePassable(target.getParent(),
					target.getParent().getTileX(),
					target.getParent().getTileY())
					|| target.getParent().euclideanTileDistanceTo(origX, origY) < minR)
					&& tries < 100);
			
			if (tries == 100) {
				target.getParent().setTileX(origX);
				target.getParent().setTileY(origY);
				return;
			}
			
			if (MGlobal.hero.inLoS(target.getParent())) {
				GameUnit.out().msg(target.getName() + " appears.");
			}
		}
	}

}
