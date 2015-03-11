/**
 *  DangerCondition.java
 *  Created on Mar 10, 2015 8:04:12 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.maps.MapThing;
import net.wombatrpgs.sdrlschema.rpg.DangerRequirementMDO;

/**
 * Dumb wrapper for danger level checks.
 */
public class DangerCondition {
	
	protected final int minLevel;
	protected final int maxLevel;
	
	/**
	 * Creates a reusable condition checker from data.
	 * @param	mdo				The data to parse from
	 */
	public DangerCondition(DangerRequirementMDO mdo) {
		if (MapThing.mdoHasProperty(mdo.boundsString)) {
			String[] tokens = mdo.boundsString.split("-");
			if (tokens.length != 2) {
				MGlobal.reporter.err("Malformatted bounds string: " + mdo.boundsString);
			}
			minLevel = Integer.valueOf(tokens[0]);
			maxLevel = Integer.valueOf(tokens[1]);
		} else {
			minLevel = Integer.MIN_VALUE;
			maxLevel = Integer.MAX_VALUE;
		}
	}
	
	/**
	 * Private constructor with explicit levels.
	 * @param	minLevel		The minimum allowed level
	 * @param	maxLevel		The maximum allowed level
	 */
	private DangerCondition(int minLevel, int maxLevel) {
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}
	
	/**
	 * Merges this requirement with another requirement. The result requirement
	 * will only be met if both source requirements are.
	 * @param	other			The other danger condition to merge in
	 * @return					The combined danger condition
	 */
	public DangerCondition merge(DangerCondition other) {
		DangerCondition result = new DangerCondition(
				Math.max(minLevel, other.minLevel),
				Math.min(maxLevel, other.maxLevel));
		return result;
	}
	
	/**
	 * Checks if this condition is satisfied.
	 * @param	dangerLevel		The current danger level
	 * @return					True if this condition is met
	 */
	public boolean isMet(int dangerLevel) {
		return (dangerLevel >= minLevel && dangerLevel <= maxLevel);
	}
	
	/**
	 * Checks if this condition is ever possible to meet.
	 * @return					True if condition can be met
	 */
	public boolean canBeMet() {
		return minLevel <= maxLevel;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return minLevel + "-" + maxLevel;
	}

}
