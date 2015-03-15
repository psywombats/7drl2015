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
import net.wombatrpgs.sdrlschema.rpg.HoardMDO;
import net.wombatrpgs.sdrlschema.rpg.RaceMDO;
import net.wombatrpgs.sdrlschema.rpg.SpeciesMDO;
import net.wombatrpgs.sdrlschema.rpg.UnitMDO;
import net.wombatrpgs.sdrlschema.rpg.data.UnitUseType;

/**
 * Spawns enemies for levels. Owned by a level generator.
 */
public class EnemyGenerator {
	
	protected static final float HOARD_CHANCE = .5f;
	protected static final float PATROL_CHANCE = .5f;
	protected static final float OUT_OF_DEPTH_CHANCE = .1f;
	protected static final int OUT_OF_DEPTH_OFFSET = 5;
	
	protected SpeciesMDO rivalSpecies;
	
	// I have no doubt this isn't scaleable but #yolo #7drl 
	protected List<EnemyDefinition> allDefinitions;
	protected List<EncounterHoard> allHoards;
	protected Map<Integer, List<EnemyDefinition>> definitionsByDL;
	protected Map<Integer, List<EncounterHoard>> hoardsByDL;
	
	/**
	 * Creates a new generator. Probably only called once per game?
	 */
	public EnemyGenerator() {
		
		definitionsByDL = new HashMap<Integer, List<EnemyDefinition>>();
		hoardsByDL = new HashMap<Integer, List<EncounterHoard>>();
		List<SpeciesMDO> allSpecies = MGlobal.data.getAll(SpeciesMDO.class);
		rivalSpecies = allSpecies.get(MGlobal.rand.nextInt(allSpecies.size()));
		
		allDefinitions = new ArrayList<EnemyDefinition>();
		for (SpeciesMDO species : allSpecies) {
			for (String raceKey : species.races) {
				if (MGlobal.data.getEntryFor(raceKey, RaceMDO.class).spawnType == UnitUseType.NO_UNITS) {
					EnemyDefinition def = new EnemyDefinition(species,
							MGlobal.data.getEntryFor(raceKey, RaceMDO.class),
							null);
					allDefinitions.add(def);
				} else {
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
		
		List<HoardMDO> allHoardMDOs = MGlobal.data.getAll(HoardMDO.class);
		allHoards = new ArrayList<EncounterHoard>();
		for (HoardMDO hoardMDO : allHoardMDOs) {
			EncounterHoard hoard = new EncounterHoard(hoardMDO);
			if (hoard.meetsRivalry(rivalSpecies)) {
				allHoards.add(hoard);
			}
		}
	}
	
	/**
	 * Generates an encounter appropriate for the given danger level.
	 * @param	dangerLevel		The DL to generate for
	 * @return					An appropriate encounter instance for that DL
	 */
	public Encounter generateEncounter(int dangerLevel) {
		int dl = dangerLevel;
		if (MGlobal.rand.nextFloat() < OUT_OF_DEPTH_CHANCE) {
			dangerLevel += OUT_OF_DEPTH_OFFSET;
		}
		if (MGlobal.rand.nextFloat() < HOARD_CHANCE) {
			EncounterHoard hoard = generateHoard(dl);
			hoard.generate();
			return hoard;
		} else if (MGlobal.rand.nextFloat() < PATROL_CHANCE) {
			int quantity = MGlobal.rand.nextInt(1)+2;
			return new EncounterPatrol(generateEnemy(dl), quantity);
		} else {
			return new EncounterSingle(generateEnemy(dl));
		}
	}
	
	/**
	 * Generates an appropriate enemy for a given danger level.
	 * @param	dangerLevel		The DL to generate for
	 * @return					An appropriate enemy instance for that DL
	 */
	protected EnemyEvent generateEnemy(int dangerLevel) {
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
	
	/**
	 * Generates an appropriate hoard for a given danger level.
	 * @param	dangerLevel		The DL to generate for
	 * @return					An appropriate hoard instance for that DL
	 */
	protected EncounterHoard generateHoard(int dangerLevel) {
		List<EncounterHoard> hoards = hoardsByDL.get(dangerLevel);
		if (hoards == null) {
			// no entry in the cache yet, so generate it
			hoards = new ArrayList<EncounterHoard>();
			for (EncounterHoard hoard : allHoards) {
				if (hoard.isAvailable(dangerLevel)) {
					hoards.add(hoard);
				}
			}
			hoardsByDL.put(dangerLevel, hoards);
		}
		return hoards.get(MGlobal.rand.nextInt(hoards.size()));
	}

}
