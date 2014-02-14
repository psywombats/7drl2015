/**
 *  TacticsScreen.java
 *  Created on Feb 12, 2014 3:25:02 AM for project tactics-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.tactics.screen;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.screen.instances.GameScreen;
import net.wombatrpgs.mgneschema.io.data.InputCommand;
import net.wombatrpgs.tactics.core.TGlobal;
import net.wombatrpgs.tactics.rpg.Battle;

/**
 * A screen where you're either walking around or beating on dudes with violins
 * or something. Creating a screen should only be done once.
 */
public class TacticsScreen extends GameScreen {
	
	protected boolean inTacticsMode;
	protected Battle battle;
	
	/**
	 * Creates a new tactics screen and sets itself as the tactics screen for
	 * the entire game.
	 */
	public TacticsScreen() {
		super();
		TGlobal.screen = this;
		inTacticsMode = false;
	}
	
	/** @return True if a tactics battle is on, false if roaming the world */
	public boolean inTacticsMode() { return inTacticsMode; }
	
	/** @param True if we should be tactics mode, false for roaming */
	public void setTacticsMode(boolean tacticsOn) { inTacticsMode = tacticsOn; }

	/**
	 * @see net.wombatrpgs.mgne.screen.instances.GameScreen#onCommand
	 * (net.wombatrpgs.mgneschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		if (inTacticsMode) {
			// I hope the battle knows what to do!
			return battle.onCommand(command);
		} else {
			// let's let the game default take care of it
			if (command == InputCommand.WORLD_PAUSE) {
				// TODO: tactics: hack to switch to tactics
				battle = new Battle(MGlobal.levelManager.getActive());
				battle.addParty();
				MGlobal.loadAsset(battle, "battle");
				battle.startBattle();
			}
			return super.onCommand(command);		
		}
	}

}