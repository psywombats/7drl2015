/**
 *  CharacterEvent.java
 *  Created on Nov 12, 2012 11:13:21 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mrogue.characters;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.wombatrpgs.mrogue.characters.ai.ActWait;
import net.wombatrpgs.mrogue.characters.ai.Action;
import net.wombatrpgs.mrogue.characters.travel.BumpStep;
import net.wombatrpgs.mrogue.characters.travel.MoveStep;
import net.wombatrpgs.mrogue.characters.travel.Step;
import net.wombatrpgs.mrogue.core.MGlobal;
import net.wombatrpgs.mrogue.graphics.FacesAnimation;
import net.wombatrpgs.mrogue.graphics.FacesAnimationFactory;
import net.wombatrpgs.mrogue.maps.Level;
import net.wombatrpgs.mrogue.maps.events.MapEvent;
import net.wombatrpgs.mrogueschema.characters.CharacterMDO;
import net.wombatrpgs.mrogueschema.graphics.DirMDO;
import net.wombatrpgs.mrogueschema.maps.data.Direction;

/**
 * A character event is an event with an MDO and an animation that looks kind of
 * like a character. It's also a character in an RPG-like game, meaning it
 * should probably be split into RPG-chara and RM-chara at some point soon.
 */
public class CharacterEvent extends MapEvent {
	
	protected static Action defaultWait;
	
	protected CharacterMDO mdo;
	
	protected FacesAnimation appearance;
	protected boolean pacing;
	protected List<Step> travelPlan;
	protected Step lastStep;
	protected int ticksRemaining;
	
	protected GameUnit unit;

	/**
	 * Creates a new char event with the specified data.
	 * @param 	mdo				The data to create the event with
	 * @param	parent			The parent level of the event
	 */
	public CharacterEvent(CharacterMDO mdo, Level parent) {
		super(parent);
		// TODO: CharacterEvent
		init(mdo);
	}
	
	/**
	 * Creates a new char event with the specified data at the specified coords.
	 * @param	mdo				The data to create the event with
	 * @param	parent			The parent level of the event
	 * @param	tileX			The x-coord of the event (in tiles)
	 * @param	tileY			The y-coord of the event (in tiles)
	 */
	public CharacterEvent(CharacterMDO mdo, Level parent, int tileX, int tileY) {
		this(mdo, parent);
		this.tileX = tileX;
		this.tileY = tileY;
		this.x = tileX * parent.getTileWidth();
		this.y = tileY * parent.getTileHeight();
	}
	
	/**
	 * Creates a new character event associated with no map from the MDO.
	 * @param 	mdo				The MDO to create the event from
	 */
	public CharacterEvent(CharacterMDO mdo) {
		super();
		init(mdo);
	}
	
	/**
	 * Creates a new character event with the specified level at the origin.
	 * @param 	parent			The parent level of the event
	 */
	protected CharacterEvent(Level parent) {
		super(parent);
	}
	
	/** @return The cardinal direction the character is facing */
	public Direction getFacing() {
		return appearance.getFacing();
	}
	
	/**
	 * Tells the animation to face a specific direction.
	 * @param 	dir				The direction to face
	 */
	public void setFacing(Direction dir) {
		if (appearance != null) {
			appearance.setFacing(dir);
		}
	}
	
	/**
	 * Gives this character a new appearance with a four-dir anim.
	 * @param 	appearance		The new anim for this character
	 */
	public void setAppearance(FacesAnimation appearance) {
		this.appearance = appearance;
		if (pacing) {
			appearance.startMoving();
		}
	}
	
	/**
	 * @return The current appearance of this character */
	public FacesAnimation getAppearance() { return appearance; }
	
	/** @return The RPG-like stats of this character */
	public Stats getStats() { return unit.getStats(); }
	
	/** @return The RPG representation of this character */
	public GameUnit getUnit() { return unit; }
	
	/**
	 * @see net.wombatrpgs.mrogue.maps.MapThing#update(float)
	 */
	@Override
	public void update(float elapsed) {
		if (hidden()) return;
		super.update(elapsed);
		if (pacing && appearance != null) {
			appearance.update(elapsed);
		}
		if (parent.isMoving()) {
			if (travelPlan.size() > 0 ) {
				int step = (int) Math.floor((float) travelPlan.size() *
						(parent.getMoveTimeElapsed() / MGlobal.constants.getDelay()));
				Step toStep = travelPlan.get(step);
				if (lastStep != toStep && lastStep != null) {
					lastStep.onEnd();
				}
				toStep.update(elapsed);
				lastStep = toStep;
			}
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.graphics.Renderable#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		if (hidden()) return;
		super.render(camera);
		if (appearance != null) {
			appearance.render(camera);
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.graphics.PreRenderable#getRegion()
	 */
	@Override
	public TextureRegion getRegion() {
		if (hidden() || appearance == null) return null;
		return appearance.getRegion();
	}

	/**
	 * Overrides the pacing action of this character.
	 * @param 	pacing			True if character should pace, false otherwise
	 */
	public void setPacing(boolean pacing) {
		this.pacing = pacing;
		if (pacing && appearance != null) {
			appearance.startMoving();
		}
	}

	/**
	 * Makes this event face towards an object on the map.
	 * @param 	event			The object to face
	 */
	public void faceToward(MapEvent event) {
		setFacing(directionTo(event));
	}
	
	/**
	 * Makes this event face towards a tile location on the map.
	 * @param	tileX			The x-coord of the tile to face (in tiles)
	 * @param	tileY			The y-coord of the tile to face (in tiles)
	 */
	public void faceTowardTile(int tileX, int tileY) {
		setFacing(directionToTile(tileX, tileY));
	}
	
	/**
	 * Face away from a particular map event.
	 * @param	event			The object to face away from
	 */
	public void faceAway(MapEvent event) {
		setFacing(Direction.getOpposite(directionTo(event)));
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.maps.events.MapEvent#halt()
	 */
	@Override
	public void halt() {
		super.halt();
		if (!pacing) {
			appearance.stopMoving();
			appearance.update(0);
		}
		targetLocation(getX(), getY());
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.maps.events.MapEvent#onMoveStart()
	 */
	@Override
	public void onMoveStart() {
		for (Step step : travelPlan) {
			step.setTime(MGlobal.constants.getDelay() / travelPlan.size());
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.events.MapEvent#isPassable()
	 */
	@Override
	public boolean isPassable() {
		return false;
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.events.MapEvent#stopMoving()
	 */
	@Override
	public void stopMoving() {
		super.stopMoving();
		if (lastStep != null) {
			lastStep.onEnd();
			lastStep = null;
			travelPlan.clear();
		}
	}

	/**
	 * Faces this event towards its current tile target.
	 */
	public void faceTarget() {
		float dx = tileX*parent.getTileWidth() - x;
		float dy = tileY*parent.getTileHeight() - y;
		if (Math.abs(dx) > Math.abs(dy)) {
			if (dx > 0) {
				setFacing(Direction.RIGHT);
			} else if (dx < 0) {
				setFacing(Direction.LEFT);
			}
		} else {
			if (dy < 0) {
				setFacing(Direction.DOWN);
			} else if (dy > 0) {
				setFacing(Direction.UP);
			}
		}
	}
	
	/**
	 * Attempts to move to a specified tile location next to this character. If
	 * there's nothing there, the character will step to that location. If
	 * there his something there, this will hit it. Also adds an appropriate
	 * travel step.
	 * @param	targetX			The target location x-coord, in pixels
	 * @param	targetY			The target location y-coord, in pixels
	 */
	public void attemptStep(int targetX, int targetY) {
		if (parent.isTilePassable(this, targetX, targetY)) {
			List<MapEvent> events = parent.getEventsAt(targetX, targetY);
			boolean colliding = false;
			for (MapEvent event : events) {
				if (!event.isPassable()) {
					travelPlan.add(new BumpStep(this, directionToTile(targetX, targetY)));
					event.collideWith(this);
					colliding = true;
				}
			}
			if (!colliding) {
				travelPlan.add(new MoveStep(this, targetX, targetY));
				tileX = targetX;
				tileY = targetY;
			}
		} else {
			travelPlan.add(new BumpStep(this, directionToTile(targetX, targetY)));
		}
	}
	
	/**
	 * Attempts to step in a particular direction
	 * @param	dir				The direction to step.
	 */
	public void attemptStep(Direction dir) {
		int targetX = (int) (tileX + dir.getVector().x);
		int targetY = (int) (tileY + dir.getVector().y);
		attemptStep(targetX, targetY);
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.events.MapEvent#collideWith
	 * (net.wombatrpgs.mrogue.characters.CharacterEvent)
	 */
	@Override
	public void collideWith(CharacterEvent character) {
		character.getUnit().attack(getUnit());
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.events.MapEvent#ticksToAct()
	 */
	@Override
	public int ticksToAct() {
		return ticksRemaining;
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.events.MapEvent#simulateTime(int)
	 */
	@Override
	public void simulateTime(int ticks) {
		ticksRemaining -= ticks;
	}

	/**
	 * Default selects an action then waits for its duration.
	 * @see net.wombatrpgs.mrogue.maps.events.MapEvent#act()
	 */
	@Override
	public void act() {
		actAndWait(selectAction());
	}
	
	/**
	 * Performs an action and then waits an appropriate time for recovery, based
	 * on the act's cost. Should be called from act, almost always.
	 * @param	act				The action to perform
	 */
	public void actAndWait(Action act) {
		act.setActor(this);
		act.act();
		ticksRemaining += act.getCost();
	}
	
	/**
	 * Automated way of selecting an action to use each turn. Enemies should
	 * follow some AI, heroes will never call this, etc.
	 * @return					The action to use
	 */
	public Action selectAction() {
		return defaultWait;
	}

	/**
	 * Creates this event from an MDO.
	 * @param 	mdo			The MDO to create the event from
	 */
	protected void init(CharacterMDO mdo) {
		this.mdo = mdo;
		if (mdoHasProperty(mdo.appearance)) {
			DirMDO dirMDO = MGlobal.data.getEntryFor(mdo.appearance, DirMDO.class);
			appearance = FacesAnimationFactory.create(dirMDO, this);
			assets.add(appearance);
		}
		setPacing(true);
		
		travelPlan = new ArrayList<Step>();
		
		unit = new GameUnit(mdo, this);
		ticksRemaining = 0;
		
		if (defaultWait == null) {
			defaultWait = new ActWait();
		}
	}

}