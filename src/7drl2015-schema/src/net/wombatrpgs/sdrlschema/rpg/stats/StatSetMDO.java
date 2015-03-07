/**
 *  StatSet.java
 *  Created on Apr 2, 2014 9:48:59 PM for project saga-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg.stats;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.HeadlessSchema;

/**
 * Headless stat grouping.
 */
public class StatSetMDO extends HeadlessSchema {
	
	@Desc("MHP - max hp")
	public Integer mhp;
	
	@Desc("HP - health points")
	public Integer hp;
	
	@Desc("MMP - max mp")
	public Integer mmp;
	
	@Desc("MP - mana points")
	public Integer mp;
	
	@Desc("MSP - max sp")
	public Integer msp;
	
	@Desc("SP - stamina points")
	public Integer sp;
	
	@Desc("DV - dodge")
	public Integer dv;
	
	@Desc("PV - armor")
	public Integer pv;
	
	@Desc("Speed - in speed points, usually 100")
	public Integer speed;
	
	@Desc("Vision - in tile radius")
	public Integer vision;
	
	@Desc("Flags")
	public Flag[] flags;

}
