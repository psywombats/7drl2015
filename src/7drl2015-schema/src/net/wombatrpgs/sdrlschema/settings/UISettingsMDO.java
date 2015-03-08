/**
 *  UISettingsMDO.java
 *  Created on Feb 2, 2013 3:51:54 AM for project RainfallSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrlschema.settings;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.Nullable;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.sdrlschema.graphics.AnimationMDO;
import net.wombatrpgs.sdrlschema.graphics.IconSetMDO;
import net.wombatrpgs.sdrlschema.ui.FontMDO;
import net.wombatrpgs.sdrlschema.ui.HudMDO;
import net.wombatrpgs.sdrlschema.ui.NarratorMDO;
import net.wombatrpgs.sdrlschema.ui.PromptMDO;
import net.wombatrpgs.sdrlschema.ui.SkillsBoxMDO;
import net.wombatrpgs.sdrlschema.ui.TextBoxMDO;

/**
 * Some UI stuff?
 */
@Path("settings/")
public class UISettingsMDO extends MainSchema {
	
	@Desc("Default font")
	@SchemaLink(FontMDO.class)
	public String font;
	
	@Desc("Default text box")
	@SchemaLink(TextBoxMDO.class)
	public String box;
	
	@Desc("Default HUD")
	@SchemaLink(HudMDO.class)
	@Nullable
	public String hud;
	
	@Desc("Default skills HUD")
	@SchemaLink(SkillsBoxMDO.class)
	@Nullable
	public String skills;
	
	@Desc("Default icon set")
	@SchemaLink(IconSetMDO.class)
	public String icons;
	
	@Desc("Default narrator")
	@SchemaLink(NarratorMDO.class)
	public String narrator;
	
	@Desc("Exit game prompt")
	@SchemaLink(PromptMDO.class)
	public String prompt;
	
	@Desc("Cursor graphic")
	@SchemaLink(AnimationMDO.class)
	public String cursor;

}
