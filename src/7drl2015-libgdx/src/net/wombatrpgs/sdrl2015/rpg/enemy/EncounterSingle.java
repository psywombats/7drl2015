/**
 *  EncounterSingle.java
 *  Created on Mar 15, 2015 1:46:56 AM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.enemy;

import net.wombatrpgs.sdrl2015.maps.Level;

/**
 * Spawn a single enemy.
 */
public class EncounterSingle extends Encounter {
	
	protected EnemyEvent enemy;
	
	/**
	 * Creates a new encounter for a single enemy.
	 * @param	enemy			The enemy to register
	 */
	public EncounterSingle(EnemyEvent enemy) {
		this.enemy = enemy;
		assets.add(enemy);
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.enemy.Encounter#spawn(net.wombatrpgs.sdrl2015.maps.Level)
	 */
	@Override
	public void spawn(Level map) {
		enemy.spawnUnseen(map);
	}

}
