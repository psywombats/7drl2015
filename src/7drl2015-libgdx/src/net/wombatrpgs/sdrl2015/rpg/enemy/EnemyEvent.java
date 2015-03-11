/**
 *  Enemy.java
 *  Created on Jan 23, 2013 9:14:38 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.enemy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.maps.MapThing;
import net.wombatrpgs.sdrl2015.rpg.CharacterEvent;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrl2015.rpg.ai.BTNode;
import net.wombatrpgs.sdrl2015.rpg.ai.IntelligenceFactory;
import net.wombatrpgs.sdrl2015.rpg.stats.SdrlStats;
import net.wombatrpgs.sdrlschema.rpg.RaceMDO;
import net.wombatrpgs.sdrlschema.rpg.SpeciesMDO;
import net.wombatrpgs.sdrlschema.rpg.UnitMDO;
import net.wombatrpgs.sdrlschema.rpg.stats.SerializedStatsMDO;
import net.wombatrpgs.sdrlschema.rpg.stats.Stat;

/**
 * The one and only class for those pesky badniks that hunt down the valiant
 * hero and hinder his quest to save the earth.
 */
public class EnemyEvent extends CharacterEvent {
	
	protected static final String KEY_DEFAULT_BEHAVIOR = "behavior_default";
	protected static final String KEY_DEFAULT_STATS = "stats_default";
	
	protected BTNode intelligence;
	
	/**
	 * Creates a new enemy on a map from a database entry.
	 * @param 	species			The species of the enemy, required
	 * @param	race			The race specialization of the enemy, or null
	 * @param	unit			The unit specialization of the enemy, or null
	 */
	public EnemyEvent(SpeciesMDO species, RaceMDO race, UnitMDO unit) {
		super(generateUnit(species, race, unit), generateAppearance(species, race, unit));
		this.intelligence = IntelligenceFactory.createIntelligence(KEY_DEFAULT_BEHAVIOR, this);
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.rpg.CharacterEvent#act()
	 */
	@Override
	public void act() {
		if (unit.get(Stat.HP) <= 0) {
			// this hack sucks, don't quite remember the reasoning here...
			this.ticksRemaining += 100000;
			return;
		} else {
			intelligence.getStatusAndAct();
		}
	}
	
	/**
	 * Creates an enemy unit from the database.
	 * @param 	species			The species of the enemy, required
	 * @param	race			The race specialization of the enemy, or null
	 * @param	unit			The unit specialization of the enemy, or null
	 * @return					The game unit result from this combination
	 */
	protected static GameUnit generateUnit(SpeciesMDO species, RaceMDO race, UnitMDO unit) {
		SerializedStatsMDO statsMDO = MGlobal.data.getEntryFor(
				KEY_DEFAULT_STATS, SerializedStatsMDO.class);
		SdrlStats stats = new SdrlStats(statsMDO.stats);
		
		if (species != null) {
			SdrlStats speciesStats = new SdrlStats(species.statsMod);
			stats.combine(speciesStats);
		}
		if (race != null) {
			SdrlStats raceStats = new SdrlStats(race.statsMod);
			stats.combine(raceStats);
		}
		if (unit != null) {
			SdrlStats unitStats = new SdrlStats(unit.statsMod);
			stats.combine(unitStats);
		}
		
		List<String> abilities = new ArrayList<String>();
		if (species != null) {
			abilities.addAll(Arrays.asList(species.abilities));
		}
		if (race != null) {
			abilities.addAll(Arrays.asList(race.abilities));
		}
		if (unit != null) {
			abilities.addAll(Arrays.asList(unit.abilities));
		}
		
		return new GameUnit(stats, abilities);
	}
	
	/**
	 * Creates an enemy unit from the database.
	 * @param 	species			The species of the enemy, required
	 * @param	race			The race specialization of the enemy, or null
	 * @param	unit			The unit specialization of the enemy, or null
	 * @return					The AnimationMDO key string for this combination
	 */
	protected static String generateAppearance(SpeciesMDO species, RaceMDO race, UnitMDO unit) {
		if (MapThing.mdoHasProperty(unit.appearance)) {
			return unit.appearance;
		}
		if (MapThing.mdoHasProperty(race.appearance)) {
			return race.appearance;
		}
		return species.appearance;
	}

}
