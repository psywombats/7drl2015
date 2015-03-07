/**
 *  NumericStat.java
 *  Created on Feb 28, 2014 5:00:44 PM for project mgne
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.stats;

/**
 * Any stat, additive or multiplicative, with a float value.
 */
public abstract class NumericStat extends StatType<Float> {
	
	/**
	 * Inherited constructor.
	 * @param	id				The unique identifier of this stat
	 * @param	zero			The zero-value of this stat, 0 or 1 really
	 */
	public NumericStat(String id, Float zero) {
		super(id, zero);
	}

}
