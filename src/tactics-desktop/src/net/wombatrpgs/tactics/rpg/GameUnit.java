/**
 *  GameUnit.java
 *  Created on Feb 12, 2014 2:39:47 AM for project tactics-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.tactics.rpg;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.core.interfaces.FinishListener;
import net.wombatrpgs.mgne.core.interfaces.Queueable;
import net.wombatrpgs.mgne.io.CommandListener;
import net.wombatrpgs.mgne.screen.TrackerCam;
import net.wombatrpgs.mgneschema.io.data.InputCommand;
import net.wombatrpgs.mgneschema.maps.EventMDO;
import net.wombatrpgs.tactics.core.TGlobal;
import net.wombatrpgs.tactics.maps.TacticsEvent;
import net.wombatrpgs.tactics.maps.TacticsMap;
import net.wombatrpgs.tacticsschema.rpg.GameUnitMDO;
import net.wombatrpgs.tacticsschema.rpg.PlayerUnitMDO;

/**
 * A unit in the tactics RPG part of the game. This includes a link to the
 * unit's physical incarnation, but does not encapsulate it. Extended by player
 * and AI versions.
 */
public abstract class GameUnit implements	CommandListener,
											Queueable {
	
	protected GameUnitMDO mdo;
	
	protected Battle battle;
	protected TacticsEvent event;
	protected TacticsMap map;
	protected Stats stats;
	
	protected boolean active;	// are we moving right now?
	protected int energy;		// highest energy moves first
	
	protected List<Queueable> assets;
	
	/**
	 * Factory method. Returns subclass based on MDO class.
	 * @return					A game unit of appropriate subtype
	 */
	public static GameUnit createGameUnit(GameUnitMDO mdo) {
		if (PlayerUnitMDO.class.isAssignableFrom(mdo.getClass())) {
			return new PlayerUnit((PlayerUnitMDO) mdo);
		} else {
			MGlobal.reporter.err("Bad gameunitmdo subtype: " + mdo.getClass());
			return null;
		}
	}
	
	/**
	 * Creates a game unit from data. Does nothing about placing it on the map
	 * or creating its physical version. Probably shouldn't be called.
	 * @param	mdo				The data to create unit from
	 */
	protected GameUnit(GameUnitMDO mdo) {
		this.mdo = mdo;
		assets = new ArrayList<Queueable>();
		
		stats = new Stats(mdo.stats);
		event = new TacticsEvent(this);
		assets.add(event);
	}
	
	/** @return This unit's stored energy, in ticks, higher is sooner */
	public int getEnergy() { return energy; }
	
	/** @param The energy this unit should gain based on some other spending */
	public void grantEnergy(int energy) { this.energy += energy; }
	
	/** @return The doll used by this unit on the level */
	public TacticsEvent getEvent() { return event; }
	
	/** @return The current stats of this unit */
	public Stats stats() { return stats; }
	
	/**
	 * @see net.wombatrpgs.mgne.core.interfaces.Queueable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		for (Queueable asset : assets) {
			asset.queueRequiredAssets(manager);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.core.interfaces.Queueable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		for (Queueable asset : assets) {
			asset.postProcessing(manager, pass);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.io.CommandListener#onCommand
	 * (net.wombatrpgs.mgneschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		// most likely, we're an enemy who doesn't care
		return false;
	}

	/**
	 * Called by the battle when it's this unit's turn. Automatically calls
	 * the appropriate internal methods.
	 */
	public final void onTurnStart() {
		active = true;
		TrackerCam cam = TGlobal.screen.getCamera();
		cam.panTo(event, new FinishListener() {
			@Override public void onFinish() {
				internalStartTurn();
			}
		});
	}
	
	/**
	 * Called by the battle when this unit's turn is 100% over.
	 */
	public final void onTurnEnd() {
		active = false;
	}
	
	/**
	 * Called by the battle to query if this unit is done taking its turn yet.
	 * This means that decision where to move has been made, and move has
	 * finished animating and resolving. Meant to be polled on update.
	 * @return					How much energy this unit spent this turn, or -1
	 * 							if the turn isn't over yet.
	 */
	public abstract int doneWithTurn();
	
	/**
	 * Called when this unit is queued for battle. This doesn't meant the unit
	 * is on the field, just they /could/ be used here.
	 * @param	battle			The battle we joined
	 */
	public void onAddedToBattle(Battle battle) {
		this.battle = battle;
	}

	/**
	 * Returns an eventMDO containing information about constructing a doll for
	 * this game unit. Safe to call/construct more than once.
	 * @return					An MDO with event information from this unit.
	 */
	public EventMDO extractEventMDO() {
		EventMDO dollMDO = new EventMDO();
		dollMDO.appearance = mdo.appearance;
		dollMDO.name = mdo.name;
		return dollMDO;
	}
	
	/**
	 * Called when this unit needs to be set back to defaults... Kind of weird
	 * use, usually only for players before they begin a new fight?
	 */
	public void reset() {
		energy = 0;
	}
	
	/**
	 * Adds this unit to the battle map at whatever location. This unit should
	 * have already been added to the battle.
	 * @param	tileX			The location to add at (in tiles)
	 * @param	tileY			The location to add at (in tiles)
	 */
	public void spawnAt(int tileX, int tileY) {
		battle.addDoll(event);
		event.setTileLocation(tileX, tileY);
	}
	
	/**
	 * Called when it's this unit's turn. Should take whatever action is needed,
	 * for AI units this is moving on its own and for players should probably
	 * just wait. This unit will already be hooked up and ready to receive
	 * commands from the player.
	 */
	protected abstract void internalStartTurn();

}