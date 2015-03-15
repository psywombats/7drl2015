/**
 *  Allegiance.java
 *  Created on Oct 16, 2013 2:09:46 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.ai;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.core.Turnable;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrlschema.rpg.data.Relation;

/**
 * A GameUnit owns one of these. It tells the unit what to attack, what not to
 * attack, etc. An allegience represents relationships to multiple factions as
 * well as to individual enemies that have wrought us harm.
 * 
 * 7DRL update: no longer encompasses a faction, but instead a race.
 */
public class Allegiance implements Turnable {
	
	protected GameUnit parent;
	protected List<GameUnit> hitlist;
	protected List<GameUnit> friendlist;
	
	/**
	 * Creates a new allegiance from a set of factions. Multi-faction support is
	 * accomplished by taking the lowest of the relations.
	 * @param	parent			The game unit with this allegiance
	 */
	public Allegiance(GameUnit parent) {
		this.parent = parent;
		this.hitlist = new ArrayList<GameUnit>();
		this.friendlist = new ArrayList<GameUnit>();
		
		friendlist.add(parent);
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.core.Turnable#onTurn()
	 */
	@Override
	public void onTurn() {
		List<GameUnit> deads = new ArrayList<GameUnit>();
		for (GameUnit unit : hitlist) {
			if (unit.isDead()) {
				deads.add(unit);
			}
		}
		for (GameUnit dead : deads) {
			hitlist.remove(dead);
		}
	}

	/**
	 * Determines the relation to some other unit, either by faction or
	 * personal preference. Not necessarily symmetric.
	 * @param	other			The game unit to get the relationship status to
	 * @return					The relation this unit has to the other unit
	 */
	public Relation getRelationTo(GameUnit other) {
		if (hitlist.contains(other)) {
			return Relation.HOSTILE;
		} else if (friendlist.contains(other)) {
			return Relation.ALLIED;
		} else if (parent == MGlobal.hero.getUnit()) {
			return Relation.HOSTILE;
		} else if (other == MGlobal.hero.getUnit()) {
			return Relation.HOSTILE;
		} else if (parent.getRace() == other.getRace()) {
			return Relation.ALLIED;
		} else {
			// TODO: maybe only ally with certain races?
			return Relation.FRIENDLY;
		}
	}
	
	/**
	 * Call this when some shithead attacks us.
	 * @param	jerk			The idiot who hit us
	 */
	public void addToHitlist(GameUnit jerk) {
		if (parent == jerk) return;
		if (getRelationTo(jerk).retaliate && !hitlist.contains(jerk)) {
			hitlist.add(jerk);
		}
	}
	
	/**
	 * This is really only called to give unnatural monsters some allegiance to
	 * each other, like when monsters spawn in a tension room.
	 * @param	buddy			The cool guy who's our friend now
	 */
	public void addToFriendlist(GameUnit buddy) {
		if (!friendlist.contains(buddy)) {
			friendlist.add(buddy);
		}
	}

}
