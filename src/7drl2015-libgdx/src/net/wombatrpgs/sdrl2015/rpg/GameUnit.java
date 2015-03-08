/**
 *  GameUnit.java
 *  Created on Oct 5, 2013 3:27:36 PM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.core.Queueable;
import net.wombatrpgs.sdrl2015.core.Turnable;
import net.wombatrpgs.sdrl2015.rpg.abil.Ability;
import net.wombatrpgs.sdrl2015.rpg.act.Action;
import net.wombatrpgs.sdrl2015.rpg.ai.Allegiance;
import net.wombatrpgs.sdrl2015.rpg.item.Inventory;
import net.wombatrpgs.sdrl2015.rpg.item.Item;
import net.wombatrpgs.sdrl2015.rpg.stats.SdrlStats;
import net.wombatrpgs.sdrl2015.rpg.stats.Stats;
import net.wombatrpgs.sdrl2015.ui.Narrator;
import net.wombatrpgs.sdrlschema.rpg.AbilityMDO;
import net.wombatrpgs.sdrlschema.rpg.HeroMDO;
import net.wombatrpgs.sdrlschema.rpg.data.Relation;
import net.wombatrpgs.sdrlschema.rpg.stats.Stat;

/**
 * A GameUnit is designed to factor out some of that shittiness that happens
 * when one class represents a unit's physical manifestation and its status as
 * an actor in an RPG. You end up with animation and move stuff side by side
 * with stats and attack methods. Bad. So now there's a GameUnit and a
 * CharacterEvent. It might be worth mentioning that the schema for a
 * character is sill the same, it just gets its functionality split between
 * the two manifestation classes.
 */
public class GameUnit implements Turnable, Queueable {
	
	/** lol this thing is because I'm too lazy to type MGlobal.ughhhh.nar ugh */
	// ^ old me was stupid
	protected static Narrator out;
	
	protected CharacterEvent parent;
	protected List<Turnable> turnChildren, toRemove;
	protected List<Queueable> assets;
	
	protected String name;
	protected SdrlStats stats;
	protected Allegiance allegiance;
	protected Inventory inventory;
	protected List<Ability> abilities;
	
	/**
	 * Creates a new character from data.
	 * @param	mdo				The data to create the character from
	 * @param	parent			The owner of this character, ie, its body
	 */
	public GameUnit(HeroMDO mdo, CharacterEvent parent) {
		this.parent = parent;
		this.turnChildren = new ArrayList<Turnable>();
		this.toRemove = new ArrayList<Turnable>();
		this.assets = new ArrayList<Queueable>();
		stats = new SdrlStats(mdo.stats);
		allegiance = new Allegiance(this);
		inventory = new Inventory(this);
		turnChildren.add(allegiance);
		
		abilities = new ArrayList<Ability>();
		for (String mdoKey : mdo.abilities) {
			abilities.add(new Ability(parent,
					MGlobal.data.getEntryFor(mdoKey, AbilityMDO.class)));
		}
		assets.addAll(abilities);
		
		if (out == null) out = MGlobal.ui.getNarrator();
	}
	
	/** @return Laziness personified */
	public static Narrator out() { return out; }
	
	/** @return The current stats of this unit */
	public Stats stats() { return stats; }
	
	/** @return The physical manifestation of this game unit */
	public CharacterEvent getParent() { return parent; }
	
	/** @param name This unit's new player-facing name */
	public void setName(String name) { this.name = name; }
	
	/** @return The political views of this unit */
	public Allegiance getAllegiance() { return this.allegiance; }
	
	/** @return True if this unit has fallen in mortal combat */
	public boolean isDead() { return get(Stat.MHP) <= 0; }
	
	/** @return The list of all the abilities this unit can perform */
	public List<Ability> getAbilities() { return abilities; }
	
	/** @return The inventory of all carried items */
	public Inventory getInventory() { return inventory; }
	
	/** @param turn The new turn child to register */
	public void addTurnChild(Turnable turn) { turnChildren.add(turn); }
	
	/** @param turn The old turn child to remove */
	public void removeTurnChild(Turnable turn) { toRemove.add(turn); }
	
	/**
	 * @see net.wombatrpgs.mrogue.core.Queueable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		for (Queueable asset : assets) {
			asset.queueRequiredAssets(manager);
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.core.Queueable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		for (Queueable asset : assets) {
			asset.postProcessing(manager, pass);
		}
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.core.Turnable#onTurn()
	 */
	@Override
	public void onTurn() {
		for (Turnable t : turnChildren) {
			t.onTurn();
		}
		for (Turnable t : toRemove) {
			turnChildren.remove(t);
		}
	}
	
	/**
	 * Calculates the current value of the requested stat, taking into account
	 * all modifiers, equipment, and status penalties.
	 * @param	stat			The stat to get the value of
	 * @return					The value of the requested stat
	 */
	public int get(Stat stat) {
		int value = Math.round(stats.stat(stat));
		// status effects apply here
		return value;
	}
	
	/**
	 * Fetches the player-facing name of this unit. This takes in-game
	 * considerations into effect, such as if the hero can see us.
	 * @return					This unit's player-facing name
	 */
	public String getName() {
		return MGlobal.hero.inLoS(parent) ? name : "something";
	}
	
	/**
	 * Calculates the energy points it would take this unit to attack in melee.
	 * @return					The melee attack cost, in energy points
	 */
	public int getAttackEP() {
		return Action.BASE_COST;
	}
	
	/**
	 * Calculates the energy points it would take this unit to walk one tile.
	 * @return					The walk cost, in energy points
	 */
	public int getWalkEP() {
		return Action.BASE_COST;
	}

	/**
	 * Determines how we respond to another unit.
	 * @param	other			The unit to respond to
	 * @return					How we respond to them
	 */
	public Relation getRelationTo(GameUnit other) {
		return allegiance.getRelationTo(other);
	}
	
	/**
	 * Determines how we respond to another character.
	 * @param	other			The chara we respond to
	 * @return					How we respond to them
	 */
	public Relation getRelationTo(CharacterEvent other) {
		return getRelationTo(other.getUnit());
	}
	
	/**
	 * Merges a stats modifier into this unit's stats.
	 * @param	stats			The stats to combine with
	 * @param	decombine		True to invert the stats first
	 */
	public void applyStatset(SdrlStats stats, boolean decombine) {
		if (decombine) {
			stats.decombine(stats);
		} else {
			stats.combine(stats);
		}
	}

	/**
	 * Launches a basic melee attack against the other unit.
	 * @param other
	 */
	public void attack(GameUnit other) {
		String us = getName();
		String them = other.getName();
		if (other.calcDodgeChance() < MGlobal.rand.nextFloat()) {
			int dealt = other.takePhysicalDamage(calcMeleeDamage());
			if (visible(this, other)) {
				if (dealt > 0) {
					out.msg(us + " attacked " + them + " for " + dealt + " damage.");
				} else {
					out.msg(us + " failed to harm " + them + ".");
				}
			}
			other.ensureAlive();
		} else {
			if (visible(this, other)) {
				out.msg(us + " missed " + them + ".");
			}
		}
		other.onAttackBy(this);
	}
	
	/**
	 * Called whenever a hit launches an attack against us, successful or not.
	 * The outcome of the attack itself is handled elsewhere.
	 * @param	other			The unit that launched the attack
	 */
	public void onAttackBy(GameUnit other) {
		allegiance.addToHitlist(other);
		for (CharacterEvent chara : parent.getParent().getCharacters()) {
			if (chara!= parent && chara.inLoS(parent) && chara.inLoS(other.parent)) {
				if (chara.getUnit().getRelationTo(this).avenge) {
					chara.getUnit().getAllegiance().addToHitlist(other);
				}
			}
		}
	}
	
	/**
	 * Inflicts a set amount of damage. Does not handle death or any other
	 * stats like attack or defense.
	 * @param	damage			The amount of damage to take, in hp
	 */
	public void takeRawDamage(int damage) {
		stats.subtract(Stat.HP, damage);
		if (damage > 0) {
			parent.flash(Color.RED, MGlobal.constants.getDelay()*1.6f);
		} else if (damage < 0) {
			parent.flash(Color.BLUE, MGlobal.constants.getDelay()*1.6f);
		}
	}
	
	/**
	 * Inflicts an amount of physical damage on this character. Deals with
	 * armor but not death. This is mostly so that printouts complete in the
	 * correct order.
	 * @param	damage			The amount of physical damage to deal, in hp
	 * @return					The amount of damage actually dealt, in hp
	 */
	public int takePhysicalDamage(int damage) {
		int dealt = calcPhysicalDamageTaken(damage);
		if (dealt > 0) {
			parent.flash(Color.RED, MGlobal.constants.getDelay()*1.6f);
		}
		return dealt;
	}
	
	/**
	 * Inflicts an amount of magic damage on this character. Deals with armor
	 * but not death. This is mostly so that printouts complete in the
	 * correct order.
	 * @param	damage			The amount of magic damage to deal, in hp
	 * @return					The amount of damage actually dealt, in hp
	 */
	public int takeMagicDamage(int damage) {
		int dealt = calcMagicDamageTaken(damage);
		if (dealt > 0) {
			parent.flash(Color.RED, MGlobal.constants.getDelay()*1.6f);
		}
		return dealt;
	}
	
	/**
	 * Heals the unit by some amount up to its maximum HP.
	 * @param	healAmount		The amount to heal
	 * @return					The amount actually healed
	 */
	public int heal(int healAmount) {
		int healt = Math.min(get(Stat.MHP) - get(Stat.HP), healAmount);
		stats.add(Stat.HP, healt);
		return healt;
	}
	
	/**
	 * Checks status effects, MP cost, etc to see if a unit is in a position to
	 * use an ability. Doesn't worry about if there are any targets in range, 
	 * or other logical things. Just hard rules.
	 * @param	abil			The ability to check
	 * @return					True if the ability can be used, false otherwise
	 */
	public boolean canUse(Ability abil) {
		// TODO: canUse(Ability abil)
		return true;
	}
	
	/**
	 * A callback for when this unit uses any sort of ability. The ability
	 * itself will call this method. MP costs and the like should be deducted
	 * here.
	 * @param	abil			The ability that was used
	 */
	public void onAbilityUsed(Ability abil) {
		// TODO: deduct resource costs
		if (visible(this)) {
			out.msg(getName() + " used " + abil.getName() + ".");
		}
	}
	
	/**
	 * Makes sure our health is above 0. If it isn't, kill self.
	 */
	public void ensureAlive() {
		if (get(Stat.HP) <= 0) {
			die();
		}
	}
	
	/**
	 * Called when this unit dies of unnatural causes, such as being slaughtered
	 * by an enemy. Not meant for things like screen removal. Everything about
	 * kill credits and whatever should go here.
	 */
	public void die() {
		parent.getParent().removeEvent(parent);
		if (visible(this)) {
			out.msg(getName() + " was killed.");
		}
		for (Item i : inventory.getItems()) {
			parent.getParent().addEvent(i.getEvent(), parent.getTileX(), parent.getTileY());
		}
	}
	
	/**
	 * Allies ourselves with a particular other unit.
	 * @param	other			The unit to ally with
	 */
	public void ally(GameUnit other) {
		allegiance.addToFriendlist(other);
		other.allegiance.addToFriendlist(this);
	}
	
	/**
	 * Gets what we're supposed to be killing at the moment, or null if no
	 * target. This scans visible enemies and identifies the nearest enemy.
	 * @return
	 */
	public GameUnit getTarget() {
		float minDist = Float.MAX_VALUE;
		GameUnit best = null;
		for (GameUnit enemy : getVisibleEnemies()) {
			float dist = enemy.getParent().distanceTo(getParent());
			if (dist < minDist) {
				minDist = dist;
				best = enemy;
			}
		}
		return best;
	}
	
	/**
	 * Gathers a list of all enemy units in sight. Relies on other LoS methods.
	 * @return					A list of all hostiles in the area
	 */
	public List<GameUnit> getVisibleEnemies() {
		List<GameUnit> units = getVisibleUnits();
		List<GameUnit> enemies = new ArrayList<GameUnit>();
		for (GameUnit unit : units) {
			if (getRelationTo(unit).attackOnSight && !(unit == this)) {
				enemies.add(unit);
			}
		}
		return enemies;
	}
	
	/**
	 * Gathers a list of all units in our line of sight. In practice this is
	 * achieved by querying everyone on the map for their position information,
	 * checking if they're in LoS. It's probably kind of cumbersome, and it may
	 * be worth handling this differently in the future.
	 * @return					A list of all units in the area
	 */	
	public List<GameUnit> getVisibleUnits() {
		List<GameUnit> units = new ArrayList<GameUnit>();
		for (CharacterEvent chara : parent.getParent().getCharacters()) {
			if (parent.inLoS(chara)) {
				units.add(chara.getUnit());
			}
		}
		return units;
	}
	
	/**
	 * Called by items when we pick them up. Add to inventory or something.
	 * @param	item			The item to pick up
	 */
	public void pickUp(Item item) {
		if (visible(this)) {
			out.msg(getName() + " picked up " + item.getName() + ".");
		}
		inventory.addItem(item);
	}
	
	/**
	 * A shortcut for an ugly if statement. Checks if the hero can see any of
	 * the units provided.
	 * @param	units			The list of units to check
	 * @return					True if any of the units are visible
	 */
	public boolean visible(GameUnit... units) {
		for (GameUnit unit : units) {
			if (MGlobal.hero.inLoS(unit.getParent())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Calculates how fast this unit should move compared to average. Does this
	 * with the speed stat.
	 * @return					The speed mod, a multiplier centered around 1.0
	 */
	public float calcSpeedMod() {
		return get(Stat.SPEED) / 100f;
	}
	
	/**
	 * Calculates the chance for this unit to dodge an arbitrary melee attack.
	 * @return					The percent chance to dodge, from 0 to 1
	 */
	protected float calcDodgeChance() {
		// Welp, this sure is balanced!
		return get(Stat.DV) / 100f;
	}
	
	/**
	 * Calculates output melee damage of this unit, given its current stats
	 * and equipment. It's fine to use the RNG.
	 * @return					A randomized melee damage output, in HP
	 */
	protected int calcMeleeDamage() {
		// TODO: obvious
		return 10;
	}
	
	/**
	 * Calculates the actual amount of damage sustained from a melee attack,
	 * taking armor and equipment into account.
	 * @param	damage			The raw incoming melee damage
	 * @return					The amount of damage to actually take
	 */
	protected int calcPhysicalDamageTaken(int damage) {
		return Math.max(0, damage - get(Stat.PV));
	}
	
	/**
	 * Calculates the actual amount of damage sustained from a magical attack,
	 * taking armor and equipment into account, if we ever add magic armor.
	 * Resistances shouldn't be checked here, ideally.
	 * @param	damage			The raw incoming magic damage
	 * @return					The amount of damage to actually take
	 */
	protected int calcMagicDamageTaken(int damage) {
		return damage;
	}

}
