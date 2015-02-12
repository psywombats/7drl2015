/**
 *  SceneWait.java
 *  Created on Jan 24, 2014 2:49:05 AM for project saga
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mgne.scenes.commands;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.scenes.SceneCommand;
import net.wombatrpgs.mgne.scenes.SceneLib;
import net.wombatrpgs.mgne.ui.text.BlockingTextBox;

/**
 * A hero speaks!
 * Usage: {@code speak([name], <speech>)}
 */
public class SceneSpeak extends VarArgFunction {

	/**
	 * @see org.luaj.vm2.lib.VarArgFunction#invoke(org.luaj.vm2.Varargs)
	 */
	@Override
	public Varargs invoke(final Varargs args) {
		SceneLib.addFunction(new SceneCommand() {
			
			BlockingTextBox box;
			String text;
			boolean block;
			
			/* Initializer */ {
				text = args.checkjstring(1);
				if (args.narg() == 2) {
					block = args.checkboolean(2);
				} else {
					block = false;
				}
			}
			
			@Override protected void internalRun() {
				if (block) {
					MGlobal.getHero().pause(true);
				}
				box = MGlobal.ui.getBlockingBox();
				if (box.isBlocking()) {
					box.hurryUp();
					boolean animateOff = (index == count-1);
					box.blockText(parent.getScreen(), text, false, animateOff);
				} else {
					boolean animateOn = (index == 0);
					boolean animateOff = (index == count-1);
					box.blockText(parent.getScreen(), text, animateOn, animateOff);
				}
			}
			
			@Override protected boolean shouldFinish() {
				return !box.isBlocking() && super.shouldFinish();
			}

			@Override protected void finish() {
				super.finish();
				if (block) {
					MGlobal.getHero().pause(false);
				}
			}
			
		});
		return LuaValue.NIL;
	}
	
}
