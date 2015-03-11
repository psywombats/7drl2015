/**
 *  EnemyDefinition.java
 *  Created on Mar 10, 2015 7:51:39 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.enemy;

import net.wombatrpgs.sdrl2015.rpg.DangerCondition;
import net.wombatrpgs.sdrlschema.rpg.RaceMDO;
import net.wombatrpgs.sdrlschema.rpg.SpeciesMDO;
import net.wombatrpgs.sdrlschema.rpg.UnitMDO;
import net.wombatrpgs.sdrlschema.rpg.data.RivalryRequirementType;

/**
 * An explicit instance of the Specices->Race->Unit breakdown.
 */
public class EnemyDefinition {
	
	protected SpeciesMDO speciesMDO;
	protected RaceMDO raceMDO;
	protected UnitMDO unitMDO;
	protected DangerCondition dangerCondition;
	
	/**
	 * Creates a new explicit enemy definition. This can be thought of as a
	 * template to generate new enemy instances.
	 * @param	speciesMDO		The species of the enemy
	 * @param	raceMDO			The race of the enemy, if any (possibly null)
	 * @param	unitMDO			The unit of the enemy, if any (possibly null)
	 */
	public EnemyDefinition(SpeciesMDO speciesMDO, RaceMDO raceMDO, UnitMDO unitMDO) {
		this.speciesMDO = speciesMDO;
		this.raceMDO = raceMDO;
		this.unitMDO = unitMDO;
		
		dangerCondition = new DangerCondition(speciesMDO.dangerLevel);
		if (raceMDO != null) {
			dangerCondition = dangerCondition.merge(new DangerCondition(raceMDO.dangerLevel));
		}
		if (unitMDO != null) {
			dangerCondition = dangerCondition.merge(new DangerCondition(unitMDO.dangerLevel));
		}
	}
	
	/**
	 * Checks if this definition is valid for this game.
	 * @param	rivalSpecies	The rivalry species for the game
	 * @return					True if this definition can ever be created
	 */
	public boolean isValid(SpeciesMDO rivalSpecies) {
		if (unitMDO != null && 
				unitMDO.rivalry == RivalryRequirementType.REQUIRES_RIVAL
				&& rivalSpecies != speciesMDO) {
			return false;
		}
		if (!dangerCondition.canBeMet()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if this definition should be generated given current conditions.
	 * Does not take rivalry requirements into account.
	 * @param	dangerLevel		The danger level of the level to generate for
	 * @return					True if this enemy should be generated
	 */
	public boolean isAvailable(int dangerLevel) {
		return dangerCondition.isMet(dangerLevel);
	}
	
	/**
	 * Creates a new enemy event from this definition.
	 * @return					A new instance of this enemy
	 */
	public EnemyEvent instantiate() {
		return new EnemyEvent(speciesMDO, raceMDO, unitMDO);
	}

}
