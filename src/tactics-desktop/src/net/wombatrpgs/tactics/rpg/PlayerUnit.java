/**
 *  PlayerUnit.java
 *  Created on Feb 12, 2014 8:33:00 PM for project tactics-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.tactics.rpg;

import net.wombatrpgs.mgneschema.io.data.InputCommand;
import net.wombatrpgs.tacticsschema.rpg.PlayerUnitMDO;

/**
 * Any unit controlled by the player.
 */
public class PlayerUnit extends GameUnit {
	
	protected PlayerUnitMDO mdo;
	protected int energySpentThisTurn;
	protected boolean endedAnimating;

	/**
	 * Constructs a player unit for a player. This should be only constructed
	 * during initialization, basically, and then assigned to battles as
	 * dictated by the player.
	 * @param	mdo				The mdo to construct from
	 */
	protected PlayerUnit(PlayerUnitMDO mdo) {
		super(mdo);
		this.mdo = mdo;
	}

	/**
	 * @see net.wombatrpgs.tactics.rpg.GameUnit#internalStartTurn()
	 */
	@Override
	public void internalStartTurn() {
		// we should be receiving commands about now
		energySpentThisTurn = 0;
		endedAnimating = false;
		
		battle.getMap().highlightMovement(this);
	}

	/**
	 * @see net.wombatrpgs.tactics.rpg.GameUnit#doneWithTurn()
	 */
	@Override
	public int doneWithTurn() {
		if (endedAnimating) {
			return energySpentThisTurn;
		} else {
			return -1;
		}
	}

	/**
	 * @see net.wombatrpgs.tactics.rpg.GameUnit#onCommand
	 * (net.wombatrpgs.mgneschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		// TODO Auto-generated method stub
		return super.onCommand(command);
	}

}
