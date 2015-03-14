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
			int origX = target.getParent().getTileX();
			int origY = target.getParent().getTileY();
			int tries = 0;
			do {
				target.getParent().setTileX(origX + MGlobal.rand.nextInt(maxR*2) - maxR);
				target.getParent().setTileY(origY + MGlobal.rand.nextInt(maxR*2) - maxR);
				tries += 1;
			} while ((!target.getParent().getParent().isTilePassable(target.getParent(),
					target.getParent().getTileX(),
					target.getParent().getTileY())
					|| target.getParent().euclideanTileDistanceTo(origX, origY) < minR)
					&& tries < 50);
		}
	}

}
