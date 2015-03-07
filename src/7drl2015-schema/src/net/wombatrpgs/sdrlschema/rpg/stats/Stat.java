/**
 *  Stat.java
 *  Created on Feb 28, 2014 6:10:13 PM for project tactics-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.stats;

/**
 * An enum of all numeric stats in Saga. The name is so brief because it's
 * going to be referenced all over the place.
 */
public enum Stat implements NumericStatLinkable {
	
	MHP					("max health"),
	HP					("health"),
	MMP					("max mana"),
	MP					("mana"),
	MSP					("max stamina"),
	SP					("stamina"),
	PV					("armor"),
	DV					("dodge"),
	SPEED				("speed"),
	VISION				("vision");
	
	
	private NumericStat stat;
	private String name;
	
	/**
	 * Internal enum constructor.
	 * @param	name			The full name for this stat
	 */
	Stat(String name) {
		this.name = name;
	}

	/**
	 * @see net.wombatrpgs.mgneschema.rpg.data.NumericStatLinkable#getStat()
	 */
	@Override
	public NumericStat getStat() {
		if (stat == null) {
			stat = new AdditiveStat(this.name());
		}
		return stat;
	}
	
	/**
	 * Returns the official manual-level quality name of this stat.
	 * @return					The name for this stat
	 */
	public String getFullName() {
		return name;
	}

}
