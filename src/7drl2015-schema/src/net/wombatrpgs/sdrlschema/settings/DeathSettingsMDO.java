/**
 *  DeathSettingsMDO.java
 *  Created on Oct 23, 2013 5:37:49 AM for project MRogueSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.settings;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.FileLink;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.MainSchema;

/**
 * What happens when you bite it.
 */
@Path("settings/")
public class DeathSettingsMDO extends MainSchema {
	
	@Desc("Death image")
	@FileLink("ui")
	public String bg;

}
