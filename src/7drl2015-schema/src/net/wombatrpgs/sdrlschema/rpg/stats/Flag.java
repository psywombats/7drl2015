/**
 *  Flag.java
 *  Created on Feb 28, 2014 6:04:18 PM for project tactics-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.stats;

/**
 * Enum of all flags in Saga.
 */
public enum Flag implements FlagStatLinkable {
	
	RESIST_FIRE,
	RESIST_ICE;
	// etc

	private FlagStat flag;
	
	/**
	 * @see net.wombatrpgs.mgneschema.rpg.data.FlagStatLinkable#getFlag()
	 */
	@Override
	public FlagStat getFlag() {
		if (flag == null) {
			flag = new FlagStat(this.name());
		}
		return flag;
	}

}
