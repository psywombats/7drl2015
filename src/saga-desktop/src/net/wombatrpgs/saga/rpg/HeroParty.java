/**
 *  HeroParty.java
 *  Created on Apr 4, 2014 7:49:30 PM for project saga-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.rpg;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.saga.core.SGlobal;
import net.wombatrpgs.sagaschema.rpg.chara.PartyMDO;

/**
 * The hero party. Contains four or five dauntless heroes.
 */
public class HeroParty extends Party {
	
	protected int gp;
	protected String location;
	
	/**
	 * Creates the hero party by looking the default up in the database.
	 */
	public HeroParty() {
		this(MGlobal.data.getEntryFor(SGlobal.settings.heroParty, PartyMDO.class));
		gp = 0;
	}

	/**
	 * The hero party. Really should only be called once at the beginning of the
	 * game. Oh and most likely it'll be empty except for testing purposes.
	 * @param	mdo				The data to start with
	 */
	protected HeroParty(PartyMDO mdo) {
		super(mdo);
	}
	
	/** @return The name of the location of the party in the world */
	public String getLocation() { return location; }
	
	/** @param name The new name of the location of the party in the world */
	public void setLocation(String name) { this.location = name; }
	
	/** @return The amount of gold carried by the party */
	public int getGP() { return gp; }
	
	/** @param gp The amount of gold to give to the party */
	public void addGP(int gp) { this.gp += gp; }
	
	/** @param gp The amount of gold to take from the party, assumes enough */
	public void removeGP(int gp) { this.gp -= gp; }
	
	/**
	 * Adds a character to the party.
	 * @param	hero			The character to add
	 */
	public void addHero(Chara hero) {
		List<Chara> group = new ArrayList<Chara>();
		group.add(hero);
		groups.add(group);
	}

}