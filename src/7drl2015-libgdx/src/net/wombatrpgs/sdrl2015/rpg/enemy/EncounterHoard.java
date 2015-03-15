/**
 *  EncounterHoard.java
 *  Created on Mar 15, 2015 1:53:26 AM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.enemy;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.maps.Level;
import net.wombatrpgs.sdrl2015.maps.MapThing;
import net.wombatrpgs.sdrl2015.rpg.DangerCondition;
import net.wombatrpgs.sdrlschema.rpg.EnemyMDO;
import net.wombatrpgs.sdrlschema.rpg.HoardMDO;
import net.wombatrpgs.sdrlschema.rpg.SpeciesMDO;
import net.wombatrpgs.sdrlschema.rpg.data.HoardEntryMDO;

/**
 * An encounter from data.
 */
public class EncounterHoard extends Encounter {
	
	protected HoardMDO mdo;
	protected List<EnemyEvent> enemies;

	/**
	 * Creates a new hoard from data.
	 * @param	mdo				The hoard data to generate from
	 */
	public EncounterHoard(HoardMDO mdo) {
		this.mdo = mdo;
		enemies = new ArrayList<EnemyEvent>();
	}
	
	/**
	 * Checks if this encounter is compatible with the rival species.
	 * @param	rival			The rival to check if compatible.
	 * @return					True if compatible
	 */
	public boolean meetsRivalry(SpeciesMDO rival) {
		if (MapThing.mdoHasProperty(mdo.rivalSpecies) &&
				!rival.key.equals(mdo.rivalSpecies)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Checks if this hoard is even eligible to spawn.
	 * @param	dangerLevel		The potential dl to generate at
	 * @return					True if this hoard should spawn
	 */
	public boolean isAvailable(int dangerLevel) {
		return new DangerCondition(mdo.danger).isMet(dangerLevel);
	}
	
	/**
	 * Creates a new hoard from this template.
	 */
	public void generate() {
		enemies.clear();
		assets.clear();
		
		for (HoardEntryMDO entryMDO : mdo.entries) {
			int quantity = parseQuantity(entryMDO.quantity);
			for (int i = 0; i < quantity; i += 1) {
				EnemyEvent enemy = new EnemyEvent(MGlobal.data.getEntryFor(entryMDO.enemy, EnemyMDO.class));
				enemies.add(enemy);
				assets.add(enemy);
			}
		}
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
	
	/**
	 * Parses and generates a quantity from a random definition string.
	 * @param	string			The param string, such as 2-3, or 1
	 * @return					A suitable number in that range
	 */
	protected int parseQuantity(String string) {
		if (!string.contains("-")) {
			return Integer.valueOf(string);
		} else {
			String[] tokens = string.split("-");
			int min = Integer.valueOf(tokens[0]);
			int max = Integer.valueOf(tokens[1]);
			return MGlobal.rand.nextInt(max - min) + min;
		}
	}

}
