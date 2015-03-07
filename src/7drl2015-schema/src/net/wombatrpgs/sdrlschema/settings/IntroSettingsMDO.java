/**
 *  IntroSettingsMDO.java
 *  Created on Feb 22, 2013 4:36:25 AM for project RainfallSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.settings;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.sdrlschema.audio.MusicMDO;
import net.wombatrpgs.sdrlschema.maps.MapMDO;

/**
 * What happens when the game starts.
 */
@Path("settings/")
public class IntroSettingsMDO extends MainSchema {
	
	@Desc("Tile screen music")
	@SchemaLink(MusicMDO.class)
	public String music;
	
	@Desc("Map - the map that things open on, usually a blank screen with the hero on it")
	@SchemaLink(MapMDO.class)
	public String map;

}
