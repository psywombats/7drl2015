/**
 *  Hero.java
 *  Created on Nov 25, 2012 8:33:22 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg;

import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.io.CommandListener;
import net.wombatrpgs.sdrl2015.maps.Level;
import net.wombatrpgs.sdrl2015.maps.events.MapEvent;
import net.wombatrpgs.sdrl2015.rpg.abil.Ability;
import net.wombatrpgs.sdrl2015.rpg.act.ActStep;
import net.wombatrpgs.sdrl2015.rpg.act.ActWait;
import net.wombatrpgs.sdrl2015.rpg.act.Action;
import net.wombatrpgs.sdrl2015.rpg.item.Item;
import net.wombatrpgs.sdrl2015.rpg.item.ItemEvent;
import net.wombatrpgs.sdrl2015.rpg.item.ItemList;
import net.wombatrpgs.sdrl2015.screen.instances.GameOverScreen;
import net.wombatrpgs.sdrl2015.ui.AbilityMenu;
import net.wombatrpgs.sdrlschema.io.data.InputCommand;
import net.wombatrpgs.sdrlschema.maps.data.EightDir;
import net.wombatrpgs.sdrlschema.rpg.HeroMDO;
import net.wombatrpgs.sdrlschema.rpg.stats.Stat;

/**
 * Placeholder class for the protagonist player.
 */
public class Hero extends CharacterEvent implements CommandListener {
	
	// terrible place to put these
	public static final int TURNS_PER_NIGHT = 100;
	public static final int PEACEFUL_TURNS_REQUIRED = 10;
	
	protected static final String HERO_DEFAULT = "hero_default";
	
	protected static final String START_PACK_DEFAULT = "itemlist_starter";
	protected static final int STARTER_ITEMS = 6;
	
	protected ActStep step;
	// to facilitate shader calls, viewtex is like a b/w image version of cache
	protected boolean[][] viewCache;
	protected boolean[][] seenCache;
	protected Pixmap p;
	protected Texture viewTex;
	protected Ability blockingAbil;
	
	// 7DRL hacky stuff about nights goes here
	protected int turnsSinceNight;
	protected int nightCount;		 // starts at 0 for no nights elapsed yet
	protected String baseMapKey;
	protected int baseX, baseY;

	/**
	 * Placeholder constructor. When the hero is finally initialized properly
	 * this will change. Right now it sets up the hero on the map like any other
	 * event. Also sets up the moveset called "default_moveset" though that
	 * should be put in the hero MDO when it gets created.
	 * MR: Creates the hero
	 * @param	parent			The level to make the hero on
	 */
	public Hero(Level parent) {
		super(MGlobal.data.getEntryFor(HERO_DEFAULT, HeroMDO.class));
		this.parent = parent;
		MGlobal.hero = this;
		step = new ActStep(this);
		getUnit().setName("you");
		turnsSinceNight = Integer.MAX_VALUE;
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.MapThing#onAddedToMap
	 * (net.wombatrpgs.mrogue.maps.Level)
	 */
	@Override
	public void onAddedToMap(Level map) {
		super.onAddedToMap(map);
		seenCache = map.getSeenCache();
	}

	/**
	 * @see net.wombatrpgs.mrogue.rpg.CharacterEvent#reset()
	 */
	@Override
	public void reset() {
		// oh hell no we ain't dyin
	}

	/**
	 * @see net.wombatrpgs.mrogue.rpg.CharacterEvent#inLoS(int, int)
	 */
	@Override
	public boolean inLoS(int targetX, int targetY) {
		if (viewCache == null) return false;
		return viewCache[targetY][targetX];
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.rpg.CharacterEvent#actAndWait(net.wombatrpgs.mrogue.rpg.act.Action)
	 */
	@Override
	public void actAndWait(Action act) {
		if (parent.isMoving()) {
			return;
		}
		MGlobal.ui.getHud().forceReset();
		MGlobal.ui.getNarrator().onTurn();
		this.onTurn();
		
		super.actAndWait(act);
		
		refreshVisibilityMap();
		parent.onTurn();
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.rpg.CharacterEvent#update(float)
	 */
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		if (MGlobal.ui.getInventory().wasEquipped()
				&& !MGlobal.ui.getInventory().isDisplaying()) {
			MGlobal.ui.getInventory().acknowledge();
			ActWait wait = new ActWait();
			actAndWait(wait);
		}
		if (unit.isDead()) {
			MGlobal.screens.pop();
			MGlobal.screens.push(new GameOverScreen());
			MGlobal.screens.playMusic(null, false);
		}
		if (blockingAbil != null) {
			if (blockingAbil.useAndBlock()) {
				blockingAbil = null;
			}
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.io.CommandListener#onCommand
	 * (net.wombatrpgs.mrogueschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		
		switch (command) {
		
		// WAIT
		case MOVE_WAIT:			actAndWait(defaultWait);	break;
		
		// MOVE
		case MOVE_NORTH:		move(EightDir.NORTH);		break;
		case MOVE_NORTHEAST:	move(EightDir.NORTHEAST);	break;
		case MOVE_EAST:			move(EightDir.EAST);		break;
		case MOVE_SOUTHEAST:	move(EightDir.SOUTHEAST);	break;
		case MOVE_SOUTH:		move(EightDir.SOUTH);		break;
		case MOVE_SOUTHWEST:	move(EightDir.SOUTHWEST);	break;
		case MOVE_WEST:			move(EightDir.WEST);		break;
		case MOVE_NORTHWEST:	move(EightDir.NORTHWEST);	break;
		
		// ABIL
		case ABIL_1:			abil(0);					break;
		case ABIL_2:			abil(1);					break;
		case ABIL_3:			abil(2);					break;
		case ABIL_4:			abil(3);					break;
		case ABIL_5:			abil(4);					break;
		case ABIL_6:			abil(5);					break;
		case ABIL_7:			abil(6);					break;
		case ABIL_8:			abil(7);					break;
		case ABIL_9:			abil(8);					break;
		case ABIL_10:			abil(9);					break;
		
		// ETC
		case INTENT_CAMP:		tryCamp();					break;
		case INTENT_INTERACT:	interact();					break;
			
		// DEFAULT
		default:
			MGlobal.reporter.warn("Unknown command " + command);
			return false;
		}
		
		return true;
	}
	
	/**
	 * @see net.wombatrpgs.sdrl2015.core.Turnable.onTurn()
	 */
	@Override
	public void onTurn() {
		super.onTurn();
		turnsSinceNight += 1;
	}
	
	/**
	 * Sets up a new camp for the night. If no camp has yet been created, this
	 * assumes that the camp setup is happening and airdrops a couple random
	 * items as a starter pack. Otherwise, it moves items from the old camp to
	 * the new one. Should also handle levelling up? Make sure to check if PC
	 * is eligible first.
	 */
	public void setUpCamp() {
		if (!isEligibleForCamp(true)) {
			MGlobal.reporter.warn("Ineligible for camp but camping anyway?");
		}
		
		if (baseMapKey == null) {
			// drop the starter pack crap
			ItemList starterPack = new ItemList(START_PACK_DEFAULT);
			for (int i = 0; i < STARTER_ITEMS; i += 1) {
				Item item = starterPack.generateItem();
				item.spawnNear(MGlobal.hero.getParent(),
						MGlobal.hero.getTileX(),
						MGlobal.hero.getTileY(),
						2);
			}
		} else {
			Level map = MGlobal.levelManager.getLevel(baseMapKey);
			for (int x = baseX - 2; x <= baseX + 2; x += 1) {
				for (int y = baseY - 2; y <= baseY + 2; y += 1) {
					List<MapEvent> events = map.getEventsAt(x, y);
					for (MapEvent event : events) {
						event.onCampMoved();
					}
				}
			}
			if (MGlobal.hero.getUnit().getAbilities().size() > 0) {
				AbilityMenu menu = new AbilityMenu(true);
				MGlobal.assetManager.loadAsset(menu, "abil level");
				menu.show();
			}
		}
		
		getUnit().onNight();
		baseMapKey = MGlobal.hero.getParent().getKey();
		baseX = MGlobal.hero.getTileX();
		baseY = MGlobal.hero.getTileY();
		nightCount += 1;
		turnsSinceNight = 0;
	}
	
	/**
	 * Checks if the hero is eligible to set up a campsite, and if not, spits
	 * the reason why out to the player. Not particularly elegant.
	 * @param	silent			True to suppress the reason messages
	 * @return					True if PC is can camp at the moment
	 */
	public boolean isEligibleForCamp(boolean silent) {
		if (getParent().getKey().equals(baseMapKey)) {
			if (!silent) {
				GameUnit.out.msg("Can't camp -- already have a camp this map");
			}
			return false;
		}
		if (turnsSinceNight < TURNS_PER_NIGHT) {
			int turnsNeeded = TURNS_PER_NIGHT - turnsSinceNight;
			if (!silent) {
				GameUnit.out().msg("Can't camp -- need to wait another " + 
						turnsNeeded + " turns");
			}
			return false;
		}
		if (unit.getTurnsSinceCombat() < PEACEFUL_TURNS_REQUIRED) {
			int turnsNeeded = PEACEFUL_TURNS_REQUIRED - unit.getTurnsSinceCombat();
			if (!silent) {
				GameUnit.out().msg("Can't camp now -- need to spend another " +
						turnsNeeded + " turns out of combat");
			}
			return false;
		}
		for (int x = getTileX()-1; x <= getTileX()+1; x += 1) {
			for (int y = getTileY() - 1; y <= getTileY()+1; y += 1) {
				if (!parent.isTilePassable(this, x, y)) {
					if (!silent) {
						GameUnit.out().msg("Can't camp next to a wall -- try "
								+ "the middle of a room");
					}
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Creates a cached table of which squares are in view. Call when things
	 * move etc.
	 */
	public void refreshVisibilityMap() {
		if (viewTex != null) {
			p.dispose();
		}
		int vision = getUnit().get(Stat.VISION);
		p = new Pixmap(parent.getWidth(), parent.getHeight(), Format.RGBA8888);
		viewCache = new boolean[parent.getHeight()][parent.getWidth()];
		Pixmap.setBlending(Blending.SourceOver);
		p.setColor(Color.BLACK);
		p.fillRectangle(0, 0, parent.getWidth(), parent.getHeight());
		int startTX = tileX - vision;
		int startTY = tileY - vision;
		int endTX = tileX + vision;
		int endTY = tileY + vision;
		if (startTX < 0) startTX = 0;
		if (startTY < 0) startTY = 0;
		if (endTX > parent.getWidth()) endTX = parent.getWidth();
		if (endTY > parent.getHeight()) endTY = parent.getHeight();
		for (int x = startTX; x < endTX; x += 1) {
			for (int y = startTY; y < endTY; y += 1) {
				boolean result = super.inLoS(x, y);
				viewCache[y][x] = result;
				if (result) seenCache[y][x] = true;
			}
		}
		for (int x = 0; x < parent.getWidth(); x += 1) {
			for (int y = 0; y < parent.getHeight(); y += 1) {
				float r = viewCache[y][x] ? 1 : 0;
				float g = seenCache[y][x] ? 1 : 0;
				p.setColor(r, g, 0, 1);
				p.drawPixel(x, y);
			}
		}
		viewTex = new Texture(p, false);
		viewTex.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
	}
	
	/**
	 * A very technical thing that returns technical things. For shaders.
	 * @return					I wrote this with a bad cold.
	 */
	public Texture getVisibleData() {
		return viewTex;
	}
	
	/**
	 * Checks if the hero has visited a particular tile on the map.
	 * @param	tileX			The x-coord of the tile to check, in tiles
	 * @param	tileY			The y-coord of the tile to check, in tiles
	 * @return					True if tile was visited, false otherwise
	 */
	public boolean seen(int tileX, int tileY) {
		return seenCache[tileY][tileX];
	}
	
	/**
	 * Relocate an item to near the hero. Used for camping.
	 * @param	item			The item event to move
	 */
	public void placeItemNear(ItemEvent item) {
		item.getItem().spawnNear(getParent(), getTileX(), getTileY(), 2);
	}
	
	/**
	 * UI function, how long til camp?
	 * @return 0-1
	 */
	public float getCampRatio() {
		float r = turnsSinceNight / (float) TURNS_PER_NIGHT;
		return (r > 1) ? 1 : r;
	}
	
	/**
	 * Called when the player tries to set up camp.
	 */
	protected void tryCamp() {
		if (isEligibleForCamp(false)) {
			setUpCamp();
		}
	}
	
	/**
	 * Movement subcommand.
	 * @param	dir				The direction the hero was ordered in
	 */
	protected void move(EightDir dir) {
		step.setDirection(dir);
		actAndWait(step);
	}
	
	/**
	 * Ability subcommand. There's some logic for blocking abilities included,
	 * ie, an ability must first be set active, then told to act. The idea here
	 * is that an ability should be able to ask the player for further input
	 * before forcing the world to move around it.
	 * @param	no				The index of the ability ordered
	 */
	protected void abil(int no) {
		if (getUnit().getAbilities().size() <= no) {
			// ability out of range
			return;
		}
		Ability abil = getUnit().getAbilities().get(no);
		if (!getUnit().canUse(abil)) {
			GameUnit.out().msg("ability not available.");
			return;
		}
		blockingAbil = abil;
	}
	
	/**
	 * Try to interact with things on the map by picking them up etc.
	 */
	protected void interact() {
		List<MapEvent> events = parent.getEventsAt(getTileX(), getTileY());
		Collections.reverse(events);
		for (MapEvent event : events) {
			if (event.onInteract()) break;
		}
	}

}
