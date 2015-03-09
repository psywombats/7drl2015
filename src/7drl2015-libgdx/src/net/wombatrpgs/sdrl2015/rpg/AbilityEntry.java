/**
 *  AbilityEntry.java
 *  Created on Mar 9, 2015 1:13:38 AM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg;

import net.wombatrpgs.sdrl2015.rpg.abil.Ability;

/**
 * An ability and a mark of how many sources it is known from.
 */
public class AbilityEntry {
	
	protected Ability ability;
	protected int sources;
	
	/**
	 * Creates a new ability entry with 1 source.
	 * @param	ability			The ability to grant
	 */
	public AbilityEntry(Ability ability) {
		this.ability = ability;
		this.sources = 1;
	}
	
	/** Increases the number of sources that grant this abil by 1 */
	public void incrementSources() { sources += 1; }
	
	/** Decreases the number of sources that grant this abil by 1 */
	public void decrementSources() { sources -= 1; }
	
	/** @return The number of ability-granting sources */
	public int getSourceCount() { return sources; }
	
	/** @return The underlying ablility of this entry */
	public Ability getAbility() { return ability; }
	
	/**
	 * Checks if this ability entry should merge with the given ability.
	 * @param	ability			The ability to check if is the same
	 * @return
	 */
	public boolean matches(Ability ability) {
		return ability.getKey().equals(this.ability.getKey());
	}
	
	/**
	 * Checks if this ability entry should merge with the given ability.
	 * @param	abilityKey		The ability key to check if is the same
	 * @return
	 */
	public boolean matches(String abilityKey) {
		return abilityKey.equals(this.ability.getKey());
	}

}
