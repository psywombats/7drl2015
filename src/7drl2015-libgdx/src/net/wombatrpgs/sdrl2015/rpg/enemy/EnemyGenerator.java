/**
 *  EnemyGenerator.java
 *  Created on Mar 10, 2015 7:46:56 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.enemy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrlschema.rpg.RaceMDO;
import net.wombatrpgs.sdrlschema.rpg.SpeciesMDO;
import net.wombatrpgs.sdrlschema.rpg.UnitMDO;

/**
 * Spawns enemies for levels. Owned by a level generator.
 */
public class EnemyGenerator {
	
	protected SpeciesMDO rivalSpecies;
	
	// I have no doubt this isn't scaleable but #yolo #7drl 
	protected List<EnemyDefinition> allDefinitions;
	protected Map<Integer, List<EnemyDefinition>> definitionsByDL;
	
	/**
	 * Creates a new generator. Probably only called once per game?
	 */
	public EnemyGenerator() {
		
		definitionsByDL = new HashMap<Integer, List<EnemyDefinition>>();
		List<SpeciesMDO> allSpecies = MGlobal.data.getEntriesByClass(SpeciesMDO.class);
		rivalSpecies = allSpecies.get(MGlobal.rand.nextInt(allSpecies.size()));
		
		allDefinitions = new ArrayList<EnemyDefinition>();
		for (SpeciesMDO species : allSpecies) {
			for (String raceKey : species.races) {
				for (String unitKey : species.units) {
					EnemyDefinition def = new EnemyDefinition(species,
							MGlobal.data.getEntryFor(raceKey, RaceMDO.class),
							MGlobal.data.getEntryFor(unitKey, UnitMDO.class));
					if (def.isValid(rivalSpecies)) {
						allDefinitions.add(def);
					}
				}
			}
		}
	}
	
	/**
	 * Generates an appropriate enemy for a given danger level.
	 * @param	dangerLevel		The DL to generate for
	 * @return					An appropriate enemy instance for that DL
	 */
	public EnemyEvent generate(int dangerLevel) {
		List<EnemyDefinition> definitions = definitionsByDL.get(dangerLevel);
		if (definitions == null) {
			// no entry in the cache yet, so generate it
			definitions = new ArrayList<EnemyDefinition>();
			for (EnemyDefinition def : allDefinitions) {
				if (def.isAvailable(dangerLevel)) {
					definitions.add(def);
				}
			}
			definitionsByDL.put(dangerLevel, definitions);
		}
		return definitions.get(MGlobal.rand.nextInt(definitions.size())).instantiate();
	}

}
