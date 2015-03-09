/**
 *  SelectionListener.java
 *  Created on Mar 9, 2015 12:13:29 AM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.ui;

/**
 * Listens for the result of a selection.
 */
public interface SelectionListener {
	
	/**
	 * Called when a selection is made.
	 * @param	selection		The selection index, from 0
	 */
	public void onResult(int selection);

	/**
	 * Called when a selection isn't made. Ha.
	 */
	public void onCancel();

}
