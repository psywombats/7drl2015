/**
 *  ObjectLayer.java
 *  Created on Nov 29, 2012 3:46:55 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mgne.maps.layers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.wombatrpgs.mgne.core.MAssets;
import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.maps.Level;
import net.wombatrpgs.mgne.maps.MapThing;
import net.wombatrpgs.mgne.maps.events.MapEvent;
import net.wombatrpgs.mgne.physics.CollisionResult;

/**
 * A renderable collection of map events, grouped into a layer in a level.
 */
public class EventLayer extends Layer {
	
	protected List<MapEvent> events;
	
	/**
	 * Creates a new object layer with a parent level and no objects.
	 * @param 	parent			The parent level of the layer
	 */
	public EventLayer(Level parent) {
		super(parent);
		events = new ArrayList<MapEvent>();
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Renderable#render
	 * (com.badlogic.gdx.graphics.g2d.SpriteBatch)
	 */
	@Override
	public void render(SpriteBatch batch) {
		for (MapEvent event : events) {
			event.render(batch);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Renderable#queueRequiredAssets
	 * (MAssets)
	 */
	@Override
	public void queueRequiredAssets(MAssets manager) {
		for (MapEvent event : events) {
			event.queueRequiredAssets(manager);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Renderable#postProcessing
	 * (MAssets, int)
	 */
	@Override
	public void postProcessing(MAssets manager, int pass) {
		for (MapEvent event : events) {
			event.postProcessing(manager, pass);
		}
	}
	
	/**
	 * @see net.wombatrpgs.mgne.maps.layers.Layer#isLowerChip()
	 */
	@Override
	public boolean isLowerChip() {
		return false;
	}
	
	/**
	 * @see net.wombatrpgs.mgne.maps.layers.Layer#isTilePassable(int, int)
	 */
	@Override
	public boolean isTilePassable(int tileX, int tileY) {
		for (MapEvent event : events) {
			if (!event.isPassable() &&
					event.getTileX() == tileX &&
					event.getTileY() == tileY) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see net.wombatrpgs.mgne.maps.layers.Layer#hasTileAt(int, int)
	 */
	@Override
	public boolean hasTileAt(int tileX, int tileY) {
		return false;
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		// events are mapthings and will be disposed there
	}

	/**
	 * Adds another map event to this layer. Charas should be added from their
	 * own method.
	 * @param 	event			The map event to add
	 */
	public void add(MapEvent event) {
		if (event == null) {
			MGlobal.reporter.warn("Added a null object to the map?");
		} else {
			events.add(event);
		}
	}
	
	/**
	 * Removes a map object from this layer.
	 * @param 	event		The map object to remove
	 */
	public void remove(MapEvent event) {
		events.remove(event);
	}
	
	/**
	 * Checks if a map object exists on this layer.
	 * @param 	mapObject		The map object to check if exists
	 * @return					True if the object exists on this layer
	 */
	public boolean contains(MapThing mapObject) {
		return events.contains(mapObject);
	}
	
	/**
	 * Fetches the first event named appropriately. Call from map.
	 * @param 	name			The string the event name has to match
	 * @return					The event with matching name, or null if none
	 */
	public MapEvent getEventByName(String name) {
		for (MapEvent event : events) {
			if (name.equals(event.getName())) {
				return event;
			}
		}
		return null;
	}
	
	/**
	 * Returns all events in specified group. Call from map.
	 * @param	groupName		The name of the group to fetch from
	 * @return					A list of all events in that group
	 */
	public List<MapEvent> getEventsByGroup(String groupName) {
		List<MapEvent> result = new ArrayList<MapEvent>();
		for (MapEvent event : events) {
			if (event.inGroup(groupName)) result.add(event);
		}
		return result;
	}
	
	/**
	 * Finds and returns all events at a given location.
	 * @param	tileX			The x-coord of the event to find, in tiles
	 * @param	tileY			The y-coord of the event to find, in tiles
	 * @return					All events there, or empty if none
	 */
	public List<MapEvent> getEventsAt(int tileX, int tileY) {
		List<MapEvent> results = new ArrayList<MapEvent>();
		for (MapEvent event : events) {
			if (event.containsTile(tileX, tileY)) {
				results.add(event);
			}
		}
		return results;
	}
	
	/**
	 * Ugly public accessor.
	 * @return					All events in the event layer
	 */
	public List<MapEvent> getAll() {
		return events;
	}
	
	/**
	 * Run collision detection for an individual event on this layer.
	 * @param	event			The event to run the checks for
	 */
	public void detectCollisions(MapEvent event) {
		// TODO: optimize these loops if it becomes an issue
		//if (!event.isMobile()) return;
		for (int i = 0; i < events.size(); i++) {
			MapEvent other = events.get(i);
			if (other != event) {
				CollisionResult result = event.getHitbox().isColliding(other.getHitbox());
				if (result.isColliding) {
					boolean res1 = event.onCollide(other, result);
					boolean res2 = other.onCollide(event, result);
					if (!res1 && !res2) {
						event.resolveCollision(other, result);
					}
					break;
				}
			}
		}
	}

}
