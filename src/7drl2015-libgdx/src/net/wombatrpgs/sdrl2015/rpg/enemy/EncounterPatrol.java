/**
 *  EncounterPatrol.java
 *  Created on Mar 15, 2015 1:49:39 AM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.enemy;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.sdrl2015.maps.Level;

/**
 * Creates a patrol of duplicate enemies.
 */
public class EncounterPatrol extends Encounter {
	
	protected List<EnemyEvent> enemies;

	/**
	 * Generates a patrol of the given enemy.
	 * @param	event			The event to spawn
	 * @param 	count			How many to spawn
	 */
	public EncounterPatrol(EnemyEvent event, int count) {
		enemies = new ArrayList<EnemyEvent>();
		enemies.add(event);
		for (int i = 1; i < count; i += 1) {
			EnemyEvent clone = new EnemyEvent(event.getSpeciesMDO(),
					event.getRaceMDO(), event.getUnitMDO());
			enemies.add(clone);
		}
		assets.addAll(enemies);
		for (EnemyEvent e1 : enemies) {
			for (EnemyEvent e2 : enemies) {
				if (e1 != e2) {
					e1.getUnit().getAllegiance().addToFriendlist(e2.getUnit());
				}
			}
		}
	}
	
	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.enemy.Encounter#spawn(net.wombatrpgs.sdrl2015.maps.Level)
	 */
	@Override
	public void spawn(Level map) {
		EnemyEvent first = enemies.get(0);
		first.spawnUnseen(map);
		for (int i = 1; i < enemies.size(); i += 1) {
			EnemyEvent clone = enemies.get(i);
			clone.spawnNear(first);
		}
	}

}
