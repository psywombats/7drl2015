/**
 *  TempStats.java
 *  Created on Apr 25, 2014 2:52:10 PM for project saga-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.stats;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;

/**
 * Struct for a stat set associated with character.
 */
public class TempStats {
	
	protected GameUnit unit;
	protected SdrlStats stats;
	protected boolean done;
	
	/**
	 * Creates a new temporary stat modifier for a character. This will apply
	 * the stats.
	 * @param	unit			The character to modifer
	 * @param	stats			The stats to modify by
	 */
	public TempStats(GameUnit unit, SdrlStats stats) {
		this.unit = unit;
		this.stats = stats;
		unit.applyStatset(stats, false);
		done = false;
	}
	
	/**
	 * Removes the statset from the character.
	 */
	public void decombine() {
		if (!done) {
			unit.applyStatset(stats, true);
		} else {
			MGlobal.reporter.warn("Tried double deapply to " + unit);
		}
	}

}
