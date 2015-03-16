/**
 *  Enemy.java
 *  Created on Jan 23, 2013 9:14:38 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.enemy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.graphics.FacesAnimation;
import net.wombatrpgs.sdrl2015.graphics.FacesAnimationFactory;
import net.wombatrpgs.sdrl2015.graphics.effects.AbilFX;
import net.wombatrpgs.sdrl2015.graphics.effects.AbilFxFactory;
import net.wombatrpgs.sdrl2015.maps.Level;
import net.wombatrpgs.sdrl2015.maps.MapThing;
import net.wombatrpgs.sdrl2015.rpg.CharacterEvent;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrl2015.rpg.abil.Ability;
import net.wombatrpgs.sdrl2015.rpg.act.ActStep;
import net.wombatrpgs.sdrl2015.rpg.act.ActWander;
import net.wombatrpgs.sdrl2015.rpg.ai.AStarPathfinder;
import net.wombatrpgs.sdrl2015.rpg.ai.TacticType;
import net.wombatrpgs.sdrl2015.rpg.item.Item;
import net.wombatrpgs.sdrl2015.rpg.item.ItemList;
import net.wombatrpgs.sdrl2015.rpg.stats.SdrlStats;
import net.wombatrpgs.sdrlschema.maps.data.EightDir;
import net.wombatrpgs.sdrlschema.rpg.EnemyMDO;
import net.wombatrpgs.sdrlschema.rpg.ItemMDO;
import net.wombatrpgs.sdrlschema.rpg.RaceMDO;
import net.wombatrpgs.sdrlschema.rpg.SpeciesMDO;
import net.wombatrpgs.sdrlschema.rpg.UnitMDO;
import net.wombatrpgs.sdrlschema.rpg.data.Relation;
import net.wombatrpgs.sdrlschema.rpg.data.UniqueEffect;
import net.wombatrpgs.sdrlschema.rpg.stats.SerializedStatsMDO;
import net.wombatrpgs.sdrlschema.rpg.stats.Stat;

/**
 * The one and only class for those pesky badniks that hunt down the valiant
 * hero and hinder his quest to save the earth.
 */
public class EnemyEvent extends CharacterEvent {
	
	protected static final Float LOOT_CHANCE = .05f;
	
	protected static final String KEY_DEFAULT_STATS = "stats_default";
	protected static final String GENERIC_LOOT_DEFAULT = "itemlist_basic";
	
	protected SpeciesMDO species;
	protected RaceMDO race;
	protected UnitMDO unit;
	
	protected CharacterEvent lastTarget;
	protected int targetX, targetY;
	
	protected AbilFX fx;
	
	/**
	 * Creates a new enemy on a map from a database entry.
	 * @param 	species			The species of the enemy, required
	 * @param	race			The race specialization of the enemy, or null
	 * @param	unit			The unit specialization of the enemy, or null
	 */
	public EnemyEvent(SpeciesMDO species, RaceMDO race, UnitMDO unit) {
		super(generateUnit(species, race, unit), generateAppearance(species, race, unit));
		this.species = species;
		this.race = race;
		this.unit = unit;
		getUnit().setParent(this);
		String name = species.raceName;
		if (race != null && MapThing.mdoHasProperty(race.prefix)) {
			if (name.length() > 0) {
				name = race.prefix + " " + name;
			} else {
				name = race.prefix;
			}
		}
		if (unit != null && MapThing.mdoHasProperty(unit.suffix)) {
			name = name + " " + unit.suffix;
		}
		name = name.toLowerCase();
		getUnit().setName(name);
		
		if (race != null && MapThing.mdoHasProperty(race.overlay)) {
			FacesAnimation overlay = FacesAnimationFactory.create(race.overlay, this);
			overlays.add(overlay);
			assets.add(overlay);
		}
		if (unit != null && MapThing.mdoHasProperty(unit.overlay)) {
			FacesAnimation overlay = FacesAnimationFactory.create(unit.overlay, this);
			overlays.add(overlay);
			assets.add(overlay);
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
		getUnit().innatelyLearnAbilities(abilities);
		
		if ((race != null && race.effect == UniqueEffect.HOLY) ||
				(unit != null && unit.effect == UniqueEffect.HOLY)) {
			fx = AbilFxFactory.createFX("abilfx_holy_sfx", null);
			assets.add(fx);
		}
		
		if (unit != null) {
			if (unit.heldItems != null) {
				for (String key : unit.heldItems) {
					Item item = new Item(MGlobal.data.getEntryFor(key, ItemMDO.class));
					assets.add(item);
					getUnit().getInventory().addItem(item);
				}
			}
			if (MapThing.mdoHasProperty(unit.lootTable)) {
				ItemList lootTable = new ItemList(unit.lootTable);
				Item item = lootTable.generateItem();
				assets.add(item);
				getUnit().getInventory().addItem(item);
			}
		}
		if (race != null) {
			if (MapThing.mdoHasProperty(race.lootTable)) {
				ItemList lootTable = new ItemList(race.lootTable);
				Item item = lootTable.generateItem();
				assets.add(item);
				getUnit().getInventory().addItem(item);
			}
		}
		
		if (getUnit().getInventory().getItems().size() == 0 &&
				MGlobal.rand.nextFloat() < LOOT_CHANCE) {
			ItemList loot = new ItemList(GENERIC_LOOT_DEFAULT);
			Item item = loot.generateItem();
			assets.add(item);
			getUnit().getInventory().addItem(item);
		}
	}
	
	/**
	 * Creates an enemy from a serialized s/r/u combo.
	 * @param	mdo				The data to load from
	 */
	public EnemyEvent(EnemyMDO mdo) {
		this(MGlobal.data.getEntryFor(mdo.species, SpeciesMDO.class),
				MapThing.mdoHasProperty(mdo.race) ? MGlobal.data.getEntryFor(mdo.race, RaceMDO.class) : null,
				MapThing.mdoHasProperty(mdo.unit) ? MGlobal.data.getEntryFor(mdo.unit, UnitMDO.class) : null);
						
	}
	
	/** @return The unit data this enemy was created with */
	public UnitMDO getUnitMDO() { return unit; }
	
	/** @return The race data this enemy was created with */
	public RaceMDO getRaceMDO() { return race; }
	
	/** @return The species data this enemy was created with */
	public SpeciesMDO getSpeciesMDO() { return species; }
	
	/**
	 * @see net.wombatrpgs.mrogue.rpg.CharacterEvent#act()
	 */
	@Override
	public void act() {
		if (getUnit().get(Stat.HP) <= 0) {
			// this hack sucks, don't quite remember the reasoning here...
			this.ticksRemaining += 100000;
			return;
		} else {
			ai();
		}
	}
	
	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.CharacterEvent#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		super.render(camera);

	}

	/**
	 * @see net.wombatrpgs.sdrl2015.maps.MapThing#onAddedToMap(net.wombatrpgs.sdrl2015.maps.Level)
	 */
	@Override
	public void onAddedToMap(Level map) {
		super.onAddedToMap(map);
		if (fx != null) {
			fx.setParent(getParent());
			fx.setOverride(this);
			fx.spawn();
		}
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.maps.MapThing#onRemovedFromMap(net.wombatrpgs.sdrl2015.maps.Level)
	 */
	@Override
	public void onRemovedFromMap(Level map) {
		super.onRemovedFromMap(map);
		if (fx != null) {
			map.removeEvent(fx);
		}
	}

	/**
	 * Stupid AI hacked together for 7DRL. Makes no use of the BTNode junk.
	 */
	protected void ai() {
		
		List<Ability> abilities = getUnit().getAbilities();
		Collections.shuffle(abilities);
		
		// check for valid support skills
		for (Ability abil : abilities) {
			if (abil.getTactic() == TacticType.SUPPORT && abil.anyInRange()) {
				// this ability is potentially valid
				if (!abil.aiShouldUse(this)) {
					continue;
				} else {
					actAndWait(abil);
					return;
				}
			}
		}
		
		// check for valid random junk (teleports)
		for (Ability abil : abilities) {
			if (abil.getTactic() == TacticType.RANDOM && abil.anyInRange()) {
				// this ability is potentially valid
				if (!abil.aiShouldUse(this)) {
					continue;
				} else {
					actAndWait(abil);
					return;
				}
			}
		}
		
		// find target
		GameUnit targetUnit = getUnit().getTarget();
		CharacterEvent target = null;
		if (targetUnit != null) {
			target = targetUnit.getParent();
		}
		
		if (target != null) {
			
			// target found, do them harm
			lastTarget = target;
			targetX = lastTarget.getTileX();
			targetY = lastTarget.getTileY();
			
			// check for all abilities that might hit target
			for (Ability abil : abilities) {
				if (abil.getTactic() == TacticType.OFFENSE && abil.anyInRange()
						&& abil.getTargets().contains(targetUnit)) {
					// this ability is potentially valid
					if (!abil.aiShouldUse(this)) {
						continue;
					} else {
						actAndWait(abil);
						return;
					}
				}
			}
			
			// no abilities found, advance towards target
			AStarPathfinder finder = new AStarPathfinder();
			finder.setMap(this.getParent());
			finder.setStart(getTileX(), getTileY());
			finder.setTarget(target.getTileX(), target.getTileY());
			List<EightDir> path = finder.getPath(this);
			ActStep step = new ActStep(this);
			if (path == null) {
				// there was no path so we'll just blindly walk in that direction
				step.setDirection(directionTo(target));
			} else if (path.size() == 0) {
				MGlobal.reporter.warn("Pathfinding to self?");
				step.setDirection(getFacing().toEight());
			} else {
				step.setDirection(path.get(0));
			}
			actAndWait(step);
			
		} else {
			
			// no target found
			
			if (lastTarget != null) {
				
				// advance towards the last target's last known location
				if (getTileX() == targetX && getTileY() == targetY) {
					// we're at their location and they escaped! damn!!
					lastTarget = null;
					ai();
					return;
				} else {
					// no abilities found, advance towards target
					AStarPathfinder finder = new AStarPathfinder();
					finder.setMap(this.getParent());
					finder.setStart(getTileX(), getTileY());
					finder.setTarget(targetX, targetY);
					List<EightDir> path = finder.getPath(this);
					ActStep step = new ActStep(this);
					if (path == null) {
						// there was no path so we'll just blindly walk in that direction
						step.setDirection(directionTo(lastTarget));
					} else if (path.size() == 0) {
						MGlobal.reporter.warn("Pathfinding to self?");
						step.setDirection(getFacing().toEight());
					} else {
						step.setDirection(path.get(0));
					}
					actAndWait(step);
				}
				
			} else {
				
				// we have no known enemies, find allies maybe?
				for (GameUnit unit : getUnit().getVisibleUnits()) {
					if (getUnit().getRelationTo(unit) == Relation.ALLIED
							&& euclideanTileDistanceTo(unit.getParent()) > 2) {
						// path to the faraway ally
						ActStep step = new ActStep(this, directionTo(unit.getParent()));
						actAndWait(step);
						return;
					}
				}
				
				// no friends, no enemies... damn
				
				// check again for junk...
				for (Ability abil : abilities) {
					if (abil.getTactic() == TacticType.RANDOM && abil.anyInRange()) {
						// this ability is potentially valid
						if (!abil.aiShouldUse(this)) {
							continue;
						} else {
							actAndWait(abil);
							return;
						}
					}
				}
				
				// welp time to wander around
				ActWander wander = new ActWander(this);
				actAndWait(wander);
			}
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
		
		return new GameUnit(stats, race);
	}
	
	/**
	 * Creates an enemy unit from the database.
	 * @param 	species			The species of the enemy, required
	 * @param	race			The race specialization of the enemy, or null
	 * @param	unit			The unit specialization of the enemy, or null
	 * @return					The AnimationMDO key string for this combination
	 */
	protected static String generateAppearance(SpeciesMDO species, RaceMDO race, UnitMDO unit) {
		if (unit != null && MapThing.mdoHasProperty(unit.appearance)) {
			return unit.appearance;
		}
		if (race != null && MapThing.mdoHasProperty(race.appearance)) {
			return race.appearance;
		}
		return species.appearance;
	}

}
