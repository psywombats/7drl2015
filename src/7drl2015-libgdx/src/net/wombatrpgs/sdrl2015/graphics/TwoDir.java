/**
 *  TwoDir.java
 *  Created on Jan 24, 2013 8:28:44 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.graphics;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.maps.events.MapEvent;
import net.wombatrpgs.sdrlschema.graphics.AnimationMDO;
import net.wombatrpgs.sdrlschema.graphics.TwoDirMDO;
import net.wombatrpgs.sdrlschema.maps.data.OrthoDir;

/**
 * The same thing as a FourDir, but with a left/right facing only.
 */
public class TwoDir extends FacesAnimation {
	
	protected TwoDirMDO mdo;
	protected int effectiveIndex;
	
	protected static final int LEFT_INDEX = 0;
	protected static final int RIGHT_INDEX = 1;

	/**
	 * Constructs and splices a 2dir
	 * @param 	mdo				The MDO with relevant data
	 * @param 	parent			The parent this 4dir is tied to
	 */
	public TwoDir(TwoDirMDO mdo, MapEvent parent) {
		super(parent, 2);
		this.mdo = mdo;
		sliceAnimations();
	}

	/**
	 * @see net.wombatrpgs.mrogue.graphics.FacesAnimation#setFacing
	 * (net.wombatrpgs.mrogueschema.maps.data.OrthoDir)
	 */
	@Override
	public void setFacing(OrthoDir dir) {
		super.setFacing(dir);
		if (dir == OrthoDir.WEST) {
			effectiveIndex = LEFT_INDEX;
		} else if (dir == OrthoDir.EAST) {
			effectiveIndex = RIGHT_INDEX;
		}
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.graphics.FacesAnimation#currentDirOrdinal()
	 */
	@Override
	protected int currentDirOrdinal() {
		return effectiveIndex;
	}

	/**
	 * @see net.wombatrpgs.mrogue.graphics.FacesAnimation#sliceAnimations()
	 */
	@Override
	protected void sliceAnimations() {
		animations[LEFT_INDEX] = new AnimationStrip(
				MGlobal.data.getEntryFor(mdo.leftAnim, AnimationMDO.class), parent);
		animations[RIGHT_INDEX] = new AnimationStrip(
				MGlobal.data.getEntryFor(mdo.rightAnim, AnimationMDO.class), parent);
	}

}
