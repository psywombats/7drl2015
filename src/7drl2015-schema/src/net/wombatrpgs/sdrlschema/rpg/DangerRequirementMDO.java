/**
 *  DangerRequirementMDO.java
 *  Created on Mar 10, 2015 7:55:32 PM for project 7drl2015-schema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.rpg;

import net.wombatrpgs.mgns.core.HeadlessSchema;
import net.wombatrpgs.mgns.core.Annotations.Desc;

/**
 * Danger level bounds.
 */
public class DangerRequirementMDO extends HeadlessSchema {
	
	@Desc("DL bounds - danger must fall between these falls (inclusive), formatted like 2-3, or blank for no bounds")
	public String boundsString;

}
